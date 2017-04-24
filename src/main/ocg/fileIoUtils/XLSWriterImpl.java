package ocg.fileIoUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;

import ocg.crfGenerator.CRFGeneratorImpl;
import ocg.crfGenerator.QuestionType;

public class XLSWriterImpl implements XLSWriter {
	private FileOutputStream out = null;

	private HSSFWorkbook crf;

	private HSSFSheet items = null;

	private Row row;

	private Map<String, Integer> headerNameIdxMap = new HashMap<String, Integer>();

	//used for show/hide questions
	private Map<String, ArrayList<String>> queAnswersMap = new HashMap<String, ArrayList<String>>();

	private List<String> answersList = new ArrayList<String>();

	private String question;

	private String questionId;

	public XLSWriterImpl() {
	}

	public XLSWriterImpl(String filename, HSSFWorkbook crf, Map<String, Integer> headerNameIdxMap) {
		try {
			out = new FileOutputStream(filename);
			this.crf = crf;
			items = this.crf.getSheet("Items");
			this.headerNameIdxMap = headerNameIdxMap;
		} catch (Exception e) {
			IOUtils.closeQuietly(out);
			CRFGeneratorImpl.logger.error("Error in creating excel writer :" + e.getMessage());
		}	
	}

	public void addCrfDetails(String crfName) {
		HSSFSheet sheet = crf.getSheet("CRF");
		Row row = sheet.createRow(1);
		row.createCell(0).setCellValue(crfName);
		row.createCell(1).setCellValue("1");
		row.createCell(3).setCellValue(crfName);
	}

	public void addItem(XLSReader xlsReader, CsvReader csvReader, Integer itemCount) {
		row = items.createRow(itemCount);
		String label =getItemName(csvReader.getColumnValue("Label"));
		questionId = csvReader.getColumnValue("Question ID");
		question = label+"_"+itemCount;

		setCellValue("ITEM_NAME",question);
		setCellValue("DESCRIPTION_LABEL", csvReader.getColumnValue("Title"));
		setCellValue("LEFT_ITEM_TEXT", csvReader.getColumnValue("Title"));
		setCellValue("SECTION_LABEL", xlsReader.getSectionLabel(crf));
		setCellValue("GROUP_LABEL", xlsReader.getGroupLabel(crf));
		setCellValue("RESPONSE_TYPE", QuestionType.getResponseType(csvReader.getColumnValue("Question Type")));
		setCellValue("RESPONSE_LABEL", "A" + itemCount);
		setCellValue("RESPONSE_OPTIONS_TEXT", "");
		setCellValue("RESPONSE_VALUES_OR_CALCULATIONS", "");
		setCellValue("DATA_TYPE", QuestionType.getDataType(csvReader.getColumnValue("Question Type")));

		CRFGeneratorImpl.logger.info("Question row is created with title" + label);

		//for show/hide form data
		if (csvReader.getColumnValue("Parent Type").equals("A")) {
			String parentAnsId = csvReader.getColumnValue("Parent ID");
			ArrayList<String> answerDataList = queAnswersMap.get(parentAnsId);
			String condition = String.join(",", answerDataList);
			String message = "Only provide answer if subject is" + answerDataList.get(1);

			setCellValue("ITEM_DISPLAY_STATUS", "Hide");
			setCellValue("SIMPLE_CONDITIONAL_DISPLAY", condition + "," +message);
		}
		answersList.clear();
	}

	public void addResponseTextAndValue(String answerId,String answer) {
		addResponses(answer);
		createQueAnswersMap(answerId, answer, questionId, question);
		String responseText = String.join(",", answersList);

		Cell cellResponseText = row.getCell(15);
		Cell cellResponseValue = row.getCell(16);
		cellResponseText.setCellValue("");
		cellResponseValue.setCellValue("");
		cellResponseText.setCellValue(responseText);
		cellResponseValue.setCellValue(responseText);
		CRFGeneratorImpl.logger.info("Response is created using answers : " + String.join(",", answersList));
	}

	public void crfWriter(HSSFWorkbook crf) {
		if (crf != null) {
			try {
				crf.write(out);
			} catch (IOException e) {
				CRFGeneratorImpl.logger.error("Error in crfWrite method while closing output file " + e.getMessage());
			}
		}
	}

	public void close() {
		IOUtils.closeQuietly(out);
	}

	private void createQueAnswersMap(String answerId, String answer, String queId, String que ) {
		ArrayList<String> answerDataList = new ArrayList<String>();
		answerDataList.add(que);
		answerDataList.add(answer);
		queAnswersMap.put(answerId, answerDataList);
	}

	private void addResponses(String response) {
		if (StringUtils.isBlank(response)) {
			return;
		}
		answersList.add(response);
	}

	private void setCellValue(String columnName, String data) {
		row.createCell(headerNameIdxMap.get(columnName)).setCellValue(data);;
	}
	
	private String getItemName(String input) {
		return input.replaceAll("[^a-zA-Z0-9]", "_");
	}
}
