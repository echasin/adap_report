package com.innvo.jasper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="jasper")
@Component
public class JasperConfiguration {
	
	
	private String serverUrl;
	
	private String serverUser;
    
	private String serverPassword;
	
	
    public String getServerUrl() {
		return serverUrl;
	}
	
    public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
    public String getServerUser() {
		return serverUser;
	}
	
    public void setServerUser(String serverUser) {
		this.serverUser = serverUser;
	}
	
    public String getServerPassword() {
		return serverPassword;
	}
	
    public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}
	
}
