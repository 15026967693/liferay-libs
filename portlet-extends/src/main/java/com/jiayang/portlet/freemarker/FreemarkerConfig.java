package com.jiayang.portlet.freemarker;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jiayang.portlet.commons.Config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerConfig {
	public static Logger freemarkerConfigLogger=LoggerFactory.getLogger(FreemarkerConfig.class);
   public static Configuration configuration;
    static {
    	configuration=new Configuration(Configuration.VERSION_2_3_28);
    	@SuppressWarnings("unchecked")
    	Map<String,Object> templateConfig=(Map<String, Object>) Config.config.get("template");
    	configuration.setDefaultEncoding((String)templateConfig.get("encoding"));
    	configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    	try {
			configuration.setDirectoryForTemplateLoading(new File(Config.config.get("rootPath")+"/"+templateConfig.get("path")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			freemarkerConfigLogger.error("{}",e);
			throw new RuntimeException("设置目录失败，不存在的目录或者目录被占用，请检查extend.yaml文件templat下的path设置是否正确");
		}
    	
    	freemarkerConfigLogger.info("{}初始化完成",configuration);
    	
    }
    
}