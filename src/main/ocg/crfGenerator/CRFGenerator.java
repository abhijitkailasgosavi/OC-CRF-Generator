package crfGenerator;

public interface CRFGenerator {

	void fileReadWrite(String inputFileName) throws Exception;

	String getDataType(String string);

}