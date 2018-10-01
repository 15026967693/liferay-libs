package com.jiayang.portlet.commons;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.jiayang.portlet.exception.ViewNullExcetion;
import com.jiayang.util.config.Config;

import org.slf4j.Logger;

public class ModelAndView implements IModelAndView<Map<String, Object>> {
	private static Logger mvLogger=LoggerFactory.getLogger(ModelAndView.class);
	private String view;
	private Map<String, Object> model=new HashMap<>();
	
	public ModelAndView() {
		// TODO Auto-generated constructor stub
	}
	public ModelAndView(String view) {
		this.view=view;
	}
	public ModelAndView(String view,Map<String, Object> model) {
		this.view=view;
		this.model=model;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getView() throws ViewNullExcetion{
		// TODO Auto-generated method stub
		if(view==null||view.trim().replaceAll(" ","").isEmpty())
			throw new ViewNullExcetion();
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
