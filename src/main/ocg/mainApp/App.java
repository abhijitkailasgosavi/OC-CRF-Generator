package mainApp;

import crfGenerator.CRFGenerator;
import crfGenerator.CRFGeneratorImpl;

public class App 
{
	public static void main(String[] args) throws Exception {	
		CRFGenerator fileOperator2 = new CRFGeneratorImpl();

		fileOperator2.fileReadWrite("/home/neha/eclipse/CRF_All_CodeBook.csv");
	}

}
