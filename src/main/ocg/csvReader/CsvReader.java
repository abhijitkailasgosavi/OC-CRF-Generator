package ocg.csvReader;

public interface CsvReader {
	public boolean hasNextRow();

	public String getColumnValue(String columnHeader);
}