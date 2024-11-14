package com.dianping.cat.alarm.server.transform;

import com.dianping.cat.alarm.server.entity.Condition;
import com.dianping.cat.alarm.server.entity.Rule;
import com.dianping.cat.alarm.server.entity.ServerAlarmRuleConfig;
import com.dianping.cat.alarm.server.entity.SubCondition;

public interface ILinker {

   public boolean onCondition(Rule parent, Condition condition);

   public boolean onRule(ServerAlarmRuleConfig parent, Rule rule);

   public boolean onSubCondition(Condition parent, SubCondition subCondition);
}
