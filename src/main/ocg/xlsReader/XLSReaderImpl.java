package xlsReader;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

public class XLSReaderImpl implements XLSReader {

	HSSFWorkbook xlsReader = null;

	public HSSFWorkbook newXLSReader(FileInputStream fileInputStream) throws IOException {
		xlsReader = new HSSFWorkbook(fileInputStream);
		return xlsReader;
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

	public  void removeRow(HSSFSheet sheet) {
		int lastRowNum = sheet.getLastRowNum();
		for (int i = 1; i<= lastRowNum; i++) {
			sheet.removeRow(sheet.getRow(i));
		}
	}
}
