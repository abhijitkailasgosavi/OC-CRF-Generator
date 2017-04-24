package ocg.crfGenerator;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ocg.connUtils.ConnUtilsImpl;
import ocg.fileIoUtils.CsvReaderImpl;
import ocg.fileIoUtils.XLSReader;
import ocg.fileIoUtils.XLSReaderImpl;
import ocg.fileIoUtils.XLSWriter;
import ocg.fileIoUtils.XLSWriterImpl;

public class CRFGeneratorImpl implements CRFGenerator {
	public static final String DEF_CRF_TEMPLATE_FILE = "resources/default-crf.xls";

	public static Logger logger = Logger.getLogger(CRFGeneratorImpl.class.getName());
	static {
		PropertyConfigurator.configure("log4j.properties");
	}

	private CsvReaderImpl csvReader;

	public void generateCrf(String inputCsv) {
		XLSReader xlsReader = new XLSReaderImpl(DEF_CRF_TEMPLATE_FILE);
		csvReader = new CsvReaderImpl(inputCsv);
		XLSWriter xlsWriter = new XLSWriterImpl();
		ConnUtilsImpl connUtils = new ConnUtilsImpl();
		Integer itemCount = 0;
		try {
			HSSFWorkbook crf = null;
			while (csvReader.hasNextRow()) {
				if (csvReader.getColumnValue("Type").equals("Study")) {
					String studyName = csvReader.getColumnValue("Title");
					String studyId =  csvReader.getColumnValue("Study ID");
					connUtils.createStudy(studyName, studyId);
				}  else if (csvReader.getColumnValue("Type").equals("Site")) {
					String siteName = csvReader.getColumnValue("Title");
					String siteId =  csvReader.getColumnValue("Site ID");
					String parentStudyId = csvReader.getColumnValue("Parent ID");
					connUtils.createSite(siteName, siteId, parentStudyId);
				} else if (csvReader.getColumnValue("Type").equals("CRF")) {
					xlsWriter.crfWriter(crf);
					String filename = getCrfName();
					crf = xlsReader.getSampleCrf(filename);
					xlsWriter = new XLSWriterImpl(filename, crf, xlsReader.getHeaderNameIdxMap());
					logger.info(filename + " CRF is created");
					xlsWriter.addCrfDetails(csvReader.getColumnValue("Title"));
					itemCount = 0;
				} else if (csvReader.getColumnValue("Type").equals("Q")) {
					xlsWriter.addItem(xlsReader, csvReader, ++itemCount);
				} else if (csvReader.getColumnValue("Type").equals("A")) {
					String title = csvReader.getColumnValue("Title");
					String answerId = csvReader.getColumnValue("Answer ID");
					xlsWriter.addResponseTextAndValue(answerId,title);					
				}
			}
			xlsWriter.crfWriter(crf);
			logger.info("Task completed");
		} catch (Exception e) {
			logger.error("Error in generateCRF method " + e.getMessage());
			e.printStackTrace();
		} finally {
			csvReader.close();
			xlsWriter.close();
		}
	}

	private String createCrfDir() {
		String filePath = "../CRFs/";
		File crfDir = new File(filePath);
		if (!crfDir.exists()) {
			try {
				crfDir.mkdir();
			} catch(SecurityException se){
				logger.error("Error while creating CRF directory " + se.getMessage());
			}
		}
		return filePath;
	}

	private String getCrfName() {
		String filepath = createCrfDir();
		String filename = csvReader.getColumnValue("Title") + ".xls";
		filename = filename.replaceAll("/", "\\\\");
		return filepath + filename; 
	}
}