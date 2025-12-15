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
package com.dianping.cat.report.page.event;

import com.dianping.cat.report.page.event.service.LocalEventService;
import com.dianping.cat.report.service.LocalModelService;
import com.dianping.cat.report.service.ModelPeriod;
import com.dianping.cat.report.service.ModelRequest;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class EventReportHandler implements PageHandler<com.dianping.cat.report.page.event.Context> {

    @Inject(type = LocalModelService.class, value = LocalEventService.ID)
    LocalEventService m_localEventService;

    @Override
    @PayloadMeta(com.dianping.cat.report.page.event.Payload.class)
    @InboundActionMeta(name = "event_report")
    public void handleInbound(com.dianping.cat.report.page.event.Context ctx) throws ServletException, IOException {
    }

    @Override
    @OutboundActionMeta(name = "event_report")
    public void handleOutbound(Context ctx) throws ServletException, IOException {
        Payload payload = ctx.getPayload();
        String domain = payload.getDomain();
        ModelPeriod period = "CURRENT".equals(payload.getPeriodStr()) ? ModelPeriod.CURRENT : ModelPeriod.LAST;
        ModelRequest request = new ModelRequest(domain, period);
        HttpServletResponse httpResponse = ctx.getHttpServletResponse();
        try {
            String xml = m_localEventService.buildReport(request, period, domain, payload);
            if (xml != null) {
                ServletOutputStream outputStream = httpResponse.getOutputStream();
                byte[] compress = compress(xml);

                httpResponse.setContentType("application/xml;charset=utf-8");
                httpResponse.addHeader("Content-Encoding", "gzip");
                outputStream.write(compress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] compress(String str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 32);
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return out.toByteArray();
    }
}
