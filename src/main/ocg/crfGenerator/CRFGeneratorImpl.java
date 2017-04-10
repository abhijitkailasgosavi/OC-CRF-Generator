package crfGenerator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import au.com.bytecode.opencsv.CSVReader;
import csvReader.CsvFileReader;
import csvReader.CsvFileReaderImpl;
import details.CrfRow;
import xlsReader.XLSReader;
import xlsReader.XLSReaderImpl;

public class CRFGeneratorImpl implements CRFGenerator {

	public static final String DEF_CRF_TEMPLATE_FILE = "sampleCRF/mySampleCRF.xls";
	public List<String> listOfLabels = new LinkedList<String>();

	public void generateCRF(String inputFileName) {
		CSVReader csvReader = null;
		FileOutputStream out =null;
		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		FileInputStream inputExcelFile = null;
		XLSReader xlsReader = new XLSReaderImpl();
		CsvFileReader csvFileReader = (CsvFileReader) new CsvFileReaderImpl();
		int rowIndex = 0, crfCount=0;
		int i = 0;
		Row row = null;
		String[] currentCrfRow = null;
		String responseOptionText = null;

		try {  
			csvReader = csvFileReader.getcsvFileReader(inputFileName);
			inputExcelFile = new FileInputStream(DEF_CRF_TEMPLATE_FILE);
			workbook = xlsReader.newXLSReader(inputExcelFile);
			while ((currentCrfRow = csvReader.readNext()) != null )
			{
				CrfRow crfRow = new CrfRow(currentCrfRow);
				if (crfRow.getType().equals("CRF")) {
					if(workbook != null && crfCount != 0) {
						workbook.write(out);
					}
					String filename = crfRow.getTitle() + ".xls";
					filename = filename.replaceAll("/", "\\\\");	
					System.out.println(filename + " is created");

					out = new FileOutputStream("files/"+filename);
					sheet = workbook.getSheet("Items");
					xlsReader.removeRow(sheet);
					listOfLabels.clear();
					setCrfName( workbook.getSheet("CRF"),crfRow.getTitle());
					rowIndex = 0;
					i=0;
					crfCount++;
				} else if (crfRow.getType().equals("Q")) {
					row = sheet.createRow(++rowIndex);
					responseOptionText = "";
					setQuestionRow(row,crfRow,xlsReader,workbook,i++);
				} else if (crfRow.getType().equals("A")) {
					responseOptionText += getResponseText(crfRow, responseOptionText);
					setResponseTextAndValue(row,responseOptionText);
				}
			}
			workbook.write(out);
			System.out.println("Task completed");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputExcelFile.close();
				out.close();
				csvReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void setQuestionRow(Row row, CrfRow crfRow, XLSReader xlsReader, HSSFWorkbook workbook, int i){
		String label = CsvFileReaderImpl.generateFormatedString(crfRow.getLabel());
		CsvFileReaderImpl csvFileReaderImpl = new CsvFileReaderImpl();
		label = generateUniqueLabel(label);
		row.createCell(0).setCellValue(label);
		row.createCell(1).setCellValue(crfRow.getTitle());
		row.createCell(2).setCellValue(crfRow.getTitle());
		row.createCell(5).setCellValue(xlsReader.getSectionLabel(workbook));
		row.createCell(6).setCellValue(xlsReader.getGroupLabel(workbook));
		row.createCell(13).setCellValue(csvFileReaderImpl.getResponseType(crfRow.getQuestionType()));
		row.createCell(14).setCellValue("A" + i);
		row.createCell(15).setCellValue("");
		row.createCell(16).setCellValue("");
		row.createCell(19).setCellValue(csvFileReaderImpl.getDataType(crfRow.getQuestionType()));
	}

	private String generateUniqueLabel(String label) {
		int i = 0;

		if (listOfLabels.isEmpty()) {
			listOfLabels.add(label);
			return label;
		}
		while(listOfLabels.contains(label)) {
			label += i++;
		}

		listOfLabels.add(label);
		return label;
	}

	private void setResponseTextAndValue(Row row, String responseOptionText) {
		Cell cellResponseText =row.getCell(15);
		Cell cellResponseValue =row.getCell(16);
		cellResponseText.setCellValue("");
		cellResponseValue.setCellValue("");
		cellResponseText.setCellValue(responseOptionText);
		cellResponseValue.setCellValue(responseOptionText);

	}

	private void setCrfName(HSSFSheet sheet, String crfName) {
		Row row = sheet.createRow(1);
		row.createCell(0).setCellValue(crfName);
		row.createCell(1).setCellValue("1");
		row.createCell(3).setCellValue(crfName);
	}

	private String getResponseText(CrfRow crfRow, String responseOptionText) {
		String title = crfRow.getTitle();
		String newResponseText = "";
		if (responseOptionText == "") {
			newResponseText = title;
			return newResponseText;
		}
		newResponseText += ",";
		newResponseText += title;
		return newResponseText;
	}
}