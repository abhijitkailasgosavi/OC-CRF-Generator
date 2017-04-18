package ocg.crfGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import ocg.enumQueType.QuestionType;

public class CRFGeneratorImpl implements CRFGenerator {
	public static final String DEF_CRF_TEMPLATE_FILE = "sampleCRF/mySampleCRF.xls";

	public static Logger logger = Logger.getLogger(CRFGeneratorImpl.class.getName());
	static {
		PropertyConfigurator.configure("log4j.properties");
	}

	private CsvReaderImpl csvReader;

	private List<String> responses = new ArrayList<String>();

	public void generateCRF(String inputCsv) {
		FileOutputStream out = null;
		HSSFSheet items = null;
		XLSReader xlsReader = new XLSReaderImpl(DEF_CRF_TEMPLATE_FILE);
		csvReader = new CsvReaderImpl(inputCsv);
		int itemCount = 0;
		Row row = null;
		try {
			HSSFWorkbook crf = null;
			while (csvReader.hasNextRow()) {
				if (csvReader.getColumnValue("Type").equals("CRF")) {
					crfWriter(crf, out);
					String filename = getCRFName();
					String filepath = createCrfDir();
					crf = xlsReader.getSampleCrf(filepath + filename);
					out = new FileOutputStream(filepath + filename);
					logger.info(filename + " CRF is created");
					addCRFDetails(crf.getSheet("CRF"));
					items = crf.getSheet("Items");
					itemCount = 0;
				} else if (csvReader.getColumnValue("Type").equals("Q")) {
					row = items.createRow(++itemCount);
					addItem(row, xlsReader, crf, itemCount);
					responses.clear();
				} else if (csvReader.getColumnValue("Type").equals("A")) {
					getResponses();
					addResponseTextAndValue(row, String.join(",", responses));
					logger.info("Response is created using answers : " + String.join(",", responses));
				}
			}
			crfWriter(crf, out);
			logger.info("Task completed");
		} catch (Exception e) {
			IOUtils.closeQuietly(out);
			logger.error("In generateCRF method " + e.getClass());
		} finally {
			try {
				out.close();
				csvReader.close();
				xlsReader.close();
			} catch (IOException e) {
				logger.error("In generateCRF method while closing file " + e.getClass());
			}
		}
	}

	private void addItem(Row row, XLSReader xlsReader, HSSFWorkbook crf, int itemCount) {
		String label = getItemName(csvReader.getColumnValue("Label"));
		logger.info("Question row is created with title" + label);
		row.createCell(0).setCellValue(label+"_"+itemCount);
		row.createCell(1).setCellValue(csvReader.getColumnValue("Title"));
		row.createCell(2).setCellValue(csvReader.getColumnValue("Title"));
		row.createCell(5).setCellValue(xlsReader.getSectionLabel(crf));
		row.createCell(6).setCellValue(xlsReader.getGroupLabel(crf));
		row.createCell(13).setCellValue(QuestionType.getResponseType(csvReader.getColumnValue("Question Type")));
		row.createCell(14).setCellValue("A" + itemCount);
		//create empty cells to write answers to response text & value
		row.createCell(15).setCellValue("");        
		row.createCell(16).setCellValue("");
		row.createCell(19).setCellValue(QuestionType.getDataType(csvReader.getColumnValue("Question Type")));
	}

	private void addResponseTextAndValue(Row row, String responseText) {
		Cell cellResponseText = row.getCell(15);
		Cell cellResponseValue = row.getCell(16);
		cellResponseText.setCellValue("");
		cellResponseValue.setCellValue("");
		cellResponseText.setCellValue(responseText);
		cellResponseValue.setCellValue(responseText);
	}

	private void addCRFDetails(HSSFSheet sheet) {
		String crfName = csvReader.getColumnValue("Title");
		Row row = sheet.createRow(1);
		row.createCell(0).setCellValue(crfName);
		row.createCell(1).setCellValue("1");
		row.createCell(3).setCellValue(crfName);
	}

	private void crfWriter(HSSFWorkbook crf,FileOutputStream out ) {
		if (crf != null) {
			try {
				crf.write(out);
			} catch (IOException e) {
				logger.error("In crfWrite method while closing output file " + e.getClass());
			}
		}
	}

	private void getResponses() {
		String response = csvReader.getColumnValue("Title");
		if (StringUtils.isBlank(response)) {
			return;
		}
		responses.add(response);
	}

	static public String getItemName(String input) {
		return input.replaceAll("[^a-zA-Z0-9]", "_");
	}

	public String createCrfDir() {
		String filePath = "../CRFs/";
		File crfDir = new File(filePath);
		if (!crfDir.exists()) {
			try {
				crfDir.mkdir();
			} catch(SecurityException se){
				logger.error("In generateCRF method while creating CRFs directory " + se.getClass());
			}
		}
		return filePath;
	}

	public String getCRFName() {
		String filename = csvReader.getColumnValue("Title") + ".xls";
		filename = filename.replaceAll("/", "\\\\");
		return filename; 
	}
}