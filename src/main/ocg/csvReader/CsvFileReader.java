package csvReader;

import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public interface CsvFileReader {

	CSVReader getcsvFileReader(String inputFileName) throws IOException;

}