package com.example.ivani.schoolscheduleonline.BaseClasses;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.ivani.schoolscheduleonline.Contracts.JsonData;
import com.example.ivani.schoolscheduleonline.RowDataManager;
import com.example.ivani.schoolscheduleonline.TabRow;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJsonData implements JsonData {
    private static final String DATABASE_ORDER_COLUMN_NAME = "order_id";
    private static final int DATABASE_ROW_INDEX = 0;
    private static final String DATABASE_SHIFT_COLUMN_NAME = "shift";

    private List<TabRow> tabRowList;
    private String jsonString;
    private String roomDay;
    private String day;
    private RowDataManager rowDataManager;
    private Context context;

    protected AbstractJsonData(String jsonString, String roomDay, String day, RowDataManager rowDataManager, Context context) {
        this.jsonString = jsonString;
        this.roomDay = roomDay;
        this.day = day;
        this.rowDataManager = rowDataManager;
        this.context = context;
        this.tabRowList = new ArrayList<>();
    }

    protected static String getDatabaseOrderColumnName() {
        return DATABASE_ORDER_COLUMN_NAME;
    }

    protected static int getDatabaseRowIndex() {
        return DATABASE_ROW_INDEX;
    }

    protected static String getDatabaseShiftColumnName() {
        return DATABASE_SHIFT_COLUMN_NAME;
    }

    protected String getJsonString() {
        return jsonString;
    }

    protected String getRoomDay() {
        return roomDay;
    }

    protected String getDay() {
        return day;
    }

    protected RowDataManager getRowDataManager() {
        return rowDataManager;
    }

    public abstract void parseJson();

    @Override
    public void printError(Exception e) {
        Log.d(e.getMessage(), "JSON parsing error");
        Toast.makeText(this.context, "Грешка при обработването на данните. Моля, опитайте по-късно.", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    @Override
    public List<TabRow> getResultList() {
        return this.tabRowList;
    }

    @Override
    public void addResult(TabRow item) {
        this.tabRowList.add(item);
    }
}
