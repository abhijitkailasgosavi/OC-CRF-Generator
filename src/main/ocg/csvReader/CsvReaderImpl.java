package ocg.csvReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.util.IOUtils;

import au.com.bytecode.opencsv.CSVReader;
import ocg.crfGenerator.CRFGeneratorImpl;

public class CsvReaderImpl implements CsvReader {

	private CSVReader csvReader;

	private int currentrowCount = 0;

	private Map<String, Integer> columnNameIdxMap = new HashMap<String, Integer>();;

	private String[] currentRow;

	public CsvReaderImpl(String inputCsv) {
		try {
			csvReader = new CSVReader(new FileReader(inputCsv));
			createColumnNameIdxMap();
		} catch (FileNotFoundException e) {
			IOUtils.closeQuietly(csvReader);
			CRFGeneratorImpl.logger.error("in csv reader " + e.getMessage());
		}
	}

	private void createColumnNameIdxMap() {
		try {
			if ((currentRow = csvReader.readNext()) != null) {
				for (int columnIndex = 0; columnIndex < currentRow.length; columnIndex++) {
					columnNameIdxMap.put(currentRow[columnIndex], columnIndex);
				}
				currentrowCount++;
			}
		} catch (IOException e) {
			CRFGeneratorImpl.logger.error("reading headers " + e.getClass());
		}
	}

	public boolean hasNextRow() {
		try {
			if ((currentRow = csvReader.readNext()) != null) {
				CRFGeneratorImpl.logger.info("Csv current row " + currentrowCount);
				currentrowCount++;
				return true;
			}
		} catch (IOException e) {
			CRFGeneratorImpl.logger.error("in reading "+ currentrowCount +" row in csv file:" + e.getClass());
		}
		return false;
	}

	public String getColumnValue(String columnHeader) {
		int columnIndex = columnNameIdxMap.get(columnHeader);
		return currentRow[columnIndex];
	}
}