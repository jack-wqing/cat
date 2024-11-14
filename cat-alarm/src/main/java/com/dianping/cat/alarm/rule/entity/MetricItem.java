package com.dianping.cat.alarm.rule.entity;

import com.dianping.cat.alarm.rule.BaseEntity;
import com.dianping.cat.alarm.rule.IVisitor;

public class MetricItem extends BaseEntity<MetricItem> {
   private Boolean m_monitorCount;

   private Boolean m_monitorSum;

   private Boolean m_monitorAvg;

   private String m_metricItemText;

   private String m_productText;

   public MetricItem() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitMetricItem(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof MetricItem) {
         MetricItem _o = (MetricItem) obj;

         if (!equals(getMonitorCount(), _o.getMonitorCount())) {
            return false;
         }

         if (!equals(getMonitorSum(), _o.getMonitorSum())) {
            return false;
         }

         if (!equals(getMonitorAvg(), _o.getMonitorAvg())) {
            return false;
         }

         if (!equals(getMetricItemText(), _o.getMetricItemText())) {
            return false;
         }

         if (!equals(getProductText(), _o.getProductText())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public String getMetricItemText() {
      return m_metricItemText;
   }

   public Boolean getMonitorAvg() {
      return m_monitorAvg;
   }

   public Boolean getMonitorCount() {
      return m_monitorCount;
   }

   public Boolean getMonitorSum() {
      return m_monitorSum;
   }

   public String getProductText() {
      return m_productText;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_monitorCount == null ? 0 : m_monitorCount.hashCode());
      hash = hash * 31 + (m_monitorSum == null ? 0 : m_monitorSum.hashCode());
      hash = hash * 31 + (m_monitorAvg == null ? 0 : m_monitorAvg.hashCode());
      hash = hash * 31 + (m_metricItemText == null ? 0 : m_metricItemText.hashCode());
      hash = hash * 31 + (m_productText == null ? 0 : m_productText.hashCode());

      return hash;
   }

   public boolean isMonitorAvg() {
      return m_monitorAvg != null && m_monitorAvg.booleanValue();
   }

   public boolean isMonitorCount() {
      return m_monitorCount != null && m_monitorCount.booleanValue();
   }

   public boolean isMonitorSum() {
      return m_monitorSum != null && m_monitorSum.booleanValue();
   }

   @Override
   public void mergeAttributes(MetricItem other) {
      if (other.getMonitorCount() != null) {
         m_monitorCount = other.getMonitorCount();
      }

      if (other.getMonitorSum() != null) {
         m_monitorSum = other.getMonitorSum();
      }

      if (other.getMonitorAvg() != null) {
         m_monitorAvg = other.getMonitorAvg();
      }

      if (other.getMetricItemText() != null) {
         m_metricItemText = other.getMetricItemText();
      }

      if (other.getProductText() != null) {
         m_productText = other.getProductText();
      }
   }

   public MetricItem setMetricItemText(String metricItemText) {
      m_metricItemText = metricItemText;
      return this;
   }

   public MetricItem setMonitorAvg(Boolean monitorAvg) {
      m_monitorAvg = monitorAvg;
      return this;
   }

   public MetricItem setMonitorCount(Boolean monitorCount) {
      m_monitorCount = monitorCount;
      return this;
   }

   public MetricItem setMonitorSum(Boolean monitorSum) {
      m_monitorSum = monitorSum;
      return this;
   }

   public MetricItem setProductText(String productText) {
      m_productText = productText;
      return this;
   }

}
