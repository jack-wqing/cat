package com.dianping.cat.configuration.client;

/**
 * 观察着模式
 * @param <T>
 */
public interface IEntity<T> {
   public void accept(IVisitor visitor);

   public void mergeAttributes(T other);

}
