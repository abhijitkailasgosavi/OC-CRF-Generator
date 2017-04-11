package csvReader;

import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class CsvFileReaderImpl implements CsvFileReader {

	public CSVReader getcsvFileReader(String inputFileName) throws IOException {
		return new CSVReader(new FileReader(inputFileName));
	}

	public enum DataType { Select_one("ST","single-select"),
		Number("INT","text"), 
		Text("ST","text"),
		Yes_No("ST","radio"),
		Date("DATE","text"),
		Select_many("ST","multi-select"),;

		private final String outputDataType;

		private final String outputResponseType;

		private DataType(String outputDataType,String outputResponseType) {
			this.outputDataType = outputDataType;  
			this.outputResponseType = outputResponseType;
		}
		public String getOutputDataType() {
			return outputDataType;
		}

		public String getOutputResponseType() {
			return outputResponseType;
		}
	} 


	static public String generateValidString(String input) {
		input = input.replaceAll("[^a-zA-Z0-9]"," ");
		input = input.replaceAll(" ", "_");
		return input;
	}


	public String getDataType(String questionType) {
		questionType = generateValidString(questionType);
		for(DataType dt : DataType.values()) {
			if(questionType.equals(dt.toString())) {
				return dt.getOutputDataType();
			}
		}
		return "ST";
	}

	public String getResponseType(String questionType) {
		questionType = generateValidString(questionType);
		for(DataType dt : DataType.values()) {
			if(questionType.equals(dt.toString())) {
				return dt.getOutputResponseType();
			}
		}
		return "text";
	}
}
