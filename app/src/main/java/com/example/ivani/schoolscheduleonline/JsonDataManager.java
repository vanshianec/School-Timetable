package com.example.ivani.schoolscheduleonline;

import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonDataManager {
    private List<TabRow> tabRowList;
    private String jsonString;
    private String teacherDay;
    private String roomDay;
    private String day;
    private RowDataManager dataManager;

    public JsonDataManager(String jsonString, String teacherDay, String roomDay, String day, RowDataManager dataManager) {
        this.jsonString = jsonString;
        this.teacherDay = teacherDay;
        this.roomDay = roomDay;
        this.day = day;
        this.dataManager = dataManager;
        this.tabRowList = new ArrayList<>();
    }

    public void parseJson() {
        int shift = 0;
        int orderCount = 1;
        try {
            JSONArray jsonarray = new JSONArray(this.jsonString);
            if (teacherDay.contains("teacher")) {
                shift = jsonarray.getJSONObject(0).getInt("shift");
            }
            String clockTime;
            int order;
            String name;
            String room;
            String borderColor;
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                name = jsonobject.getString(this.teacherDay);
                //check if teacher exists in the current tab row
                if (name != null && !name.isEmpty() && !name.equals("0")) {
                    order = jsonobject.getInt("order_id");
                    //set clock time based on shift
                    if (teacherDay.contains("teacher")) {
                        clockTime = this.dataManager.getCustomClockTimeBasedOnShift(shift, order);
                    } else {
                        clockTime = this.dataManager.getTeacherCustomClockTime(order);
                    }
                    name = name.replace("/", "\n");
                    room = jsonobject.getString(this.roomDay);
                    room = room.replace("/", " / ");
                    borderColor = this.dataManager.getColorBasedOnRealTime(clockTime, this.day);
                    if (teacherDay.contains("teacher")) {
                        this.tabRowList.add(new TabRow(order, clockTime, name, room, borderColor));
                    } else {
                        this.tabRowList.add(new TabRow(orderCount, clockTime, name, room, borderColor));
                        orderCount++;
                    }
                }
            }

        } catch (JSONException e) {
            Log.d(e.getMessage(), "JSON parsing error");
            e.printStackTrace();
        }

    }

    public List<TabRow> getTabRowList() {
        return this.tabRowList;
    }

}
