package com.sky.test.auto.util.provider;
//import com.auto.pingp.testplan.Message;

import java.io.*;
import java.util.*;

public class DataSourceCSV implements DataSource {
    private int currentRow;
    private int rowCount;
    private int columnCount;
    private List<String> dataHeader;
    private List<LinkedHashMap<String, String>> mapListDataBody;
    private Object[][] doubleArrayDataBody;

    public void setDataSource(String csvFileFullName, String sheetName) {
        this.dataHeader = new ArrayList();
        this.mapListDataBody = new ArrayList();

        try {
            File file = new File(csvFileFullName);
            if (!file.exists()) {
//                Message.error("The file cannot be found: " + csvFileFullName + "!");
                throw new RuntimeException("The file cannot be found: " + csvFileFullName + "!");
            }

            FileInputStream fileInputStream = new FileInputStream(csvFileFullName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            String parameters = bufferedReader.readLine();
            this.setDataHeader(parameters);

            String readCurrentLine;
            while ((readCurrentLine = bufferedReader.readLine()) != null) {
                String[] currentArray = readCurrentLine.split(",");
                String flag = currentArray[0].trim();
                if (flag.toUpperCase().equals("Y")) {
                    this.mapListDataBody.add(this.setDataBody(readCurrentLine));
                }
            }

            this.rowCount = this.mapListDataBody.size();
            this.columnCount = this.dataHeader.size();
            bufferedReader.close();
            fileInputStream.close();
        } catch (Exception var1) {
            throw new RuntimeException("Error occurs when setDataSource.", var1);
        }
    }

    public void setDataSourceArray(String csvFileFullName, String sheetName) {
        this.dataHeader = new ArrayList();

        try {
            File file = new File(csvFileFullName);
            if (!file.exists()) {
//                Message.error("The file cannot be found: " + csvFileFullName + "!");
                throw new RuntimeException("The file cannot be found: " + csvFileFullName + "!");
            }

            FileInputStream fileInputStream = new FileInputStream(csvFileFullName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            String parameters = bufferedReader.readLine();
            this.setDataHeader(parameters);
            this.rowCount = this.getDoubleArrayLen(csvFileFullName);
            this.columnCount = this.dataHeader.size();
            this.doubleArrayDataBody = new Object[this.rowCount][];
            int index = 0;

            String readCurrentLine;
            while ((readCurrentLine = bufferedReader.readLine()) != null) {
                String[] currentArray = readCurrentLine.split(",");
                String flag = currentArray[0].trim();
                if (flag.toUpperCase().equals("Y")) {
                    this.doubleArrayDataBody[index] = currentArray;
                    ++index;
                }
            }

            this.setCurrentRow(1);
            bufferedReader.close();
            fileInputStream.close();
        } catch (Exception var1) {
            throw new RuntimeException("Error occurs when setDataSource.", var1);
        }
    }

    private int getDoubleArrayLen(String csvFileFullName) throws IOException {
        File file = new File(csvFileFullName);
        if (!file.exists()) {
//            Message.error("The file cannot be found: " + csvFileFullName + "!");
            throw new RuntimeException("The file cannot be found: " + csvFileFullName + "!");
        }

        FileInputStream fileInputStream = new FileInputStream(csvFileFullName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
        String parameters = bufferedReader.readLine();
        int count = 0;

        String readCurrentLine;
        while ((readCurrentLine = bufferedReader.readLine()) != null) {
            String[] currentArray = readCurrentLine.split(",");
            String flag = currentArray[0].trim();
            if (flag.toUpperCase().equals("Y")) {
                ++count;
            }
        }

        bufferedReader.close();
        fileInputStream.close();
        return count;
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColCount() {
        return this.columnCount;
    }

    public int getCurrentRow() {
        return this.currentRow;
    }

    public void setCurrentRow(int row) {
        if (row > this.getRowCount()) {
            this.currentRow = -1;
//            Message.error("The row numner '" + row + "' is bigger than max row count, please change it");
            throw new RuntimeException("The row numner '" + row + "' is bigger than max row count, please change it");
        }

        this.currentRow = row;
    }

    public List<String> getDataHeader() {
        return this.dataHeader;
    }

    public void setDataHeader(String line) {
        String[] array = line.split(",");

        for (int i = 0; i < array.length; ++i) {
            String temp = array[i].trim();
            this.dataHeader.add(temp.replace("\ufeff", ""));
        }

    }

    public List<LinkedHashMap<String, String>> getDataBodyMap() {
        return this.mapListDataBody;
    }

    public Object[][] getDataBodyArray() {
        return this.doubleArrayDataBody;
    }

    private LinkedHashMap<String, String> setDataBody(String strDataRow) {
        LinkedHashMap<String, String> dataRow = new LinkedHashMap();
        String[] dataList = strDataRow.split(",");

        for (int i = 0; i < this.dataHeader.size(); ++i) {
            if (i < dataList.length) {
                dataRow.put(this.dataHeader.get(i), dataList[i].trim());
            } else {
                dataRow.put(this.dataHeader.get(i), "");
            }
        }

        return dataRow;
    }

    public String getValue(String columnName) {
        new HashMap();
        HashMap<String, String> currentRowMap = (HashMap) this.mapListDataBody.get(this.currentRow - 1);
        Iterator it = currentRowMap.entrySet().iterator();
        String value = null;

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            if (key.equals(columnName)) {
                value = (String) entry.getValue();
                break;
            }
        }

        return value;
    }
}
