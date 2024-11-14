package com.dianping.cat.alarm.server.entity;

import com.dianping.cat.alarm.server.BaseEntity;
import com.dianping.cat.alarm.server.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class ServerAlarmRuleConfig extends BaseEntity<ServerAlarmRuleConfig> {
   private String m_id;

   private List<Rule> m_rules = new ArrayList<Rule>();

   public ServerAlarmRuleConfig() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitServerAlarmRuleConfig(this);
   }

   public ServerAlarmRuleConfig addRule(Rule rule) {
      m_rules.add(rule);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ServerAlarmRuleConfig) {
         ServerAlarmRuleConfig _o = (ServerAlarmRuleConfig) obj;

         if (!equals(getId(), _o.getId())) {
            return false;
         }

         if (!equals(getRules(), _o.getRules())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public String getId() {
      return m_id;
   }

   public List<Rule> getRules() {
      return m_rules;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_id == null ? 0 : m_id.hashCode());
      for (Rule e : m_rules) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(ServerAlarmRuleConfig other) {
      if (other.getId() != null) {
         m_id = other.getId();
      }
   }

   public ServerAlarmRuleConfig setId(String id) {
      m_id = id;
      return this;
   }

}
