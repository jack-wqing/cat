package com.dianping.cat.alarm.server.transform;

import com.dianping.cat.alarm.server.IEntity;
import com.dianping.cat.alarm.server.entity.Condition;
import com.dianping.cat.alarm.server.entity.Rule;
import com.dianping.cat.alarm.server.entity.ServerAlarmRuleConfig;
import com.dianping.cat.alarm.server.entity.SubCondition;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.dianping.cat.alarm.server.Constants.ATTR_ALERT_TYPE;
import static com.dianping.cat.alarm.server.Constants.ATTR_DURATION;
import static com.dianping.cat.alarm.server.Constants.ATTR_END_TIME;
import static com.dianping.cat.alarm.server.Constants.ATTR_ID;
import static com.dianping.cat.alarm.server.Constants.ATTR_INTERVAL;
import static com.dianping.cat.alarm.server.Constants.ATTR_START_TIME;
import static com.dianping.cat.alarm.server.Constants.ATTR_TYPE;
import static com.dianping.cat.alarm.server.Constants.ATTR_VALUE;
import static com.dianping.cat.alarm.server.Constants.ENTITY_CONDITIONS;
import static com.dianping.cat.alarm.server.Constants.ENTITY_RULES;
import static com.dianping.cat.alarm.server.Constants.ENTITY_SUB_CONDITIONS;

public class DefaultJsonParser {

   private DefaultLinker m_linker = new DefaultLinker(true);

   private Stack<String> m_tags = new Stack<String>();

   private Stack<Object> m_objs = new Stack<Object>();

   private List<Object> m_entities = new ArrayList<Object>();

   private Class<?> m_entityClass;

   private DefaultJsonParser(Class<?> entityClass) {
      m_entityClass = entityClass;
   }

   public static <T extends IEntity<T>> T parse(Class<T> entityClass, InputStream in) throws IOException {
      return parse(entityClass, new InputStreamReader(in, "utf-8"));
   }

   @SuppressWarnings("unchecked")
      public static <T extends IEntity<T>> T parse(Class<T> entityClass, Reader reader) throws IOException {
      DefaultJsonParser parser = new DefaultJsonParser(entityClass);

      parser.onArrayBegin();
      parser.parse(new JsonReader(reader));
      parser.onArrayEnd();

      if (parser.m_entities.isEmpty()) {
         return null;
      } else {
         return (T) parser.m_entities.get(0);
      }
   }

   public static <T extends IEntity<T>> T parse(Class<T> entityClass, String json) throws IOException {
      return parse(entityClass, new StringReader(json));
   }

   public static <T extends IEntity<T>> List<T> parseArray(Class<T> entityClass, InputStream in) throws Exception {
      return parseArray(entityClass, new InputStreamReader(in, "utf-8"));
   }

   @SuppressWarnings("unchecked")
   public static <T extends IEntity<T>> List<T> parseArray(Class<T> entityClass, Reader reader) throws Exception {
      DefaultJsonParser parser = new DefaultJsonParser(entityClass);

      parser.parse(new JsonReader(reader));
      return (List<T>) (List<?>) parser.m_entities;
   }

   public static <T extends IEntity<T>> List<T> parseArray(Class<T> entityClass, String json) throws Exception {
      return parseArray(entityClass, new StringReader(json));
   }


   @SuppressWarnings("unchecked")
   protected <T> T convert(Class<T> type, String value, T defaultValue) {
      if (value == null || value.length() == 0) {
         return defaultValue;
      }

      if (type == Boolean.class) {
         return (T) Boolean.valueOf(value);
      } else if (type == Integer.class) {
         return (T) Integer.valueOf(value);
      } else if (type == Long.class) {
         return (T) Long.valueOf(value);
      } else if (type == Short.class) {
         return (T) Short.valueOf(value);
      } else if (type == Float.class) {
         return (T) Float.valueOf(value);
      } else if (type == Double.class) {
         return (T) Double.valueOf(value);
      } else if (type == Byte.class) {
         return (T) Byte.valueOf(value);
      } else if (type == Character.class) {
         return (T) (Character) value.charAt(0);
      } else {
         return (T) value;
      }
   }

   private Object createRootEntity() {
      try {
         Object entity = m_entityClass.newInstance();

         return entity;
      } catch (Exception e) {
         throw new RuntimeException(String.format("Unable to create entity(%s) instance!", m_entityClass.getName()), e);
      }
   }

   private boolean isTopLevel() {
      return m_objs.size() == 1;
   }

   protected void onArrayBegin() {
      if (m_objs.isEmpty()) {
         m_objs.push(m_entities);
         m_tags.push("");
      } else {
         Object parent = m_objs.peek();
         String tag = m_tags.peek();

         if (parent instanceof ServerAlarmRuleConfig) {
            if (ENTITY_RULES.equals(tag)) {
               m_objs.push(parent);
            } else {
               throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));
            }
         } else if (parent instanceof Rule) {
            if (ENTITY_CONDITIONS.equals(tag)) {
               m_objs.push(parent);
            } else {
               throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));
            }
         } else if (parent instanceof Condition) {
            if (ENTITY_SUB_CONDITIONS.equals(tag)) {
               m_objs.push(parent);
            } else {
               throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));
            }
         } else {
            throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));
         }
      }   }

   protected void onArrayEnd() {
      m_objs.pop();
      m_tags.pop();

   }
   protected void onName(String name) {
      m_tags.push(name);
   }

   protected void onObjectBegin() {
      if (isTopLevel()) {
         m_objs.push(createRootEntity());
         m_tags.push("");
      } else {
         Object parent = m_objs.peek();
         String tag = m_tags.peek();

         if (parent instanceof ServerAlarmRuleConfig) {
            if (ENTITY_RULES.equals(tag)) {
               Rule rules = new Rule();

               m_linker.onRule((ServerAlarmRuleConfig) parent, rules);
               m_objs.push(rules);
               m_tags.push("");
            } else {
               throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags)); ///
            }
         } else if (parent instanceof Rule) {
            if (ENTITY_CONDITIONS.equals(tag)) {
               Condition conditions = new Condition();

               m_linker.onCondition((Rule) parent, conditions);
               m_objs.push(conditions);
               m_tags.push("");
            } else {
               throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags)); ///
            }
         } else if (parent instanceof Condition) {
            if (ENTITY_SUB_CONDITIONS.equals(tag)) {
               SubCondition subConditions = new SubCondition();

               m_linker.onSubCondition((Condition) parent, subConditions);
               m_objs.push(subConditions);
               m_tags.push("");
            } else {
               throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags)); ///
            }
         } else {
            throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));
         }
      }
   }

   protected void onObjectEnd() {
      m_tags.pop();

      Object entity = m_objs.pop();

      if (isTopLevel()) {
         m_entities.add(entity);
      }
   }

   protected void onValue(String value) {
      Object parent = m_objs.peek();
      String tag = m_tags.pop();

      if (parent instanceof ServerAlarmRuleConfig) {
         parseForServerAlarmRuleConfig((ServerAlarmRuleConfig) parent, tag, value);
      } else if (parent instanceof Rule) {
         parseForRule((Rule) parent, tag, value);
      } else if (parent instanceof Condition) {
         parseForCondition((Condition) parent, tag, value);
      } else if (parent instanceof SubCondition) {
         parseForSubCondition((SubCondition) parent, tag, value);
      } else {
         throw new RuntimeException(String.format("Unknown tag(%s) under %s!", tag, parent));
      }
   }

   private void parse(JsonReader reader) throws IOException {
      try {
         reader.parse(this);
      } catch (EOFException e) {
         if (m_objs.size() > 1) {
            throw new EOFException(String.format("Unexpected end while parsing json! tags: %s.", m_tags));
         }
      }

      m_linker.finish();
   }

   public void parseForCondition(Condition condition, String tag, String value) {
      if (ENTITY_SUB_CONDITIONS.equals(tag)) {
         // do nothing here
      } else if (ATTR_INTERVAL.equals(tag)) {
         condition.setInterval(value);
      } else if (ATTR_DURATION.equals(tag)) {
         condition.setDuration(convert(Integer.class, value, 0));
      } else if (ATTR_ALERT_TYPE.equals(tag)) {
         condition.setAlertType(value);
      } else {
         throw new RuntimeException(String.format("Unknown tag(%s) of %s under %s!", tag, condition, m_tags));
      }
   }

   public void parseForRule(Rule rule, String tag, String value) {
      if (ENTITY_CONDITIONS.equals(tag)) {
         // do nothing here
      } else if (ATTR_START_TIME.equals(tag)) {
         rule.setStartTime(value);
      } else if (ATTR_END_TIME.equals(tag)) {
         rule.setEndTime(value);
      } else {
         throw new RuntimeException(String.format("Unknown tag(%s) of %s under %s!", tag, rule, m_tags));
      }
   }

   public void parseForServerAlarmRuleConfig(ServerAlarmRuleConfig serverAlarmRuleConfig, String tag, String value) {
      if (ENTITY_RULES.equals(tag)) {
         // do nothing here
      } else if (ATTR_ID.equals(tag)) {
         serverAlarmRuleConfig.setId(value);
      } else {
         throw new RuntimeException(String.format("Unknown tag(%s) of %s under %s!", tag, serverAlarmRuleConfig, m_tags));
      }
   }

   public void parseForSubCondition(SubCondition subCondition, String tag, String value) {
      if (ATTR_TYPE.equals(tag)) {
         subCondition.setType(value);
      } else if (ATTR_VALUE.equals(tag)) {
         subCondition.setValue(value);
      } else {
         throw new RuntimeException(String.format("Unknown tag(%s) of %s under %s!", tag, subCondition, m_tags));
      }
   }


   static class JsonReader {
      private Reader m_reader;

      private char[] m_buffer = new char[2048];

      private int m_size;

      private int m_index;

      public JsonReader(Reader reader) {
         m_reader = reader;
      }

      private char next() throws IOException {
         if (m_index >= m_size) {
            m_size = m_reader.read(m_buffer);
            m_index = 0;

            if (m_size == -1) {
               throw new EOFException();
            }
         }

         return m_buffer[m_index++];
      }

      public void parse(DefaultJsonParser parser) throws IOException {
         StringBuilder sb = new StringBuilder();
         boolean flag = false;

         while (true) {
            char ch = next();

            switch (ch) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
               break;
            case '{':
               parser.onObjectBegin();
               flag = false;
               break;
            case '}':
               if (flag) { // have value
                  parser.onValue(sb.toString());
                  sb.setLength(0);
               }

               parser.onObjectEnd();
               flag = false;
               break;
            case '\'':
            case '"':
               while (true) {
                  char ch2 = next();

                  if (ch2 != ch) {
                     if (ch2 == '\\') {
                        char ch3 = next();

                        switch (ch3) {
                        case 't':
                        	sb.append('\t');
                        	break;
                        case 'r':
                        	sb.append('\r');
                        	break;
                        case 'n':
                        	sb.append('\n');
                        	break;
                        default:
                        	sb.append(ch3);
                        	break;
                        }
                     } else {
                        sb.append(ch2);
                     }
                  } else {
                     if (!flag) {
                        parser.onName(sb.toString());
                     } else {
                        parser.onValue(sb.toString());
                        flag = false;
                     }

                     sb.setLength(0);
                     break;
                  }
               }

               break;
            case ':':
               if (sb.length() != 0) {
                  parser.onName(sb.toString());
                  sb.setLength(0);
               }

               flag = true;
               break;
            case ',':
               if (sb.length() != 0) {
                  if (!flag) {
                     parser.onName(sb.toString());
                  } else {
                     parser.onValue(sb.toString());
                  }

                  sb.setLength(0);
               }

               flag = false;
               break;
            case '[':
               parser.onArrayBegin();
               flag = false;
               break;
            case ']':
               parser.onArrayEnd();
               flag = false;
               break;
            default:
               sb.append(ch);
               break;
            }
         }
      }
   }
}
