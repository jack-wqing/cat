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
package com.dianping.cat.report.page.transaction.service;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.Constants;
import com.dianping.cat.consumer.transaction.TransactionAnalyzer;
import com.dianping.cat.consumer.transaction.TransactionReportMerger;
import com.dianping.cat.consumer.transaction.model.entity.*;
import com.dianping.cat.consumer.transaction.model.transform.DefaultSaxParser;
import com.dianping.cat.consumer.transaction.model.transform.DefaultXmlBuilder;
import com.dianping.cat.helper.TimeHelper;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.mvc.ApiPayload;
import com.dianping.cat.report.ReportBucket;
import com.dianping.cat.report.ReportBucketManager;
import com.dianping.cat.report.page.transaction.Payload;
import com.dianping.cat.report.service.LocalModelService;
import com.dianping.cat.report.service.ModelPeriod;
import com.dianping.cat.report.service.ModelRequest;
import org.codehaus.plexus.util.StringUtils;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import java.util.*;

@Named(type = LocalModelService.class, value = LocalTransactionService.ID)
public class LocalTransactionService extends LocalModelService<TransactionReport> {

    public static final String ID = TransactionAnalyzer.ID;

    @Inject
    private ReportBucketManager m_bucketManager;

    public LocalTransactionService() {
        super(TransactionAnalyzer.ID);
    }

    public String buildReport(ModelRequest request, ModelPeriod period, String domain, Payload payload)
            throws Exception {
        Transaction t = Cat.newTransaction(CatConstants.TYPE_CALL, "new_transaction_build_report");

        LocalTransactionService.Filter filter =
                new LocalTransactionService.Filter(payload.getIp(), payload.getQueryType(), payload.getName(),
                                                   payload.getType());
        List<TransactionReport> reports = super.getReport(period, domain);
        TransactionReport report = new TransactionReport();

        if (reports != null) {
            report.setDomain(domain);
            TransactionReportMerger merger = new TransactionReportMerger(report);

            for (TransactionReport tmp : reports) {
                tmp.accept(merger);
            }
        }

        if (report.getIps().isEmpty() && period.isLast()) {
            long startTime = request.getStartTime();
            report = filter.filter(getReportFromLocalDisk(startTime, domain));
        }
        report = filter.filter(report);
        DefaultXmlBuilder xmlBuilder = new DefaultXmlBuilder(true, new StringBuilder(DEFAULT_SIZE));
        t.setStatus(Message.SUCCESS);
        t.complete();
        return xmlBuilder.buildXml(report);
    }

    @Override
    public String buildReport(ModelRequest request, ModelPeriod period, String domain, ApiPayload payload)
            throws Exception {
        Transaction t = Cat.newTransaction(CatConstants.TYPE_CALL, "old_transaction_build_report");

        List<TransactionReport> reports = super.getReport(period, domain);
        TransactionReport report = null;

        if (reports != null) {
            report = new TransactionReport(domain);
            TransactionReportMerger merger = new TransactionReportMerger(report);

            for (TransactionReport tmp : reports) {
                tmp.accept(merger);
            }
        }

        if ((report == null || report.getIps().isEmpty()) && period.isLast()) {
            long startTime = request.getStartTime();
            report = getReportFromLocalDisk(startTime, domain);
        }
        t.setStatus(Message.SUCCESS);
        t.complete();
        return filterReport(payload, report);
    }

    private String filterReport(ApiPayload payload, TransactionReport report) {
        String type = payload.getType();
        String name = payload.getName();
        String ip = payload.getIpAddress();
        int min = payload.getMin();
        int max = payload.getMax();
        String xml;

        try {
            TransactionReportFilter filter = new TransactionReportFilter(type, name, ip, min, max);

            xml = filter.buildXml(report);
        } catch (Exception e) {
            TransactionReportFilter filter = new TransactionReportFilter(type, name, ip, min, max);

            xml = filter.buildXml(report);
        }
        return xml;
    }

    private TransactionReport getReportFromLocalDisk(long timestamp, String domain) throws Exception {
        TransactionReport report = new TransactionReport(domain);
        TransactionReportMerger merger = new TransactionReportMerger(report);

        report.setStartTime(new Date(timestamp));
        report.setEndTime(new Date(timestamp + TimeHelper.ONE_HOUR - 1));

        for (int i = 0; i < getAnalyzerCount(); i++) {
            ReportBucket bucket = null;
            try {
                bucket = m_bucketManager.getReportBucket(timestamp, TransactionAnalyzer.ID, i);
                String xml = bucket.findById(domain);

                if (xml != null) {
                    TransactionReport tmp = DefaultSaxParser.parse(xml);

                    tmp.accept(merger);
                }
            } finally {
                if (bucket != null) {
                    m_bucketManager.closeBucket(bucket);
                }
            }
        }
        return report;
    }

    public static class TransactionReportFilter
            extends com.dianping.cat.consumer.transaction.model.transform.DefaultXmlBuilder {
        private String m_ipAddress;

        private String m_name;

        private String m_type;

        private int m_min;

        private int m_max;

        public TransactionReportFilter(String type, String name, String ip, int min, int max) {
            super(true, new StringBuilder(DEFAULT_SIZE));
            m_type = type;
            m_name = name;
            m_ipAddress = ip;
            m_min = min;
            m_max = max;
        }

        @Override
        public void visitAllDuration(AllDuration duration) {
        }

        @Override
        public void visitDuration(Duration duration) {
            if (m_type != null && m_name != null) {
                super.visitDuration(duration);
            }
        }

        @Override
        public void visitMachine(com.dianping.cat.consumer.transaction.model.entity.Machine machine) {
            if (m_ipAddress == null || m_ipAddress.equals(Constants.ALL)) {
                super.visitMachine(machine);
            } else if (machine.getIp().equals(m_ipAddress)) {
                super.visitMachine(machine);
            }
        }

        @Override
        public void visitName(TransactionName name) {
            if (m_type != null) {
                visitTransactionName(name);
            }
        }

        @Override
        public void visitRange(Range range) {
            if (m_type != null && m_name != null) {
                int minute = range.getValue();

                if (m_min == -1 && m_max == -1) {
                    super.visitRange(range);
                } else if (minute <= m_max && minute >= m_min) {
                    super.visitRange(range);
                }
            }
        }

        private void visitTransactionName(TransactionName name) {
            super.visitName(name);
        }

        @Override
        public void visitTransactionReport(TransactionReport transactionReport) {
            synchronized (transactionReport) {
                super.visitTransactionReport(transactionReport);
            }
        }

        @Override
        public void visitType(TransactionType type) {
            if (m_type == null) {
                super.visitType(type);
            } else if (type.getId().equals(m_type)) {
                super.visitType(type);
            }
        }

    }

    private static class Filter {
        private String m_ip;

        // view is show the summary,detail show the thread info
        private String m_queryType;

        private String m_name;

        private List<String> m_type_list;

        public Filter(String ip, String queryType, String name, String type) {
            m_ip = ip;
            m_queryType = queryType;
            m_name = name;

            if (type != null) {
                m_type_list = Arrays.asList(StringUtils.split(type, Constants.SEPARATOR_COMMA));
            }
        }

        public TransactionReport filter(TransactionReport report) {
            TransactionReport ret = new TransactionReport(report.getDomain());
            ret.setStartTime(report.getStartTime());
            ret.setEndTime(report.getEndTime());

            for (String ip : report.getIps()) {
                ret.addIp(ip);
            }

            if (m_ip == null || "All".equals(m_ip)) {
                for (Machine machine : report.getMachines().values()) {
                    ret.addMachine(filterMachine(machine));
                }
            } else {
                Machine machine = report.getMachines().get(m_ip);

                if (machine != null) {
                    ret.addMachine(filterMachine(machine));
                }
            }

            return ret;
        }

        public Machine filterMachine(Machine machine) {
            if (m_type_list == null) {
                return filterTransactionName(machine);
            }
            Machine ret = new Machine(machine.getIp());

            for (TransactionType transactionType : machine.getTypes().values()) {
                if (!m_type_list.contains(transactionType.getId())) {
                    continue;
                }
                ret.addType(filterTransactionType(transactionType));
            }

            return ret;
        }

        private Machine filterTransactionName(Machine machine) {
            Map<String, TransactionType> types = machine.getTypes();
            if (types == null || types.size() == 0) {
                return machine;
            }
            Machine newMachine = new Machine(machine.getIp());
            for (Map.Entry<String, TransactionType> typeEntry : types.entrySet()) {
                TransactionType oldTransactionValue = typeEntry.getValue();
                TransactionType newTransactionType = getNewTransactionType(oldTransactionValue);
                newMachine.addType(newTransactionType);
            }
            return newMachine;
        }

        private TransactionType getNewTransactionType(TransactionType oldTransactionValue) {
            TransactionType newTransactionType = new TransactionType(oldTransactionValue.getId());
            newTransactionType.setTotalCount(oldTransactionValue.getTotalCount());
            newTransactionType.setFailCount(oldTransactionValue.getFailCount());
            newTransactionType.setFailPercent(oldTransactionValue.getFailPercent());
            newTransactionType.setMin(oldTransactionValue.getMin());
            newTransactionType.setMax(oldTransactionValue.getMax());
            newTransactionType.setAvg(oldTransactionValue.getAvg());
            newTransactionType.setSum(oldTransactionValue.getSum());
            newTransactionType.setSum2(oldTransactionValue.getSum2());
            newTransactionType.setStd(oldTransactionValue.getStd());
            newTransactionType.setSuccessMessageUrl(oldTransactionValue.getSuccessMessageUrl());
            newTransactionType.setFailMessageUrl(oldTransactionValue.getFailMessageUrl());
            newTransactionType.setTps(oldTransactionValue.getTps());
            newTransactionType.setLine95Value(oldTransactionValue.getLine95Value());
            newTransactionType.setLine50Value(oldTransactionValue.getLine50Value());
            newTransactionType.setLine90Value(oldTransactionValue.getLine90Value());
            newTransactionType.setLine99Value(oldTransactionValue.getLine99Value());
            newTransactionType.setLine999Value(oldTransactionValue.getLine999Value());
            newTransactionType.setLine9999Value(oldTransactionValue.getLine9999Value());
            newTransactionType.setLongestMessageUrl(oldTransactionValue.getLongestMessageUrl());
            return newTransactionType;
        }

        public TransactionType filterTransactionType(TransactionType transactionType) {
            if (m_name == null) {
                return getNewTransactionName(getNewTransactionType(transactionType), transactionType);
            }
            if (Objects.equals("*", m_name) || Objects.equals("All", m_name) || transactionType.getNames() == null) {
                return transactionType;
            }
            TransactionType ret = new TransactionType(transactionType.getId());

            for (Map.Entry<String, TransactionName> transactionNameMap : transactionType.getNames().entrySet()) {
                if (Objects.equals(transactionNameMap.getKey(), m_name)) {
                    ret.addName(transactionNameMap.getValue());
                }
            }
            return ret;
        }

        private TransactionType getNewTransactionName(TransactionType newTransactionType,
                                                      TransactionType transactionType) {
            Map<String, TransactionName> names = transactionType.getNames();
            if(names == null || names.size() == 0){
                return transactionType;
            }

            for (Map.Entry<String, TransactionName> transactionNameEntry : names.entrySet()) {
                TransactionName transactionName = transactionNameEntry.getValue();
                TransactionName newTransactionName = createTransactionName(transactionName);
                newTransactionType.addName(newTransactionName);
            }

            return newTransactionType;
        }

        private TransactionName createTransactionName(TransactionName transactionName) {
            TransactionName newTransactionName = new TransactionName(transactionName.getId());
            newTransactionName.setTotalCount(transactionName.getTotalCount());
            newTransactionName.setFailCount(transactionName.getFailCount());
            newTransactionName.setFailPercent(transactionName.getFailPercent());
            newTransactionName.setMin(transactionName.getMin());
            newTransactionName.setMax(transactionName.getMax());
            newTransactionName.setAvg(transactionName.getAvg());
            newTransactionName.setSum(transactionName.getSum());
            newTransactionName.setSum2(transactionName.getSum2());
            newTransactionName.setStd(transactionName.getStd());
            newTransactionName.setSuccessMessageUrl(transactionName.getSuccessMessageUrl());
            newTransactionName.setFailMessageUrl(transactionName.getFailMessageUrl());
            newTransactionName.setTps(transactionName.getTps());
            newTransactionName.setLine95Value(transactionName.getLine95Value());
            newTransactionName.setLine50Value(transactionName.getLine50Value());
            newTransactionName.setLine90Value(transactionName.getLine90Value());
            newTransactionName.setLine99Value(transactionName.getLine99Value());
            newTransactionName.setLine999Value(transactionName.getLine999Value());
            newTransactionName.setLine9999Value(transactionName.getLine9999Value());
            newTransactionName.setLongestMessageUrl(transactionName.getLongestMessageUrl());
            return newTransactionName;
        }

    }
}
