package com.dianping.cat.configuration.client.transform;

import com.dianping.cat.configuration.client.IVisitor;
import com.dianping.cat.configuration.client.entity.Bind;
import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.client.entity.Domain;
import com.dianping.cat.configuration.client.entity.Property;
import com.dianping.cat.configuration.client.entity.Server;

public abstract class BaseVisitor implements IVisitor {
   @Override
   public void visitBind(Bind bind) {
   }

   @Override
   public void visitConfig(ClientConfig config) {
      for (Server server : config.getServers()) {
         visitServer(server);
      }

      for (Domain domain : config.getDomains().values()) {
         visitDomain(domain);
      }

      if (config.getBind() != null) {
         visitBind(config.getBind());
      }

      for (Property property : config.getProperties().values()) {
         visitProperty(property);
      }
   }

   @Override
   public void visitDomain(Domain domain) {
   }

   @Override
   public void visitProperty(Property property) {
   }

   @Override
   public void visitServer(Server server) {
   }
}
