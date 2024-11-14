package com.dianping.cat.alarm.rule.transform;

import com.dianping.cat.alarm.rule.entity.Condition;
import com.dianping.cat.alarm.rule.entity.Config;
import com.dianping.cat.alarm.rule.entity.MetricItem;
import com.dianping.cat.alarm.rule.entity.MonitorRules;
import com.dianping.cat.alarm.rule.entity.Rule;
import com.dianping.cat.alarm.rule.entity.SubCondition;
import org.xml.sax.Attributes;

import java.util.Map;

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

public class DefaultSaxMaker implements IMaker<Attributes> {

   @Override
   public Condition buildCondition(Attributes attributes) {
      String minute = attributes.getValue(ATTR_MINUTE);
      String title = attributes.getValue(ATTR_TITLE);
      String alertType = attributes.getValue(ATTR_ALERTTYPE);
      Condition condition = new Condition();

      if (minute != null) {
         condition.setMinute(convert(Integer.class, minute, null));
      }

      if (title != null) {
         condition.setTitle(title);
      }

      if (alertType != null) {
         condition.setAlertType(alertType);
      }

      return condition;
   }

   @Override
   public Config buildConfig(Attributes attributes) {
      String starttime = attributes.getValue(ATTR_STARTTIME);
      String endtime = attributes.getValue(ATTR_ENDTIME);
      Config config = new Config();

      if (starttime != null) {
         config.setStarttime(starttime);
      }

      if (endtime != null) {
         config.setEndtime(endtime);
      }

      return config;
   }

   @Override
   public MetricItem buildMetricItem(Attributes attributes) {
      String monitorCount = attributes.getValue(ATTR_MONITORCOUNT);
      String monitorSum = attributes.getValue(ATTR_MONITORSUM);
      String monitorAvg = attributes.getValue(ATTR_MONITORAVG);
      String metricItemText = attributes.getValue(ATTR_METRICITEMTEXT);
      String productText = attributes.getValue(ATTR_PRODUCTTEXT);
      MetricItem metricItem = new MetricItem();

      if (monitorCount != null) {
         metricItem.setMonitorCount(convert(Boolean.class, monitorCount, null));
      }

      if (monitorSum != null) {
         metricItem.setMonitorSum(convert(Boolean.class, monitorSum, null));
      }

      if (monitorAvg != null) {
         metricItem.setMonitorAvg(convert(Boolean.class, monitorAvg, null));
      }

      if (metricItemText != null) {
         metricItem.setMetricItemText(metricItemText);
      }

      if (productText != null) {
         metricItem.setProductText(productText);
      }

      return metricItem;
   }

   @Override
   public MonitorRules buildMonitorRules(Attributes attributes) {
      MonitorRules monitorRules = new MonitorRules();

      return monitorRules;
   }

   @Override
   public Rule buildRule(Attributes attributes) {
      String id = attributes.getValue(ATTR_ID);
      String available = attributes.getValue(ATTR_AVAILABLE);
      Rule rule = new Rule(id);

      if (available != null) {
         rule.setAvailable(convert(Boolean.class, available, null));
      }

      Map<String, String> dynamicAttributes = rule.getDynamicAttributes();
      int _length = attributes == null ? 0 : attributes.getLength();

      for (int i = 0; i < _length; i++) {
         String _name = attributes.getQName(i);
         String _value = attributes.getValue(i);

         dynamicAttributes.put(_name, _value);
      }

      dynamicAttributes.remove(ATTR_ID);
      dynamicAttributes.remove(ATTR_AVAILABLE);

      return rule;
   }

   @Override
   public SubCondition buildSubCondition(Attributes attributes) {
      String type = attributes.getValue(ATTR_TYPE);
      String text = attributes.getValue(ATTR_TEXT);
      SubCondition subCondition = new SubCondition();

      if (type != null) {
         subCondition.setType(type);
      }

      if (text != null) {
         subCondition.setText(text);
      }

      return subCondition;
   }

   @SuppressWarnings("unchecked")
   protected <T> T convert(Class<T> type, String value, T defaultValue) {
      if (value == null || value.length() == 0) {
         return defaultValue;
      }

      if (type == Boolean.class || type == Boolean.TYPE) {
         return (T) Boolean.valueOf(value);
      } else if (type == Integer.class || type == Integer.TYPE) {
         return (T) Integer.valueOf(value);
      } else if (type == Long.class || type == Long.TYPE) {
         return (T) Long.valueOf(value);
      } else if (type == Short.class || type == Short.TYPE) {
         return (T) Short.valueOf(value);
      } else if (type == Float.class || type == Float.TYPE) {
         return (T) Float.valueOf(value);
      } else if (type == Double.class || type == Double.TYPE) {
         return (T) Double.valueOf(value);
      } else if (type == Byte.class || type == Byte.TYPE) {
         return (T) Byte.valueOf(value);
      } else if (type == Character.class || type == Character.TYPE) {
         return (T) (Character) value.charAt(0);
      } else {
         return (T) value;
      }
   }
}
