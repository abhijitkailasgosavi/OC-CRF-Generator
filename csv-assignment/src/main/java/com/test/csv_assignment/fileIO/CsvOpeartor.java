package com.test.csv_assignment.fileIO;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.test.csv_assignment.details.SampleCRF;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

public class CsvOpeartor {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List csvFileRead(String fileName) throws IOException {
		CSVReader csvReader = null;
		
		try {  
		  csvReader = new CSVReader(new FileReader(fileName),',' , '"',1);
          CsvToBean csv = new CsvToBean();
		  List<SampleCRF> listparticipantsDetails = csv.parse(setColumMapping(), csvReader);
		  		  
		  for (SampleCRF sampleCRF :listparticipantsDetails ) {
			  System.out.println(sampleCRF);
		  }
		  
		  return listparticipantsDetails;
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			csvReader.close();
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static ColumnPositionMappingStrategy setColumMapping() {
		  ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
	      strategy.setType(SampleCRF.class);
	      String[] columns = new String[] {"parentId", "parentType", "studyId", "siteId", "crfId","questionId","answerId",
	    		                           "type","title","label","questionType","questionMandatory","questiondefault" }; 
	      strategy.setColumnMapping(columns);
	      
	      return strategy;
	}
}
