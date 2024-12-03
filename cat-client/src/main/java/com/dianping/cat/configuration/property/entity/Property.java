package com.dianping.cat.configuration.property.entity;

import static com.dianping.cat.configuration.property.Constants.ATTR_ID;
import static com.dianping.cat.configuration.property.Constants.ENTITY_PROPERTY;

import com.dianping.cat.configuration.property.BaseEntity;
import com.dianping.cat.configuration.property.IVisitor;
// key value
public class Property extends BaseEntity<Property> {
   private String m_id;

   private String m_value;

   public Property() {
   }

   public Property(String id) {
      m_id = id;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitProperty(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Property) {
         Property _o = (Property) obj;

         if (!equals(getId(), _o.getId())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public String getId() {
      return m_id;
   }

   public String getValue() {
      return m_value;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_id == null ? 0 : m_id.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(Property other) {
      assertAttributeEquals(other, ENTITY_PROPERTY, ATTR_ID, m_id, other.getId());

      if (other.getValue() != null) {
         m_value = other.getValue();
      }
   }

   public Property setId(String id) {
      m_id = id;
      return this;
   }

   public Property setValue(String value) {
      m_value = value;
      return this;
   }

}
