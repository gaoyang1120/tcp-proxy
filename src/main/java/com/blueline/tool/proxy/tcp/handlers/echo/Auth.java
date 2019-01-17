package com.blueline.tool.proxy.tcp.handlers.echo;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class Auth {
private static SSLContext sslContext;
	
	public static SSLContext getSSLContext() throws Exception{
		Properties p = Configuration.getConfig();
		String protocol = p.getProperty("protocol");
		String sCertificateFile = p.getProperty("serverCertificateFile");
		String sCertificatePwd = p.getProperty("serverCertificatePwd");
		String sMainPwd = p.getProperty("serverMainPwd");
		String cCertificateFile = p.getProperty("clientCertificateFile");
		String cCertificatePwd = p.getProperty("clientCertificatePwd");
		String cMainPwd = p.getProperty("clientMainPwd");
			
		//KeyStore class is used to save certificate.
		char[] c_pwd = sCertificatePwd.toCharArray();
		KeyStore keyStore = KeyStore.getInstance("JKS");  
		keyStore.load(new FileInputStream(sCertificateFile), c_pwd);  
		
		//KeyManagerFactory class is used to create KeyManager class.
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509"); 
		char[] m_pwd = sMainPwd.toCharArray();
		keyManagerFactory.init(keyStore, m_pwd); 
		//KeyManager class is used to choose a certificate 
		//to prove the identity of the server side. 
		KeyManager[] kms = keyManagerFactory.getKeyManagers();
		
		TrustManager[] tms = null;
		if(Configuration.getConfig().getProperty("authority").equals("2")){
			//KeyStore class is used to save certificate.
			c_pwd = cCertificatePwd.toCharArray();
			keyStore = KeyStore.getInstance("JKS");  
			keyStore.load(new FileInputStream(cCertificateFile), c_pwd);  
			
			//TrustManagerFactory class is used to create TrustManager class.
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509"); 
			m_pwd = cMainPwd.toCharArray();
			trustManagerFactory.init(keyStore); 
			//TrustManager class is used to decide weather to trust the certificate 
			//or not. 
			tms = trustManagerFactory.getTrustManagers();
		}
		
		//SSLContext class is used to set all the properties about secure communication.
		//Such as protocol type and so on.
		sslContext = SSLContext.getInstance(protocol);
		sslContext.init(kms, tms, null);  
		
		return sslContext;
	}
}
