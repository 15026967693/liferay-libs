package com.jiayang.portlet.commons;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.util.config.MergeConfig;



public class Config {
	public static Logger configLogger=LoggerFactory.getLogger(Config.class);
	public static Map<String,Object> config;
	static {
		config=new MergeConfig("extend.yaml").getConfig();
		configLogger.debug("配置完成，详细配置信息为：{}",config);
	}
		
          
	

}
