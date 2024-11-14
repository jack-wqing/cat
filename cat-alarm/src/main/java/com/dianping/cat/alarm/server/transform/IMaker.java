package com.dianping.cat.alarm.server.transform;

import com.dianping.cat.alarm.server.entity.Condition;
import com.dianping.cat.alarm.server.entity.Rule;
import com.dianping.cat.alarm.server.entity.ServerAlarmRuleConfig;
import com.dianping.cat.alarm.server.entity.SubCondition;

public interface IMaker<T> {

   public Condition buildCondition(T node);

   public Rule buildRule(T node);

   public ServerAlarmRuleConfig buildServerAlarmRuleConfig(T node);

   public SubCondition buildSubCondition(T node);
}
