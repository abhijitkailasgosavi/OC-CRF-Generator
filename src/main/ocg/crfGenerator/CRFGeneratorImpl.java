package crfGenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import au.com.bytecode.opencsv.CSVReader;
import details.CrfRow;

public class CRFGeneratorImpl implements CRFGenerator {

	public static final String DEF_CRF_TEMPLATE_FILE = "sampleCRF/mySampleCRF.xls";

	public enum DataType { Select_one("ST","single-select"),
		Number("INT","text"), 
		Text("ST","text"),
		Yes_No("ST","radio"),
		Date("DATE","text"),
		Select_many("ST","multi-select"),;

		private final String outputDataType;

		private final String outputResponseType;

		private DataType(String outputDataType,String outputResponseType) {
			this.outputDataType = outputDataType;  
			this.outputResponseType = outputResponseType;
		}
		public String getOutputDataType() {
			return outputDataType;
		}

		public String getOutputResponseType() {
			return outputResponseType;
		}
	} 

	public void fileReadWrite(String inputFileName) throws Exception {
		CSVReader csvReader = null;
		FileOutputStream out =null;
		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		FileInputStream inputExcelFile = null;
		int i=0,rowIndex = 0,crfCount=0;
		Row row = null;
		String[] currentCrfRow = null;
		String responseOptionText=null;

		try {  
			csvReader = new CSVReader(new FileReader(inputFileName));
			inputExcelFile = new FileInputStream(DEF_CRF_TEMPLATE_FILE);
			workbook = new HSSFWorkbook(inputExcelFile);

			while ((currentCrfRow = csvReader.readNext()) != null )
			{
				CrfRow crfRow = createObject(currentCrfRow);
				if (crfRow.getType().equals("CRF")) {
					if(workbook != null && crfCount != 0) {
						workbook.write(out);
					}
					String filename = crfRow.getTitle()+".xls";
					filename = filename.replaceAll("/", "\\\\");	
					System.out.println(filename + " is created");

					out = new FileOutputStream("files/"+filename);
					sheet = workbook.getSheet("Items");
					removeRow(sheet);
					setCrfName( workbook.getSheet("CRF"),crfRow.getTitle());
					rowIndex = 0;
					i=0;
					crfCount++;
				} else if (crfRow.getType().equals("Q")) {
					row = sheet.createRow(++rowIndex);
					String label = generateFormatedString(crfRow.getLabel());
					responseOptionText = "";

					row.createCell(0).setCellValue(label);
					row.createCell(1).setCellValue(crfRow.getTitle());
					row.createCell(2).setCellValue(crfRow.getTitle());
					row.createCell(5).setCellValue(getSectionLabel(workbook));
					row.createCell(6).setCellValue(getGroupLabel(workbook));
					row.createCell(13).setCellValue(getResponseType(crfRow.getQuestionType()));
					row.createCell(14).setCellValue("A" + i++);
					row.createCell(15).setCellValue(responseOptionText);
					row.createCell(16).setCellValue(responseOptionText);
					row.createCell(19).setCellValue(getDataType(crfRow.getQuestionType()));
				} else if (crfRow.getType().equals("A")) {
					responseOptionText += getResponseText(crfRow, responseOptionText);

					Cell cellResponseText =row.getCell(15);
					Cell cellResponseValue =row.getCell(16);
					cellResponseText.setCellValue("");
					cellResponseValue.setCellValue("");
					cellResponseText.setCellValue(responseOptionText);
					cellResponseValue.setCellValue(responseOptionText);
				}
			}
			workbook.write(out);
			System.out.println("Task completed");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			inputExcelFile.close();
			out.close();
			csvReader.close();
		}
	}

	public  void removeRow(HSSFSheet sheet) {
		int lastRowNum = sheet.getLastRowNum();
		for (int i = 1; i<= lastRowNum; i++) {
			sheet.removeRow(sheet.getRow(i));
		}
	}

	private String generateFormatedString(String label) {
		label = label.replaceAll("[^a-zA-Z0-9]"," ");
		label = label.replaceAll(" ", "_");
		return label;
	}

	private void setCrfName(HSSFSheet sheet, String crfName) {
		Row row = sheet.createRow(1);
		row.createCell(0).setCellValue(crfName);
		row.createCell(1).setCellValue("1");
		row.createCell(3).setCellValue(crfName);
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


	public String getDataType(String questionType) {
		questionType = generateFormatedString(questionType);
		for(DataType dt : DataType.values()) {
			if(questionType.equals(dt.toString())) {
				return dt.getOutputDataType();
			}
		}
		return "ST";
	}

	private String getResponseType(String questionType) {
		questionType = generateFormatedString(questionType);
		for(DataType dt : DataType.values()) {
			if(questionType.equals(dt.toString())) {
				return dt.getOutputResponseType();
			}
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
}
