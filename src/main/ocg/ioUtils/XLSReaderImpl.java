package ocg.ioUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;

import ocg.crfGenerator.CRFGeneratorImpl;

public class XLSReaderImpl implements XLSReader {
	InputStream inputExcelFile;

	HSSFWorkbook xlsSample;

	public XLSReaderImpl(String sampleInputFile) {
		try {
			inputExcelFile = new FileInputStream(sampleInputFile);
			xlsSample = new HSSFWorkbook(inputExcelFile);
			inputExcelFile = (InputStream) POIFSFileSystem.createNonClosingInputStream(inputExcelFile);
		} catch (IOException e) {
			IOUtils.closeQuietly(xlsSample);
			IOUtils.closeQuietly(inputExcelFile);
			CRFGeneratorImpl.logger.error("in sample excel reader :" + e.getMessage());
		}
	}

	private void createSampleCrf(String filename) {
		FileOutputStream fout = null;
		try {
			File file = new File(filename);
			if (file.exists()) {
				file.delete();
			}
			fout = new FileOutputStream(file);
			HSSFWorkbook newexcel = xlsSample;
			newexcel.write(fout);
		} catch (Exception e) {
			CRFGeneratorImpl.logger.error("in create sample crf :" + e.getMessage());
		} finally {
			IOUtils.closeQuietly(fout);
		}
	}

	public HSSFWorkbook getSampleCrf(String filename) {
		HSSFWorkbook xlsSample = null;
		try {
			createSampleCrf(filename);
			InputStream inputExcelFile = new FileInputStream(filename);
			xlsSample = new HSSFWorkbook(inputExcelFile);
		} catch (Exception e) {
			IOUtils.closeQuietly(xlsSample);
			CRFGeneratorImpl.logger.error("in get sample crf :" + e.getMessage());
		} 
		return xlsSample;
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

	public void close() {
		IOUtils.closeQuietly(inputExcelFile);
	}
}