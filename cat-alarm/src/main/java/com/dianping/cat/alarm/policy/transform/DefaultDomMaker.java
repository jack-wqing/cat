package com.dianping.cat.alarm.policy.transform;

import com.dianping.cat.alarm.policy.entity.AlertPolicy;
import com.dianping.cat.alarm.policy.entity.Group;
import com.dianping.cat.alarm.policy.entity.Level;
import com.dianping.cat.alarm.policy.entity.Type;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.dianping.cat.alarm.policy.Constants.ATTR_ID;
import static com.dianping.cat.alarm.policy.Constants.ATTR_RECOVERMINUTE;
import static com.dianping.cat.alarm.policy.Constants.ATTR_SEND;
import static com.dianping.cat.alarm.policy.Constants.ATTR_SUSPENDMINUTE;

public class DefaultDomMaker implements IMaker<Node> {

   @Override
   public AlertPolicy buildAlertPolicy(Node node) {
      AlertPolicy alertPolicy = new AlertPolicy();

      return alertPolicy;
   }

   @Override
   public Group buildGroup(Node node) {
      String id = getAttribute(node, ATTR_ID);

      Group group = new Group(id);

      return group;
   }

   @Override
   public Level buildLevel(Node node) {
      String id = getAttribute(node, ATTR_ID);
      String send = getAttribute(node, ATTR_SEND);
      String suspendMinute = getAttribute(node, ATTR_SUSPENDMINUTE);
      String recoverMinute = getAttribute(node, ATTR_RECOVERMINUTE);

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
   public Type buildType(Node node) {
      String id = getAttribute(node, ATTR_ID);

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
