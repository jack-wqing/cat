package com.dianping.cat.report.page.problem.transform;

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
import com.dianping.cat.report.page.problem.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MinuteErrorBuilder {

    public static  String build(ProblemReport report, Model m_model) {
        if (report == null) {
            return "";
        }
        JSONObject result = initResult(m_model);
        Map<Integer, Map<String, Detail>> errorCount = new HashMap<>();
        result.put("data", new ArrayList<>());

        if (!Constants.ALL.equals(m_model.getIpAddress()) && (CollectionUtils.isEmpty(m_model.getIpList()) || MapUtils.isEmpty(report.getMachines()))) {
            return result.toJSONString();
        }
        List<Machine> machineList = report.getMachines().values().stream()
                .filter(machine -> isAll(m_model) || m_model.getIpList().contains(machine.getIp()))
                .collect(Collectors.toList());
        for (Machine machine : machineList) {
            Collection<Entity> entityList = machine.getEntities().values();
            canculatePerMachine(m_model, errorCount, machine, entityList);
        }

        List<Detail> details = errorCount.values().stream().map(Map::values).flatMap(Collection::stream).collect(Collectors.toList());
        result.put("data", details);
        result.put("total", details.stream().map(Detail::getCount).reduce(Integer::sum).orElse(0));
        return result.toJSONString();
    }

    private static void canculatePerMachine(Model m_model, Map<Integer, Map<String, Detail>> errorCount, Machine machine, Collection<Entity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        for (Entity entity : entityList) {
            if (!entity.getType().equals("error")) {
                continue;
            }
            for (JavaThread javaThread : entity.getThreads().values()) {
                calculatePerThread(m_model, errorCount, machine, entity, javaThread);
            }
        }
    }

    private static void calculatePerThread(Model m_model, Map<Integer, Map<String, Detail>> errorCount, Machine machine, Entity entity, JavaThread javaThread) {
        String ip = isAll(m_model) ? Constants.ALL : machine.getIp();
        for (Map.Entry<Integer, Segment> segmentEntry  : javaThread.getSegments().entrySet()) {
            if (segmentEntry.getKey() < m_model.getStartMinute() || segmentEntry.getKey() > m_model.getLastMinute()) {
                continue;
            }
            Map<String, Detail> machineDetailMap = errorCount.get(segmentEntry.getKey());
            if (machineDetailMap == null) {
                machineDetailMap = new HashMap<>();
            }
            Detail detail = machineDetailMap.getOrDefault(ip, new Detail(segmentEntry.getKey(), ip));
            detail.add(segmentEntry.getValue().getCount(), entity.getStatus());
            machineDetailMap.put(ip, detail);
            errorCount.put(segmentEntry.getKey(), machineDetailMap);
        }
    }

    private static JSONObject initResult(Model m_model) {
        JSONObject result = new JSONObject();
        result.put("ip", isAll(m_model) ? Constants.ALL : m_model.getIpList().toString());
        result.put("domain", m_model.getDomain());
        result.put("startMinute", m_model.getStartMinute());
        result.put("endMinute", m_model.getLastMinute());
        result.put("total", 0);
        return result;
    }

    private static Boolean isAll(Model m_model) {
        return Constants.ALL.equals(m_model.getIpAddress()) &&
                (CollectionUtils.isEmpty(m_model.getIpList()) ||
                        CollectionUtils.isEmpty(m_model.getIpList().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList())));
    }

    private static class Detail {
        private Integer minute;
        private Integer count;
        private String ip;
        private Map<String, Integer> detailCount;

        public Detail(Integer minute, String ip) {
            this.minute = minute;
            this.count = 0;
            this.detailCount = new HashMap<>();
            this.ip = ip;
        }

        public Detail(Integer minute, Integer count, String ip) {
            this.minute = minute;
            this.count = count;
            this.detailCount = new HashMap<>();
            this.ip = ip;
        }

        private void add(Integer tCount, String m_status) {
            this.count += tCount;
            detailCount.put(m_status, detailCount.getOrDefault(m_status, 0) + tCount);
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

        public Map<String, Integer> getDetailCount() {
            return detailCount;
        }

        public void setDetailCount(Map<String, Integer> detailCount) {
            this.detailCount = detailCount;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }
}
