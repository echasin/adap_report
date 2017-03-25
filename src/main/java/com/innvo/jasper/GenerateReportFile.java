package com.innvo.jasper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innvo.domain.Report;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
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
	
	
	public byte[] generateReportRestClient(Report report,String params) throws Exception{
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
	ObjectMapper mapper = new ObjectMapper();
    List<Parameters> objects = mapper.readValue(params, new TypeReference<List<Parameters>>(){});
    System.out.println(params);
    System.out.println(objects);
    Map<String,String> map=new HashMap<String,String>();
    for(Parameters param:objects){
    	map.put(param.getKey(), param.getValue());
    }
    System.out.println(map);
	Document resourceXML = parsingService.parseResource(resourceResponse);
    resourceXML = parsingService.addParametersToResource(resourceXML,map);
	resource = client.resource(jasperConfiguration.getServerUrl() + generateReportPath);
	resource.accept(MediaType.TEXT_XML);
	String reportResponse = resource.put(String.class, parsingService.serializetoXML(resourceXML));
	String urlReport = parsingService.parseReport(reportResponse,jasperConfiguration.getServerUrl());
	resource = client.resource(urlReport);
	File remoteFile = resource.get(File.class);

	FileInputStream fileInputStream=null;
    byte[] bFile = new byte[(int) remoteFile.length()];
    fileInputStream = new FileInputStream(remoteFile);
    fileInputStream.read(bFile);
    fileInputStream.close();
    
    FileOutputStream fileOuputStream = new FileOutputStream(jasperConfiguration.getReportpath()+report.getReporttemplatename()+"."+report.getReportoutputtypecode());
    fileOuputStream.write(bFile);
    fileOuputStream.close();

	
	return bFile;	
	}
	
	/**
	 * 
	 * @throws JRException
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public void generateReportEngine(Report report,String params) throws JRException, JsonParseException, JsonMappingException, IOException{
		DBConnection connection=new DBConnection();
				
		JasperReport jasperReport = JasperCompileManager
	               .compileReport(jasperConfiguration.getJrxmlpath()+report.getReporttemplatename()+".jrxml");
	       ObjectMapper mapper = new ObjectMapper();
   	       List<Parameters> objects = mapper.readValue(params, new TypeReference<List<Parameters>>(){});
           Map<String,Object> parametersMap=new HashMap<String,Object>();
           for(Parameters param:objects){
        	   parametersMap.put(param.getKey(), param.getValue());
           }
   	    
	       JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	               parametersMap, connection.getConnection());

	       JasperExportManager.exportReportToPdfFile(jasperPrint,
	               jasperConfiguration.getReportpath()+report.getReporttemplatename()+"."+report.getReportoutputtypecode());
	        
	}
	
	
	public void generateReport(Report report,String parameters) throws Exception{
		if(jasperConfiguration.getReportingengine().equals("jasperengine")){
			generateReportEngine(report, parameters);
			System.out.println("jasper engine");
		}else if(jasperConfiguration.getReportingengine().equals("jasperserver")){
			generateReportRestClient(report, parameters);
			System.out.println("jasper server");
		}
	}
	
}
