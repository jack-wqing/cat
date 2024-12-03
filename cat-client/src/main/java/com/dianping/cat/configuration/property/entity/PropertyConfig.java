package com.dianping.cat.configuration.property.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import com.dianping.cat.configuration.property.BaseEntity;
import com.dianping.cat.configuration.property.IVisitor;
// key -> property(key, value)
public class PropertyConfig extends BaseEntity<PropertyConfig> {
   private Map<String, Property> m_properties = new LinkedHashMap<String, Property>();

   public PropertyConfig() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitPropertyConfig(this);
   }

   public PropertyConfig addProperty(Property property) {
      m_properties.put(property.getId(), property);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof PropertyConfig) {
         PropertyConfig _o = (PropertyConfig) obj;

         if (!equals(getProperties(), _o.getProperties())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public Property findProperty(String id) {
      return m_properties.get(id);
   }

   public Map<String, Property> getProperties() {
      return m_properties;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_properties == null ? 0 : m_properties.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(PropertyConfig other) {
   }

   public Property removeProperty(String id) {
      return m_properties.remove(id);
   }

}
