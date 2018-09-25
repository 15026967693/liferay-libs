package com.util.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;



public class MergeConfig {
	private static Logger mergeCofigLogger=LoggerFactory.getLogger(MergeConfig.class);
	private  Map<String,Object> config;
	
	public MergeConfig(String configPath) {
		Yaml yaml=new Yaml();
		String configFilePath=null;
		Reader configReader=null;
		Reader defaultConfigReader=null;
		Reader globalConfigReader=null;
		Map<String,Object> defaultConfig=null;
		Map<String,Object> globalConfig=null;
		try {
			defaultConfigReader=new InputStreamReader(MergeConfig.class.getClassLoader().getResourceAsStream(configPath));
			defaultConfig=yaml.load(defaultConfigReader);
			globalConfigReader=new InputStreamReader(MergeConfig.class.getClassLoader().getResourceAsStream("extend.global.yaml"));
			globalConfig=yaml.load(globalConfigReader);
			configFilePath=new File(MergeConfig.class.getClassLoader().getResource("").getFile())
					.getParentFile().getCanonicalPath()+"/"+configPath;
			if(new File(configFilePath).exists()) {
				configReader=new FileReader(configFilePath);
			    config=yaml.load(configReader);
			    mergeMap(config, defaultConfig);
			    mergeMap(config, globalConfig);   	
			    }
			else {
				config=defaultConfig;
				mergeMap(config, globalConfig); 
			}
			config.put("rootPath",new File(MergeConfig.class.getClassLoader().getResource("").getFile()).getParentFile().getCanonicalPath());
				
				
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mergeCofigLogger.error("{}:{}",new Date(),e);
		}
		finally {
			try {
				if(configReader!=null)
					configReader.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mergeCofigLogger.error("{}:{}",new Date(),e);
			} 
			try {
				if(defaultConfigReader!=null)
					defaultConfigReader.close();
			}
			catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
				mergeCofigLogger.error("{}:{}",new Date(),e);
			}
			try {
				if(globalConfigReader!=null)
					globalConfigReader.close();
			}
			catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
				mergeCofigLogger.error("{}:{}",new Date(),e);
			}
		}
          
		
		
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

	private void mergeMap(Map<String,Object> targetMap,Map<String,Object> appendMap ) {
		if(appendMap==null)
			return;
		for(String key:appendMap.keySet())
		{
			if(!targetMap.containsKey(key)&&!(appendMap.get(key) instanceof Map))
			{
				targetMap.put(key,appendMap.get(key));	
			}
			else if((appendMap.get(key) instanceof Map)&&!(appendMap.get(key).equals(targetMap.get(key))))
			{
				if(targetMap.get(key)==null)
				targetMap.put(key,new HashMap<String,Object>());
				 mergeMap((Map<String, Object>)targetMap.get(key),(Map<String,Object>)appendMap.get(key));
			}
		}
		
		
	}
	
	


}

