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
package com.dianping.cat.analysis;

public class PeriodStrategy {
	private long m_duration;

	private long m_extraTime;

	private long m_aheadTime;

	private long m_lastStartTime;

	private long m_lastEndTime;

	public PeriodStrategy(long duration, long extraTime, long aheadTime) {
		m_duration = duration;
		m_extraTime = extraTime;
		m_aheadTime = aheadTime;
		m_lastStartTime = -1;
		m_lastEndTime = 0;
	}

	public long getDuration() {
		return m_duration;
	}

	public long next(long now) {
		// 当前
		long startTime = now - now % m_duration;

		// for current period
		if (startTime > m_lastStartTime) {
			m_lastStartTime = startTime;
			return startTime;
		}

		// prepare next period ahead 表示准备下一个周期 上个周期的第57-59分钟内
		if (now - m_lastStartTime >= m_duration - m_aheadTime) {
			m_lastStartTime = startTime + m_duration;
			return startTime + m_duration;
		}

		// last period is over 在如果是当前周期内 小于第57分钟，则触发结束上一个周期的任务
		if (now - m_lastEndTime >= m_duration + m_extraTime) {
			long lastEndTime = m_lastEndTime;
			m_lastEndTime = startTime;
			return -lastEndTime;
		}

		return 0;
	}
}