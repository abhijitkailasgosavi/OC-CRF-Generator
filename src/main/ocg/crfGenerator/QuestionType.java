package ocg.crfGenerator;

public enum QuestionType {
	Select_one("ST", "radio"),

	Number("INT", "text"), 

	Text("ST", "text"),

	Yes_No("ST","radio"),

	Date("DATE", "text"),

	Select_many("ST", "multi-select"),;

	private final String outputDataType;

	private final String outputResponseType;

	private QuestionType(String outputDataType, String outputResponseType) {
		this.outputDataType = outputDataType;
		this.outputResponseType = outputResponseType;
	}

	public String getOutputDataType() {
		return outputDataType;
	}

	public String getOutputResponseType() {
		return outputResponseType;
	}

	public static String getDataType(String questionType) {
		questionType = questionType.replaceAll("[^a-zA-Z0-9]", "_");
		String dataType = null;
		for (QuestionType dt : QuestionType.values()) {
			if (questionType.equals(dt.toString())) {
				dataType = dt.getOutputDataType();
			}
		}
		return dataType;
	}

	public static String getResponseType(String questionType) {
		questionType = questionType.replaceAll("[^a-zA-Z0-9]", "_");
		String responsType = null;
		for (QuestionType dt : QuestionType.values()) {
			if (questionType.equals(dt.toString())) {
				responsType = dt.getOutputResponseType();
			}
		}
		return responsType;
	}
}
