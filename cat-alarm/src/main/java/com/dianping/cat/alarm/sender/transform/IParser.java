package com.dianping.cat.alarm.sender.transform;

import com.dianping.cat.alarm.sender.entity.Par;
import com.dianping.cat.alarm.sender.entity.Sender;
import com.dianping.cat.alarm.sender.entity.SenderConfig;

public interface IParser<T> {
   public SenderConfig parse(IMaker<T> maker, ILinker linker, T node);

   public void parseForPar(IMaker<T> maker, ILinker linker, Par parent, T node);

   public void parseForSender(IMaker<T> maker, ILinker linker, Sender parent, T node);
}
