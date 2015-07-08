package com.revimedia.log.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Configuration {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static Configuration mInstance;
	
	private Properties mProperties = new Properties();

	private String mConfigName = "app.config";
	
	public static Configuration getInstance() {
		if(mInstance == null) {
			mInstance = new Configuration();
		}
		
		return mInstance;
	}
	
	public Configuration() {
		LOGGER.config("Reading configuration from " + mConfigName);
		try(
			InputStream is = getClass().getClassLoader().getResourceAsStream(mConfigName)
		) {
			if(is != null) {
				try {
					mProperties.load(is);
				} catch (IOException e) {
					LOGGER.warning("Config file load error:\n" + e.getMessage());
				}
			} else {
				LOGGER.warning("Config file couldn\'t be found");
			}
		} catch (IOException e1) {
			LOGGER.warning("Config file stream close error:\n" + e1.getMessage());
		}
	}
	
	public String getProperty(String key) {
		return mProperties.getProperty(key, "");
	}

	public int getPropertyAsInt(String key, int defaultValue) {
		
		String intKeyValue = mProperties.getProperty(key, "");
		if(intKeyValue.isEmpty()) return defaultValue;

		return Integer.parseInt(intKeyValue);
	}
}
