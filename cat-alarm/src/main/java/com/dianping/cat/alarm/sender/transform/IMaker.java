package com.dianping.cat.alarm.sender.transform;

import com.dianping.cat.alarm.sender.entity.Par;
import com.dianping.cat.alarm.sender.entity.Sender;
import com.dianping.cat.alarm.sender.entity.SenderConfig;

public interface IMaker<T> {

   public Par buildPar(T node);

   public Sender buildSender(T node);

   public SenderConfig buildSenderConfig(T node);
}
