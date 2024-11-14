package com.dianping.cat.alarm.policy.transform;

import com.dianping.cat.alarm.policy.entity.AlertPolicy;
import com.dianping.cat.alarm.policy.entity.Group;
import com.dianping.cat.alarm.policy.entity.Level;
import com.dianping.cat.alarm.policy.entity.Type;

public interface IMaker<T> {

   public AlertPolicy buildAlertPolicy(T node);

   public Group buildGroup(T node);

   public Level buildLevel(T node);

   public Type buildType(T node);
}
