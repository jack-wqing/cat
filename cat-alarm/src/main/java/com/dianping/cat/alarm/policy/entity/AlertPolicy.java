package com.dianping.cat.alarm.policy.entity;

import com.dianping.cat.alarm.policy.BaseEntity;
import com.dianping.cat.alarm.policy.IVisitor;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlertPolicy extends BaseEntity<AlertPolicy> {
   private Map<String, Type> m_types = new LinkedHashMap<String, Type>();

   public AlertPolicy() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitAlertPolicy(this);
   }

   public AlertPolicy addType(Type type) {
      m_types.put(type.getId(), type);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AlertPolicy) {
         AlertPolicy _o = (AlertPolicy) obj;

         if (!equals(getTypes(), _o.getTypes())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public Type findType(String id) {
      return m_types.get(id);
   }

   public Type findOrCreateType(String id) {
      Type type = m_types.get(id);

      if (type == null) {
         synchronized (m_types) {
            type = m_types.get(id);

            if (type == null) {
               type = new Type(id);
               m_types.put(id, type);
            }
         }
      }

      return type;
   }

   public Map<String, Type> getTypes() {
      return m_types;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_types == null ? 0 : m_types.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(AlertPolicy other) {
   }

   public Type removeType(String id) {
      return m_types.remove(id);
   }

}
