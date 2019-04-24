package com.cjm.core.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.cjm.utils.StringUtil;

/**
 * 配置文件加载器
 */
public class ConfigLoader {
	public static Config load() throws Exception {
		Configuration propConfig = new PropertiesConfiguration("config.properties");
		
		Config config = new Config();
		config.setDriver(StringUtil.trim(propConfig.getString("driver")));
		config.setUrl(StringUtil.trim(propConfig.getString("url")));
		config.setUid(StringUtil.trim(propConfig.getString("uid")));
		config.setPwd(StringUtil.trim(propConfig.getString("pwd")));
		config.setTableSchema(StringUtil.trim(propConfig.getString("tableSchema")));
		config.setOutputFile(StringUtil.trim(propConfig.getString("outputFile")));
		config.setQuickCreateDataPath(StringUtil.trim(propConfig.getString("quickCreateDataPath")));
		
		return config;
	}
	
	public static void main(String[] args) throws Exception {
		ConfigLoader.load();
	}
	
}
