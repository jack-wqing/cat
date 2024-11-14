package com.dianping.cat.alarm;

import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.annotation.Entity;
import org.unidal.dal.jdbc.annotation.Variable;

@Entity(logicalName = "server-alarm-rule", physicalName = "server_alarm_rule", alias = "sar")
public class ServerAlarmRuleEntity {

   @Attribute(field = "id", nullable = false, primaryKey = true, autoIncrement = true)
   public static final DataField ID = new DataField("id");

   @Attribute(field = "category", nullable = false)
   public static final DataField CATEGORY = new DataField("category");

   @Attribute(field = "endPoint", nullable = false)
   public static final DataField END_POINT = new DataField("end-point");

   @Attribute(field = "measurement", nullable = false)
   public static final DataField MEASUREMENT = new DataField("measurement");

   @Attribute(field = "tags", nullable = false)
   public static final DataField TAGS = new DataField("tags");

   @Attribute(field = "content", nullable = false)
   public static final DataField CONTENT = new DataField("content");

   @Attribute(field = "type", nullable = false)
   public static final DataField TYPE = new DataField("type");

   @Attribute(field = "creator")
   public static final DataField CREATOR = new DataField("creator");

   @Attribute(field = "creation_date", nullable = false, insertExpr = "NOW()")
   public static final DataField CREATION_DATE = new DataField("creation-date");

   @Attribute(field = "updatetime", nullable = false, insertExpr = "NOW()", updateExpr = "NOW()")
   public static final DataField UPDATETIME = new DataField("updatetime");

   @Variable
   public static final DataField KEY_ID = new DataField("key-id");

   public static final Readset<ServerAlarmRule> READSET_FULL = new Readset<ServerAlarmRule>(ID, CATEGORY, END_POINT, MEASUREMENT, TAGS, CONTENT, TYPE, CREATOR, CREATION_DATE, UPDATETIME);

   public static final Updateset<ServerAlarmRule> UPDATESET_FULL = new Updateset<ServerAlarmRule>(ID, CATEGORY, END_POINT, MEASUREMENT, TAGS, CONTENT, TYPE, CREATOR, UPDATETIME);

   public static final QueryDef FIND_BY_PK = new QueryDef("findByPK", ServerAlarmRuleEntity.class, QueryType.SELECT, 
      "SELECT <FIELDS/> FROM <TABLE/> WHERE <FIELD name='id'/> = ${key-id}");

   public static final QueryDef INSERT = new QueryDef("insert", ServerAlarmRuleEntity.class, QueryType.INSERT, 
      "INSERT INTO <TABLE/>(<FIELDS/>) VALUES(<VALUES/>)");

   public static final QueryDef UPDATE_BY_PK = new QueryDef("updateByPK", ServerAlarmRuleEntity.class, QueryType.UPDATE, 
      "UPDATE <TABLE/> SET <FIELDS/> WHERE <FIELD name='id'/> = ${key-id}");

   public static final QueryDef DELETE_BY_PK = new QueryDef("deleteByPK", ServerAlarmRuleEntity.class, QueryType.DELETE, 
      "DELETE FROM <TABLE/> WHERE <FIELD name='id'/> = ${key-id}");

   public static final QueryDef FIND_ALL = new QueryDef("findAll", ServerAlarmRuleEntity.class, QueryType.SELECT, 
      "SELECT <FIELDS/> FROM <TABLE/>");

}
