package com.dianping.cat.alarm.rule.entity;

import com.dianping.cat.alarm.rule.BaseEntity;
import com.dianping.cat.alarm.rule.IVisitor;

import java.util.ArrayList;
import java.util.List;

public class Condition extends BaseEntity<Condition> {
   private Integer m_minute = 3;

   private List<SubCondition> m_subConditions = new ArrayList<SubCondition>();

   private String m_title;

   private String m_alertType = "error";

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

         if (!equals(getMinute(), _o.getMinute())) {
            return false;
         }

         if (!equals(getSubConditions(), _o.getSubConditions())) {
            return false;
         }

         if (!equals(getTitle(), _o.getTitle())) {
            return false;
         }

         if (!equals(getAlertType(), _o.getAlertType())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public String getAlertType() {
      return m_alertType;
   }

   public Integer getMinute() {
      return m_minute;
   }

   public List<SubCondition> getSubConditions() {
      return m_subConditions;
   }

   public String getTitle() {
      return m_title;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_minute == null ? 0 : m_minute.hashCode());
      for (SubCondition e : m_subConditions) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }

      hash = hash * 31 + (m_title == null ? 0 : m_title.hashCode());
      hash = hash * 31 + (m_alertType == null ? 0 : m_alertType.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(Condition other) {
      if (other.getMinute() != null) {
         m_minute = other.getMinute();
      }

      if (other.getTitle() != null) {
         m_title = other.getTitle();
      }

      if (other.getAlertType() != null) {
         m_alertType = other.getAlertType();
      }
   }

   public Condition setAlertType(String alertType) {
      m_alertType = alertType;
      return this;
   }

   public Condition setMinute(Integer minute) {
      m_minute = minute;
      return this;
   }

   public Condition setTitle(String title) {
      m_title = title;
      return this;
   }

}
