package com.dianping.cat.alarm.rule.transform;

import com.dianping.cat.alarm.rule.entity.Condition;
import com.dianping.cat.alarm.rule.entity.Config;
import com.dianping.cat.alarm.rule.entity.MetricItem;
import com.dianping.cat.alarm.rule.entity.MonitorRules;
import com.dianping.cat.alarm.rule.entity.Rule;
import com.dianping.cat.alarm.rule.entity.SubCondition;

public interface ILinker {

   public boolean onCondition(Config parent, Condition condition);

   public boolean onConfig(Rule parent, Config config);

   public boolean onMetricItem(Rule parent, MetricItem metricItem);

   public boolean onRule(MonitorRules parent, Rule rule);

   public boolean onSubCondition(Condition parent, SubCondition subCondition);
}
