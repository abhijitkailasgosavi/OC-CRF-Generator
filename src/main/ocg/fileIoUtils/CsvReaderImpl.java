package ocg.fileIoUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.util.IOUtils;

import au.com.bytecode.opencsv.CSVReader;
import ocg.crfGenerator.CRFGeneratorImpl;

public class CsvReaderImpl implements CsvReader {
	private CSVReader csvReader;

	private Map<String, Integer> columnNameIdxMap = new HashMap<String, Integer>();

	private String[] currentRow;

	private long currentRowCount = 0;

	public CsvReaderImpl(String inputCsv) {
		try {
			csvReader = new CSVReader(new FileReader(inputCsv), '\t');
			createColumnNameIdxMap();
		} catch (FileNotFoundException e) {
			IOUtils.closeQuietly(csvReader);
			CRFGeneratorImpl.logger.error("Error in csv reader " + e.getMessage());
		}
	}

	public boolean hasNextRow() {
		try {
			currentRow = csvReader.readNext();
			currentRowCount++;
		} catch (IOException e) {
			IOUtils.closeQuietly(csvReader);
			CRFGeneratorImpl.logger.error("Error In reading row in csv file:" + e.getMessage());
		}
		return currentRow != null;
	}

	public String getColumnValue(String columnHeader) {
		int columnIndex = columnNameIdxMap.get(columnHeader);
		return currentRow[columnIndex];
	}

	public void close() {
		IOUtils.closeQuietly(csvReader);
	}

	public long getCurrentRowCount() {
		return currentRowCount;
	}

	private void createColumnNameIdxMap() {
		if (hasNextRow()) {
			for (int columnIndex = 0; columnIndex < currentRow.length; columnIndex++) {
				columnNameIdxMap.put(currentRow[columnIndex], columnIndex);
			}
		}
	}
}