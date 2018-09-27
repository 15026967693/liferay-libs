package com.jiayang.portlet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jiayang.portlet.enumeration.HttpMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.TYPE,ElementType.METHOD})
public @interface RequestMapping {
   String  value() default "/";
   String  path() default "";
   HttpMethod method() default HttpMethod.ALL;
   
}
