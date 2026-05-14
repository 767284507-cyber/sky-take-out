package com.sky.test.auto.util.provider;

import java.util.LinkedHashMap;
import java.util.List;

public interface DataSource {
    void setDataSource(String var1, String var2);

    void setDataSourceArray(String var1, String var2);

    int getRowCount();

    int getColCount();

    List<String> getDataHeader();

    Object[][] getDataBodyArray();

    List<LinkedHashMap<String, String>> getDataBodyMap();
}