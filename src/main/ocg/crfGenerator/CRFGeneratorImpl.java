package ocg.crfGenerator;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import au.com.bytecode.opencsv.CSVReader;
import ocg.ioUtils.CsvReaderImpl;
import ocg.ioUtils.XLSReader;
import ocg.ioUtils.XLSReaderImpl;
import ocg.ioUtils.XLSWriter;
import ocg.ioUtils.XLSWriterImpl;

public class CRFGeneratorImpl implements CRFGenerator {
	public static final String DEF_CRF_TEMPLATE_FILE = "sampleCRF/mySampleCRF.xls";

	public static Logger logger = Logger.getLogger(CRFGeneratorImpl.class.getName());
	static {
		PropertyConfigurator.configure("log4j.properties");
	}

	private CsvReaderImpl csvReader;

	private List<String> responses = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		CSVReader csvReader = new CSVReader(new FileReader("inputFile.csv"));
		CRFGenerator crfGenerator = new CRFGeneratorImpl();
		String[] filename = null;

		if ((filename = csvReader.readNext()) != null) {
			crfGenerator.generateCRF(filename[0]);
		}
		csvReader.close();
	}

	public void generateCRF(String inputCsv) {
		XLSReader xlsReader = new XLSReaderImpl(DEF_CRF_TEMPLATE_FILE);
		csvReader = new CsvReaderImpl(inputCsv);
		XLSWriter xlsWriter = new XLSWriterImpl();
		Integer itemCount = 0;
		try {
			HSSFWorkbook crf = null;
			while (csvReader.hasNextRow()) {
				if (csvReader.getColumnValue("Type").equals("CRF")) {
					xlsWriter.crfWriter(crf);
					String filename = getCRFName();
					crf = xlsReader.getSampleCrf(filename);
					xlsWriter = new XLSWriterImpl(filename, crf);
					logger.info(filename + " CRF is created");
					xlsWriter.addCRFDetails(csvReader.getColumnValue("Title"));
					itemCount = 0;
				} else if (csvReader.getColumnValue("Type").equals("Q")) {
					xlsWriter.addItem(xlsReader, csvReader, ++itemCount);
					responses.clear();
				} else if (csvReader.getColumnValue("Type").equals("A")) {
					getResponses();
					xlsWriter.addResponseTextAndValue(String.join(",", responses));
					logger.info("Response is created using answers : " + String.join(",", responses));
				}
			}
			xlsWriter.crfWriter(crf);
			logger.info("Task completed");
		} catch (Exception e) {
			logger.error("In generateCRF method " + e.getMessage());
		} finally {
			csvReader.close();
			xlsReader.close();
			xlsWriter.close();
		}
	}

	private void getResponses() {
		String response = csvReader.getColumnValue("Title");
		if (StringUtils.isBlank(response)) {
			return;
		}
		responses.add(response);
	}

	static public String getItemName(String input) {
		return input.replaceAll("[^a-zA-Z0-9]", "_");
	}

	public String createCrfDir() {
		String filePath = "../CRFs/";
		File crfDir = new File(filePath);
		if (!crfDir.exists()) {
			try {
				crfDir.mkdir();
			} catch(SecurityException se){
				logger.error("In generateCRF method while creating CRFs directory " + se.getMessage());
			}
		}
		return filePath;
	}

	public String getCRFName() {
		String filepath = createCrfDir();
		String filename = csvReader.getColumnValue("Title") + ".xls";
		filename = filename.replaceAll("/", "\\\\");
		return filepath + filename; 
	}
}