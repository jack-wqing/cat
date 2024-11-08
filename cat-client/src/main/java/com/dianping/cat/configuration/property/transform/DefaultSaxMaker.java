package com.dianping.cat.configuration.property.transform;

import static com.dianping.cat.configuration.property.Constants.ATTR_ID;
import static com.dianping.cat.configuration.property.Constants.ATTR_VALUE;

import org.xml.sax.Attributes;

import com.dianping.cat.configuration.property.entity.Property;
import com.dianping.cat.configuration.property.entity.PropertyConfig;

public class DefaultSaxMaker implements IMaker<Attributes> {

   @Override
   public Property buildProperty(Attributes attributes) {
      String id = attributes.getValue(ATTR_ID);
      String value = attributes.getValue(ATTR_VALUE);
      Property property = new Property(id);

      if (value != null) {
         property.setValue(value);
      }

      return property;
   }

   @Override
   public PropertyConfig buildPropertyConfig(Attributes attributes) {
      PropertyConfig propertyConfig = new PropertyConfig();

      return propertyConfig;
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
