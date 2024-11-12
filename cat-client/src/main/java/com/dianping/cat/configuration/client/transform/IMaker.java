package com.dianping.cat.configuration.client.transform;

import com.dianping.cat.configuration.client.entity.Bind;
import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.client.entity.Domain;
import com.dianping.cat.configuration.client.entity.Property;
import com.dianping.cat.configuration.client.entity.Server;

/**
 * 创建 客户端需要的对象
 * @param <T>
 */
public interface IMaker<T> {

   public Bind buildBind(T node);

   public ClientConfig buildConfig(T node);

   public Domain buildDomain(T node);

   public Property buildProperty(T node);

   public Server buildServer(T node);
}
