package com.ex.file.manager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.ex.file.manager.MyStatus;
 
public class Worker<MyStatus> implements Callable<MyStatus> {

    protected static String catchCompany = null;
    protected static String[] header = null ;
    
    public static void setSourceFile(String sourceFile) {
		Worker.sourceFile = sourceFile;
	}

	public static void setOutFolder(String outFolder) {
		OutFolder = outFolder;
	}

	public static String sourceFile="." ;
    public static String OutFolder =".";

	@Override
	public MyStatus call() throws Exception {
		com.ex.file.manager.MyStatus s = new com.ex.file.manager.MyStatus();
		try {
		Date dateStart = new Date ();
		Reader in = new FileReader(sourceFile);
		CSVFormat formatWithHeader = CSVFormat.RFC4180.withFirstRecordAsHeader().withTrim() ;
		Iterable<CSVRecord> records = formatWithHeader.parse(in);

		CSVPrinter printer =  null;

		for (CSVRecord record : records) {

			if(header == null) {
				String headerStr = ( record.toString().split("mapping=")[1].split(", recordNumber")[0] );
				header = setHeader(headerStr);
				System.out.println(headerStr);	
				System.out.println(header);
			}

			if(!record.get(0).equalsIgnoreCase(catchCompany)) {
				if(printer!=null)printer.close(Boolean.TRUE);
				catchCompany = record.get(0);
				printer = new CSVPrinter(new FileWriter(OutFolder+"/"+catchCompany+".csv"), CSVFormat.EXCEL.withHeader(header));
			}			
			if(printer!=null) printer.printRecord(record); 
		}
		if(printer!=null)printer.close(Boolean.TRUE);

		Date dateEnd = new Date ();
		Double diff = (double) (dateEnd.getTime() - dateStart.getTime());
		Double diffSeconds = diff / 1000 % 60;
		Double diffMinutes = diff / (60 * 1000) % 60;
		Double diffHours = diff / (60 * 60 * 1000);

		s.setDiff(diff).setDiffSeconds(diffSeconds).setDiffMinutes(diffMinutes).setDiffHours(diffHours);

		} catch (Exception e) {
			s.setError(Boolean.TRUE);
		}
		return   (MyStatus) s;
	}

	private String[] setHeader(String header) throws Exception {
		int i =0;String [] strArr =null;
		try {
			header = header.replace("{", "").replace("}", "");
	          List<String> dataList = Arrays.asList( header.split(","));
	          strArr = new String[dataList.size()];
	         for (String data : dataList) {
	        	 strArr[i++]  = data.split("=")[0].trim();
			}
		} catch (Exception e) {
			 throw e;
		}
		return  strArr;
	}
}
