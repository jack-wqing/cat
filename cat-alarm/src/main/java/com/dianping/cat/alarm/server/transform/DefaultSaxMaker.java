package com.dianping.cat.alarm.server.transform;

import com.dianping.cat.alarm.server.entity.Condition;
import com.dianping.cat.alarm.server.entity.Rule;
import com.dianping.cat.alarm.server.entity.ServerAlarmRuleConfig;
import com.dianping.cat.alarm.server.entity.SubCondition;
import org.xml.sax.Attributes;

import static com.dianping.cat.alarm.server.Constants.ATTR_ALERT_TYPE;
import static com.dianping.cat.alarm.server.Constants.ATTR_DURATION;
import static com.dianping.cat.alarm.server.Constants.ATTR_END_TIME;
import static com.dianping.cat.alarm.server.Constants.ATTR_ID;
import static com.dianping.cat.alarm.server.Constants.ATTR_INTERVAL;
import static com.dianping.cat.alarm.server.Constants.ATTR_START_TIME;
import static com.dianping.cat.alarm.server.Constants.ATTR_TYPE;
import static com.dianping.cat.alarm.server.Constants.ATTR_VALUE;

public class DefaultSaxMaker implements IMaker<Attributes> {

   @Override
   public Condition buildCondition(Attributes attributes) {
      String interval = attributes.getValue(ATTR_INTERVAL);
      String duration = attributes.getValue(ATTR_DURATION);
      String alertType = attributes.getValue(ATTR_ALERT_TYPE);
      Condition condition = new Condition();

      if (interval != null) {
         condition.setInterval(interval);
      }

      if (duration != null) {
         condition.setDuration(convert(Integer.class, duration, 0));
      }

      if (alertType != null) {
         condition.setAlertType(alertType);
      }

      return condition;
   }

   @Override
   public Rule buildRule(Attributes attributes) {
      String startTime = attributes.getValue(ATTR_START_TIME);
      String endTime = attributes.getValue(ATTR_END_TIME);
      Rule rule = new Rule();

      if (startTime != null) {
         rule.setStartTime(startTime);
      }

      if (endTime != null) {
         rule.setEndTime(endTime);
      }

      return rule;
   }

   @Override
   public ServerAlarmRuleConfig buildServerAlarmRuleConfig(Attributes attributes) {
      String id = attributes.getValue(ATTR_ID);
      ServerAlarmRuleConfig serverAlarmRuleConfig = new ServerAlarmRuleConfig();

      if (id != null) {
         serverAlarmRuleConfig.setId(id);
      }

      return serverAlarmRuleConfig;
   }

   @Override
   public SubCondition buildSubCondition(Attributes attributes) {
      String type = attributes.getValue(ATTR_TYPE);
      String value = attributes.getValue(ATTR_VALUE);
      SubCondition subCondition = new SubCondition();

      if (type != null) {
         subCondition.setType(type);
      }

      if (value != null) {
         subCondition.setValue(value);
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
