package com.sky.test.auto.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.*;
import java.text.DecimalFormat;

public class ExcelUtil {
    private String filePath;
    private String xlsSheetName;
    private Sheet sheet;
    private int rowCount;
    private int columnCount;
    private int currentRow;
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;
    private HSSFWorkbook hssfWorkBook;

    public ExcelUtil() {
    }

    public ExcelUtil(String fileFullPath, String sheetName) {
        this.filePath = fileFullPath;
        this.xlsSheetName = sheetName;
        this.initWorkSheet();
    }

    public void initWorkSheet() {
        File file = new File(this.filePath);
        if (!file.exists()) {
//            Message.error("The file cannot be found: " + this.filePath + "!");
          throw new RuntimeException("The file cannot be found: " + this.filePath + "!");
        }

        try {
            this.fileInputStream = new FileInputStream(this.filePath);
            this.hssfWorkBook = new HSSFWorkbook(new BufferedInputStream(this.fileInputStream));
            this.sheet = this.hssfWorkBook.getSheet(this.xlsSheetName);
            if (this.sheet == null) {
//                Message.error("The sheet name '" + this.xlsSheetName + "' cannot be found!");
                throw new RuntimeException("The sheet name '" + this.xlsSheetName + "' cannot be found!");
            } else {
                Row row = this.sheet.getRow(0);
                this.columnCount = row.getLastCellNum();
                this.rowCount = this.sheet.getLastRowNum() + 1;
            }

            this.fileInputStream.close();
        } catch (Exception var1) {
            this.sheet = null;
            throw new RuntimeException("Error occurs when getWorkSheet", var1);
        }
    }

    public void setValue(String column, String value) {
        try {
            Row row = this.sheet.getRow(this.currentRow);
            int columnIndex = this.getColumnIndex(column);
            Cell cell = row.getCell(columnIndex);
            cell.setCellValue(value);
            this.fileOutputStream = new FileOutputStream(this.filePath);
            this.hssfWorkBook.write(this.fileOutputStream);
            this.fileOutputStream.close();
        } catch (Exception var1) {
            throw new RuntimeException("Error occurs when setValue", var1);
        }
    }

    public String getValue(String column) {
        try {
            Row row = this.sheet.getRow(this.currentRow);
            int columnIndex = this.getColumnIndex(column);
            String cellValue;
            if (columnIndex == -1) {
                cellValue = null;
            } else {
                Cell cell = row.getCell(columnIndex);
                cellValue = this.getCellAsString(cell);
            }

            return cellValue;
        } catch (Exception var1) {
            String cellValue = null;
            throw new RuntimeException("Error occurs when getValue", var1);
        }
    }

    public String getValue(int row, int column) {
        try {
            Row rowNum = this.sheet.getRow(row);
            String cellValue;
            if (column == -1) {
                cellValue = null;
            } else {
                Cell cell = rowNum.getCell(column);
                cellValue = this.getCellAsString(cell);
            }

            return cellValue;
        } catch (Exception var1) {
            String cellValue = null;
            throw new RuntimeException("Error occurs when getValue", var1);
        }
    }

    public int getColumnIndex(String columnName) throws IOException {
        Row row = this.sheet.getRow(0);
        int columnIndex = -1;

        for(int i = 0; i < this.columnCount; ++i) {
            Cell cell = row.getCell(i);
            String cellValue = this.getCellAsString(cell);
            if (cellValue.equals(columnName)) {
                columnIndex = i;
                break;
            }
        }

        if (columnIndex == -1) {
//            Message.error("Cannot find the column '" + columnName + "' in the file: " + this.filePath);
            throw new IOException("Cannot find the column '" + columnName + "' in the file: " + this.filePath);
        }

        return columnIndex;
    }

    // ... existing code ...
    public String getCellAsString(Cell cell) {
        String cellValue = null;
        if (cell == null) {
            cellValue = "";
        } else {
            CellType cellType = cell.getCellType();
            switch (cellType) {
                case NUMERIC:
                    DecimalFormat decimalFormat = new DecimalFormat(".00");
                    String temp = decimalFormat.format(cell.getNumericCellValue());
                    cellValue = this.getCellNum(temp);
                    break;
                case STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case FORMULA:
                    try {
                        cellValue = String.valueOf(cell.getStringCellValue());
                    } catch (IllegalStateException var8) {
                        DecimalFormat df = new DecimalFormat(".00");
                        String defTemp = df.format(cell.getNumericCellValue());
                        cellValue = this.getCellNum(defTemp);
                    }
                    break;
                case BLANK:
                    cellValue = "";
                    break;
                case ERROR:
                    cellValue = "Error";
                    break;
                default:
                    cellValue = "undefined";
            }
        }

        return cellValue;
    }


    private String getCellNum(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        // 去除末尾的 .00，但保留整数部分
        if (value.endsWith(".00")) {
            String result = value.substring(0, value.length() - 3);
            // 如果去掉 .00 后为空或者是 "-"，说明原值是 0 或 -0，返回 "0"
            if (result.isEmpty() || result.equals("-")) {
                return "0";
            }
            return result;
        }
        
        return value;
    }



    public static void main(String[] args) {
        ExcelUtil excelUtil = new ExcelUtil("D:\\test.xls", "Sheet1");
        System.out.println(excelUtil.getValue(0, 0));
        System.out.println(excelUtil.getRowCount());
        System.out.println(excelUtil.getColumnCount());
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getXlsSheetName() {
        return this.xlsSheetName;
    }

    public Sheet getSheet() {
        return this.sheet;
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public int getCurrentRow() {
        return this.currentRow;
    }

    public FileOutputStream getFileOutputStream() {
        return this.fileOutputStream;
    }

    public FileInputStream getFileInputStream() {
        return this.fileInputStream;
    }

    public HSSFWorkbook getHssfWorkBook() {
        return this.hssfWorkBook;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setXlsSheetName(String xlsSheetName) {
        this.xlsSheetName = xlsSheetName;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public void setFileOutputStream(FileOutputStream fileOutputStream) {
        this.fileOutputStream = fileOutputStream;
    }

    public void setFileInputStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public void setHssfWorkBook(HSSFWorkbook hssfWorkBook) {
        this.hssfWorkBook = hssfWorkBook;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ExcelUtil)) {
            return false;
        } else {
            ExcelUtil other = (ExcelUtil)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$filePath = this.getFilePath();
                Object other$filePath = other.getFilePath();
                if (this$filePath == null) {
                    if (other$filePath != null) {
                        return false;
                    }
                } else if (!this$filePath.equals(other$filePath)) {
                    return false;
                }

                Object this$xlsSheetName = this.getXlsSheetName();
                Object other$xlsSheetName = other.getXlsSheetName();
                if (this$xlsSheetName == null) {
                    if (other$xlsSheetName != null) {
                        return false;
                    }
                } else if (!this$xlsSheetName.equals(other$xlsSheetName)) {
                    return false;
                }

                Object this$sheet = this.getSheet();
                Object other$sheet = other.getSheet();
                if (this$sheet == null) {
                    if (other$sheet != null) {
                        return false;
                    }
                } else if (!this$sheet.equals(other$sheet)) {
                    return false;
                }

                if (this.getRowCount() != other.getRowCount()) {
                    return false;
                } else if (this.getColumnCount() != other.getColumnCount()) {
                    return false;
                } else if (this.getCurrentRow() != other.getCurrentRow()) {
                    return false;
                } else {
                    Object this$fileOutputStream = this.getFileOutputStream();
                    Object other$fileOutputStream = other.getFileOutputStream();
                    if (this$fileOutputStream == null) {
                        if (other$fileOutputStream != null) {
                            return false;
                        }
                    } else if (!this$fileOutputStream.equals(other$fileOutputStream)) {
                        return false;
                    }

                    Object this$fileInputStream = this.getFileInputStream();
                    Object other$fileInputStream = other.getFileInputStream();
                    if (this$fileInputStream == null) {
                        if (other$fileInputStream != null) {
                            return false;
                        }
                    } else if (!this$fileInputStream.equals(other$fileInputStream)) {
                        return false;
                    }

                    Object this$hssfWorkBook = this.getHssfWorkBook();
                    Object other$hssfWorkBook = other.getHssfWorkBook();
                    if (this$hssfWorkBook == null) {
                        if (other$hssfWorkBook != null) {
                            return false;
                        }
                    } else if (!this$hssfWorkBook.equals(other$hssfWorkBook)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ExcelUtil;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $filePath = this.getFilePath();
        result = result * 59 + ($filePath == null ? 43 : $filePath.hashCode());
        Object $xlsSheetName = this.getXlsSheetName();
        result = result * 59 + ($xlsSheetName == null ? 43 : $xlsSheetName.hashCode());
        Object $sheet = this.getSheet();
        result = result * 59 + ($sheet == null ? 43 : $sheet.hashCode());
        result = result * 59 + this.getRowCount();
        result = result * 59 + this.getColumnCount();
        result = result * 59 + this.getCurrentRow();
        Object $fileOutputStream = this.getFileOutputStream();
        result = result * 59 + ($fileOutputStream == null ? 43 : $fileOutputStream.hashCode());
        Object $fileInputStream = this.getFileInputStream();
        result = result * 59 + ($fileInputStream == null ? 43 : $fileInputStream.hashCode());
        Object $hssfWorkBook = this.getHssfWorkBook();
        result = result * 59 + ($hssfWorkBook == null ? 43 : $hssfWorkBook.hashCode());
        return result;
    }

    public String toString() {
        return "ExcelUtil(filePath=" + this.getFilePath() + ", xlsSheetName=" + this.getXlsSheetName() + ", sheet=" + this.getSheet() + ", rowCount=" + this.getRowCount() + ", columnCount=" + this.getColumnCount() + ", currentRow=" + this.getCurrentRow() + ", fileOutputStream=" + this.getFileOutputStream() + ", fileInputStream=" + this.getFileInputStream() + ", hssfWorkBook=" + this.getHssfWorkBook() + ")";
    }
}
