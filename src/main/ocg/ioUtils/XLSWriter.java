package ocg.ioUtils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ocg.ioUtils.CsvReader;

public interface XLSWriter {
	public void addCRFDetails(String crfName);

	public void addItem(XLSReader xlsReader, CsvReader csvReader, Integer itemCount);

	public void addResponseTextAndValue(String responseText);

	public void crfWriter(HSSFWorkbook samplecrf);
	
	public void close();
}