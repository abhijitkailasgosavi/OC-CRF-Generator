package ocg.fileIoUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;

import ocg.crfGenerator.CRFGeneratorImpl;

public class XLSReaderImpl implements XLSReader {
	HSSFWorkbook defaultXls;

	public XLSReaderImpl(String defaultInputFile) {
		InputStream inputExcelFile = null;
		try {
			inputExcelFile = new FileInputStream(defaultInputFile);
			defaultXls = new HSSFWorkbook(inputExcelFile);
		} catch (IOException e) {
			IOUtils.closeQuietly(defaultXls);
			CRFGeneratorImpl.logger.error("Error in sample excel reader :" + e.getMessage());
		} finally {
			IOUtils.closeQuietly(inputExcelFile);
		}
	}

	public HSSFWorkbook getSampleCrf(String filename) {
		HSSFWorkbook emptycrf = null;
		try {
			createSampleCrf(filename);
			InputStream inputExcelFile = new FileInputStream(filename);
			emptycrf = new HSSFWorkbook(inputExcelFile);
		} catch (Exception e) {
			IOUtils.closeQuietly(emptycrf);
			CRFGeneratorImpl.logger.error("Error in get sample crf :" + e.getMessage());
		} 
		return emptycrf;
	}

	public String getSectionLabel(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet("Sections");
		Row row = sheet.getRow(1);
		return row.getCell(0).getStringCellValue();
	}

	public String getGroupLabel(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet("Groups");
		Row row = sheet.getRow(1);
		return row.getCell(0).getStringCellValue();
	}

	private void createSampleCrf(String filename) {
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(filename);
			defaultXls.write(fout);
		} catch (Exception e) {
			CRFGeneratorImpl.logger.error("Error in create sample crf :" + e.getMessage());
		} finally {
			IOUtils.closeQuietly(fout);
		}
	}
}
