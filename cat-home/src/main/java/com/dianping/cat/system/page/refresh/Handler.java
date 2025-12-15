/*
 * Copyright (c) 2011-2018, Meituan Dianping. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dianping.cat.system.page.refresh;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Constants;
import com.dianping.cat.consumer.problem.model.entity.ProblemReport;
import com.dianping.cat.consumer.transaction.model.entity.Machine;
import com.dianping.cat.consumer.transaction.model.entity.TransactionName;
import com.dianping.cat.consumer.transaction.model.entity.TransactionReport;
import com.dianping.cat.consumer.transaction.model.entity.TransactionType;
import com.dianping.cat.helper.TimeHelper;
import com.dianping.cat.home.exception.entity.ExceptionExclude;
import com.dianping.cat.home.exception.entity.ExceptionLimit;
import com.dianping.cat.report.alert.exception.ExceptionAlert;
import com.dianping.cat.report.alert.exception.ExceptionRuleConfigManager;
import com.dianping.cat.report.page.dependency.TopMetric;
import com.dianping.cat.report.page.problem.LongConfig;
import com.dianping.cat.report.page.problem.service.ProblemReportService;
import com.dianping.cat.report.page.problem.transform.ProblemStatistics;
import com.dianping.cat.report.page.transaction.XmlViewer;
import com.dianping.cat.report.page.transaction.service.TransactionReportService;
import com.dianping.cat.report.page.transaction.transform.TransactionMergeHelper;
import com.dianping.cat.service.ProjectService;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class Handler implements PageHandler<Context> {


    @Inject
    public ProjectService m_projectService;

    @Inject
    public ExceptionAlert exceptionAlert;

    @Inject
    private TransactionReportService m_reportService;

    @Inject
    private ProblemReportService m_p_reportService;

    @Inject
    private TransactionMergeHelper m_mergeHelper;

    @Inject
    private ExceptionRuleConfigManager m_exceptionRuleConfigManager;

    @Inject
    private XmlViewer m_xmlViewer;

    private final String ERROR = "error";


    @Override
    @PayloadMeta(Payload.class)
    @InboundActionMeta(name = "refresh")
    public void handleInbound(Context ctx) throws ServletException, IOException {
        // display only, no action here
    }

    @Override
    @OutboundActionMeta(name = "refresh")
    public void handleOutbound(Context ctx) throws ServletException, IOException {
        Payload payload = ctx.getPayload();
        Action action = payload.getAction();

        switch (action) {
            case REFRESH_PROJECT:
                m_projectService.refreshProject(payload.getDomain());
                break;
            case REFRESH_EXCEPTION:
                refreshExceptionConfig(payload);
                break;
            case REFRESH_EXCLUDE_EXCEPTION:
                refreshExceptionExcludeConfig(payload);
                break;
            case GET_REPORT:
                getReport(ctx, payload);
            case GET_TOP_ERROR_REPORT:
                getTopErrorReport(ctx, payload);
            case GET_ERROR_REPORT:
                getErrorReport(ctx, payload);
        }
    }

    private void getTopErrorReport(Context ctx, Payload payload) {
        long current = System.currentTimeMillis();
        TopMetric topMetric = exceptionAlert.buildTopMetric(new Date(current - TimeHelper.ONE_MINUTE - current%TimeHelper.ONE_MINUTE));
        Map<String, List<TopMetric.Item>> result = topMetric.getError().getResult();
        viewJSON(ctx, result);
    }


    private void getErrorReport(Context ctx, Payload payload) {
        ProblemStatistics problemStatistics = new ProblemStatistics();
        String ip = payload.getIpAddress();
        String domain = payload.getDomain();
        LongConfig longConfig = new LongConfig();

        problemStatistics.setLongConfig(longConfig);
        ProblemReport problemReport = showSummarizeReport(payload);

        if (ip.equals(Constants.ALL)) {
            problemStatistics.setAllIp(true);
            problemStatistics.visitProblemReport(problemReport);
        } else {
            problemStatistics.setIp(ip);
            problemStatistics.visitProblemReport(problemReport);
        }

        filterReport(problemStatistics);
        filterExcludeError(problemStatistics, domain);
        viewJSON(ctx, problemStatistics);
    }

    private void filterExcludeError(ProblemStatistics problemStatistics, String domain) {
        List<ExceptionExclude> exceptionExcludeList = m_exceptionRuleConfigManager.queryAllExceptionExcludes();
        if (exceptionExcludeList == null || exceptionExcludeList.isEmpty()) {
            return;
        }
        Map<String, Set<String>> domainNameMap = transformDomainNameMap(exceptionExcludeList);
        Set<String> excludeNameSet = domainNameMap.get(domain);
        if (excludeNameSet == null || excludeNameSet.isEmpty()) {
            return;
        }
        excludeErrorName(problemStatistics, excludeNameSet);
    }

    private void excludeErrorName(ProblemStatistics problemStatistics, Set<String> excludeNameSet) {
        Map<String, ProblemStatistics.TypeStatistics> status = problemStatistics.getStatus();
        if (status == null || status.isEmpty()) {
            return;
        }

        ProblemStatistics.TypeStatistics typeStatistics = status.get(ERROR);
        if (typeStatistics == null) {
            return;
        }

        Map<String, ProblemStatistics.StatusStatistics> statisticsStatus = typeStatistics.getStatus();
        if (statisticsStatus == null || statisticsStatus.isEmpty()) {
            return;
        }

        int totalExcludeCount = 0;
        Map<String, ProblemStatistics.StatusStatistics> retainProblemMap = Maps.newHashMap();
        for (Map.Entry<String, ProblemStatistics.StatusStatistics> statisticsEntry : statisticsStatus.entrySet()) {
            String name = statisticsEntry.getKey();
            ProblemStatistics.StatusStatistics statistics = statisticsEntry.getValue();

            if (!excludeNameSet.contains(name)) {
                retainProblemMap.put(name, statistics);
                continue;
            }

            int count = statistics.getCount();
            totalExcludeCount += count;
        }

        typeStatistics.setStatus(retainProblemMap);
        typeStatistics.setCount(typeStatistics.getCount() - totalExcludeCount);
    }

    private Map<String, Set<String>> transformDomainNameMap(List<ExceptionExclude> exceptionExcludeList) {
        Map<String, Set<String>> domainNameMap = Maps.newHashMap();

        for (ExceptionExclude exceptionExclude : exceptionExcludeList) {
            String domain = exceptionExclude.getDomain();
            Set<String> names = domainNameMap.get(domain);
            if (names == null || names.isEmpty()) {
                names = Sets.newHashSet();
            }
            names.add(exceptionExclude.getName());
            domainNameMap.put(domain, names);
        }
        return domainNameMap;
    }

    private void filterReport(ProblemStatistics problemStatistics) {
        Map<String, ProblemStatistics.TypeStatistics> status = problemStatistics.getStatus();
        if (status == null || status.size() == 0) {
            return;
        }

        for (Map.Entry<String, ProblemStatistics.TypeStatistics> statisticsEntry : status.entrySet()) {
            ProblemStatistics.TypeStatistics statistics = statisticsEntry.getValue();
            Map<String, ProblemStatistics.StatusStatistics> statisticsStatus = statistics.getStatus();
            if (statisticsStatus == null || statisticsStatus.isEmpty()) {
                continue;
            }
            for (Map.Entry<String, ProblemStatistics.StatusStatistics> entry : statisticsStatus.entrySet()) {
                ProblemStatistics.StatusStatistics entryValue = entry.getValue();
                entryValue.setLinks(null);
            }
        }
    }

    private ProblemReport showSummarizeReport(Payload payload) {
        Date start = payload.getHistoryStartDate();
        Date end = payload.getHistoryEndDate();

        return m_p_reportService.queryReport(payload.getDomain(), start, end);
    }


    private void getReport(Context ctx, Payload payload) {
        TransactionReport report = m_reportService.queryReport(payload.getDomain(), payload.getHistoryStartDate(), payload.getHistoryEndDate());
        String ipAddress = payload.getIpAddress();
        report = m_mergeHelper.mergeAllMachines(report, ipAddress);
        if (report != null) {
            filterReport(report);
            viewJSON(ctx, report);
        }
    }

    private void filterReport(TransactionReport report) {
        Map<String, Machine> machines = report.getMachines();
        for (Map.Entry<String, Machine> machineEntry : machines.entrySet()) {
            Machine value = machineEntry.getValue();
            Map<String, TransactionType> types = value.getTypes();
            if (types == null || types.size() == 0) {
                continue;
            }

            for (Map.Entry<String, TransactionType> transactionTypeEntry : types.entrySet()) {
                TransactionType transactionType = transactionTypeEntry.getValue();
                transactionType.setGraphTrend(null);
                Map<String, TransactionName> names = transactionType.getNames();
                if (names == null || names.size() == 0) {
                    continue;
                }

                for (Map.Entry<String, TransactionName> transactionNameEntry : names.entrySet()) {
                    TransactionName transactionName = transactionNameEntry.getValue();
                    transactionName.setGraphTrend(null);
                }
            }
        }
    }

    private void viewJSON(Context ctx, Object report) {
        HttpServletResponse res = ctx.getHttpServletResponse();
        try {
            if (report != null) {
                ServletOutputStream out = null;

                out = res.getOutputStream();
                res.setContentType("application/json");
                out.print(JSONObject.toJSONString(report));
            } else {
                res.sendError(404, "Not found!");
            }
        } catch (Exception e) {

        }
    }

    private void refreshExceptionExcludeConfig(Payload payload) {
        if ("add".equals(payload.getMethodType())) {
            ExceptionExclude exclude = new ExceptionExclude();
            exclude.setDomain(payload.getDomain().trim());
            exclude.setName(payload.getExceptionName().trim());
            exclude.setId(payload.getDomain() + ":" + payload.getExceptionName());
            m_exceptionRuleConfigManager.insertExceptionExclude(exclude);
        } else if ("delete".equals(payload.getMethodType())) {
            m_exceptionRuleConfigManager.deleteExceptionExclude(payload.getDomain(), payload.getExceptionName());
        } else {

        }
    }

    private void refreshExceptionConfig(Payload payload) {
        String exceptionLimitStr = payload.getExceptionLimit();
        if (Objects.equals(exceptionLimitStr, "null")) {
            m_exceptionRuleConfigManager.deleteExceptionLimit(payload.getDomain(), payload.getExceptionName());
            return;
        }
        ExceptionLimit exceptionLimit = JSONArray.parseObject(exceptionLimitStr, ExceptionLimit.class);
        m_exceptionRuleConfigManager.insertExceptionLimit(exceptionLimit);
    }

}
