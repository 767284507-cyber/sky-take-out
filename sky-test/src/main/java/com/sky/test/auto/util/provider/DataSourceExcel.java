package com.sky.test.auto.util.provider;

//import com.auto.pingp.testplan.Environment;
//import com.auto.pingp.testplan.Message;

import com.sky.test.auto.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DataSourceExcel implements DataSource {
    private Sheet sheet;
    private int rowCount;
    private int columnCount;
    private Workbook workbook;
    private List<String> dataHeader;
    private List<LinkedHashMap<String, String>> mapListDataBody;
    private Object[][] doubleArrayDataBody;
    private final String flagColumnName = "TestCaseFlag";
    private ExcelUtil excelUtil = new ExcelUtil();

    public void setDataSource(String xlsFileName, String sheetName) {
        this.dataHeader = new ArrayList();
        this.mapListDataBody = new ArrayList();

        try {
            File file = new File(xlsFileName);
            if (!file.exists()) {
//                Message.error("The file cannot be found: " + xlsFileName + "!");
                throw new RuntimeException("The file cannot be found: " + xlsFileName + "!");
            }

            FileInputStream fileInputStream = new FileInputStream(xlsFileName);
            this.workbook = WorkbookFactory.create(new BufferedInputStream(fileInputStream));
            this.sheet = this.workbook.getSheet(sheetName);
            if (this.sheet == null) {
//                Message.error("The sheet name '" + sheetName + "' cannot be found!");
                throw new RuntimeException("The sheet name '" + sheetName + "' cannot be found!");

            }

            this.rowCount = this.sheet.getLastRowNum() + 1;
            Row row = this.sheet.getRow(0);
            this.columnCount = row.getLastCellNum();
            this.setDataHeader();
            this.setDataBody();
            fileInputStream.close();
        } catch (Exception var1) {
            throw new RuntimeException("Error occurs when initialize excelDatasource", var1);
        }
    }

    public void setDataSourceArray(String xlsFileName, String sheetName) {
        this.dataHeader = new ArrayList();
        this.mapListDataBody = new ArrayList();

        try {
            File file = new File(xlsFileName);
            if (!file.exists()) {
//                Message.error("The file cannot be found: " + xlsFileName + "!");
                throw new RuntimeException("The file cannot be found: " + xlsFileName + "!");
            }

            FileInputStream fileInputStream = new FileInputStream(xlsFileName);
            this.workbook = WorkbookFactory.create(new BufferedInputStream(fileInputStream));
            this.sheet = this.workbook.getSheet(sheetName);
            if (this.sheet == null) {
//                Message.error("The sheet name '" + sheetName + "' cannot be found!");
                throw new RuntimeException("The sheet name '" + sheetName + "' cannot be found!");

            }

            this.rowCount = this.sheet.getLastRowNum() + 1;
            Row row = this.sheet.getRow(0);
            this.columnCount = row.getLastCellNum();
            int len = 0;

            for (int iRow = 1; iRow < this.rowCount; ++iRow) {
                Row rowa = this.sheet.getRow(iRow);
                Cell flag = rowa.getCell(0);
                String flagValue = this.excelUtil.getCellAsString(flag);
                if (flagValue.toUpperCase().equals("Y")) {
                    ++len;
                }
            }

            this.doubleArrayDataBody = new Object[len - 1][];
            int index = 0;

            for (int iRow = 1; iRow < this.rowCount; ++iRow) {
                Row rowb = this.sheet.getRow(iRow);
                Cell flag = rowb.getCell(0);
                String flagValue = this.excelUtil.getCellAsString(flag);
                String[] array = new String[this.columnCount - 1];
                if (flagValue.toUpperCase().equals("Y")) {
                    for (int iColumn = 0; iColumn < this.columnCount; ++iColumn) {
                        Cell cell = rowb.getCell(iColumn);
                        array[iColumn] = this.excelUtil.getCellAsString(cell);
                    }

                    this.doubleArrayDataBody[index] = array;
                    ++index;
                }
            }

            fileInputStream.close();
        } catch (Exception var1) {
            throw new RuntimeException("Error occurs when setDataSource.", var1);
        }
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColCount() {
        return this.columnCount;
    }

    public List<String> getDataHeader() {
        return this.dataHeader;
    }

    private void setDataHeader() {
        Row row = this.sheet.getRow(0);

        for (int i = 0; i < this.columnCount; ++i) {
            Cell cell = row.getCell(i);
            this.dataHeader.add(this.excelUtil.getCellAsString(cell));
        }

    }

    public List<LinkedHashMap<String, String>> getDataBodyMap() {
        return this.mapListDataBody;
    }

    public Object[][] getDataBodyArray() {
        return this.doubleArrayDataBody;
    }

    private void setDataBody() {
//        String envName = Environment.getEnvironmentName().toUpperCase();


        for (int iRow = 1; iRow < this.rowCount; ++iRow) {
            LinkedHashMap<String, String> dataRow = new LinkedHashMap();
            dataRow.clear();
            Row row = this.sheet.getRow(iRow);
            Cell flag = row.getCell(0);
            String flagValue = this.excelUtil.getCellAsString(flag);
            if (flagValue.toUpperCase().equals("Y")) {
                if (this.dataHeader.contains("TestEnv")) {
                    Cell envData = row.getCell(1);
                    String envValue = this.excelUtil.getCellAsString(envData);
//                    if (envValue.toUpperCase().equals(envName)) {
                    for (int iColumn = 0; iColumn < this.columnCount; ++iColumn) {
                        Cell cell = row.getCell(iColumn);
                        dataRow.put(this.dataHeader.get(iColumn), this.excelUtil.getCellAsString(cell));
                    }

                    this.mapListDataBody.add(dataRow);
//                    }
                } else {
                    for (int iColumn = 0; iColumn < this.columnCount; ++iColumn) {
                        Cell cell = row.getCell(iColumn);
                        dataRow.put(this.dataHeader.get(iColumn), this.excelUtil.getCellAsString(cell));
                    }

                    this.mapListDataBody.add(dataRow);
                }
            }
        }

    }
}

