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

	private Map<String, String> questionTitleIdxMap = new HashMap<String, String>();

	//used for show/hide questions
	private Map<String, ArrayList<String>> queAnswersMap = new HashMap<String, ArrayList<String>>();

	private List<String> answersList = new ArrayList<String>();

	private List<String> valuesList = new ArrayList<String>();

	private String question;

	private String responseType;

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
		String label = getItemName(csvReader.getColumnValue("Label"));
		question = label;
		responseType = QuestionType.getResponseType(csvReader.getColumnValue("Question.Type"));
		String dataType = QuestionType.getDataType(csvReader.getColumnValue("Question.Type"));

		if (StringUtils.isBlank(responseType) || StringUtils.isBlank(dataType)) {
			CRFGeneratorImpl.logger.error("Error: Question Type is wrong at line " +
					csvReader.getCurrentRowCount());
		}

		String leftItemText = csvReader.getColumnValue("Design.Title");
		if (StringUtils.isBlank(leftItemText)) {
			leftItemText = csvReader.getColumnValue("Title");
		}

		setCellValue("ITEM_NAME",question);
		setCellValue("DESCRIPTION_LABEL", csvReader.getColumnValue("Title"));
		setCellValue("LEFT_ITEM_TEXT", leftItemText);
		setCellValue("SECTION_LABEL", xlsReader.getSectionLabel(crf));
		setCellValue("GROUP_LABEL", xlsReader.getGroupLabel(crf));
		setCellValue("RESPONSE_TYPE", responseType);
		setCellValue("RESPONSE_LABEL", "A" + itemCount);
		setCellValue("RESPONSE_OPTIONS_TEXT", "");
		setCellValue("RESPONSE_VALUES_OR_CALCULATIONS", "");
		setCellValue("DATA_TYPE", dataType);

		String queMandatory = csvReader.getColumnValue("Question.Mandatory");
		if (queMandatory.equals("1st and 2nd Data Entry") || queMandatory.equals("1st Data Entry")) {
			setCellValue("REQUIRED", "1");
		}

		CRFGeneratorImpl.logger.info("Question row is created with title " + label);

		questionTitleIdxMap.put(csvReader.getColumnValue("Question.ID"), question);
		//for show/hide form data
		if (csvReader.getColumnValue("Parent.Type").equals("A")) {
			String parentAnsId = csvReader.getColumnValue("Parent.ID");
			addShowHideCells(parentAnsId);
		}

		answersList.clear();
		valuesList.clear();
	}

	public void addResponseTextAndValue(String answerId,String answer, String answerValue) {
		addResponse(answer, answersList);
		addResponse(answerValue, valuesList);

		createQueAnswersMap(answerId, answer, question, responseType, answerValue);
		String responseText = String.join(",", answersList);
		String responseValue = String.join(",", valuesList);
		if (row == null) {
			CRFGeneratorImpl.logger.error("Error: Question is not created");
			return;
		}

		Cell cellResponseText = row.getCell(15);
		Cell cellResponseValue = row.getCell(16);
		cellResponseText.setCellValue("");
		cellResponseValue.setCellValue("");
		cellResponseText.setCellValue(responseText);
		cellResponseValue.setCellValue(responseValue);
		CRFGeneratorImpl.logger.info("Response is created using answers : " + String.join(",", answersList));
	}

	public void addResponseTextAndValue(String parentId, String answerId, String answer, String answerValue) {
		int lastRow = items.getLastRowNum();
		String currentQuestion = questionTitleIdxMap.get(parentId);
		int i = 0;
		Row currentQueRow = null;
		while (i <= lastRow) {
			currentQueRow = items.getRow(i);
			if (currentQuestion.equals(currentQueRow.getCell(0).getStringCellValue())) {
				break;
			}
			i++;
		}

		String QuestionRespnseType = currentQueRow.getCell(13).getStringCellValue();
		createQueAnswersMap(answerId, answer, currentQuestion, QuestionRespnseType, answerValue);
		Cell cellResponseText = currentQueRow.getCell(15);
		Cell cellResponseValue = currentQueRow.getCell(16);
		String responseText = cellResponseText.getStringCellValue() + "," + answer;
		String responseValue = cellResponseValue.getStringCellValue() + "," + answerValue;;

		cellResponseText.setCellValue("");
		cellResponseValue.setCellValue("");
		cellResponseText.setCellValue(responseText);
		cellResponseValue.setCellValue(responseValue);
		CRFGeneratorImpl.logger.info("Response is created using parent answers : " + responseText);
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

	private void addShowHideCells(String parentAnsId) {
		ArrayList<String> answerDataList = queAnswersMap.get(parentAnsId);
		String responseType = answerDataList.get(2);

		if (responseType != null) {
			if (responseType.equals("single-select") || responseType.equals("multi-select") ||
					responseType.equals("radio")) {
				String condition = answerDataList.get(0) +"," + answerDataList.get(3);
				String message = "Only provide answer if subject is " + answerDataList.get(1);

				setCellValue("ITEM_DISPLAY_STATUS", "Hide");
				setCellValue("SIMPLE_CONDITIONAL_DISPLAY", condition + ", " +message);
			}
		}
	}

	private void createQueAnswersMap(String answerId, String answer, String que,
			String responsetype, String answerValue ) {
		ArrayList<String> answerDataList = new ArrayList<String>();
		answerDataList.add(que);
		answerDataList.add(answer);
		answerDataList.add(responsetype);
		answerDataList.add(answerValue);

		queAnswersMap.put(answerId, answerDataList);
	}

	private void addResponse(String response, List<String> list) {
		if (StringUtils.isBlank(response)) {
			return;
		}

		list.add(response);
	}

	private void setCellValue(String columnName, String data) {
		row.createCell(headerNameIdxMap.get(columnName)).setCellValue(data);;
	}

	private String getItemName(String input) {
		return input.replaceAll("[^a-zA-Z0-9]", "_");
	}
}
