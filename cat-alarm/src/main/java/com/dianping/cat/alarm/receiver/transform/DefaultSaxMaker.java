package com.dianping.cat.alarm.receiver.transform;

import com.dianping.cat.alarm.receiver.entity.AlertConfig;
import com.dianping.cat.alarm.receiver.entity.Receiver;
import org.xml.sax.Attributes;

import static com.dianping.cat.alarm.receiver.Constants.ATTR_ENABLE;
import static com.dianping.cat.alarm.receiver.Constants.ATTR_ID;

public class DefaultSaxMaker implements IMaker<Attributes> {

   @Override
   public AlertConfig buildAlertConfig(Attributes attributes) {
      String enable = attributes.getValue(ATTR_ENABLE);
      AlertConfig alertConfig = new AlertConfig();

      if (enable != null) {
         alertConfig.setEnable(convert(Boolean.class, enable, null));
      }

      return alertConfig;
   }

   @Override
   public String buildDx(Attributes attributes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String buildEmail(Attributes attributes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String buildPhone(Attributes attributes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Receiver buildReceiver(Attributes attributes) {
      String id = attributes.getValue(ATTR_ID);
      String enable = attributes.getValue(ATTR_ENABLE);
      Receiver receiver = new Receiver(id);

      if (enable != null) {
         receiver.setEnable(convert(Boolean.class, enable, null));
      }

      return receiver;
   }

   @Override
   public String buildWeixin(Attributes attributes) {
      throw new UnsupportedOperationException();
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
