package com.dianping.cat.report.page.transaction.transform;


import com.dianping.cat.consumer.transaction.model.entity.TransactionReport;

public class QueryMinuteTransactionParam {

    private TransactionReport report;
    private String  domain;
    private String type;
    private String name;
    private String ip;
    private Integer startMinute;
    private Integer endMinute;

    public TransactionReport getReport() {
        return report;
    }

    public void setReport(TransactionReport report) {
        this.report = report;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public Integer getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(Integer endMinute) {
        this.endMinute = endMinute;
    }
}
