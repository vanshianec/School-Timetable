package Constants;

public final class Constants {
    public static final int COLUMN_START_INDEX = 1;
    //TODO GET COLUMN END INDEX
    //TODO
    public static final int ROOM_COLUMN_START_INDEX = 1;
    //TODO
    public static final int ORDER_COLUMN_START_INDEX = 0;
    public static final int ROW_START_INDEX = 1;
    public static final int ROOM_ROW_END_INDEX = 11;
    public static final int ROOM_ROW_SECOND_SHIFT_START_INDEX = 13;
    public static final int ROOM_ROW_SECOND_SHIFT_END_INDEX = 23;
    //TODO EXCEL ONE EXTRA ROW
    public static final int MONDAY_ROW_END_INDEX = 11;

    public static final int TUESDAY_ROW_START_INDEX = 12;
    public static final int TUESDAY_ROW_END_INDEX = 22;

    public static final int WEDNESDAY_ROW_END_INDEX = 10;

    public static final int THURSDAY_ROW_START_INDEX = 12;
    public static final int THURSDAY_ROW_END_INDEX = 21;

    public static final int FRIDAY_ROW_END_INDEX = 10;
    public static final int FRIDAY_ROOM_FIRST_SHIFT_END_INDEX = 11;
    public static final int FRIDAY_ROOM_SECOND_SHIFT_START_INDEX = 12;
    public static final int FRIDAY_ROOM_SECOND_SHIFT_END_INDEX = 22;

    public static final String ENGLISH_TEXT_GRADE = "11e";

    public static final String MONDAY_ROOM_COLUMN = "monday_room";
    public static final String TUESDAY_ROOM_COLUMN = "tuesday_room";
    public static final String WEDNESDAY_ROOM_COLUMN = "wednesday_room";
    public static final String THURSDAY_ROOM_COLUMN = "thursday_room";
    public static final String FRIDAY_ROOM_COLUMN = "friday_room";
    public static final String MONDAY_TEACHER_COLUMN = "monday_teacher";
    public static final String TUESDAY_TEACHER_COLUMN = "tuesday_teacher";
    public static final String WEDNESDAY_TEACHER_COLUMN = "wednesday_teacher";
    public static final String THURSDAY_TEACHER_COLUMN = "thursday_teacher";
    public static final String FRIDAY_TEACHER_COLUMN = "friday_teacher";
    public static final String MONDAY_GRADE_COLUMN = "monday_grade";
    public static final String TUESDAY_GRADE_COLUMN = "tuesday_grade";
    public static final String WEDNESDAY_GRADE_COLUMN = "wednesday_grade";
    public static final String THURSDAY_GRADE_COLUMN = "thursday_grade";
    public static final String FRIDAY_GRADE_COLUMN = "friday_grade";

    public static final int TEACHER_DATA_START_ROW = 1;
    public static final int TEACHER_ID_COLUMN = 0;
    public static final int TEACHER_NAME_COLUMN = 1;

    public static final String TEACHER_CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS `%1$d` (" +
            "  `order_id` int(11) NOT NULL," +
            "  `monday_grade` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL," +
            "  `monday_room` int(11) DEFAULT NULL," +
            "  `tuesday_grade` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL," +
            "  `tuesday_room` int(11) DEFAULT NULL," +
            "  `wednesday_grade` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL," +
            "  `wednesday_room` int(11) DEFAULT NULL," +
            "  `thursday_grade` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL," +
            "  `thursday_room` int(11) DEFAULT NULL," +
            "  `friday_grade` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL," +
            "  `friday_room` int(11) DEFAULT NULL," +
            "  PRIMARY KEY (`order_id`)" +
            ");" +
            "INSERT INTO `%1$d` (`order_id`, `monday_grade`, `monday_room`, `tuesday_grade`, `tuesday_room`, `wednesday_grade`, `wednesday_room`, `thursday_grade`, `thursday_room`, `friday_grade`, `friday_room`) VALUES" +
            "(1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(4, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(5, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(6, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(8, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(9, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(10, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(11, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(12, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(13, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(14, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);";

    public static final String GRADE_CREATE_TABLE_IF_NOT_EXISTS_QUERY = "CREATE TABLE IF NOT EXISTS `%1$s` (" +
            "`order_id` INT(1)," +
            "`monday_teacher` VARCHAR(50)," +
            "`monday_subject` VARCHAR(50)," +
            "`monday_room` VARCHAR(50)," +
            "`tuesday_teacher` VARCHAR(50)," +
            "`tuesday_subject` VARCHAR(50)," +
            "`tuesday_room` VARCHAR(50)," +
            "`wednesday_teacher` VARCHAR(50)," +
            "`wednesday_subject` VARCHAR(50)," +
            "`wednesday_room` VARCHAR(50)," +
            "`thursday_teacher` VARCHAR(50)," +
            "`thursday_subject` VARCHAR(50)," +
            "`thursday_room` VARCHAR(50)," +
            "`friday_teacher` VARCHAR(50)," +
            "`friday_subject` VARCHAR(50)," +
            "`friday_room` VARCHAR(50)," +
            "`shift` int(2)," +
            "PRIMARY KEY (`order_id`)" +
            ");" +
            "INSERT INTO `%1$s` (`order_id`, `monday_teacher`, `monday_subject`, `monday_room`," +
            " `tuesday_teacher`, `tuesday_subject`, `tuesday_room`," +
            " `wednesday_teacher`, `wednesday_subject`, `wednesday_room`," +
            " `thursday_teacher`, `thursday_subject`, `thursday_room`," +
            " `friday_teacher`, `friday_subject`, `friday_room`, `shift`) VALUES" +
            "(0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(4, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(5, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(6, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)," +
            "(7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);";

    public static final String TEACHER_CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS `teachers` (" +
            "`teacher_id` VARCHAR(50)," +
            "`name`  VARCHAR(50)," +
            "`subject` VARCHAR(50)," +
            " PRIMARY KEY (`teacher_id`)" +
            ");";
    public static final String GRADE_CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS `grades` (" +
            "`grade` VARCHAR(50)," +
            " PRIMARY KEY (`grade`)" +
            ");";
    public static final String INSERT_GRADE_QUERY = "INSERT INTO `grades` (`grade`) VALUES ('%s');";

}
