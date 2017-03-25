package com.innvo.jasper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innvo.domain.Reportparameter;

/**
 * 
 * @author ali
 *
 */
public class ParsingService {

	
	/**
	 * 
	 * @param resourceAsText
	 * @return
	 * @throws Exception
	 */
	public  Document parseResource(String resourceAsText) throws Exception {
		Document document;
		try {
			document = DocumentHelper.parseText(resourceAsText);
		} catch (DocumentException e) {
			throw e;
		}
		return document;
	}

	/**
	 * 
	 * @param reportResponse
	 * @return
	 * @throws Exception
	 */
	public String parseReport(String reportResponse,String serverUrl) throws Exception {
		String urlReport = null;
		try {
			Document document = DocumentHelper.parseText(reportResponse);
			Node node = document.selectSingleNode("/report/uuid");
			String uuid = node.getText();
			node = document.selectSingleNode("/report/totalPages");
			Integer totalPages = Integer.parseInt(node.getText());
			if (totalPages == 0) {
			//	throw new Exception("Error generando reporte");
			}
			urlReport = serverUrl+ "rest/report/" + uuid + "?file=report";
		} catch (DocumentException e) {
			throw e;
		}
		return urlReport;
	}


	/**
	 * 
	 * @param aEncodingScheme
	 * @throws IOException
	 * @throws Exception
	 */
	public String serializetoXML(Document resource) throws Exception {
		OutputFormat outformat = OutputFormat.createCompactFormat();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		outformat.setEncoding("ISO-8859-1");

		try {
			XMLWriter writer = new XMLWriter(out, outformat);
			writer.write(resource);
			writer.flush();
		} catch (IOException e) {
			throw e;
		}
		return out.toString();
	}
	
/**
 * 
 * @param resource
 * @param reportparameter
 * @return
 * @throws IOException 
 * @throws JsonMappingException 
 * @throws JsonParseException 
 */
	
	public Document addParametersToResource(Document resource,Map<String, String> params) throws JsonParseException, JsonMappingException, IOException {

		Element root = resource.getRootElement();
		
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key != null && value != null) {
				root.addElement("parameter").addAttribute("name", key).addText(value);
			}
		}
		return resource;
	}
}
