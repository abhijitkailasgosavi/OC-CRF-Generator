package ocg.ioUtils;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;

import ocg.crfGenerator.CRFGeneratorImpl;
import ocg.crfGenerator.QuestionType;

public class XLSWriterImpl implements XLSWriter {
	private FileOutputStream out = null;

	private HSSFWorkbook crf;

	private HSSFSheet items = null;

	private Row row;

	public XLSWriterImpl() {
	}

	public XLSWriterImpl(String filename, HSSFWorkbook crf) {
		try {
			out = new FileOutputStream(filename);
			this.crf = crf;
			items = this.crf.getSheet("Items");
		} catch (Exception e) {
			IOUtils.closeQuietly(out);
			CRFGeneratorImpl.logger.error("in creating excel writer :" + e.getMessage());
		}	
	}

	public void addCRFDetails(String crfName) {
		HSSFSheet sheet = crf.getSheet("CRF");
		Row row = sheet.createRow(1);
		row.createCell(0).setCellValue(crfName);
		row.createCell(1).setCellValue("1");
		row.createCell(3).setCellValue(crfName);
	}

	public void addItem(XLSReader xlsReader,CsvReader csvReader, Integer itemCount) {
		row = items.createRow(itemCount);
		String label =CRFGeneratorImpl.getItemName(csvReader.getColumnValue("Label"));
		CRFGeneratorImpl.logger.info("Question row is created with title" + label);
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

	public void addResponseTextAndValue(String responseText) {
		Cell cellResponseText = row.getCell(15);
		Cell cellResponseValue = row.getCell(16);
		cellResponseText.setCellValue("");
		cellResponseValue.setCellValue("");
		cellResponseText.setCellValue(responseText);
		cellResponseValue.setCellValue(responseText);
	}

	public void crfWriter(HSSFWorkbook samplecrf) {
		if (samplecrf != null) {
			try {
				crf.write(out);
			} catch (IOException e) {
				CRFGeneratorImpl.logger.error("In crfWrite method while closing output file " + e.getMessage());
			}
		}
	}

	public void close() {
		IOUtils.closeQuietly(out);
	}
}
