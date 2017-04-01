package com.ocCrfGenerator.mainApp;

import java.util.List;

import com.ocCrfGenerator.details.OcCrf;
import com.ocCrfGenerator.fileIO.CsvOpeartor;
import com.ocCrfGenerator.fileIO.ExcelOperator;

public class App 
{
	public static void main(String[] args) throws Exception {
		CsvOpeartor csvOperator =new CsvOpeartor();
		ExcelOperator excelOperator = new ExcelOperator();

		List<OcCrf> list = csvOperator.csvFileRead("/home/neha/eclipse/sampleTestCRF.csv") ;
		excelOperator.excelFilewriter("newCRF.xls", list);  
	}

}
