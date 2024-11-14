package com.dianping.cat.alarm.server;

import com.dianping.cat.alarm.server.entity.Condition;
import com.dianping.cat.alarm.server.entity.Rule;
import com.dianping.cat.alarm.server.entity.ServerAlarmRuleConfig;
import com.dianping.cat.alarm.server.entity.SubCondition;

public interface IVisitor {

   public void visitCondition(Condition condition);

   public void visitRule(Rule rule);

   public void visitServerAlarmRuleConfig(ServerAlarmRuleConfig serverAlarmRuleConfig);

   public void visitSubCondition(SubCondition subCondition);
}
