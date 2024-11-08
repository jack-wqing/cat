package com.dianping.cat.configuration.property.transform;

import com.dianping.cat.configuration.property.entity.Property;
import com.dianping.cat.configuration.property.entity.PropertyConfig;

public interface IParser<T> {
   public PropertyConfig parse(IMaker<T> maker, ILinker linker, T node);

   public void parseForProperty(IMaker<T> maker, ILinker linker, Property parent, T node);
}
