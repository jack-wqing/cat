package com.dianping.cat.alarm.sender.entity;

import com.dianping.cat.alarm.sender.BaseEntity;
import com.dianping.cat.alarm.sender.IVisitor;

import java.util.LinkedHashMap;
import java.util.Map;

public class SenderConfig extends BaseEntity<SenderConfig> {
   private Map<String, Sender> m_senders = new LinkedHashMap<String, Sender>();

   public SenderConfig() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitSenderConfig(this);
   }

   public SenderConfig addSender(Sender sender) {
      m_senders.put(sender.getId(), sender);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof SenderConfig) {
         SenderConfig _o = (SenderConfig) obj;

         if (!equals(getSenders(), _o.getSenders())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public Sender findSender(String id) {
      return m_senders.get(id);
   }

   public Map<String, Sender> getSenders() {
      return m_senders;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_senders == null ? 0 : m_senders.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(SenderConfig other) {
   }

   public Sender removeSender(String id) {
      return m_senders.remove(id);
   }

}
