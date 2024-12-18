package com.dianping.cat.alarm.rule.entity;

import com.dianping.cat.alarm.rule.BaseEntity;
import com.dianping.cat.alarm.rule.IVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.dianping.cat.alarm.rule.Constants.ATTR_ID;
import static com.dianping.cat.alarm.rule.Constants.ENTITY_RULE;

public class Rule extends BaseEntity<Rule> {
   private String m_id;

   private Boolean m_available;
   // 指标项
   private List<MetricItem> m_metricItems = new ArrayList<MetricItem>();

   private List<Config> m_configs = new ArrayList<Config>();

   private Map<String, String> m_dynamicAttributes = new LinkedHashMap<String, String>();

   public Rule() {
   }

   public Rule(String id) {
      m_id = id;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitRule(this);
   }

   public Rule addConfig(Config config) {
      m_configs.add(config);
      return this;
   }

   public Rule addMetricItem(MetricItem metricItem) {
      m_metricItems.add(metricItem);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Rule) {
         Rule _o = (Rule) obj;

         if (!equals(getId(), _o.getId())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public String getDynamicAttribute(String name) {
      return m_dynamicAttributes.get(name);
   }

   public Map<String, String> getDynamicAttributes() {
      return m_dynamicAttributes;
   }

   public Boolean getAvailable() {
      return m_available;
   }

   public List<Config> getConfigs() {
      return m_configs;
   }

   public String getId() {
      return m_id;
   }

   public List<MetricItem> getMetricItems() {
      return m_metricItems;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_id == null ? 0 : m_id.hashCode());

      return hash;
   }

   public boolean isAvailable() {
      return m_available != null && m_available.booleanValue();
   }

   @Override
   public void mergeAttributes(Rule other) {
      assertAttributeEquals(other, ENTITY_RULE, ATTR_ID, m_id, other.getId());

      for (Map.Entry<String, String> e : other.getDynamicAttributes().entrySet()) {
         m_dynamicAttributes.put(e.getKey(), e.getValue());
      }

      if (other.getAvailable() != null) {
         m_available = other.getAvailable();
      }
   }

   public void setDynamicAttribute(String name, String value) {
      m_dynamicAttributes.put(name, value);
   }

   public Rule setAvailable(Boolean available) {
      m_available = available;
      return this;
   }

   public Rule setId(String id) {
      m_id = id;
      return this;
   }

}
