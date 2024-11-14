package com.dianping.cat.alarm.rule.entity;

import com.dianping.cat.alarm.rule.BaseEntity;
import com.dianping.cat.alarm.rule.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class Config extends BaseEntity<Config> {
   private String m_starttime;

   private String m_endtime;

   private List<Condition> m_conditions = new ArrayList<Condition>();

   public Config() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitConfig(this);
   }

   public Config addCondition(Condition condition) {
      m_conditions.add(condition);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Config) {
         Config _o = (Config) obj;

         if (!equals(getStarttime(), _o.getStarttime())) {
            return false;
         }

         if (!equals(getEndtime(), _o.getEndtime())) {
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

   public String getEndtime() {
      return m_endtime;
   }

   public String getStarttime() {
      return m_starttime;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_starttime == null ? 0 : m_starttime.hashCode());
      hash = hash * 31 + (m_endtime == null ? 0 : m_endtime.hashCode());
      for (Condition e : m_conditions) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(Config other) {
      if (other.getStarttime() != null) {
         m_starttime = other.getStarttime();
      }

      if (other.getEndtime() != null) {
         m_endtime = other.getEndtime();
      }
   }

   public Config setEndtime(String endtime) {
      m_endtime = endtime;
      return this;
   }

   public Config setStarttime(String starttime) {
      m_starttime = starttime;
      return this;
   }

}
