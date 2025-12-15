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
package com.dianping.cat.report.page.problem.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.consumer.problem.model.entity.*;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.report.page.problem.Payload;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.consumer.problem.ProblemAnalyzer;
import com.dianping.cat.consumer.problem.ProblemReportMerger;
import com.dianping.cat.consumer.problem.model.transform.DefaultSaxParser;
import com.dianping.cat.helper.TimeHelper;
import com.dianping.cat.mvc.ApiPayload;
import com.dianping.cat.report.ReportBucket;
import com.dianping.cat.report.ReportBucketManager;
import com.dianping.cat.report.service.LocalModelService;
import com.dianping.cat.report.service.ModelPeriod;
import com.dianping.cat.report.service.ModelRequest;
import com.dianping.cat.consumer.problem.model.transform.DefaultXmlBuilder;
import org.unidal.webres.resource.loader.ClassLoaders;

import javax.crypto.Mac;

@Named(type = LocalModelService.class, value = LocalProblemService.ID)
public class LocalProblemService extends LocalModelService<ProblemReport> {

	public static final String ID = ProblemAnalyzer.ID;

	@Inject
	private ReportBucketManager m_bucketManager;

	public LocalProblemService() {
		super(ProblemAnalyzer.ID);
	}

	private String filterReport(ApiPayload payload, ProblemReport report) {
		String ipAddress = payload.getIpAddress();
		String type = payload.getType();
		String queryType = payload.getQueryType();
		String name = payload.getName();
		ProblemReportFilter filter = new ProblemReportFilter(ipAddress, type, queryType, name);

		return filter.buildXml(report);
	}

	public String buildReport(ModelRequest request, ModelPeriod period, String domain, Payload payload)
			throws Exception {
		Transaction t = Cat.newTransaction(CatConstants.TYPE_CALL, "new_problem_build_report");

		Filter filter = new Filter(payload.getIp(), payload.getType(), payload.getQueryType(), payload.getStatus());
		List<ProblemReport> reports = super.getReport(period, domain);
		ProblemReport report = new ProblemReport();

		if (reports != null) {
			report.setDomain(domain);
			ProblemReportMerger merger = new ProblemReportMerger(report);

			for (ProblemReport tmp : reports) {
				filter.filter(tmp).accept(merger);
			}
		}

		if (report.getIps().isEmpty() && period.isLast()) {
			long startTime = request.getStartTime();
			report = filter.filter(getReportFromLocalDisk(startTime, domain));
		}
		DefaultXmlBuilder xmlBuilder = new DefaultXmlBuilder(true, new StringBuilder(DEFAULT_SIZE));

		t.setStatus(Message.SUCCESS);
		t.complete();
		return xmlBuilder.buildXml(report);
	}

	@Override
	public String buildReport(ModelRequest request, ModelPeriod period, String domain, ApiPayload payload)
							throws Exception {
		Transaction t = Cat.newTransaction(CatConstants.TYPE_CALL, "old_problem_build_report");

		List<ProblemReport> reports = super.getReport(period, domain);
		ProblemReport report = null;

		if (reports != null) {
			report = new ProblemReport(domain);
			ProblemReportMerger merger = new ProblemReportMerger(report);

			for (ProblemReport tmp : reports) {
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

	private ProblemReport getReportFromLocalDisk(long timestamp, String domain) throws Exception {
		ProblemReport report = new ProblemReport(domain);
		ProblemReportMerger merger = new ProblemReportMerger(report);

		report.setStartTime(new Date(timestamp));
		report.setEndTime(new Date(timestamp + TimeHelper.ONE_HOUR - 1));

		for (int i = 0; i < getAnalyzerCount(); i++) {
			ReportBucket bucket = null;
			try {
				bucket = m_bucketManager.getReportBucket(timestamp, ProblemAnalyzer.ID, i);
				String xml = bucket.findById(domain);

				if (xml != null) {
					ProblemReport tmp = DefaultSaxParser.parse(xml);

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

	public static class ProblemReportFilter extends com.dianping.cat.consumer.problem.model.transform.DefaultXmlBuilder {
		private String m_ipAddress;

		// view is show the summary,detail show the thread info
		private String m_type;

		private String m_queryType;

		private String m_status;

		public ProblemReportFilter(String ipAddress, String type, String queryType, String name) {
			super(true, new StringBuilder(DEFAULT_SIZE));
			m_ipAddress = ipAddress;
			m_type = type;
			m_status = name;
			m_queryType = queryType;
		}

		@Override
		public void visitDuration(com.dianping.cat.consumer.problem.model.entity.Duration duration) {
			super.visitDuration(duration);
		}

		@Override
		public void visitEntry(Entry entry) {
			if (m_type == null) {
				super.visitEntry(entry);
			} else {
				if (m_status == null) {
					if (entry.getType().equals(m_type)) {
						super.visitEntry(entry);
					}
				} else {
					if (entry.getType().equals(m_type) && entry.getStatus().equals(m_status)) {
						super.visitEntry(entry);
					}
				}
			}
		}

		@Override
		public void visitMachine(Machine machine) {
			if (m_ipAddress == null) {
				super.visitMachine(machine);
			} else if (machine.getIp().equals(m_ipAddress)) {
				super.visitMachine(machine);
			}
		}

		@Override
		public void visitSegment(Segment segment) {
			super.visitSegment(segment);
		}

		@Override
		public void visitThread(JavaThread thread) {
			if ("detail".equals(m_queryType)) {
				super.visitThread(thread);
			}
		}
	}

	private static class Filter {
		private String m_ip;

		private String m_type;

		// view is show the summary,detail show the thread info
		private String m_queryType;

		private String m_status;

		public Filter(String ip, String type, String queryType, String status) {
			m_ip = ip;
			m_type = type;
			m_queryType = queryType;
			m_status = status;
		}

		public ProblemReport filter(ProblemReport report) {
			ProblemReport ret = new ProblemReport(report.getDomain());
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
			Machine ret = new Machine(machine.getIp());

			if (m_type != null && m_status != null) {
				String id = m_type + ":" + m_status;
				Entity tmp = machine.getEntities().get(id);
				if (tmp != null) {
					ret.addEntity(filterEntity(tmp));
				}
				return ret;
			}

			for (Entity entity : machine.getEntities().values()) {
				if (m_type != null && !m_type.equals(entity.getType())) {
					continue;
				}
				if (m_status != null && !m_status.equals(entity.getStatus())) {
					continue;
				}
				ret.addEntity(filterEntity(entity));
			}

			return ret;
		}

		public Entity filterEntity(Entity entity) {
			Entity ret = new Entity(entity.getId());
			ret.setType(entity.getType());
			ret.setStatus(entity.getStatus());
			if ("view".equals(m_queryType)) {
				for (Duration duration : entity.getDurations().values()) {
					ret.addDuration(duration);
				}
			} else {
				for (JavaThread thread : entity.getThreads().values()) {
					ret.addThread(filterThread(thread));
				}
				for (Duration duration : entity.getDurations().values()) {
					Duration d = new Duration(duration.getValue());
					d.setCount(duration.getCount());
					List<String> messages = duration.getMessages();
					for (String message : messages) {
						d.addMessage(message);
					}
					ret.addDuration(d);
				}
			}
			return ret;
		}

		public JavaThread filterThread(JavaThread thread) {
			JavaThread ret = new JavaThread(thread.getId());
			ret.setGroupName(thread.getGroupName());
			ret.setName(thread.getName());
			for (Segment seg : thread.getSegments().values()) {
				Segment tmp = new Segment();
				tmp.setId(seg.getId());
				tmp.setCount(seg.getCount());
				for (String message : seg.getMessages()) {
					tmp.addMessage(message);
				}
				ret.addSegment(tmp);
			}
			return ret;
		}
	}
}
