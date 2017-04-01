package com.test.csv_assignment.test;

import java.util.List;

import com.test.csv_assignment.details.SampleCRF;
import com.test.csv_assignment.fileIO.CsvOpeartor;
import com.test.csv_assignment.fileIO.ExcelOperator;

public class App 
{
  
	public static void main(String[] args) throws Exception
    {
    
    		CsvOpeartor csvOperator =new CsvOpeartor();
            ExcelOperator excelOperator = new ExcelOperator();
			
            List<SampleCRF> list = csvOperator.csvFileRead("/home/neha/eclipse/sampleTestCRF.csv") ;
            
            excelOperator.excelFilewriter("sampletestCRF.xls", list);  
    }

	
}
