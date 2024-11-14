package com.dianping.cat.alarm.policy.transform;

import com.dianping.cat.alarm.policy.entity.AlertPolicy;
import com.dianping.cat.alarm.policy.entity.Group;
import com.dianping.cat.alarm.policy.entity.Level;
import com.dianping.cat.alarm.policy.entity.Type;
import org.xml.sax.Attributes;

import static com.dianping.cat.alarm.policy.Constants.ATTR_ID;
import static com.dianping.cat.alarm.policy.Constants.ATTR_RECOVERMINUTE;
import static com.dianping.cat.alarm.policy.Constants.ATTR_SEND;
import static com.dianping.cat.alarm.policy.Constants.ATTR_SUSPENDMINUTE;

public class DefaultSaxMaker implements IMaker<Attributes> {

   @Override
   public AlertPolicy buildAlertPolicy(Attributes attributes) {
      AlertPolicy alertPolicy = new AlertPolicy();

      return alertPolicy;
   }

   @Override
   public Group buildGroup(Attributes attributes) {
      String id = attributes.getValue(ATTR_ID);
      Group group = new Group(id);

      return group;
   }

   @Override
   public Level buildLevel(Attributes attributes) {
      String id = attributes.getValue(ATTR_ID);
      String send = attributes.getValue(ATTR_SEND);
      String suspendMinute = attributes.getValue(ATTR_SUSPENDMINUTE);
      String recoverMinute = attributes.getValue(ATTR_RECOVERMINUTE);
      Level level = new Level(id);

      if (send != null) {
         level.setSend(send);
      }

      if (suspendMinute != null) {
         level.setSuspendMinute(convert(Integer.class, suspendMinute, null));
      }

      if (recoverMinute != null) {
         level.setRecoverMinute(convert(Integer.class, recoverMinute, null));
      }

      return level;
   }

   @Override
   public Type buildType(Attributes attributes) {
      String id = attributes.getValue(ATTR_ID);
      Type type = new Type(id);

      return type;
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
