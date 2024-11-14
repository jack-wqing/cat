package com.dianping.cat.alarm;

import org.unidal.dal.jdbc.AbstractDao;
import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;

import java.util.List;

public class ServerAlarmRuleDao extends AbstractDao {
   public ServerAlarmRule createLocal() {
      ServerAlarmRule proto = new ServerAlarmRule();

      return proto;
   }

   public int deleteByPK(ServerAlarmRule proto) throws DalException {
      return getQueryEngine().deleteSingle(
            ServerAlarmRuleEntity.DELETE_BY_PK,
            proto);
   }
   
   public List<ServerAlarmRule> findAll(Readset<ServerAlarmRule> readset) throws DalException {
      ServerAlarmRule proto = new ServerAlarmRule();

      List<ServerAlarmRule> result = getQueryEngine().queryMultiple(
            ServerAlarmRuleEntity.FIND_ALL, 
            proto,
            readset);
      
      return result;
   }
   
   public ServerAlarmRule findByPK(int keyId, Readset<ServerAlarmRule> readset) throws DalException {
      ServerAlarmRule proto = new ServerAlarmRule();

      proto.setKeyId(keyId);

      ServerAlarmRule result = getQueryEngine().querySingle(
            ServerAlarmRuleEntity.FIND_BY_PK, 
            proto,
            readset);
      
      return result;
   }
   
   @Override
   protected Class<?>[] getEntityClasses() {
      return new Class<?>[] { ServerAlarmRuleEntity.class };
   }

   public int insert(ServerAlarmRule proto) throws DalException {
      return getQueryEngine().insertSingle(
            ServerAlarmRuleEntity.INSERT,
            proto);
   }
   
   public int updateByPK(ServerAlarmRule proto, Updateset<ServerAlarmRule> updateset) throws DalException {
      return getQueryEngine().updateSingle(
            ServerAlarmRuleEntity.UPDATE_BY_PK,
            proto,
            updateset);
   }
   
}
