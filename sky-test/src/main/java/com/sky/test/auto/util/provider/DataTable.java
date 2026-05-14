package com.sky.test.auto.util.provider;

import java.util.*;

public class DataTable {
    private String dataTableName;
    private String methodName;
    private final String methodColumnName = "TestCaseMethod";
    private String sheetName;
    private DataSource dataSource;

    public String getDataTableName() {
        return this.dataTableName;
    }

    public String getExcelSheetName() {
        return this.sheetName;
    }

    public void setlSheetName(String sheet) {
        this.sheetName = sheet;
    }

    public void setDataTable(String fileName, String sheetName, String method) {
        String fileEx = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (fileEx.toUpperCase().equals("CSV")) {
            this.setDataTableName(fileName);
            this.setlSheetName(sheetName);
            this.setMethodName(method);
            this.dataSource = new DataSourceCSV();
        } else if (fileEx.toUpperCase().equals("XLS")||fileEx.toUpperCase().equals("XLSX")) {
            this.setDataTableName(fileName);
            this.setlSheetName(sheetName);
            this.setMethodName(method);
            this.dataSource = new DataSourceExcel();
        } else {
//            Message.error("Test TestData file only support csv and xls format.");
            throw new RuntimeException("Test TestData file only support csv and xls/xlsx format.");

        }

    }

    public void setDataTableName(String dataSourceNameX) {
        this.dataTableName = dataSourceNameX;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodNameX) {
        this.methodName = methodNameX;
    }

    public Iterator<Object[]> getDataTable() {
        List<LinkedHashMap<String, String>> dataBody = null;
        this.dataSource.setDataSource(this.dataTableName, this.sheetName);
        dataBody = this.dataSource.getDataBodyMap();
        List<Object[]> arrayList = new ArrayList();
        Iterator it = dataBody.iterator();

        while (it.hasNext()) {
            LinkedHashMap<String, String> map = (LinkedHashMap) it.next();
            if (this.methodName.trim().length() == 0) {
                arrayList.add(new Object[]{map});
            } else if (map.containsKey("TestCaseMethod")) {
                if (((String) map.get("TestCaseMethod")).equals(this.methodName)) {
                    arrayList.add(new Object[]{map});
                }
            } else {
//                Message.error("Please make sure the column 'TestCaseMethod' existing in the test TestData file!");
                arrayList = null;
                throw new RuntimeException("Please make sure the column 'TestCaseMethod' existing in the test TestData file!");
            }
        }

        if (this.methodName.trim().length() == 0) {
            if (arrayList.size() == 0) {
//                Message.error("Test TestData file has no TestData!");
                arrayList = null;
                throw new RuntimeException("Test TestData file has no TestData!");

            }
        } else if (arrayList.size() == 0) {
//            Message.error("Test TestData file has no TestData for current method '" + this.methodName + "'!");
            throw new RuntimeException("Test TestData file has no TestData for current method '" + this.methodName + "'!");
        }

        return arrayList.iterator();
    }

    public Iterator<Object[]> getDataTable(LinkedHashMap linkedMap) {
        this.dataSource.setDataSource(this.dataTableName, "Sheet1");
        List<LinkedHashMap<String, String>> dataBody = this.dataSource.getDataBodyMap();
        List<Object[]> arrayList = new ArrayList();
        Iterator it = dataBody.iterator();

        while (it.hasNext()) {
            LinkedHashMap<String, String> rowMap = (LinkedHashMap) it.next();
            Object[] objArray = new Object[linkedMap.size() + 1];
            objArray[0] = rowMap;
            Iterator linkedMapIterator = linkedMap.entrySet().iterator();

            for (int count = 1; linkedMapIterator.hasNext(); ++count) {
                Map.Entry entry = (Map.Entry) linkedMapIterator.next();
                String linkedMapKey = (String) entry.getKey();
                int length = linkedMapKey.length();
                LinkedHashMap<String, String> singleMap = new LinkedHashMap();

                for (Map.Entry entryA : rowMap.entrySet()) {
                    String keyA = (String) entryA.getKey();
                    String valueA = (String) entryA.getValue();
                    if (keyA.length() > length) {
                        String temp = keyA.substring(0, length + 1);
                        if (temp.equals(linkedMapKey + ".")) {
                            singleMap.put(keyA.substring(length + 1), valueA);
                        }
                    }
                }

                objArray[count] = singleMap;
            }

            if (rowMap.containsKey("TestCaseMethod")) {
                if (((String) rowMap.get("TestCaseMethod")).equals(this.methodName)) {
                    arrayList.add(objArray);
                }
            } else {
//                Message.error("Please make sure the column 'TestCaseMethod' existing in the test TestData file!");
                arrayList = null;
                throw new RuntimeException("Please make sure the column 'TestCaseMethod' existing in the test TestData file!");

            }
        }

        return arrayList.iterator();
    }


    //表头和
    public Object[][] getDataTableArray() {
        this.dataSource.setDataSourceArray(this.dataTableName, "Sheet1");
        Object[][] arrayDataBody01 = this.dataSource.getDataBodyArray();
        List<String> dataHeader = this.dataSource.getDataHeader();
        int index = 0;

        for (int i = 0; i < dataHeader.size(); ++i) {
            if (((String) dataHeader.get(i)).equals("TestCaseMethod")) {
                index = i;
                break;
            }
        }

        if (index == 0) {
//            Message.error("Please make sure the column 'TestCaseMethod' existing in the test TestData file!");
            throw new RuntimeException("Please make sure the column 'TestCaseMethod' existing in the test TestData file!");

        }

        int newLenth = 0;

        for (int i = 0; i < arrayDataBody01.length; ++i) {
            String str1 = (String) arrayDataBody01[i][index];
            if (str1.equals(this.methodName)) {
                ++newLenth;
            }
        }

        Object[][] arrayDataBody02 = new Object[newLenth][];
        int newIndex = 0;

        for (int i = 0; i < arrayDataBody01.length; ++i) {
            String str2 = (String) arrayDataBody01[i][index];
            if (str2.equals(this.methodName)) {
                arrayDataBody02[newIndex] = arrayDataBody01[i];
                ++newIndex;
            }
        }

        if (newIndex == 0) {
            throw new RuntimeException("Test TestData file does not have TestData for current method '" + this.methodName + "'!");

//            Message.error("Test TestData file does not have TestData for current method '" + this.methodName + "'!");
        }

        return arrayDataBody02;
    }

    //测试main方法

//    public static void main(String[] args) {
//        String fileName = System.getProperty("user.dir")+"/src/test/testcases/resources/login/provider.xls";
//        DataTable dataTable = new DataTable();
//        dataTable.setDataTable(fileName, "Sheet1", "test01");
//        System.out.println("Test Data file path is: " + fileName);
//        Message.outPut("Test Data file path is: " + fileName);
//        Iterator<Object[]> it = dataTable.getDataTable();
//        System.out.println("Test");
//        Message.outPut("Test");
//    }
}

