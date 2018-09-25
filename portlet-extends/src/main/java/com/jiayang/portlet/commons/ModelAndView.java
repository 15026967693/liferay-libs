package com.jiayang.portlet.commons;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

public class ModelAndView implements IModelAndView<Map<String, Object>> {
	private static Logger mvLogger=LoggerFactory.getLogger(ModelAndView.class);
	private String view;
	private Map<String, Object> model=new HashMap<>();
	@Override
	public String getView() {
		// TODO Auto-generated method stub
		return view+((Map<String, Object>)Config.config.get("template")).get("suffix");
	}

	@Override
	public Map<String, Object> getModel() {
		
		return model;
	}

	@Override
	public void setView(String view) {
		this.view=view;
		
	}

	@Override
	public void setModel(Map<String, Object> model) {
		// TODO Auto-generated method stub
		this.model=model;
	}
	public void setAttr(String name,Object value)
	{
	  if(model.containsKey(name))
		  mvLogger.debug("您覆盖了一个已有元素请确定您的操作中不存在不必要的操作，这可能降低程序的效率，键值为：{}",name);
	  model.put(name, value);
	}

}