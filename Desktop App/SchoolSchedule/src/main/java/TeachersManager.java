package main.java;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static Constants.Constants.*;

public class TeachersManager extends Common {
    private Map<Integer, Map<Integer, String>> mondayGrades;
    private Map<Integer, Map<Integer, String>> tuesdayGrades;
    private Map<Integer, Map<Integer, String>> wednesdayGrades;
    private Map<Integer, Map<Integer, String>> thursdayGrades;
    private Map<Integer, Map<Integer, String>> fridayGrades;

    private Map<Integer, Map<Integer, Integer>> mondayRooms;
    private Map<Integer, Map<Integer, Integer>> tuesdayRooms;
    private Map<Integer, Map<Integer, Integer>> wednesdayRooms;
    private Map<Integer, Map<Integer, Integer>> thursdayRooms;
    private Map<Integer, Map<Integer, Integer>> fridayRooms;

    //TODO ADD TEACHER ID AND SUBJECT
    private Map<Integer,String> teacherIdAndName;

    public Map<Integer, Map<Integer, String>> getMondayGrades() {
        return mondayGrades;
    }

    public Map<Integer, Map<Integer, String>> getTuesdayGrades() {
        return tuesdayGrades;
    }

    public Map<Integer, Map<Integer, String>> getWednesdayGrades() {
        return wednesdayGrades;
    }

    public Map<Integer, Map<Integer, String>> getThursdayGrades() {
        return thursdayGrades;
    }

    public Map<Integer, Map<Integer, String>> getFridayGrades() {
        return fridayGrades;
    }

    public TeachersManager(Workbook workbook, int[] indices) {
        super.setGradesShift(workbook);
        setTeachers(workbook, indices);
    }

    private void setTeachers(Workbook workbook, int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            setSortedTeachers(workbook, indices[i]);
        }
    }

    private void setSortedTeachers(Workbook workbook, int index) {
        Sheet sheet = workbook.getSheetAt(index);
        //Set column end index based on sheet index
        //example : if the sheet index is '5' then the roomsColumnEndIndex will be set in the superclass
        //or if the sheet index is '1' then the gradesColumnEndIndex will be set
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
                this.mondayRooms = sortRooms(sheet, ROOM_ROW_END_INDEX, ROOM_ROW_SECOND_SHIFT_START_INDEX, ROOM_ROW_SECOND_SHIFT_END_INDEX);
                break;
            case 4:
                this.tuesdayRooms = sortRooms(sheet, ROOM_ROW_END_INDEX, ROOM_ROW_SECOND_SHIFT_START_INDEX, ROOM_ROW_SECOND_SHIFT_END_INDEX);
                break;
            case 5:
                this.wednesdayRooms = sortRooms(sheet, ROOM_ROW_END_INDEX, ROOM_ROW_SECOND_SHIFT_START_INDEX, ROOM_ROW_SECOND_SHIFT_END_INDEX);
                break;
            case 6:
                this.thursdayRooms = sortRooms(sheet, ROOM_ROW_END_INDEX, ROOM_ROW_SECOND_SHIFT_START_INDEX, ROOM_ROW_SECOND_SHIFT_END_INDEX);
                break;
            case 7:
                this.fridayRooms = sortRooms(sheet, FRIDAY_ROOM_FIRST_SHIFT_END_INDEX, FRIDAY_ROOM_SECOND_SHIFT_START_INDEX, FRIDAY_ROOM_SECOND_SHIFT_END_INDEX);
            case 9:
               // this.teacherIdAndName =
               // break;
        }
    }

    private Map<Integer, Map<Integer, String>> sortGrades(Sheet sheet, int rowStartIndex, int rowEndIndex) {
        Map<Integer, Map<Integer, String>> sortedGrades = new LinkedHashMap<>();
        for (int i = rowStartIndex + 1; i < rowEndIndex; i++) {
            for (int j = COLUMN_START_INDEX; j < super.getGradesColumnEndIndex(); j++) {
                String teacherIds = new DataFormatter().formatCellValue(sheet.getRow(i).getCell(j));
                if (teacherIdsIsValid(teacherIds)) {
                    sortedGrades = addSortedGrades(sortedGrades, teacherIds, j, i, sheet);
                }
            }
        }
        return sortedGrades;
    }

    private Map<Integer, Map<Integer, String>> addSortedGrades(Map<Integer, Map<Integer, String>> sortedGrades, String teacherIds,
                                                               int column, int row, Sheet sheet) {
        if (teacherIds.contains("ч")) {
            teacherIds = teacherIds.replace("ч", "");
        }
        if (teacherIds.contains("\n")) {
            teacherIds = teacherIds.replace("\n", "");
        }
        teacherIds = teacherIds.trim();
        String[] ids = teacherIds.split("\\s+");
        if (ids.length == 2) {
            int firstTeacherId = Integer.parseInt(ids[0]);
            int secondTeacherId = Integer.parseInt(ids[1]);
            if (!sortedGrades.containsKey(firstTeacherId)) {
                sortedGrades.put(Integer.parseInt(ids[0]), new TreeMap<>());
            }
            sortedGrades = addSortedGradesValue(sortedGrades, firstTeacherId, column, row, sheet);
            if (!sortedGrades.containsKey(secondTeacherId)) {
                sortedGrades.put(Integer.parseInt(ids[1]), new TreeMap<>());
            }
            sortedGrades = addSortedGradesValue(sortedGrades, secondTeacherId, column, row, sheet);
        } else if (ids.length == 1) {
            int teacherId = Integer.parseInt(ids[0]);
            if (!sortedGrades.containsKey(teacherId)) {
                sortedGrades.put(teacherId, new TreeMap<>());
            }
            sortedGrades = addSortedGradesValue(sortedGrades, teacherId, column, row, sheet);
        }
        return sortedGrades;

    }

    private Map<Integer, Map<Integer, String>> addSortedGradesValue(Map<Integer, Map<Integer, String>> sortedGrades,
                                                                    int teacherId, int column, int row, Sheet sheet) {

        int orderValue = Integer.parseInt(new DataFormatter().formatCellValue(sheet.getRow(row).getCell(ORDER_COLUMN_START_INDEX)));
        String grade = new DataFormatter().formatCellValue(sheet.getRow(ROW_START_INDEX).getCell(column));
        grade = super.getGrade(grade);
        int order = super.getGradesShift().get(grade) == 1 ? orderValue : orderValue + 8;
        sortedGrades.get(teacherId).put(order, grade);
        return sortedGrades;
    }

    public Map<Integer, Map<Integer, Integer>> getMondayRooms() {
        return mondayRooms;
    }

    public Map<Integer, Map<Integer, Integer>> getTuesdayRooms() {
        return tuesdayRooms;
    }

    public Map<Integer, Map<Integer, Integer>> getWednesdayRooms() {
        return wednesdayRooms;
    }

    public Map<Integer, Map<Integer, Integer>> getThursdayRooms() {
        return thursdayRooms;
    }

    public Map<Integer, Map<Integer, Integer>> getFridayRooms() {
        return fridayRooms;
    }

    private Map<Integer, Map<Integer, Integer>> sortRooms(Sheet sheet, int endIndex, int secondShiftStartIndex, int secondShiftEndIndex) {
        Map<Integer, Map<Integer, Integer>> sortedRooms = new LinkedHashMap<>();
        int orderAdderBasedOnShift = 0;
        for (int i = ROW_START_INDEX + 2; i < endIndex; i++) {
            addSortedRooms(sheet, sortedRooms, orderAdderBasedOnShift, i, ROW_START_INDEX);
        }
        orderAdderBasedOnShift = 8;
        for (int i = secondShiftStartIndex + 2; i < secondShiftEndIndex; i++) {
            addSortedRooms(sheet, sortedRooms, orderAdderBasedOnShift, i, secondShiftStartIndex);
        }

        return sortedRooms;
    }

    private void addSortedRooms(Sheet sheet, Map<Integer, Map<Integer, Integer>> sortedRooms, int orderAdderBasedOnShift, int i, int startIndex) {
        for (int j = ROOM_COLUMN_START_INDEX; j < super.getRoomsColumnEndIndex(); j++) {
            //TODO ADD TO SUPERCLASS
            int room;
            if (j == super.getRoomsColumnEndIndex() - 3) {
                room = 1;
            } else if (j == super.getRoomsColumnEndIndex() - 2) {
                room = 2;
            } else if (j == super.getRoomsColumnEndIndex() - 1) {
                room = 3;
            } else {
                room = Integer.parseInt(new DataFormatter().formatCellValue(sheet.getRow(startIndex).getCell(j)));
            }
            String teacherIdValue = new DataFormatter().formatCellValue(sheet.getRow(i).getCell(j));
            if (teacherIdValue.equals("")) {
                continue;
            }
            int teacherId = Integer.parseInt(teacherIdValue.replace("ч", "").trim());
            int order = Integer.parseInt(new DataFormatter().formatCellValue(sheet.getRow(i).getCell(ORDER_COLUMN_START_INDEX)));
            order += orderAdderBasedOnShift;

            if (!sortedRooms.containsKey(teacherId)) {
                sortedRooms.put(teacherId, new TreeMap<>());
            }
            sortedRooms.get(teacherId).put(order, room);
        }
    }


    private boolean teacherIdsIsValid(String teacherIds) {
        return !teacherIds.contains("ф") && !teacherIds.contains("-") && !teacherIds.contains("/") && !teacherIds.equals("") &&
                !teacherIds.equalsIgnoreCase("ТП") && teacherIds.split("\\s+").length <= 2;
    }

}
