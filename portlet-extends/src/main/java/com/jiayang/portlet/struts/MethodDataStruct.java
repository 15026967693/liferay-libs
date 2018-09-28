package com.jiayang.portlet.struts;
/**
 * 
 * 
 * 
 * @author jiayang
 *
 *添加元数据的方法对象
 *
 */

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.jiayang.portlet.enumeration.ResourceType;

public class MethodDataStruct {
private  Method method;
private Map<String, Object> data=new HashMap<>();
public Method getMethod() {
	return method;
}
public void setMethod(Method method) {
	this.method = method;
}
public Map<String, Object> getData() {
	return data;
}
public void setData(Map<String, Object> data) {
	this.data = data;
}

public <T> T getAttribute(String attrName,Class<T> clazz) {
	return  clazz.cast(this.data.get(attrName));
}
@SuppressWarnings("unchecked")
public <T> T getAttribute(String attrName) {
	return (T) this.data.get(attrName);
}

}
