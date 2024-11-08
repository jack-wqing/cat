package com.dianping.cat.status.model.entity;

import static com.dianping.cat.status.model.Constants.ATTR_KEY;
import static com.dianping.cat.status.model.Constants.ENTITY_CUSTOMINFO;

import com.dianping.cat.status.model.BaseEntity;
import com.dianping.cat.status.model.IVisitor;

public class CustomInfo extends BaseEntity<CustomInfo> {
   private String m_key;

   private String m_value;

   public CustomInfo() {
   }

   public CustomInfo(String key) {
      m_key = key;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitCustomInfo(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CustomInfo) {
         CustomInfo _o = (CustomInfo) obj;

         if (!equals(getKey(), _o.getKey())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public String getKey() {
      return m_key;
   }

   public String getValue() {
      return m_value;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_key == null ? 0 : m_key.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(CustomInfo other) {
      assertAttributeEquals(other, ENTITY_CUSTOMINFO, ATTR_KEY, m_key, other.getKey());

      if (other.getValue() != null) {
         m_value = other.getValue();
      }
   }

   public CustomInfo setKey(String key) {
      m_key = key;
      return this;
   }

   public CustomInfo setValue(String value) {
      m_value = value;
      return this;
   }

}
