package com.example.ivani.schoolscheduleonline;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class RowDataManager {


    public String getColorBasedOnRealTime(String customTime, String day) {
        //short break time
        int tenMinutesInMillis = 600000;
        //there are two long breaks during the day the first is when the hour is '10' and the second is when the hours is '16'
        if (customTime.startsWith("10") || customTime.startsWith("16")) {
            tenMinutesInMillis = 600000 * 2;
        }
        //time example -  "7 : 30 - 8 : 10"
        String startTime = customTime.split(" - ")[0];
        String endTime = customTime.split(" - ")[1];
        int startTimeHours = Integer.parseInt(startTime.split(" : ")[0]);
        int startTimeMinutes = Integer.parseInt(startTime.split(" : ")[1]);
        int endTimeHours = Integer.parseInt(endTime.split(" : ")[0]);
        int endTimeMinutes = Integer.parseInt(endTime.split(" : ")[1]);
        //get the time when the subject starts
        Calendar calendarFirst = Calendar.getInstance();
        calendarFirst.set(Calendar.HOUR_OF_DAY, startTimeHours);
        calendarFirst.set(Calendar.MINUTE, startTimeMinutes);
        calendarFirst.set(Calendar.SECOND, 0);
        //get the time when the subject ends
        Calendar calendarSecond = Calendar.getInstance();
        calendarSecond.set(Calendar.HOUR_OF_DAY, endTimeHours);
        calendarSecond.set(Calendar.MINUTE, endTimeMinutes);
        calendarSecond.set(Calendar.SECOND, 0);
        //get the current time
        Calendar currentTimeCalendar = Calendar.getInstance();

        long firstTime = calendarFirst.getTimeInMillis() - tenMinutesInMillis;
        long secondTime = calendarSecond.getTimeInMillis();
        long realTime = currentTimeCalendar.getTimeInMillis();
        String realTimeDay = getDayOfWeek(currentTimeCalendar.get(Calendar.DAY_OF_WEEK));
        //if the current time is between the end of the current subject and the end of the next subject we return a color which is not white
        return (firstTime < realTime && realTime <= secondTime && realTimeDay.equals(day)) ? "#D81B60" : "#FFFFFF";
    }

    public String getCustomClockTimeBasedOnShift(int shift, int order) {
        String clockTime = "";
        switch (shift) {
            case 1:
                switch (order) {
                    case 1:
                        clockTime = "7 : 30 - 8 : 10";
                        break;
                    case 2:
                        clockTime = "8 : 20 - 9 : 00";
                        break;
                    case 3:
                        clockTime = "9 : 10 - 9 : 50";
                        break;
                    case 4:
                        clockTime = "10 : 10 - 10 : 50";
                        break;
                    case 5:
                        clockTime = "11 : 00 - 11 : 40";
                        break;
                    case 6:
                        clockTime = "11 : 50 - 12 : 30";
                        break;
                    case 7:
                        clockTime = "12 : 40 - 13 : 20";
                        break;
                }
                break;
            case 2:
                switch (order) {
                    case 0:
                        clockTime = "12 : 40 - 13 : 20";
                        break;
                    case 1:
                        clockTime = "13 : 30 - 14 : 10";
                        break;
                    case 2:
                        clockTime = "14 : 20 - 15 : 00";
                        break;
                    case 3:
                        clockTime = "15 : 10 - 15 : 50";
                        break;
                    case 4:
                        clockTime = "16 : 10 - 16 : 50";
                        break;
                    case 5:
                        clockTime = "17 : 00 - 17 : 40";
                        break;
                    case 6:
                        clockTime = "17 : 50 - 18 : 30";
                        break;
                    case 7:
                        clockTime = "18 : 40 - 19 : 20";
                        break;
                }
                break;
        }
        return clockTime;
    }

    public String getTeacherCustomClockTime(int order) {
        String clockTime = "";
        switch (order) {
            case 1:
                clockTime = "7 : 30 - 8 : 10";
                break;
            case 2:
                clockTime = "8 : 20 - 9 : 00";
                break;
            case 3:
                clockTime = "9 : 10 - 9 : 50";
                break;
            case 4:
                clockTime = "10 : 10 - 10 : 50";
                break;
            case 5:
                clockTime = "11 : 00 - 11 : 40";
                break;
            case 6:
                clockTime = "11 : 50 - 12 : 30";
                break;
            case 7:
                clockTime = "12 : 40 - 13 : 20";
                break;
            case 8:
                clockTime = "13 : 30 - 14 : 10";
                break;
            case 9:
                clockTime = "14 : 20 - 15 : 00";
                break;
            case 10:
                clockTime = "15 : 10 - 15 : 50";
                break;
            case 11:
                clockTime = "16 : 10 - 16 : 50";
                break;
            case 12:
                clockTime = "17 : 00 - 17 : 40";
                break;
            case 13:
                clockTime = "17 : 50 - 18 : 30";
                break;
            case 14:
                clockTime = "18 : 40 - 19 : 20";
                break;
        }
        return clockTime;
    }

    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
        }
        return day;
    }
}
