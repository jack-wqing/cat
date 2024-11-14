package com.dianping.cat.alarm.rule.transform;

import com.dianping.cat.alarm.rule.entity.Condition;
import com.dianping.cat.alarm.rule.entity.Config;
import com.dianping.cat.alarm.rule.entity.MetricItem;
import com.dianping.cat.alarm.rule.entity.MonitorRules;
import com.dianping.cat.alarm.rule.entity.Rule;
import com.dianping.cat.alarm.rule.entity.SubCondition;

public interface IMaker<T> {

   public Condition buildCondition(T node);

   public Config buildConfig(T node);

   public MetricItem buildMetricItem(T node);

   public MonitorRules buildMonitorRules(T node);

   public Rule buildRule(T node);

   public SubCondition buildSubCondition(T node);
}
