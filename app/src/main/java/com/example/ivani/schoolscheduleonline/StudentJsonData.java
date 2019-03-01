package com.example.ivani.schoolscheduleonline;

import android.content.Context;

import com.example.ivani.schoolscheduleonline.BaseClasses.AbstractJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentJsonData extends AbstractJsonData {

    private String teacherDay;

    public StudentJsonData(String jsonString, String teacherDay, String roomDay, String day, RowDataManager rowDataManager, Context context) {
        super(jsonString, roomDay, day, rowDataManager, context);
        this.teacherDay = teacherDay;
    }

    @Override
    public void parseJson() {
        try {
            JSONArray jsonarray = new JSONArray(super.getJsonString());
            int shift = jsonarray.getJSONObject(getDatabaseRowIndex())
                    .getInt(getDatabaseShiftColumnName());
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString(this.teacherDay);
                //check if teacher exists in the current tab row
                if (name != null && !name.isEmpty() && !name.equals("0")) {
                    //add the result to the tab row list
                    addResult(shift, name, jsonobject);
                }
            }
        } catch (JSONException e) {
            super.printError(e);
        }
    }

    private void addResult(int shift, String name, JSONObject jsonobject) throws JSONException {
        int order = jsonobject.getInt(getDatabaseOrderColumnName());
        //invalid excel table input
        if (order == 0 && shift == 1) {
            return;
        }
        //set clock time based on shift
        String clockTime = super.getRowDataManager().getCustomClockTimeBasedOnShift(shift, order);
        //display two teachers on separated lines for better view
        //example teacherName1/teacherName2 -> teacherName1 \n teacherName2
        name = name.replace("/", "\n");
        String room = jsonobject.getString(super.getRoomDay());
        //add space between two rooms for better view
        room = room.replace("/", " / ");
        //set the border color on the current tab row
        String borderColor = super.getRowDataManager().getColorBasedOnRealTime(clockTime, super.getDay());
        super.addResult(new TabRow(order, clockTime, name, room, borderColor));
    }


}
