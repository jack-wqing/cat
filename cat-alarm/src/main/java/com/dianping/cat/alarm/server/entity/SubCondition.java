package com.dianping.cat.alarm.server.entity;

import com.dianping.cat.alarm.server.BaseEntity;
import com.dianping.cat.alarm.server.IVisitor;

public class SubCondition extends BaseEntity<SubCondition> {
   private String m_type;

   private String m_value;

   public SubCondition() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitSubCondition(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof SubCondition) {
         SubCondition _o = (SubCondition) obj;

         if (!equals(getType(), _o.getType())) {
            return false;
         }

         if (!equals(getValue(), _o.getValue())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public String getType() {
      return m_type;
   }

   public String getValue() {
      return m_value;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_type == null ? 0 : m_type.hashCode());
      hash = hash * 31 + (m_value == null ? 0 : m_value.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(SubCondition other) {
      if (other.getType() != null) {
         m_type = other.getType();
      }

      if (other.getValue() != null) {
         m_value = other.getValue();
      }
   }

   public SubCondition setType(String type) {
      m_type = type;
      return this;
   }

   public SubCondition setValue(String value) {
      m_value = value;
      return this;
   }

}
