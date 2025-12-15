package com.dianping.cat.report.page.transaction.transform;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.MapUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.dianping.cat.Constants;
import com.dianping.cat.consumer.problem.model.entity.Entity;
import com.dianping.cat.consumer.problem.model.entity.JavaThread;
import com.dianping.cat.consumer.problem.model.entity.Machine;
import com.dianping.cat.consumer.problem.model.entity.ProblemReport;
import com.dianping.cat.consumer.problem.model.entity.Segment;
import com.dianping.cat.consumer.transaction.model.entity.Range;
import com.dianping.cat.consumer.transaction.model.entity.TransactionName;
import com.dianping.cat.consumer.transaction.model.entity.TransactionReport;
import com.dianping.cat.consumer.transaction.model.entity.TransactionType;
import com.dianping.cat.report.page.problem.Model;
import com.dianping.cat.report.page.problem.transform.MinuteErrorBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MinuteTransactionBuilder {

    public static  String build(QueryMinuteTransactionParam param) {
        TransactionType t = param.getReport().findOrCreateMachine(param.getIp()).findOrCreateType(param.getType());
        TransactionName transactionName = t.findOrCreateName(param.getName());
        JSONObject minutesResult = initResult(param);
        Map<Integer, MinuteTransactionBuilder.Detail> transactionDetailMap = new HashMap<>();
        minutesResult.put("data", new ArrayList<>());
        if (transactionName == null) {
            return minutesResult.toJSONString();
        }
        fillHitsCount(transactionName, param, transactionDetailMap);
        fillTotal(transactionDetailMap, minutesResult);
        //fill other quota
        minutesResult.put("data", transactionDetailMap.values());
        return minutesResult.toJSONString();
    }

    private static void fillTotal(Map<Integer, Detail> transactionDetailMap, JSONObject minutesResult) {
        if (MapUtils.isEmpty(transactionDetailMap)) {
            return;
        }
        Integer total = 0;
        for (Map.Entry<Integer, Detail> detailEntry : transactionDetailMap.entrySet()) {
            total += detailEntry.getValue().getCount();
        }
        minutesResult.put("total", total);
    }

    private static void fillHitsCount(TransactionName transactionName, QueryMinuteTransactionParam param, Map<Integer, Detail> transactionDetailMap) {
        for (Range range : transactionName.getRanges().values()) {
            if (range.getValue() < param.getStartMinute() || range.getValue() > param.getEndMinute()) {
                continue;
            }
            Detail detail = transactionDetailMap.getOrDefault(range.getValue(), new Detail(range.getValue()));
            detail.setCount(detail.getCount() + range.getCount());
            transactionDetailMap.put(range.getValue(), detail);
        }
    }

    private static JSONObject initResult(QueryMinuteTransactionParam param) {
        JSONObject result = new JSONObject();
        result.put("ip", param.getIp());
        result.put("domain", param.getDomain());
        result.put("startMinute", param.getStartMinute());
        result.put("endMinute", param.getEndMinute());
        result.put("total", 0);
        return result;
    }

    private static class Detail {
        private Integer minute;
        private Integer count;

        public Detail(Integer minute) {
            this.minute = minute;
            this.count = 0;
        }

        public Integer getMinute() {
            return minute;
        }

        public void setMinute(Integer minute) {
            this.minute = minute;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

}
