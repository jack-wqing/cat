package com.dianping.cat.alarm.policy;

import com.dianping.cat.alarm.policy.entity.AlertPolicy;
import com.dianping.cat.alarm.policy.entity.Group;
import com.dianping.cat.alarm.policy.entity.Level;
import com.dianping.cat.alarm.policy.entity.Type;

public interface IVisitor {

   public void visitAlertPolicy(AlertPolicy alertPolicy);

   public void visitGroup(Group group);

   public void visitLevel(Level level);

   public void visitType(Type type);
}
