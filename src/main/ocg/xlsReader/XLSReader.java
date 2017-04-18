package ocg.xlsReader;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface XLSReader {
	public HSSFWorkbook getSampleCrf(String filename);
	
	public String getSectionLabel(HSSFWorkbook workbook);

	public String getGroupLabel(HSSFWorkbook workbook);
	
	public void close();
}