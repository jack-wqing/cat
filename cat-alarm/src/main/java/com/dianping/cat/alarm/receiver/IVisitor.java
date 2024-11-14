package com.dianping.cat.alarm.receiver;

import com.dianping.cat.alarm.receiver.entity.AlertConfig;
import com.dianping.cat.alarm.receiver.entity.Receiver;

public interface IVisitor {

   public void visitAlertConfig(AlertConfig alertConfig);

   public void visitReceiver(Receiver receiver);
}
