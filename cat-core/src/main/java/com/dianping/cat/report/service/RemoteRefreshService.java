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
package com.dianping.cat.report.service;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.unidal.lookup.annotation.Inject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

import static com.dianping.cat.Cat.newTransaction;

/**
 * @author limaozhan
 */
public class RemoteRefreshService {

    private String m_host;

    private Integer m_port;

    private final Integer RETRY_TIME = 3;

    @Inject
    private String m_service_Uri = "/cat/s/refresh";

    public void refreshProjectConfig(String domain)  {
        String url = String.format("http://%s:%s%s?op=%s&domain=%s", m_host, m_port, m_service_Uri,"refresh_project", domain);
        invoke(url);
    }

    public void refreshExceptionConfig(String domain, String exceptionName, String exceptionLimit)  {
        String url = null;
        try {
            exceptionLimit = Objects.equals(exceptionLimit, null) ? null : URLEncoder.encode(exceptionLimit, "utf-8");
            url = String.format("http://%s:%s%s?op=%s&domain=%s&exceptionName=%s&exceptionLimit=%s", m_host, m_port, m_service_Uri, "refresh_exception",
                                domain, exceptionName,exceptionLimit);
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        }
        invoke(url);
    }

    public void refreshExceptionExcludeConfig(String domain, String exceptionName, String methodType)  {
        String url = null;
        url = String.format("http://%s:%s%s?op=%s&domain=%s&exceptionName=%s&methodType=%s", m_host, m_port, m_service_Uri, "refresh_exclude_exception",
                domain, exceptionName, methodType);
        invoke(url);
    }


    public void invoke(String url) {
        Transaction transaction = newTransaction("RemoteFreshService", getClass().getSimpleName());

        URL connURL = null;
        try {
            connURL = new URL(url);
        } catch (MalformedURLException e) {
            Cat.logError(e);
        }

        for (int i = 0; i < RETRY_TIME; i++) {
            try {
                postRequestForRefresh(connURL);
                transaction.setStatus(Transaction.SUCCESS);
                break;
            } catch (Exception e) {
                Cat.logError("RemoteFreshService error", e);
                transaction.setStatus(e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                    Cat.logError(e);
                }
            }
            i++;
        }
    }

    public void postRequestForRefresh(URL connURL) throws IOException {
        StringBuffer buffer = new StringBuffer();
        java.net.HttpURLConnection httpConn = null;
        httpConn = (java.net.HttpURLConnection) connURL
                .openConnection();
        httpConn.setRequestProperty("Accept", "*/*");
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("User-Agent",
                                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
        httpConn.connect();
        InputStream inputStream = httpConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        httpConn.disconnect();
    }

    public String getM_host() {
        return m_host;
    }

    public void setM_host(String m_host) {
        this.m_host = m_host;
    }

    public Integer getM_port() {
        return m_port;
    }

    public void setM_port(Integer m_port) {
        this.m_port = m_port;
    }
}
