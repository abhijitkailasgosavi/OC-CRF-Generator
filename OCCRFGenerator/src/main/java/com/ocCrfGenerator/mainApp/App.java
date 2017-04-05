package com.ocCrfGenerator.mainApp;

import com.ocCrfGenerator.fileIO.Fileoperator;

public class App 
{
	public static void main(String[] args) throws Exception {	
		Fileoperator fileOperator = new Fileoperator();

		fileOperator.fileReadWrite("/home/neha/eclipse/CRF_All_CodeBook.csv");
	}

}
