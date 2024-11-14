package com.dianping.cat.alarm.rule.transform;

import com.dianping.cat.alarm.rule.entity.Condition;
import com.dianping.cat.alarm.rule.entity.Config;
import com.dianping.cat.alarm.rule.entity.MetricItem;
import com.dianping.cat.alarm.rule.entity.MonitorRules;
import com.dianping.cat.alarm.rule.entity.Rule;
import com.dianping.cat.alarm.rule.entity.SubCondition;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

public class DefaultDomMaker implements IMaker<Node> {

   @Override
   public Condition buildCondition(Node node) {
      String minute = getAttribute(node, ATTR_MINUTE);
      String title = getAttribute(node, ATTR_TITLE);
      String alertType = getAttribute(node, ATTR_ALERTTYPE);

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
   public Config buildConfig(Node node) {
      String starttime = getAttribute(node, ATTR_STARTTIME);
      String endtime = getAttribute(node, ATTR_ENDTIME);

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
   public MetricItem buildMetricItem(Node node) {
      String monitorCount = getAttribute(node, ATTR_MONITORCOUNT);
      String monitorSum = getAttribute(node, ATTR_MONITORSUM);
      String monitorAvg = getAttribute(node, ATTR_MONITORAVG);
      String metricItemText = getAttribute(node, ATTR_METRICITEMTEXT);
      String productText = getAttribute(node, ATTR_PRODUCTTEXT);

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
   public MonitorRules buildMonitorRules(Node node) {
      MonitorRules monitorRules = new MonitorRules();

      return monitorRules;
   }

   @Override
   public Rule buildRule(Node node) {
      String id = getAttribute(node, ATTR_ID);
      String available = getAttribute(node, ATTR_AVAILABLE);

      Rule rule = new Rule(id);

      if (available != null) {
         rule.setAvailable(convert(Boolean.class, available, null));
      }

      Map<String, String> dynamicAttributes = rule.getDynamicAttributes();
      NamedNodeMap attributes = node.getAttributes();
      int length = attributes == null ? 0 : attributes.getLength();

      for (int i = 0; i < length; i++) {
         Node item = attributes.item(i);

         dynamicAttributes.put(item.getNodeName(), item.getNodeValue());
      }

      dynamicAttributes.remove(ATTR_ID);
      dynamicAttributes.remove(ATTR_AVAILABLE);

      return rule;
   }

   @Override
   public SubCondition buildSubCondition(Node node) {
      String type = getAttribute(node, ATTR_TYPE);
      String text = getAttribute(node, ATTR_TEXT);

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

   protected String getAttribute(Node node, String name) {
      Node attribute = node.getAttributes().getNamedItem(name);

      return attribute == null ? null : attribute.getNodeValue();
   }

   protected Node getChildTagNode(Node parent, String name) {
      NodeList children = parent.getChildNodes();
      int len = children.getLength();

      for (int i = 0; i < len; i++) {
         Node child = children.item(i);

         if (child.getNodeType() == Node.ELEMENT_NODE) {
            if (child.getNodeName().equals(name)) {
               return child;
            }
         }
      }

      return null;
   }
}
