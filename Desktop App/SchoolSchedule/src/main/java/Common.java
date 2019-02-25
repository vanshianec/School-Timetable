package main.java;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.LinkedHashMap;
import java.util.Map;

import static Constants.Constants.*;

public abstract class Common {
    private int gradesColumnEndIndex;
    private int roomsColumnEndIndex;
    private int shiftsColumnEndIndex;
    private Map<String,Integer> gradesShift;

    public Map<String, Integer> getGradesShift() {
        return gradesShift;
    }

    protected Common(){
        this.gradesShift = new LinkedHashMap<>();
    }

    public void setGradesShift(Workbook workbook) {
        this.setColumnEndIndex(workbook,8);
        for (int i = COLUMN_START_INDEX; i < this.getShiftsColumnEndIndex(); i++) {
            //TODO CONSTANTS
            String grade = new DataFormatter().formatCellValue(workbook.getSheetAt(8).getRow(0).getCell(i));
            grade = this.getGrade(grade);
            int shift = Integer.parseInt(new DataFormatter().
                    formatCellValue(workbook.getSheetAt(8).getRow(ROW_START_INDEX).getCell(i)));
            this.gradesShift.put(grade, shift);
        }
    }

    public void setColumnEndIndex(Workbook workbook, int sheetIndex) {
        int endIndex = COLUMN_START_INDEX;
        while (!new DataFormatter().formatCellValue(workbook.getSheetAt(sheetIndex).getRow(ROW_START_INDEX).getCell(endIndex)).isEmpty()) {
            endIndex++;
        }
        if (sheetIndex == 8) {
            this.shiftsColumnEndIndex = endIndex;
        } else if (sheetIndex == 0 || sheetIndex == 1 || sheetIndex == 2) {
            this.gradesColumnEndIndex = endIndex;
        } else {
            this.roomsColumnEndIndex = endIndex;
        }
    }

    //TODO FIX E
    protected String getGrade(String grade) {
        //in the excel table the grade '11e' contains english letter 'e' so we return it with bulgarian letter 'e'
        return grade.equals(ENGLISH_TEXT_GRADE) ? "11е" : grade;
    }

    public int getGradesColumnEndIndex() {
        return gradesColumnEndIndex;
    }

    public int getRoomsColumnEndIndex() {
        return roomsColumnEndIndex;
    }

    public int getShiftsColumnEndIndex() {
        return shiftsColumnEndIndex;
    }
}
