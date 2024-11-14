package com.dianping.cat.alarm.sender.transform;

import com.dianping.cat.alarm.sender.entity.Par;
import com.dianping.cat.alarm.sender.entity.Sender;
import com.dianping.cat.alarm.sender.entity.SenderConfig;

import java.util.ArrayList;
import java.util.List;

public class DefaultLinker implements ILinker {
   private boolean m_deferrable;

   private List<Runnable> m_deferedJobs = new ArrayList<Runnable>();

   public DefaultLinker(boolean deferrable) {
      m_deferrable = deferrable;
   }

   public void finish() {
      for (Runnable job : m_deferedJobs) {
         job.run();
      }
   }

   @Override
   public boolean onPar(final Sender parent, final Par par) {
      parent.addPar(par);
      return true;
   }

   @Override
   public boolean onSender(final SenderConfig parent, final Sender sender) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addSender(sender);
            }
         });
      } else {
         parent.addSender(sender);
      }

      return true;
   }
}
