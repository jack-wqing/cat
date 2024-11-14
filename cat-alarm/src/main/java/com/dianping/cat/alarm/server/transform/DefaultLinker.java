package com.dianping.cat.alarm.server.transform;

import com.dianping.cat.alarm.server.entity.Condition;
import com.dianping.cat.alarm.server.entity.Rule;
import com.dianping.cat.alarm.server.entity.ServerAlarmRuleConfig;
import com.dianping.cat.alarm.server.entity.SubCondition;

import java.util.ArrayList;
import java.util.List;

public class DefaultLinker implements ILinker {
   @SuppressWarnings("unused")
   private boolean m_deferrable;

   private List<Runnable> m_deferedJobs = new ArrayList<Runnable>();

   public DefaultLinker(boolean deferrable) {
      m_deferrable = deferrable;
   }

   public void finish() {
      for (Runnable job : m_deferedJobs) {
         job.run();
      }
   }

   @Override
   public boolean onCondition(final Rule parent, final Condition condition) {
      parent.addCondition(condition);
      return true;
   }

   @Override
   public boolean onRule(final ServerAlarmRuleConfig parent, final Rule rule) {
      parent.addRule(rule);
      return true;
   }

   @Override
   public boolean onSubCondition(final Condition parent, final SubCondition subCondition) {
      parent.addSubCondition(subCondition);
      return true;
   }
}
