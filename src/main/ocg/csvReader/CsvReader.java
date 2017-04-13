package ocg.csvReader;

public interface CsvReader {
	boolean hasNextRow();

	String getColumnValue(String columnHeader);
}