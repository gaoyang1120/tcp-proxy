package com.blueline.tool.proxy.tcp.handlers.echo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	private static Properties config;
	Logger logger = LoggerFactory.getLogger(Configuration.class);

	public static Properties getConfig() {
		try {
			if (null == config) {
				File configFile = new File("./conf/conf.properties");
				if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
					InputStream input = new FileInputStream(configFile);
					config = new Properties();
					config.load(input);
				}
			}
		} catch (Exception e) {
			// default set
			config = new Properties();
			config.setProperty("protocol", "TLSV1");
			config.setProperty("serverCertificateFile", "./certificate/server_rsa.key");
			config.setProperty("serverCertificatePwd", "123456");
			config.setProperty("serverMainPwd", "654321");
			config.setProperty("clientCertificateFile", "./certificate/client_rsa.key");
			config.setProperty("clientCertificatePwd", "123456");
			config.setProperty("clientMainPwd", "654321");
			config.setProperty("serverListenPort", "10000");
			config.setProperty("serverThreadPoolSize", "5");
			config.setProperty("serverRequestQueueSize", "10");
			config.setProperty("socketStreamEncoding", "UTF-8");
		}
		return config;
	}
}
