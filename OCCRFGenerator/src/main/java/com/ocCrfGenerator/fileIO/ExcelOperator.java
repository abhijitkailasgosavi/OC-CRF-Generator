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
			ListIterator<OcCrf> listIterator = list.listIterator();

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

			while (listIterator.hasNext()) {

				OcCrf ocCrf = listIterator.next();

				row = sheet.createRow(rowIndex++);

				row.createCell(0).setCellValue(ocCrf.getParentId());
				row.createCell(1).setCellValue(ocCrf.getParentType());
				row.createCell(2).setCellValue(ocCrf.getStudyId());
				row.createCell(3).setCellValue(ocCrf.getSiteId());
				row.createCell(4).setCellValue(ocCrf.getCrfId());
				row.createCell(5).setCellValue(ocCrf.getQuestionId());
				row.createCell(6).setCellValue(ocCrf.getAnswerId());
				row.createCell(7).setCellValue(ocCrf.getType());
				row.createCell(8).setCellValue(ocCrf.getTitle());
				row.createCell(9).setCellValue(ocCrf.getLabel());
				row.createCell(10).setCellValue(ocCrf.getQuestionType());
				row.createCell(11).setCellValue(ocCrf.getQuestionMandatory());
				row.createCell(12).setCellValue(ocCrf.getQuestiondefault());
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
