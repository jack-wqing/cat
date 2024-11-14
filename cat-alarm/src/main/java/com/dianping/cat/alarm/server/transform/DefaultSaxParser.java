package com.dianping.cat.alarm.server.transform;

import com.dianping.cat.alarm.server.IEntity;
import com.dianping.cat.alarm.server.entity.Condition;
import com.dianping.cat.alarm.server.entity.Rule;
import com.dianping.cat.alarm.server.entity.ServerAlarmRuleConfig;
import com.dianping.cat.alarm.server.entity.SubCondition;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import static com.dianping.cat.alarm.server.Constants.ENTITY_CONDITION;
import static com.dianping.cat.alarm.server.Constants.ENTITY_RULE;
import static com.dianping.cat.alarm.server.Constants.ENTITY_SERVER_ALARM_RULE_CONFIG;
import static com.dianping.cat.alarm.server.Constants.ENTITY_SUB_CONDITION;

public class DefaultSaxParser extends DefaultHandler {

   private DefaultLinker m_linker = new DefaultLinker(true);

   private DefaultSaxMaker m_maker = new DefaultSaxMaker();

   private Stack<String> m_tags = new Stack<String>();

   private Stack<Object> m_objs = new Stack<Object>();

   private IEntity<?> m_entity;

   private StringBuilder m_text = new StringBuilder();

   public static ServerAlarmRuleConfig parse(InputStream in) throws SAXException, IOException {
      return parseEntity(ServerAlarmRuleConfig.class, new InputSource(removeBOM(in)));
   }

   public static ServerAlarmRuleConfig parse(Reader reader) throws SAXException, IOException {
      return parseEntity(ServerAlarmRuleConfig.class, new InputSource(removeBOM(reader)));
   }

   public static ServerAlarmRuleConfig parse(String xml) throws SAXException, IOException {
      return parseEntity(ServerAlarmRuleConfig.class, new InputSource(new StringReader(removeBOM(xml))));
   }

   @SuppressWarnings("unchecked")
   private static <T extends IEntity<?>> T parseEntity(Class<T> type, InputSource is) throws SAXException, IOException {
      try {
         DefaultSaxParser handler = new DefaultSaxParser();
         SAXParserFactory factory = SAXParserFactory.newInstance();

         factory.setValidating(false);
         factory.setFeature("http://xml.org/sax/features/validation", false);

         factory.newSAXParser().parse(is, handler);
         return (T) handler.getEntity();
      } catch (ParserConfigurationException e) {
         throw new IllegalStateException("Unable to get SAX parser instance!", e);
      }
   }

   public static <T extends IEntity<?>> T parseEntity(Class<T> type, InputStream in) throws SAXException, IOException {
      return parseEntity(type, new InputSource(removeBOM(in)));
   }

   public static <T extends IEntity<?>> T parseEntity(Class<T> type, String xml) throws SAXException, IOException {
      return parseEntity(type, new InputSource(new StringReader(removeBOM(xml))));
   }

   // to remove Byte Order Mark(BOM) at the head of windows utf-8 file
   @SuppressWarnings("unchecked")
   private static <T> T removeBOM(T obj) throws IOException {
      if (obj instanceof String) {
         String str = (String) obj;

         if (str.length() != 0 && str.charAt(0) == 0xFEFF) {
            return (T) str.substring(1);
         } else {
            return obj;
         }
      } else if (obj instanceof InputStream) {
         BufferedInputStream in = new BufferedInputStream((InputStream) obj);

         in.mark(3);

         if (in.read() != 0xEF || in.read() != 0xBB || in.read() != 0xBF) {
            in.reset();
         }

         return (T) in;
      } else if (obj instanceof Reader) {
         BufferedReader in = new BufferedReader((Reader) obj);

         in.mark(1);

         if (in.read() != 0xFEFF) {
            in.reset();
         }

         return (T) in;
      } else {
         return obj;
      }
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

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      m_text.append(ch, start, length);
   }

   @Override
   public void endDocument() throws SAXException {
      m_linker.finish();
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (uri == null || uri.length() == 0) {
         m_objs.pop();
         m_tags.pop();

      }

      m_text.setLength(0);
   }

   private IEntity<?> getEntity() {
      return m_entity;
   }

   protected String getText() {
      return m_text.toString();
   }

   private void parseForCondition(Condition parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_SUB_CONDITION.equals(qName)) {
         SubCondition subCondition = m_maker.buildSubCondition(attributes);

         m_linker.onSubCondition(parentObj, subCondition);
         m_objs.push(subCondition);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under condition!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForRule(Rule parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_CONDITION.equals(qName)) {
         Condition condition = m_maker.buildCondition(attributes);

         m_linker.onCondition(parentObj, condition);
         m_objs.push(condition);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under rule!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForServerAlarmRuleConfig(ServerAlarmRuleConfig parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_RULE.equals(qName)) {
         Rule rule = m_maker.buildRule(attributes);

         m_linker.onRule(parentObj, rule);
         m_objs.push(rule);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under server-alarm-rule-config!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForSubCondition(SubCondition parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      m_objs.push(parentObj);
      m_tags.push(qName);
   }

   private void parseRoot(String qName, Attributes attributes) throws SAXException {
      if (ENTITY_SERVER_ALARM_RULE_CONFIG.equals(qName)) {
         ServerAlarmRuleConfig serverAlarmRuleConfig = m_maker.buildServerAlarmRuleConfig(attributes);

         m_entity = serverAlarmRuleConfig;
         m_objs.push(serverAlarmRuleConfig);
         m_tags.push(qName);
      } else if (ENTITY_RULE.equals(qName)) {
         Rule rule = m_maker.buildRule(attributes);

         m_entity = rule;
         m_objs.push(rule);
         m_tags.push(qName);
      } else if (ENTITY_CONDITION.equals(qName)) {
         Condition condition = m_maker.buildCondition(attributes);

         m_entity = condition;
         m_objs.push(condition);
         m_tags.push(qName);
      } else if (ENTITY_SUB_CONDITION.equals(qName)) {
         SubCondition subCondition = m_maker.buildSubCondition(attributes);

         m_entity = subCondition;
         m_objs.push(subCondition);
         m_tags.push(qName);
      } else {
         throw new SAXException("Unknown root element(" + qName + ") found!");
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (uri == null || uri.length() == 0) {
         if (m_objs.isEmpty()) { // root
            parseRoot(qName, attributes);
         } else {
            Object parent = m_objs.peek();
            String tag = m_tags.peek();

            if (parent instanceof ServerAlarmRuleConfig) {
               parseForServerAlarmRuleConfig((ServerAlarmRuleConfig) parent, tag, qName, attributes);
            } else if (parent instanceof Rule) {
               parseForRule((Rule) parent, tag, qName, attributes);
            } else if (parent instanceof Condition) {
               parseForCondition((Condition) parent, tag, qName, attributes);
            } else if (parent instanceof SubCondition) {
               parseForSubCondition((SubCondition) parent, tag, qName, attributes);
            } else {
               throw new RuntimeException(String.format("Unknown entity(%s) under %s!", qName, parent.getClass().getName()));
            }
         }

         m_text.setLength(0);
        } else {
         throw new SAXException(String.format("Namespace(%s) is not supported by %s.", uri, this.getClass().getName()));
      }
   }
}
