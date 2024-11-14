package com.dianping.cat.alarm.sender.transform;

import com.dianping.cat.alarm.sender.entity.Par;
import com.dianping.cat.alarm.sender.entity.Sender;
import com.dianping.cat.alarm.sender.entity.SenderConfig;
import org.xml.sax.Attributes;

import static com.dianping.cat.alarm.sender.Constants.ATTR_BATCHSEND;
import static com.dianping.cat.alarm.sender.Constants.ATTR_ID;
import static com.dianping.cat.alarm.sender.Constants.ATTR_SUCCESSCODE;
import static com.dianping.cat.alarm.sender.Constants.ATTR_TYPE;
import static com.dianping.cat.alarm.sender.Constants.ATTR_URL;

public class DefaultSaxMaker implements IMaker<Attributes> {

   @Override
   public Par buildPar(Attributes attributes) {
      String id = attributes.getValue(ATTR_ID);
      Par par = new Par();

      if (id != null) {
         par.setId(id);
      }

      return par;
   }

   @Override
   public Sender buildSender(Attributes attributes) {
      String id = attributes.getValue(ATTR_ID);
      String url = attributes.getValue(ATTR_URL);
      String type = attributes.getValue(ATTR_TYPE);
      String successCode = attributes.getValue(ATTR_SUCCESSCODE);
      String batchSend = attributes.getValue(ATTR_BATCHSEND);
      Sender sender = new Sender(id);

      if (url != null) {
         sender.setUrl(url);
      }

      if (type != null) {
         sender.setType(type);
      }

      if (successCode != null) {
         sender.setSuccessCode(successCode);
      }

      if (batchSend != null) {
         sender.setBatchSend(convert(Boolean.class, batchSend, false));
      }

      return sender;
   }

   @Override
   public SenderConfig buildSenderConfig(Attributes attributes) {
      SenderConfig senderConfig = new SenderConfig();

      return senderConfig;
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
