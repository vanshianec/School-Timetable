package com.example.ivani.schoolscheduleonline;

import android.content.Context;
import android.widget.Toast;

import com.example.ivani.schoolscheduleonline.BaseClasses.AbstractJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherJsonData extends AbstractJsonData {
    private String gradeDay;

    public TeacherJsonData(String jsonString, String gradeDay, String roomDay, String day, RowDataManager rowDataManager, Context context) {
        super(jsonString, roomDay, day, rowDataManager, context);
        this.gradeDay = gradeDay;
    }

    @Override
    public void parseJson() {
        int orderCount = 1;
        try {
            System.out.println();
            JSONArray jsonarray = new JSONArray(super.getJsonString());
            String name;
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                name = jsonobject.getString(this.gradeDay);
                //check if teacher exists in the current tab row
                if (name != null && !name.isEmpty() && !name.equals("0")) {
                    //add result to the tab row list
                    addResult(orderCount, name, jsonobject);
                    orderCount++;
                }
            }
        } catch (JSONException e) {
            super.printError(e);
        }
    }

    private void addResult(int orderCount, String name, JSONObject jsonobject) throws JSONException {
        int order = jsonobject.getInt(getDatabaseOrderColumnName());
        //set clock time based on subject order
        String clockTime = super.getRowDataManager().getTeacherCustomClockTime(order);
        String room = jsonobject.getString(super.getRoomDay());
        String borderColor = super.getRowDataManager().getColorBasedOnRealTime(clockTime, super.getDay());
        super.addResult(new TabRow(orderCount, clockTime, name, room, borderColor));
    }

}
