package com.jiayang.portlet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jiayang.portlet.enumeration.ResourceType;

@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.TYPE,ElementType.METHOD})
public @interface ResourceMapping {
String[] resourceId();
String encoding() default "UTF-8";
ResourceType resourceType() default ResourceType.JSON;
}
