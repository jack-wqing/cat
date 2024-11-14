package com.dianping.cat.alarm.receiver.transform;

import com.dianping.cat.alarm.receiver.entity.AlertConfig;
import com.dianping.cat.alarm.receiver.entity.Receiver;

public interface IParser<T> {
   public AlertConfig parse(IMaker<T> maker, ILinker linker, T node);

   public void parseForReceiver(IMaker<T> maker, ILinker linker, Receiver parent, T node);
}
