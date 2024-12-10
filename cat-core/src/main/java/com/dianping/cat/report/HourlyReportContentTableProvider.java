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
package com.dianping.cat.report;

import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.lookup.annotation.Named;
// HourlyReportContentTable
@Named(type = TableProvider.class, value = HourlyReportContentTableProvider.LOGIC_TABLE_NAME)
public class HourlyReportContentTableProvider implements TableProvider, Initializable {

	public final static String LOGIC_TABLE_NAME = "report-content";

	protected String m_logicalTableName = LOGIC_TABLE_NAME;

	@Override
	public String getDataSourceName(Map<String, Object> hints, String logicalTableName) {
		return "cat";
	}

	@Override
	public String getPhysicalTableName(Map<String, Object> hints, String logicalTableName) {
		return "hourly_report_content";
	}

	@Override
	public void initialize() throws InitializationException {
	}

	public void setLogicalTableName(String logicalTableName) {
		m_logicalTableName = logicalTableName;
	}

}