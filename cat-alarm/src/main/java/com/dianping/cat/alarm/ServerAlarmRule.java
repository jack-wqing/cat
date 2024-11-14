package com.dianping.cat.alarm;

import org.unidal.dal.jdbc.DataObject;

import static com.dianping.cat.alarm.ServerAlarmRuleEntity.CATEGORY;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.CONTENT;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.CREATION_DATE;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.CREATOR;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.END_POINT;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.ID;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.KEY_ID;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.MEASUREMENT;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.TAGS;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.TYPE;
import static com.dianping.cat.alarm.ServerAlarmRuleEntity.UPDATETIME;

public class ServerAlarmRule extends DataObject {
   private int m_id;

   private String m_category;

   private String m_endPoint;

   private String m_measurement;

   private String m_tags;

   private String m_content;

   private String m_type;

   private String m_creator;

   private java.util.Date m_creationDate;

   private java.util.Date m_updatetime;

   private int m_keyId;

   @Override
   public void afterLoad() {
      m_keyId = m_id;
      super.clearUsage();
   }

   public String getCategory() {
      return m_category;
   }

   public String getContent() {
      return m_content;
   }

   public java.util.Date getCreationDate() {
      return m_creationDate;
   }

   public String getCreator() {
      return m_creator;
   }

   public String getEndPoint() {
      return m_endPoint;
   }

   public int getId() {
      return m_id;
   }

   public int getKeyId() {
      return m_keyId;
   }

   public String getMeasurement() {
      return m_measurement;
   }

   public String getTags() {
      return m_tags;
   }

   public String getType() {
      return m_type;
   }

   public java.util.Date getUpdatetime() {
      return m_updatetime;
   }

   public ServerAlarmRule setCategory(String category) {
      setFieldUsed(CATEGORY, true);
      m_category = category;
      return this;
   }

   public ServerAlarmRule setContent(String content) {
      setFieldUsed(CONTENT, true);
      m_content = content;
      return this;
   }

   public ServerAlarmRule setCreationDate(java.util.Date creationDate) {
      setFieldUsed(CREATION_DATE, true);
      m_creationDate = creationDate;
      return this;
   }

   public ServerAlarmRule setCreator(String creator) {
      setFieldUsed(CREATOR, true);
      m_creator = creator;
      return this;
   }

   public ServerAlarmRule setEndPoint(String endPoint) {
      setFieldUsed(END_POINT, true);
      m_endPoint = endPoint;
      return this;
   }

   public ServerAlarmRule setId(int id) {
      setFieldUsed(ID, true);
      m_id = id;

      setFieldUsed(KEY_ID, true);
      m_keyId = id;
      return this;
   }

   public ServerAlarmRule setKeyId(int keyId) {
      setFieldUsed(KEY_ID, true);
      m_keyId = keyId;
      return this;
   }

   public ServerAlarmRule setMeasurement(String measurement) {
      setFieldUsed(MEASUREMENT, true);
      m_measurement = measurement;
      return this;
   }

   public ServerAlarmRule setTags(String tags) {
      setFieldUsed(TAGS, true);
      m_tags = tags;
      return this;
   }

   public ServerAlarmRule setType(String type) {
      setFieldUsed(TYPE, true);
      m_type = type;
      return this;
   }

   public ServerAlarmRule setUpdatetime(java.util.Date updatetime) {
      setFieldUsed(UPDATETIME, true);
      m_updatetime = updatetime;
      return this;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(1024);

      sb.append("ServerAlarmRule[");
      sb.append("category: ").append(m_category);
      sb.append(", content: ").append(m_content);
      sb.append(", creation-date: ").append(m_creationDate);
      sb.append(", creator: ").append(m_creator);
      sb.append(", end-point: ").append(m_endPoint);
      sb.append(", id: ").append(m_id);
      sb.append(", key-id: ").append(m_keyId);
      sb.append(", measurement: ").append(m_measurement);
      sb.append(", tags: ").append(m_tags);
      sb.append(", type: ").append(m_type);
      sb.append(", updatetime: ").append(m_updatetime);
      sb.append("]");
      return sb.toString();
   }

}
