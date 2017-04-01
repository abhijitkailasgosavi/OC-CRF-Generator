package com.ocCrfGenerator.fileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ListIterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ocCrfGenerator.details.OcCrf;

public class ExcelOperator {

	public void excelFilewriter(String fileName, List list) throws Exception {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("crf data");
		FileOutputStream out =null;
		try {
			out = new FileOutputStream(new File(fileName));
			ListIterator<OcCrf> participantsListIterator = list.listIterator();

			int rowIndex = 0;

			Row row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue("parentId");
			row.createCell(1).setCellValue("parent Type");
			row.createCell(2).setCellValue("Study ID");
			row.createCell(3).setCellValue("site Id");
			row.createCell(4).setCellValue("CRF Id");
			row.createCell(5).setCellValue("question Id");
			row.createCell(6).setCellValue("answer Id");
			row.createCell(7).setCellValue("type");
			row.createCell(8).setCellValue("title");
			row.createCell(9).setCellValue("label");
			row.createCell(10).setCellValue("question Type");
			row.createCell(11).setCellValue("question Mandatory");
			row.createCell(12).setCellValue("question default");

			while (participantsListIterator.hasNext()) {

				OcCrf sampleCRF = participantsListIterator.next();

				row = sheet.createRow(rowIndex++);

				row.createCell(0).setCellValue(sampleCRF.getParentId());
				row.createCell(1).setCellValue(sampleCRF.getParentType());
				row.createCell(2).setCellValue(sampleCRF.getStudyId());
				row.createCell(3).setCellValue(sampleCRF.getSiteId());
				row.createCell(4).setCellValue(sampleCRF.getCrfId());
				row.createCell(5).setCellValue(sampleCRF.getQuestionId());
				row.createCell(6).setCellValue(sampleCRF.getAnswerId());
				row.createCell(7).setCellValue(sampleCRF.getType());
				row.createCell(8).setCellValue(sampleCRF.getTitle());
				row.createCell(9).setCellValue(sampleCRF.getLabel());
				row.createCell(10).setCellValue(sampleCRF.getQuestionType());
				row.createCell(11).setCellValue(sampleCRF.getQuestionMandatory());
				row.createCell(12).setCellValue(sampleCRF.getQuestiondefault());
			}

			workbook.write(out);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			out.close();
			workbook.close();
		}


	}

}
