package com.dianping.cat.configuration.property;

import com.dianping.cat.configuration.property.entity.Property;
import com.dianping.cat.configuration.property.entity.PropertyConfig;

public interface IVisitor {

   public void visitProperty(Property property);

   public void visitPropertyConfig(PropertyConfig propertyConfig);
}
