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
package com.dianping.cat.report.page.event.service;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.Constants;
import com.dianping.cat.consumer.event.EventAnalyzer;
import com.dianping.cat.consumer.event.EventReportMerger;
import com.dianping.cat.consumer.event.model.entity.EventName;
import com.dianping.cat.consumer.event.model.entity.EventReport;
import com.dianping.cat.consumer.event.model.entity.EventType;
import com.dianping.cat.consumer.event.model.entity.Machine;
import com.dianping.cat.consumer.event.model.transform.DefaultSaxParser;
import com.dianping.cat.consumer.event.model.transform.DefaultXmlBuilder;
import com.dianping.cat.helper.TimeHelper;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.mvc.ApiPayload;
import com.dianping.cat.report.ReportBucket;
import com.dianping.cat.report.ReportBucketManager;
import com.dianping.cat.report.page.event.Payload;
import com.dianping.cat.report.service.LocalModelService;
import com.dianping.cat.report.service.ModelPeriod;
import com.dianping.cat.report.service.ModelRequest;
import org.codehaus.plexus.util.StringUtils;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import java.util.*;

@Named(type = LocalModelService.class, value = LocalEventService.ID)
public class LocalEventService extends LocalModelService<EventReport> {

    public static final String ID = EventAnalyzer.ID;

    @Inject
    private ReportBucketManager m_bucketManager;

    public LocalEventService() {
        super(EventAnalyzer.ID);
    }

    public String buildReport(ModelRequest request, ModelPeriod period, String domain, Payload payload)
            throws Exception {
        Transaction t = Cat.newTransaction(CatConstants.TYPE_CALL, "new_event_build_report");

        LocalEventService.Filter filter =
                new LocalEventService.Filter(payload.getIp(), payload.getType(), payload.getQueryType(),
                                             payload.getName());
        List<EventReport> reports = super.getReport(period, domain);
        EventReport report = new EventReport();

        if (reports != null) {
            report.setDomain(domain);
            EventReportMerger merger = new EventReportMerger(report);

            for (EventReport tmp : reports) {
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

    private String filterReport(ApiPayload payload, EventReport report) {
        String ipAddress = payload.getIpAddress();
        String type = payload.getType();
        String name = payload.getName();
        EventReportFilter filter = new EventReportFilter(type, name, ipAddress);

        return filter.buildXml(report);
    }

    @Override
    public String buildReport(ModelRequest request, ModelPeriod period, String domain, ApiPayload payload)
            throws Exception {
        Transaction t = Cat.newTransaction(CatConstants.TYPE_CALL, "old_event_build_report");

        List<EventReport> reports = super.getReport(period, domain);
        EventReport report = null;

        if (reports != null) {
            report = new EventReport(domain);
            EventReportMerger merger = new EventReportMerger(report);

            for (EventReport tmp : reports) {
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

    private EventReport getReportFromLocalDisk(long timestamp, String domain) throws Exception {
        EventReport report = new EventReport(domain);
        EventReportMerger merger = new EventReportMerger(report);

        report.setStartTime(new Date(timestamp));
        report.setEndTime(new Date(timestamp + TimeHelper.ONE_HOUR - 1));

        for (int i = 0; i < getAnalyzerCount(); i++) {
            ReportBucket bucket = null;
            try {
                bucket = m_bucketManager.getReportBucket(timestamp, EventAnalyzer.ID, i);
                String xml = bucket.findById(domain);

                if (xml != null) {
                    EventReport tmp = DefaultSaxParser.parse(xml);

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

    public static class EventReportFilter extends com.dianping.cat.consumer.event.model.transform.DefaultXmlBuilder {
        private String m_ipAddress;

        private String m_name;

        private String m_type;

        public EventReportFilter(String type, String name, String ip) {
            super(true, new StringBuilder(DEFAULT_SIZE));
            m_type = type;
            m_name = name;
            m_ipAddress = ip;
        }

        @Override
        public void visitMachine(com.dianping.cat.consumer.event.model.entity.Machine machine) {
            if (m_ipAddress == null || m_ipAddress.equals(Constants.ALL)) {
                super.visitMachine(machine);
            } else if (machine.getIp().equals(m_ipAddress)) {
                super.visitMachine(machine);
            }
        }

        @Override
        public void visitName(EventName name) {
            if (m_type != null) {
                super.visitName(name);
            }
        }

        @Override
        public void visitRange(com.dianping.cat.consumer.event.model.entity.Range range) {
            if (m_type != null && m_name != null) {
                super.visitRange(range);
            }
        }

        @Override
        public void visitType(EventType type) {
            if (m_type == null) {
                super.visitType(type);
            } else if (type.getId().equals(m_type)) {
                type.setSuccessMessageUrl(null);
                type.setFailMessageUrl(null);

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

        public Filter(String ip, String type, String queryType, String name) {
            m_ip = ip;
            m_queryType = queryType;
            m_name = name;

            if (type != null) {
                m_type_list = Arrays.asList(StringUtils.split(type, Constants.SEPARATOR_COMMA));
            }
        }

        public EventReport filter(EventReport report) {
            EventReport ret = new EventReport(report.getDomain());
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
                return filterEventName(machine);
            }
            Machine ret = new Machine(machine.getIp());

            for (EventType eventType : machine.getTypes().values()) {
                if (!m_type_list.contains(eventType.getId())) {
                    continue;
                }
                ret.addType(filterEventType(eventType));
            }

            return ret;
        }

        private Machine filterEventName(Machine machine) {
            Map<String, EventType> types = machine.getTypes();
            if (types == null || types.size() == 0) {
                return machine;
            }
            Machine newMachine = new Machine(machine.getIp());
            for (Map.Entry<String, EventType> typeEntry : types.entrySet()) {
                EventType oldEventValue = typeEntry.getValue();
                EventType newEventType = getNewEventType(oldEventValue);
                newMachine.addType(newEventType);
            }
            return newMachine;
        }

        private EventType getNewEventType(EventType oldEventValue) {
            EventType newEventType = new EventType(oldEventValue.getId());
            newEventType.setTotalCount(oldEventValue.getTotalCount());
            newEventType.setFailCount(oldEventValue.getFailCount());
            newEventType.setFailPercent(oldEventValue.getFailPercent());
            newEventType.setSuccessMessageUrl(oldEventValue.getSuccessMessageUrl());
            newEventType.setFailMessageUrl(oldEventValue.getFailMessageUrl());
            newEventType.setTps(oldEventValue.getTps());
            return newEventType;
        }

        public EventType filterEventType(EventType eventType) {
            if (m_name == null) {
                return getNewEventName(getNewEventType(eventType), eventType);
            }

            if (eventType.getNames() == null || Objects.equals("*", m_name) || Objects.equals("All", m_name)) {
                return eventType;
            }

            EventType ret = new EventType(eventType.getId());

            for (Map.Entry<String, EventName> eventNameMap : eventType.getNames().entrySet()) {
                if (Objects.equals(eventNameMap.getKey(), m_name)) {
                    ret.addName(eventNameMap.getValue());
                }
            }

            return ret;
        }

        private EventType getNewEventName(EventType newEventType, EventType eventType) {
            Map<String, EventName> names = eventType.getNames();
            if(names == null || names.size() == 0){
                return eventType;
            }

            for (Map.Entry<String, EventName> eventNameEntry : names.entrySet()) {
                EventName eventName = eventNameEntry.getValue();
                EventName newEventName = createEventName(eventName);
                newEventType.addName(newEventName);
            }

            return newEventType;
        }

        private EventName createEventName(EventName eventName) {
            EventName newEventName = new EventName(eventName.getId());
            newEventName.setTotalCount(eventName.getTotalCount());
            newEventName.setFailCount(eventName.getFailCount());
            newEventName.setFailPercent(eventName.getFailPercent());
            newEventName.setSuccessMessageUrl(eventName.getSuccessMessageUrl());
            newEventName.setFailMessageUrl(eventName.getFailMessageUrl());
            newEventName.setTps(eventName.getTps());
            return newEventName;
        }
    }

}
