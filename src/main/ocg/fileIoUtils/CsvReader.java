package ocg.fileIoUtils;

public interface CsvReader {
	public boolean hasNextRow();

	public String getColumnValue(String columnHeader);
	
	public void close();
	
	public long getCurrentRowCount();
}