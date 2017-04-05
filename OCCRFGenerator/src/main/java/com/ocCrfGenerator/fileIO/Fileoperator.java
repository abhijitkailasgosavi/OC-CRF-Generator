package com.ocCrfGenerator.fileIO;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.ocCrfGenerator.details.CrfRow;

import au.com.bytecode.opencsv.CSVReader;

public class Fileoperator {

	public void fileReadWrite(String inputFileName) throws Exception {
		CSVReader csvReader = null;
		FileOutputStream out =null;
		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		FileInputStream inputExcelFile = null;
		int i=0;

		try {  
			Long totalRows = getNoOfRows(inputFileName);
			int currentRowCount = 0;
			System.out.println("number of rows "+totalRows);

			int rowIndex = 0;
			Row row = null;
			String[] currentOcCrfRow = null;
			String itemName=null,label=null, responseType=null, responseOptionText=null, dataType=null;
			csvReader = new CSVReader(new FileReader(inputFileName),',' , '"',0);

			while(currentRowCount <= totalRows )
			{
				currentOcCrfRow = csvReader.readNext();
				CrfRow crfRow = createObject(currentOcCrfRow);
				currentRowCount++;
				if (crfRow.getType().equals("CRF")) {
					if(workbook!=null) {
						workbook.write(out);
					}
					String filename =crfRow.getTitle()+".xls";
					filename = filename.replaceAll("/", "\\\\");	
					System.out.println(filename + " "+currentRowCount);

					inputExcelFile = new FileInputStream("sampleCRF/mySampleCRF.xls");
					out = new FileOutputStream("files/"+filename);
					workbook = new HSSFWorkbook(inputExcelFile);
					sheet = workbook.getSheet("Items");
					rowIndex = 0;
					i=0;
				} else if (crfRow.getType().equals("Q")) {

					row = sheet.createRow(++rowIndex);
					
					itemName = crfRow.getTitle();
					label = crfRow.getLabel();
					label = label.replaceAll(" ", "_");
					label = label.replaceAll("[\\.\\:\\/]", "");
					responseType = getResponseType(crfRow.getQuestionType());
					dataType = getDataType(crfRow.getQuestionType());
					responseOptionText = "";
					
					row.createCell(0).setCellValue(label);
					row.createCell(1).setCellValue(itemName);
					row.createCell(2).setCellValue(itemName);
					row.createCell(5).setCellValue(getSectionLabel(workbook));
					row.createCell(6).setCellValue(getGroupLabel(workbook));
					row.createCell(13).setCellValue(responseType);
					row.createCell(14).setCellValue("A"+i++);
					row.createCell(15).setCellValue(responseOptionText);
					row.createCell(16).setCellValue(responseOptionText);
					row.createCell(19).setCellValue(dataType);
				} else if (crfRow.getType().equals("A")) {
					responseOptionText += getResponseText(crfRow, responseOptionText);
					
					Cell cellResponseText =row.getCell(15);
					Cell cellResponseValue =row.getCell(16);
					cellResponseText.setCellValue("");
					cellResponseValue.setCellValue("");
					cellResponseText.setCellValue(responseOptionText);
					cellResponseValue.setCellValue(responseOptionText);
				}

				if (currentRowCount == totalRows+1) {
					workbook.write(out);
				}

			}	
			out.close();
			System.out.println("Task completed");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			csvReader.close();
		}
	}

	private String getSectionLabel(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet("Sections");
		Row row = sheet.getRow(1);
		return row.getCell(0).getStringCellValue();
	}


	private String getGroupLabel(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet("Groups");
		Row row = sheet.getRow(1);
		return row.getCell(0).getStringCellValue();
	}

	private String getResponseText(CrfRow crfRow, String responseOptionText) {
		String title = crfRow.getTitle();
		String newResponseText = "";
		if (responseOptionText=="") {

			newResponseText = title;
			return newResponseText;
		}
		newResponseText +=",";
		newResponseText +=title;
		return newResponseText;
	}

	private String getDataType(String questionType) {
		if (questionType.equals("Select one")) {
			return "ST";
		} else if (questionType.equals("Number")) {
			return "INT";
		} else if (questionType.equals("Text")) {
			return "ST";
		} else if (questionType.equals("Yes/No")) {
			return "ST";
		} else if (questionType.equals("Date")) {
			return "DATE";
		} else if (questionType.equals("Select many")) {
			return "ST";
		} 
		return "ST";
	}

	private String getResponseType(String questionType) {
		if (questionType.equals("Select one")) {
			return "single-select";
		} else if (questionType.equals("Number")) {
			return "text";
		} else if (questionType.equals("Text")) {
			return "text";
		} else if (questionType.equals("Yes/No")) {
			return "radio";
		} else if (questionType.equals("Date")) {
			return "text";
		} else if (questionType.equals("Select many")) {
			return "multi-select";
		} 
		return "text";
	}

	private CrfRow  createObject(String[] row) {
		CrfRow crfRow = new CrfRow();

		crfRow.setParentId(row[0]);
		crfRow.setParentType(row[1]);
		crfRow.setStudyId(row[2]);
		crfRow.setSiteId(row[3]);
		crfRow.setCrfId(row[4]);
		crfRow.setQuestionId(row[5]);
		crfRow.setAnswerId(row[6]);
		crfRow.setType(row[7]);
		crfRow.setTitle(row[8]);
		crfRow.setLabel(row[9]);
		crfRow.setQuestionType(row[10]);
		crfRow.setQuestionMandatory(row[11]);
		crfRow.setQuestiondefault(row[12]);

		return crfRow;
	}

	private Long getNoOfRows(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			Long count = (long) 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count-1;
		} finally {
			is.close();
		}
	}
}
