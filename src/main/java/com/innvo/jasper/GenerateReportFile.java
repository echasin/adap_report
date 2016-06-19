package com.innvo.jasper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.innvo.domain.Report;
import com.innvo.domain.Reportparameter;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
/**
 * 
 * @author ali
 *
 */
@Component
public class GenerateReportFile {
	
	ParsingService parsingService=new ParsingService();   
	
	@Autowired
	JasperConfiguration jasperConfiguration;
	
	
	public void generateReport(Report report,Map<String, String> params) throws Exception{
    ClientConfig clientConfig;
    Map<String, String> resourceCache=new HashMap<String, String>();
	clientConfig = new DefaultApacheHttpClientConfig();
	clientConfig.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
	clientConfig.getProperties().put(ApacheHttpClientConfig.PROPERTY_HANDLE_COOKIES, true);
    ApacheHttpClient client = ApacheHttpClient.create(clientConfig);
	client.addFilter(new HTTPBasicAuthFilter(jasperConfiguration.getServerUser(), jasperConfiguration.getServerPassword()));
	String describeResourcePath = "/rest/resource" + "/report/"+report.getReporttemplatename();
	String generateReportPath = "/rest/report" + "/report/"+report.getReporttemplatename() + "?RUN_OUTPUT_FORMAT"+report.getReportoutputtypecode() ;
	WebResource resource = null;
	String resourceResponse = null;
	if (resourceCache.containsKey(describeResourcePath)) {
		resourceResponse = resourceCache.get(describeResourcePath);
	} else {
		resource = client.resource(jasperConfiguration.getServerUrl());
		resource.accept(MediaType.APPLICATION_XML);
		resourceResponse = resource.path(describeResourcePath).get(String.class);
		resourceCache.put(describeResourcePath, resourceResponse);
	}
	Document resourceXML = parsingService.parseResource(resourceResponse);
    resourceXML = parsingService.addParametersToResource(resourceXML,params);
	resource = client.resource(jasperConfiguration.getServerUrl() + generateReportPath);
	resource.accept(MediaType.TEXT_XML);
	String reportResponse = resource.put(String.class, parsingService.serializetoXML(resourceXML));
	String urlReport = parsingService.parseReport(reportResponse,jasperConfiguration.getServerUrl());
	resource = client.resource(urlReport);
	File destFile = null;
	try {
		File remoteFile = resource.get(File.class);
		File parentDir = new File("/home/ali/report");
		destFile = File.createTempFile("report_", "." + report.getReportoutputtypecode(), parentDir);
		FileUtils.copyFile(remoteFile, destFile);
	} catch (IOException e) {
		throw e;
	}
}
	
	
}
