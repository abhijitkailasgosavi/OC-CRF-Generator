package ocg.ioUtils;

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

	private Map<String, Integer> columnNameIdxMap = new HashMap<String, Integer>();;

	private String[] currentRow;

	public CsvReaderImpl(String inputCsv) {
		try {
			csvReader = new CSVReader(new FileReader(inputCsv));
			createColumnNameIdxMap();
		} catch (FileNotFoundException e) {
			IOUtils.closeQuietly(csvReader);
			CRFGeneratorImpl.logger.error("In csv reader " + e.getMessage());
		}
	}

	private void createColumnNameIdxMap() {
		if (hasNextRow()) {
			for (int columnIndex = 0; columnIndex < currentRow.length; columnIndex++) {
				columnNameIdxMap.put(currentRow[columnIndex], columnIndex);
			}
		}
	}

	public boolean hasNextRow() {
		try {
			currentRow = csvReader.readNext();
		} catch (IOException e) {
			IOUtils.closeQuietly(csvReader);
			CRFGeneratorImpl.logger.error("In reading row in csv file:" + e.getMessage());
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
}