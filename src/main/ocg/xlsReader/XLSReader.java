package xlsReader;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface XLSReader {

	HSSFWorkbook newXLSReader(FileInputStream fileInputStream) throws IOException;

	String getSectionLabel(HSSFWorkbook workbook);

	String getGroupLabel(HSSFWorkbook workbook);

	void removeRow(HSSFSheet sheet);

}