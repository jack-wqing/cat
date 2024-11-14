package com.dianping.cat.alarm.receiver.entity;

import com.dianping.cat.alarm.receiver.BaseEntity;
import com.dianping.cat.alarm.receiver.IVisitor;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlertConfig extends BaseEntity<AlertConfig> {
   private Map<String, Receiver> m_receivers = new LinkedHashMap<String, Receiver>();

   private Boolean m_enable;

   public AlertConfig() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitAlertConfig(this);
   }

   public AlertConfig addReceiver(Receiver receiver) {
      m_receivers.put(receiver.getId(), receiver);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AlertConfig) {
         AlertConfig _o = (AlertConfig) obj;

         if (!equals(getReceivers(), _o.getReceivers())) {
            return false;
         }

         if (!equals(getEnable(), _o.getEnable())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public Receiver findReceiver(String id) {
      return m_receivers.get(id);
   }

   public Receiver findOrCreateReceiver(String id) {
      Receiver receiver = m_receivers.get(id);

      if (receiver == null) {
         synchronized (m_receivers) {
            receiver = m_receivers.get(id);

            if (receiver == null) {
               receiver = new Receiver(id);
               m_receivers.put(id, receiver);
            }
         }
      }

      return receiver;
   }

   public Boolean getEnable() {
      return m_enable;
   }

   public Map<String, Receiver> getReceivers() {
      return m_receivers;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_receivers == null ? 0 : m_receivers.hashCode());
      hash = hash * 31 + (m_enable == null ? 0 : m_enable.hashCode());

      return hash;
   }

   public boolean isEnable() {
      return m_enable != null && m_enable.booleanValue();
   }

   @Override
   public void mergeAttributes(AlertConfig other) {
      if (other.getEnable() != null) {
         m_enable = other.getEnable();
      }
   }

   public Receiver removeReceiver(String id) {
      return m_receivers.remove(id);
   }

   public AlertConfig setEnable(Boolean enable) {
      m_enable = enable;
      return this;
   }

}
