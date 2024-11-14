package com.dianping.cat.alarm.server.entity;

import com.dianping.cat.alarm.server.BaseEntity;
import com.dianping.cat.alarm.server.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class Condition extends BaseEntity<Condition> {
   private String m_interval;

   private int m_duration;

   private String m_alertType;

   private List<SubCondition> m_subConditions = new ArrayList<SubCondition>();

   public Condition() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitCondition(this);
   }

   public Condition addSubCondition(SubCondition subCondition) {
      m_subConditions.add(subCondition);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Condition) {
         Condition _o = (Condition) obj;

         if (!equals(getInterval(), _o.getInterval())) {
            return false;
         }

         if (getDuration() != _o.getDuration()) {
            return false;
         }

         if (!equals(getAlertType(), _o.getAlertType())) {
            return false;
         }

         if (!equals(getSubConditions(), _o.getSubConditions())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public String getAlertType() {
      return m_alertType;
   }

   public int getDuration() {
      return m_duration;
   }

   public String getInterval() {
      return m_interval;
   }

   public List<SubCondition> getSubConditions() {
      return m_subConditions;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_interval == null ? 0 : m_interval.hashCode());
      hash = hash * 31 + m_duration;
      hash = hash * 31 + (m_alertType == null ? 0 : m_alertType.hashCode());
      for (SubCondition e : m_subConditions) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(Condition other) {
      if (other.getInterval() != null) {
         m_interval = other.getInterval();
      }

      m_duration = other.getDuration();

      if (other.getAlertType() != null) {
         m_alertType = other.getAlertType();
      }
   }

   public Condition setAlertType(String alertType) {
      m_alertType = alertType;
      return this;
   }

   public Condition setDuration(int duration) {
      m_duration = duration;
      return this;
   }

   public Condition setInterval(String interval) {
      m_interval = interval;
      return this;
   }

}
