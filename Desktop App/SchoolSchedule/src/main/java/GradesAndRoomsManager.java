package main.java;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static Constants.Constants.*;

public class GradesAndRoomsManager extends Common {

    private Map<String, Map<Integer, String>> mondayGrades;
    private Map<String, Map<Integer, String>> tuesdayGrades;
    private Map<String, Map<Integer, String>> wednesdayGrades;
    private Map<String, Map<Integer, String>> thursdayGrades;
    private Map<String, Map<Integer, String>> fridayGrades;

    private Map<Integer, Map<Integer, String>> mondayFirstShiftRooms;
    private Map<Integer, Map<Integer, String>> mondaySecondShiftRooms;
    private Map<Integer, Map<Integer, String>> tuesdayFirstShiftRooms;
    private Map<Integer, Map<Integer, String>> tuesdaySecondShiftRooms;
    private Map<Integer, Map<Integer, String>> wednesdayFirstShiftRooms;
    private Map<Integer, Map<Integer, String>> wednesdaySecondShiftRooms;
    private Map<Integer, Map<Integer, String>> thursdayFirstShiftRooms;
    private Map<Integer, Map<Integer, String>> thursdaySecondShiftRooms;
    private Map<Integer, Map<Integer, String>> fridayFirstShiftRooms;
    private Map<Integer, Map<Integer, String>> fridaySecondShiftRooms;

    private Map<String, Map<Integer, String>> mondayRooms;
    private Map<String, Map<Integer, String>> tuesdayRooms;
    private Map<String, Map<Integer, String>> wednesdayRooms;
    private Map<String, Map<Integer, String>> thursdayRooms;
    private Map<String, Map<Integer, String>> fridayRooms;

    public GradesAndRoomsManager(Workbook workbook, int[] indices) {
        super.setGradesShift(workbook);
        setGradesAndRooms(workbook, indices);
        setGradesShift(workbook);
        setRoomsResult();
    }

    private void setGradesAndRooms(Workbook workbook, int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            setSortedGradesAndRooms(workbook, indices[i]);
        }

    }

    private void setSortedGradesAndRooms(Workbook workbook, int index) {
        Sheet sheet = workbook.getSheetAt(index);
        super.setColumnEndIndex(workbook, index);
        switch (index) {
            case 0:
                this.mondayGrades = sortGrades(sheet, ROW_START_INDEX, MONDAY_ROW_END_INDEX);
                this.tuesdayGrades = sortGrades(sheet, TUESDAY_ROW_START_INDEX, TUESDAY_ROW_END_INDEX);
                break;
            case 1:
                this.wednesdayGrades = sortGrades(sheet, ROW_START_INDEX, WEDNESDAY_ROW_END_INDEX);
                this.thursdayGrades = sortGrades(sheet, THURSDAY_ROW_START_INDEX, THURSDAY_ROW_END_INDEX);
                break;
            case 2:
                this.fridayGrades = sortGrades(sheet, ROW_START_INDEX, FRIDAY_ROW_END_INDEX);
                break;
            case 3:
                this.mondayFirstShiftRooms = sortRooms(sheet, ROW_START_INDEX, ROOM_ROW_END_INDEX);
                this.mondaySecondShiftRooms = sortRooms(sheet, ROOM_ROW_SECOND_SHIFT_START_INDEX, ROOM_ROW_SECOND_SHIFT_END_INDEX);
                break;
            case 4:
                this.tuesdayFirstShiftRooms = sortRooms(sheet, ROW_START_INDEX, ROOM_ROW_END_INDEX);
                this.tuesdaySecondShiftRooms = sortRooms(sheet, ROOM_ROW_SECOND_SHIFT_START_INDEX, ROOM_ROW_SECOND_SHIFT_END_INDEX);
                break;
            case 5:
                this.wednesdayFirstShiftRooms = sortRooms(sheet, ROW_START_INDEX, ROOM_ROW_END_INDEX);
                this.wednesdaySecondShiftRooms = sortRooms(sheet, ROOM_ROW_SECOND_SHIFT_START_INDEX, ROOM_ROW_SECOND_SHIFT_END_INDEX);
                break;
            case 6:
                this.thursdayFirstShiftRooms = sortRooms(sheet, ROW_START_INDEX, ROOM_ROW_END_INDEX);
                this.thursdaySecondShiftRooms = sortRooms(sheet, ROOM_ROW_SECOND_SHIFT_START_INDEX, ROOM_ROW_SECOND_SHIFT_END_INDEX);
                break;
            case 7:
                //TODO RENAME AND ROW
                this.fridayFirstShiftRooms = sortRooms(sheet, ROW_START_INDEX, FRIDAY_ROOM_FIRST_SHIFT_END_INDEX);
                this.fridaySecondShiftRooms = sortRooms(sheet, FRIDAY_ROOM_SECOND_SHIFT_START_INDEX, FRIDAY_ROOM_SECOND_SHIFT_END_INDEX);
                break;
        }
    }

    private Map<String, Map<Integer, String>> sortGrades(Sheet sheet, int rowStartIndex, int rowEndIndex) {

        Row gradesRow = sheet.getRow(rowStartIndex);

        Map<String, Map<Integer, String>> sortedGrades = new LinkedHashMap<>();
        for (int i = COLUMN_START_INDEX; i < super.getGradesColumnEndIndex(); i++) {
            String grade = new DataFormatter().formatCellValue(gradesRow.getCell(i));
            grade = getGrade(grade);
            sortedGrades.put(grade, new LinkedHashMap<>());
            for (int j = rowStartIndex + 1; j < rowEndIndex; j++) {
                addInnerMap(sheet, i, j, sortedGrades.get(grade));
            }
        }
        return sortedGrades;
    }

    private Map<Integer, Map<Integer, String>> sortRooms(Sheet sheet, int rowStartIndex, int rowEndIndex) {
        Row roomsRow = sheet.getRow(rowStartIndex);
        Map<Integer, Map<Integer, String>> sortedRooms = new LinkedHashMap<>();
        //then add the rooms
        for (int i = ROOM_COLUMN_START_INDEX; i < super.getRoomsColumnEndIndex(); i++) {
            int room;
            //empty column in the table
            if (i == super.getRoomsColumnEndIndex() - 3) {
                room = 1;
            } else if (i == super.getRoomsColumnEndIndex() - 2) {
                room = 2;
            } else if (i == super.getRoomsColumnEndIndex() - 1) {
                room = 3;
            } else {
                room = Integer.parseInt(new DataFormatter().formatCellValue(roomsRow.getCell(i)));
            }
            sortedRooms.put(room, new LinkedHashMap<>());
            for (int j = rowStartIndex + 2; j < rowEndIndex; j++) {
                addInnerMap(sheet, i, j, sortedRooms.get(room));
            }

        }
        return sortedRooms;
    }

    private void addInnerMap(Sheet sheet, int i, int j, Map<Integer, String> integerStringMap) {
        Row row = sheet.getRow(j);
        int order = Integer.parseInt(new DataFormatter().formatCellValue(row.getCell(ORDER_COLUMN_START_INDEX)));
        String teacherId = new DataFormatter().formatCellValue(row.getCell(i));
        //check if the cell is valid
        if (!(teacherId.equals("") || teacherId.contains("ф") || teacherId.contains("-") ||
                teacherId.contains("/") || teacherId.split("\\s+").length > 2)) {

            //TODO DUPLICATED CODE
            if (teacherId.contains("ч")) {
                teacherId = teacherId.replace("ч", "");
            }
            //TODO TEST WITH TRIM
            if (teacherId.contains("\n")) {
                teacherId = teacherId.replace("\n", "");
            }
            teacherId = teacherId.trim();
            integerStringMap.put(order, teacherId);
        }
    }



    private void setRoomsResult() {
        this.mondayRooms = getSortedRooms(mondayGrades, mondayFirstShiftRooms, mondaySecondShiftRooms);
        this.tuesdayRooms = getSortedRooms(tuesdayGrades, tuesdayFirstShiftRooms, tuesdaySecondShiftRooms);
        this.wednesdayRooms = getSortedRooms(wednesdayGrades, wednesdayFirstShiftRooms, wednesdaySecondShiftRooms);
        this.thursdayRooms = getSortedRooms(thursdayGrades, thursdayFirstShiftRooms, thursdaySecondShiftRooms);
        this.fridayRooms = getSortedRooms(fridayGrades, fridayFirstShiftRooms, fridaySecondShiftRooms);
    }

    private Map<String, Map<Integer, String>> getSortedRooms(
            Map<String, Map<Integer, String>> dayGrades,
            Map<Integer, Map<Integer, String>> dayFirstShiftRooms,
            Map<Integer, Map<Integer, String>> daySecondShiftRooms) {

        Map<String, Map<Integer, String>> result = new LinkedHashMap<>();

        for (Map.Entry<String, Map<Integer, String>> gradesEntry : dayGrades.entrySet()) {
            //example key: 12г
            String grade = gradesEntry.getKey();
            //example value: key - 1 : value - 54 23
            Map<Integer, String> orderTeacherIdGrade = gradesEntry.getValue();
            //get rooms table based on the grade's shift
            Map<Integer, Map<Integer, String>> dayRooms = super.getGradesShift().get(grade) == 1 ? dayFirstShiftRooms : daySecondShiftRooms;
            for (Map.Entry<Integer, Map<Integer, String>> roomsEntry : dayRooms.entrySet()) {
                //example key: 504
                String room = roomsEntry.getKey() + "";
                //example value: key - 1 : value - 54
                Map<Integer, String> orderTeacherIdRoom = roomsEntry.getValue();
                for (Map.Entry<Integer, String> orderTeacherIdEntry : orderTeacherIdRoom.entrySet()) {
                    //room order can be 0,1,2,3,4,5,6,7
                    int order = orderTeacherIdEntry.getKey();
                    String teacherIdRoom = orderTeacherIdEntry.getValue();
                    if (orderTeacherIdGrade.containsKey(order)) {
                        //get the teacher id from grades
                        //note: there can be one or two teacher ids
                        String teacherIdGrade = orderTeacherIdGrade.get(order);
                        String[] teacherIdArray = teacherIdGrade.split("\\s+");
                        //check if there are two teacher ids
                        if (teacherIdArray.length == 2) {
                            String firstTeacherId = teacherIdArray[0];
                            String secondTeacherId = teacherIdArray[1];
                            //get two rooms since there two teacher ids
                            String rooms = getTwoTeachersRooms(order, firstTeacherId, secondTeacherId, dayRooms);
                            if (!result.containsKey(grade)) {
                                result.put(grade, new TreeMap<>());
                            }
                            result.get(grade).put(order, rooms);
                        } else if (teacherIdArray.length == 1) {
                            if (!result.containsKey(grade)) {
                                result.put(grade, new TreeMap<>());
                            }
                            //this subject has no room
                            if (teacherIdGrade.equalsIgnoreCase("ТП")) {
                                result.get(grade).put(order, "ТП");
                            } else if (teacherIdRoom.trim().equals(teacherIdGrade.trim())) {
                                result.get(grade).put(order, room);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private String getTwoTeachersRooms(int order, String firstTeacherId, String secondTeacherId, Map<Integer, Map<Integer, String>> dayRooms) {
        String firstRoom = "";
        String secondRoom = "";
        for (Map.Entry<Integer, Map<Integer, String>> roomsEntry : dayRooms.entrySet()) {
            String room = roomsEntry.getKey() + "";
            Map<Integer, String> orderTeacherIdRoom = roomsEntry.getValue();
            for (Map.Entry<Integer, String> orderTeacherIdEntry : orderTeacherIdRoom.entrySet()) {
                int currentOrder = orderTeacherIdEntry.getKey();
                if (order == currentOrder) {
                    String teacherId = orderTeacherIdEntry.getValue();
                    if (teacherId.equals(firstTeacherId)) {
                        firstRoom = room;
                    } else if (teacherId.equals(secondTeacherId)) {
                        secondRoom = room;
                    }
                }
            }
        }
        return firstRoom + "/" + secondRoom;
    }

    public Map<String, Integer> getGradesShift() {
        return super.getGradesShift();
    }

    public Map<String, Map<Integer, String>> getMondayGrades() {
        return mondayGrades;
    }

    public Map<String, Map<Integer, String>> getTuesdayGrades() {
        return tuesdayGrades;
    }

    public Map<String, Map<Integer, String>> getWednesdayGrades() {
        return wednesdayGrades;
    }

    public Map<String, Map<Integer, String>> getThursdayGrades() {
        return thursdayGrades;
    }

    public Map<String, Map<Integer, String>> getFridayGrades() {
        return fridayGrades;
    }

    public Map<String, Map<Integer, String>> getMondayRooms() {
        return mondayRooms;
    }

    public Map<String, Map<Integer, String>> getTuesdayRooms() {
        return tuesdayRooms;
    }

    public Map<String, Map<Integer, String>> getWednesdayRooms() {
        return wednesdayRooms;
    }

    public Map<String, Map<Integer, String>> getThursdayRooms() {
        return thursdayRooms;
    }

    public Map<String, Map<Integer, String>> getFridayRooms() {
        return fridayRooms;
    }
}
