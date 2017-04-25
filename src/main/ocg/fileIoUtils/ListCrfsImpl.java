package ocg.fileIoUtils;

import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;
import ocg.crfGenerator.CRFGeneratorImpl;

public class ListCrfsImpl implements ListCrfs {
	String csv = "../ListCrfs.csv";

	CSVWriter writer;

	public ListCrfsImpl() {
		try {
			writer = new CSVWriter(new FileWriter(csv));
			headerWrite();
		} catch (IOException e) {
			CRFGeneratorImpl.logger.error("Error in ListCrfs creation :" + e.getMessage());
		}
	}

	public void writeCrfDetails(String[] crfDetails) {
		writer.writeNext(crfDetails);
	}

	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			CRFGeneratorImpl.logger.error("Error in listCrf writer closing :" + e.getMessage());
		}
	}

	private void headerWrite() {
		String [] header= "Study Id,Site Id,CRF Name".split(",");
		writer.writeNext(header);
	}
}
