package com.dianping.cat.alarm.rule.transform;

import com.dianping.cat.alarm.rule.IEntity;
import com.dianping.cat.alarm.rule.IVisitor;
import com.dianping.cat.alarm.rule.entity.Condition;
import com.dianping.cat.alarm.rule.entity.Config;
import com.dianping.cat.alarm.rule.entity.MetricItem;
import com.dianping.cat.alarm.rule.entity.MonitorRules;
import com.dianping.cat.alarm.rule.entity.Rule;
import com.dianping.cat.alarm.rule.entity.SubCondition;

import java.lang.reflect.Array;
import java.util.Collection;

import static com.dianping.cat.alarm.rule.Constants.ATTR_ALERTTYPE;
import static com.dianping.cat.alarm.rule.Constants.ATTR_AVAILABLE;
import static com.dianping.cat.alarm.rule.Constants.ATTR_ENDTIME;
import static com.dianping.cat.alarm.rule.Constants.ATTR_ID;
import static com.dianping.cat.alarm.rule.Constants.ATTR_METRICITEMTEXT;
import static com.dianping.cat.alarm.rule.Constants.ATTR_MINUTE;
import static com.dianping.cat.alarm.rule.Constants.ATTR_MONITORAVG;
import static com.dianping.cat.alarm.rule.Constants.ATTR_MONITORCOUNT;
import static com.dianping.cat.alarm.rule.Constants.ATTR_MONITORSUM;
import static com.dianping.cat.alarm.rule.Constants.ATTR_PRODUCTTEXT;
import static com.dianping.cat.alarm.rule.Constants.ATTR_STARTTIME;
import static com.dianping.cat.alarm.rule.Constants.ATTR_TEXT;
import static com.dianping.cat.alarm.rule.Constants.ATTR_TITLE;
import static com.dianping.cat.alarm.rule.Constants.ATTR_TYPE;
import static com.dianping.cat.alarm.rule.Constants.ENTITY_CONDITION;
import static com.dianping.cat.alarm.rule.Constants.ENTITY_CONFIG;
import static com.dianping.cat.alarm.rule.Constants.ENTITY_METRIC_ITEM;
import static com.dianping.cat.alarm.rule.Constants.ENTITY_MONITOR_RULES;
import static com.dianping.cat.alarm.rule.Constants.ENTITY_RULE;
import static com.dianping.cat.alarm.rule.Constants.ENTITY_SUB_CONDITION;

public class DefaultXmlBuilder implements IVisitor {

   private IVisitor m_visitor = this;

   private int m_level;

   private StringBuilder m_sb;

   private boolean m_compact;

   public DefaultXmlBuilder() {
      this(false);
   }

   public DefaultXmlBuilder(boolean compact) {
      this(compact, new StringBuilder(4096));
   }

   public DefaultXmlBuilder(boolean compact, StringBuilder sb) {
      m_compact = compact;
      m_sb = sb;
      m_sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
   }

   public String buildXml(IEntity<?> entity) {
      entity.accept(m_visitor);
      return m_sb.toString();
   }

   protected void endTag(String name) {
      m_level--;

      indent();
      m_sb.append("</").append(name).append(">\r\n");
   }

   protected String escape(Object value) {
      return escape(value, false);
   }
   
   protected String escape(Object value, boolean text) {
      if (value == null) {
         return null;
      }

      String str = toString(value);
      int len = str.length();
      StringBuilder sb = new StringBuilder(len + 16);

      for (int i = 0; i < len; i++) {
         final char ch = str.charAt(i);

         switch (ch) {
         case '<':
            sb.append("&lt;");
            break;
         case '>':
            sb.append("&gt;");
            break;
         case '&':
            sb.append("&amp;");
            break;
         case '"':
            if (!text) {
               sb.append("&quot;");
               break;
            }
         default:
            sb.append(ch);
            break;
         }
      }

      return sb.toString();
   }
   
   protected void indent() {
      if (!m_compact) {
         for (int i = m_level - 1; i >= 0; i--) {
            m_sb.append("   ");
         }
      }
   }

   protected void startTag(String name) {
      startTag(name, false, null);
   }
   
   protected void startTag(String name, boolean closed, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      startTag(name, null, closed, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      startTag(name, null, false, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, Object text, boolean closed, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      indent();

      m_sb.append('<').append(name);

      int len = nameValues.length;

      for (int i = 0; i + 1 < len; i += 2) {
         Object attrName = nameValues[i];
         Object attrValue = nameValues[i + 1];

         if (attrValue != null) {
            m_sb.append(' ').append(attrName).append("=\"").append(escape(attrValue)).append('"');
         }
      }

      if (dynamicAttributes != null) {
         for (java.util.Map.Entry<String, String> e : dynamicAttributes.entrySet()) {
            m_sb.append(' ').append(e.getKey()).append("=\"").append(escape(e.getValue())).append('"');
         }
      }

      if (text != null && closed) {
         m_sb.append('>');
         m_sb.append(escape(text, true));
         m_sb.append("</").append(name).append(">\r\n");
      } else {
         if (closed) {
            m_sb.append('/');
         } else {
            m_level++;
         }
   
         m_sb.append(">\r\n");
      }
   }

   @SuppressWarnings("unchecked")
   protected String toString(Object value) {
      if (value instanceof String) {
         return (String) value;
      } else if (value instanceof Collection) {
         Collection<Object> list = (Collection<Object>) value;
         StringBuilder sb = new StringBuilder(32);
         boolean first = true;

         for (Object item : list) {
            if (first) {
               first = false;
            } else {
               sb.append(',');
            }

            if (item != null) {
               sb.append(item);
            }
         }

         return sb.toString();
      } else if (value.getClass().isArray()) {
         int len = Array.getLength(value);
         StringBuilder sb = new StringBuilder(32);
         boolean first = true;

         for (int i = 0; i < len; i++) {
            Object item = Array.get(value, i);

            if (first) {
               first = false;
            } else {
               sb.append(',');
            }

            if (item != null) {
               sb.append(item);
            }
         }
		
         return sb.toString();
      }
 
      return String.valueOf(value);
   }

   @Override
   public void visitCondition(Condition condition) {
      startTag(ENTITY_CONDITION, null, ATTR_MINUTE, condition.getMinute(), ATTR_TITLE, condition.getTitle(), ATTR_ALERTTYPE, condition.getAlertType());

      if (!condition.getSubConditions().isEmpty()) {
         for (SubCondition subCondition : condition.getSubConditions()) {
            subCondition.accept(m_visitor);
         }
      }

      endTag(ENTITY_CONDITION);
   }

   @Override
   public void visitConfig(Config config) {
      startTag(ENTITY_CONFIG, null, ATTR_STARTTIME, config.getStarttime(), ATTR_ENDTIME, config.getEndtime());

      if (!config.getConditions().isEmpty()) {
         for (Condition condition : config.getConditions()) {
            condition.accept(m_visitor);
         }
      }

      endTag(ENTITY_CONFIG);
   }

   @Override
   public void visitMetricItem(MetricItem metricItem) {
      startTag(ENTITY_METRIC_ITEM, true, null, ATTR_MONITORCOUNT, metricItem.getMonitorCount(), ATTR_MONITORSUM, metricItem.getMonitorSum(), ATTR_MONITORAVG, metricItem.getMonitorAvg(), ATTR_METRICITEMTEXT, metricItem.getMetricItemText(), ATTR_PRODUCTTEXT, metricItem.getProductText());
   }

   @Override
   public void visitMonitorRules(MonitorRules monitorRules) {
      startTag(ENTITY_MONITOR_RULES, null);

      if (!monitorRules.getRules().isEmpty()) {
         for (Rule rule : monitorRules.getRules().values()) {
            rule.accept(m_visitor);
         }
      }

      endTag(ENTITY_MONITOR_RULES);
   }

   @Override
   public void visitRule(Rule rule) {
      startTag(ENTITY_RULE, rule.getDynamicAttributes(), ATTR_ID, rule.getId(), ATTR_AVAILABLE, rule.getAvailable());

      if (!rule.getMetricItems().isEmpty()) {
         for (MetricItem metricItem : rule.getMetricItems()) {
            metricItem.accept(m_visitor);
         }
      }

      if (!rule.getConfigs().isEmpty()) {
         for (Config config : rule.getConfigs()) {
            config.accept(m_visitor);
         }
      }

      endTag(ENTITY_RULE);
   }

   @Override
   public void visitSubCondition(SubCondition subCondition) {
      startTag(ENTITY_SUB_CONDITION, true, null, ATTR_TYPE, subCondition.getType(), ATTR_TEXT, subCondition.getText());
   }
}
