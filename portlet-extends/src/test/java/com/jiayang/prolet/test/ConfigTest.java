package com.jiayang.prolet.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jiayang.portlet.commons.Config;

public class ConfigTest {
	@Test
	public void ConfigInit() {
		try {
			Class.forName("com.jiayang.portlet.commons.Config");
			Logger logger=LoggerFactory.getLogger(this.getClass());
			int i=0;
			while(i<Integer.MAX_VALUE)
			logger.error("测试错误"+(i++));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
