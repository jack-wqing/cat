package com.dianping.cat.alarm.receiver.transform;

import com.dianping.cat.alarm.receiver.entity.AlertConfig;
import com.dianping.cat.alarm.receiver.entity.Receiver;

public interface IMaker<T> {

   public AlertConfig buildAlertConfig(T node);

   public String buildDx(T node);

   public String buildEmail(T node);

   public String buildPhone(T node);

   public Receiver buildReceiver(T node);

   public String buildWeixin(T node);
}
