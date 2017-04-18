package ocg.crfGenerator;

public interface CRFGenerator {
	public void generateCRF(String inputFileName);

	public String createCrfDir();

	public String getCRFName();
}