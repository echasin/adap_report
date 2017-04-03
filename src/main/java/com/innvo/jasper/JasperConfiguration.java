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
	
	private String jrxmlpath;
	
	private String reportpath;
	
	private String reportingengine;
	
	private String accessKey;
	
	private String secretAcessKey;
	
	
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

	public String getJrxmlpath() {
		return jrxmlpath;
	}

	public void setJrxmlpath(String jrxmlpath) {
		this.jrxmlpath = jrxmlpath;
	}

	public String getReportpath() {
		return reportpath;
	}

	public void setReportpath(String reportpath) {
		this.reportpath = reportpath;
	}

	public String getReportingengine() {
		return reportingengine;
	}

	public void setReportingengine(String reportingengine) {
		this.reportingengine = reportingengine;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretAcessKey() {
		return secretAcessKey;
	}

	public void setSecretAcessKey(String secretAcessKey) {
		this.secretAcessKey = secretAcessKey;
	}
    
}
