package com.dianping.cat.alarm.server.entity;

import com.dianping.cat.alarm.server.BaseEntity;
import com.dianping.cat.alarm.server.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class Rule extends BaseEntity<Rule> {
   private String m_startTime;

   private String m_endTime;

   private List<Condition> m_conditions = new ArrayList<Condition>();

   public Rule() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitRule(this);
   }

   public Rule addCondition(Condition condition) {
      m_conditions.add(condition);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Rule) {
         Rule _o = (Rule) obj;

         if (!equals(getStartTime(), _o.getStartTime())) {
            return false;
         }

         if (!equals(getEndTime(), _o.getEndTime())) {
            return false;
         }

         if (!equals(getConditions(), _o.getConditions())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public List<Condition> getConditions() {
      return m_conditions;
   }

   public String getEndTime() {
      return m_endTime;
   }

   public String getStartTime() {
      return m_startTime;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_startTime == null ? 0 : m_startTime.hashCode());
      hash = hash * 31 + (m_endTime == null ? 0 : m_endTime.hashCode());
      for (Condition e : m_conditions) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(Rule other) {
      if (other.getStartTime() != null) {
         m_startTime = other.getStartTime();
      }

      if (other.getEndTime() != null) {
         m_endTime = other.getEndTime();
      }
   }

   public Rule setEndTime(String endTime) {
      m_endTime = endTime;
      return this;
   }

   public Rule setStartTime(String startTime) {
      m_startTime = startTime;
      return this;
   }

}
