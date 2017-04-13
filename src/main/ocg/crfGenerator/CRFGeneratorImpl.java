package ocg.crfGenerator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;

import ocg.csvReader.CsvReaderImpl;
import ocg.xlsReader.XLSReader;
import ocg.xlsReader.XLSReaderImpl;

public class CRFGeneratorImpl implements CRFGenerator {
	public static final String DEF_CRF_TEMPLATE_FILE = "sampleCRF/mySampleCRF.xls";

	public static Logger logger = Logger.getLogger(CRFGeneratorImpl.class.getName());

	public List<String> listOfLabels = new LinkedList<String>();

	private CsvReaderImpl csvReaderImpl;
	
	public void generateCRF(String inputCsv) {
		PropertyConfigurator.configure("log4j.properties");
		FileOutputStream out = null;
		HSSFSheet sheet = null;
		XLSReader xlsReader = new XLSReaderImpl();
		csvReaderImpl = new CsvReaderImpl(inputCsv);
		int rowIndex = 0, crfCount = 0;
		int labelCount = 0;
		Row row = null;

		try {
			FileInputStream inputExcelFile = new FileInputStream(DEF_CRF_TEMPLATE_FILE);
			HSSFWorkbook workbook = xlsReader.newXLSReader(inputExcelFile);
			String responseOptionText = null;
			while (csvReaderImpl.hasNextRow()) {
				if (csvReaderImpl.getColumnValue("Type").equals("CRF")) {
					if (workbook != null && crfCount != 0) {
						workbook.write(out);
					}
					String filename = csvReaderImpl.getColumnValue("Title") + ".xls";
					filename = filename.replaceAll("/", "\\\\");
					out = new FileOutputStream("files/" + filename);
					logger.info(filename + " CRF is created");

					sheet = workbook.getSheet("Items");
					xlsReader.removeRow(sheet);
					listOfLabels.clear();
					setCrfName(workbook.getSheet("CRF"), csvReaderImpl.getColumnValue("Title"));
					rowIndex = 0;
					labelCount = 0;
					crfCount++;
				} else if (csvReaderImpl.getColumnValue("Type").equals("Q")) {
					row = sheet.createRow(++rowIndex);
					responseOptionText = "";
					setQuestionRow(row, xlsReader, workbook, labelCount++);
				} else if (csvReaderImpl.getColumnValue("Type").equals("A")) {
					responseOptionText += getResponseText(responseOptionText);
					setResponseTextAndValue(row, responseOptionText);
					logger.info("Response is created using answers : " + responseOptionText);
				}
			}
			workbook.write(out);
			logger.info("Task completed");
			inputExcelFile.close();
			out.close();
		} catch (Exception e) {
			IOUtils.closeQuietly(out);
			logger.error("in generateCRF method " + e.getClass());
		}
	}

	private void setQuestionRow(Row row, XLSReader xlsReader, HSSFWorkbook workbook, int labelCount) {
		String label = generateValidString(csvReaderImpl.getColumnValue("Label"));
		logger.info("Question row is created with title" + label);
		label = generateUniqueLabel(label);
		row.createCell(0).setCellValue(label);
		row.createCell(1).setCellValue(csvReaderImpl.getColumnValue("Title"));
		row.createCell(2).setCellValue(csvReaderImpl.getColumnValue("Title"));
		row.createCell(5).setCellValue(xlsReader.getSectionLabel(workbook));
		row.createCell(6).setCellValue(xlsReader.getGroupLabel(workbook));
		row.createCell(13).setCellValue(getResponseType(csvReaderImpl.getColumnValue("Question Type")));
		row.createCell(14).setCellValue("A" + labelCount);
		row.createCell(15).setCellValue("");
		row.createCell(16).setCellValue("");
		row.createCell(19).setCellValue(getDataType(csvReaderImpl.getColumnValue("Question Type")));
	}

	private String generateUniqueLabel(String label) {
		int i = 1;

		if (listOfLabels.isEmpty()) {
			listOfLabels.add(label);
			return label;
		}
		while (listOfLabels.contains(label)) {
			label += i++;
		}
		listOfLabels.add(label);
		return label;
	}

	private void setResponseTextAndValue(Row row, String responseOptionText) {
		Cell cellResponseText = row.getCell(15);
		Cell cellResponseValue = row.getCell(16);
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

	private String getResponseText(String responseOptionText) {
		String title = csvReaderImpl.getColumnValue("Title");
		String newResponseText = "";
		if (responseOptionText == "") {
			newResponseText = title;
			return newResponseText;
		}
		newResponseText += ",";
		newResponseText += title;
		return newResponseText;
	}
	
	public enum DataType {
		Select_one("ST", "single-select"), Number("INT", "text"), Text("ST", "text"), Yes_No("ST",
				"radio"), Date("DATE", "text"), Select_many("ST", "multi-select"),;

		private final String outputDataType;

		private final String outputResponseType;

		private DataType(String outputDataType, String outputResponseType) {
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

	static public String generateValidString(String input) {
		input = input.replaceAll("[^a-zA-Z0-9]", " ");
		input = input.replaceAll(" ", "_");
		return input;
	}

	public String getDataType(String questionType) {
		questionType = generateValidString(questionType);
		for (DataType dt : DataType.values()) {
			if (questionType.equals(dt.toString())) {
				return dt.getOutputDataType();
			}
		}
		return "ST";
	}

	public String getResponseType(String questionType) {
		questionType = generateValidString(questionType);
		for (DataType dt : DataType.values()) {
			if (questionType.equals(dt.toString())) {
				return dt.getOutputResponseType();
			}
		}
		return "text";
	}
}