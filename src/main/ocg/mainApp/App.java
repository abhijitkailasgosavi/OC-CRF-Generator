package mainApp;

import au.com.bytecode.opencsv.CSVReader;
import crfGenerator.CRFGenerator;
import crfGenerator.CRFGeneratorImpl;
import csvReader.CsvFileReader;
import csvReader.CsvFileReaderImpl;

public class App 
{
	public static void main(String[] args) throws Exception {
		CsvFileReader csvFileReader = new CsvFileReaderImpl();
		CRFGenerator fileOperator2 = new CRFGeneratorImpl();
		String[] filename = null;

		CSVReader csvReader = csvFileReader.getcsvFileReader("inputFile.csv");
		if ((filename = csvReader.readNext()) != null) {
			fileOperator2.fileReadWrite(filename[0]);
		}
	}
}
