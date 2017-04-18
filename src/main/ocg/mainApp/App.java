package ocg.mainApp;

import java.io.FileReader;

import au.com.bytecode.opencsv.CSVReader;
import ocg.crfGenerator.CRFGenerator;
import ocg.crfGenerator.CRFGeneratorImpl;

public class App {
	public static void main(String[] args) throws Exception {
		CSVReader csvReader = new CSVReader(new FileReader("inputFile.csv"));
		CRFGenerator crfGenerator = new CRFGeneratorImpl();
		String[] filename = null;

		if ((filename = csvReader.readNext()) != null) {
			crfGenerator.generateCRF(filename[0]);
		}
		csvReader.close();
	}
}