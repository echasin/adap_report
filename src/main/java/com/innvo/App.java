package com.innvo;

import com.innvo.domain.Report;
import com.innvo.jasper.GenerateReportFile;

public class App {

  public static void main(String arg[]) throws Exception{
	   GenerateReportFile generateReportFile=new GenerateReportFile();
	    Report report = new Report();
     System.out.println("1111111111111111111111111111111111111111111111");
     System.out.println("************************************************");
	//   report.setReporttemplatename("testreport_1");
     //  report.setReportoutputtypecode("PDF");
       generateReportFile.generateReport(report);
  }
}
