import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import static Constants.Constants.*;

public abstract class Common {
    private int firstShiftDividerIndex;

    protected void setFirstShiftDividerIndex(Workbook workbook) {
        Row row = workbook.getSheetAt(0).getRow(ROW_START_INDEX);
        for (int i = COLUMN_START_INDEX; i < COLUMN_END_INDEX; i++) {
            String cellValue = new DataFormatter().formatCellValue(row.getCell(i));
            if (cellValue.equals(LAST_FIRST_SHIFT_GRADE)) {
                this.firstShiftDividerIndex = i + 1;
                break;
            }
        }
    }

    protected String getGrade(String grade) {
        //in the excel table the grade '11e' contains english letter 'e' so we return it with bulgarian letter 'e'
        return grade.equals(ENGLISH_TEXT_GRADE) ? "11е" : grade;
    }

    public int getFirstShiftDividerIndex() {
        return firstShiftDividerIndex;
    }
}
