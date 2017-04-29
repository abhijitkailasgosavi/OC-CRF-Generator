package ocg.crfGenerator;

public class MainApp {
	public static void main(String[] args) throws Exception {
		CRFGenerator crfGenerator = new CRFGeneratorImpl();

		crfGenerator.generateCrf(args[0],args[1],args[2]);
	}
}