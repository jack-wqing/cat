package com.dianping.cat.alarm.sender;

import com.dianping.cat.alarm.sender.entity.Par;
import com.dianping.cat.alarm.sender.entity.Sender;
import com.dianping.cat.alarm.sender.entity.SenderConfig;

public interface IVisitor {

   public void visitPar(Par par);

   public void visitSender(Sender sender);

   public void visitSenderConfig(SenderConfig senderConfig);
}
