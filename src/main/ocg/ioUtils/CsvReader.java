package ocg.ioUtils;

public interface CsvReader {
	public boolean hasNextRow();

	public String getColumnValue(String columnHeader);
}