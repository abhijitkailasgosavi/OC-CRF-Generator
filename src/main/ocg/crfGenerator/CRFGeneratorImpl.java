package ocg.crfGenerator;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ocg.connUtils.ConnUtilsImpl;
import ocg.fileIoUtils.CsvReaderImpl;
import ocg.fileIoUtils.ListCrfs;
import ocg.fileIoUtils.ListCrfsImpl;
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

	public void generateCrf(String inputCsv, String username, String password) {
		XLSReader xlsReader = new XLSReaderImpl(DEF_CRF_TEMPLATE_FILE);
		csvReader = new CsvReaderImpl(inputCsv);
		XLSWriter xlsWriter = new XLSWriterImpl();
		ConnUtilsImpl connUtils = new ConnUtilsImpl();
		ListCrfs listCrfs = new ListCrfsImpl();
		String filepath = createCrfDir();
		Integer itemCount = 0;
		String studyUniqueId = null;
		String siteUniqueId = null;
		try {
			HSSFWorkbook crf = null;
			String apiKey = connUtils.getUserApiKey(username, password);
			while (csvReader.hasNextRow()) {
				if (csvReader.getColumnValue("Type").equals("Study")) {
					String studyName = csvReader.getColumnValue("Title");
					String studyId =  csvReader.getColumnValue("Study ID");
					studyUniqueId = connUtils.createStudy(studyName, studyId,apiKey);
				}  else if (csvReader.getColumnValue("Type").equals("Site")) {
					String siteName = csvReader.getColumnValue("Title");
					String siteId =  csvReader.getColumnValue("Site ID");
					String parentStudyId = csvReader.getColumnValue("Parent ID");
					siteUniqueId = connUtils.createSite(siteName, siteId, parentStudyId,apiKey);
				} else if (csvReader.getColumnValue("Type").equals("CRF")) {
					xlsWriter.crfWriter(crf);
					String filename = getCrfName();
					crf = xlsReader.getSampleCrf(filepath + filename);
					xlsWriter = new XLSWriterImpl(filepath + filename, crf, xlsReader.getHeaderNameIdxMap());
					logger.info(filename + " CRF is created");
					xlsWriter.addCrfDetails(csvReader.getColumnValue("Title"));
					itemCount = 0;

					String[] crfDetails = {studyUniqueId,siteUniqueId,filename};
					listCrfs.writeCrfDetails(crfDetails);
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
		} finally {
			csvReader.close();
			xlsWriter.close();
			listCrfs.close();
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
		String filename = csvReader.getColumnValue("Title") + ".xls";
		filename = filename.replaceAll("/", "\\\\");
		return filename; 
	}
}