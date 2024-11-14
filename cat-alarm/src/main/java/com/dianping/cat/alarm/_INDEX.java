package com.dianping.cat.alarm;

public class _INDEX {
   public static Class<?>[] getEntityClasses() {
      return new Class<?>[] { AlertEntity.class, ServerAlarmRuleEntity.class, UserDefineRuleEntity.class };
   }

   public static Class<?>[] getDaoClasses() {
      return new Class<?>[] { AlertDao.class, ServerAlarmRuleDao.class, UserDefineRuleDao.class };
   }

   public static Class<?>[] getDoClasses() {
      return new Class<?>[] { Alert.class, ServerAlarmRule.class, UserDefineRule.class };
   }

}
