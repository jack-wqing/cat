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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.dianping.cat.Cat;
import com.dianping.cat.report.service.ModelRequest;
import org.xml.sax.SAXException;

import com.dianping.cat.consumer.problem.ProblemAnalyzer;
import com.dianping.cat.consumer.problem.model.entity.ProblemReport;
import com.dianping.cat.consumer.problem.model.transform.DefaultSaxParser;
import com.dianping.cat.report.service.BaseRemoteModelService;

public class RemoteProblemService extends BaseRemoteModelService<ProblemReport> {

    private String m_serviceUrl = "/cat/r/problem_report";

    public RemoteProblemService() {
        super(ProblemAnalyzer.ID);
    }

    public URL buildUrl(ModelRequest request) throws MalformedURLException {
        StringBuilder sb = new StringBuilder(256);

        for (Map.Entry<String, String> e : request.getProperties().entrySet()) {
            if (e.getValue() != null) {
                try {
                    if ("status".equals(e.getKey()) && e.getValue().contains("{")) {
                        continue;
                    }
                    sb.append('&');
                    sb.append(e.getKey()).append('=').append(URLEncoder.encode(e.getValue(), "utf-8"));
                } catch (Exception ex) {
                    Cat.logError(ex);
                }
            }
        }
        String url = String.format("http://%s:%s%s?domain=%s&periodStr=%s%s",
                super.getHost(), super.getPort(), m_serviceUrl, request.getDomain(), request.getPeriod(), sb.toString());

        return new URL(url);
    }

    @Override
    protected ProblemReport buildModel(String xml) throws SAXException, IOException {
        return DefaultSaxParser.parse(xml);
    }

    @Override
    public boolean isServersFixed() {
        return true;
    }
}
