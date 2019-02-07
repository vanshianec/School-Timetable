import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static Constants.Constants.*;

public class DatabaseManager {
    private static final String UPDATE_DATABASE_SERVER_URL = "https://liverpoolynwa.000webhostapp.com/update_data.php";
    private static final String GET_TEACHERS_SERVER_URL = "https://liverpoolynwa.000webhostapp.com/get_teachers.php";

    private GradesAndRoomsManager gradesAndRoomsManager;
    private TeachersManager teachersManager;
    private StringBuilder queryStringBuilder;
    private Map<Integer, String> teacherIdAndName;

    public DatabaseManager(GradesAndRoomsManager gradesAndRoomsManager, TeachersManager teachersManager) throws IOException {
        this.gradesAndRoomsManager = gradesAndRoomsManager;
        this.teachersManager = teachersManager;
        this.queryStringBuilder = new StringBuilder();
        this.teacherIdAndName = new LinkedHashMap<>();
        this.setTeacherIdAndName();
    }

    public Map<Integer, String> getTeacherIdAndName() {
        return teacherIdAndName;
    }

    private void setTeacherIdAndName() throws IOException {
        //get teachers data from database
        String jsonString = setUpConnection(GET_TEACHERS_SERVER_URL, "teacher", "teachers");
        //read the returned json from the database and add its values in teacherIdAndName variable
        readJSON(jsonString);
    }

    public void updateDatabase() throws IOException {
        //add query to update the shifts of the grades
        addQueryShifts();
        //add query to update the rooms and teachers
        addQueryRoomsAndTeachers();
        //add query to update the teacher tables
        addQueryTeacherTables();
        //connect to the database and execute all queries appended to the StringBuilder
        setUpConnection(UPDATE_DATABASE_SERVER_URL, "query", this.queryStringBuilder.toString());
    }

    private void addQueryTeacherTables() {
        Map<Integer, Map<Integer, String>> mondayGrades = this.teachersManager.getMondayGrades();
        Map<Integer, Map<Integer, String>> tuesdayGrades = this.teachersManager.getTuesdayGrades();
        Map<Integer, Map<Integer, String>> wednesdayGrades = this.teachersManager.getWednesdayGrades();
        Map<Integer, Map<Integer, String>> thursdayGrades = this.teachersManager.getThursdayGrades();
        Map<Integer, Map<Integer, String>> fridayGrades = this.teachersManager.getFridayGrades();

        Map<Integer, Map<Integer, Integer>> mondayRooms = this.teachersManager.getMondayRooms();
        Map<Integer, Map<Integer, Integer>> tuesdayRooms = this.teachersManager.getTuesdayRooms();
        Map<Integer, Map<Integer, Integer>> wednesdayRooms = this.teachersManager.getWednesdayRooms();
        Map<Integer, Map<Integer, Integer>> thursdayRooms = this.teachersManager.getThursdayRooms();
        Map<Integer, Map<Integer, Integer>> fridayRooms = this.teachersManager.getFridayRooms();
        //EXAMPLE 51 ->       51 ->
        //        1 -> 12g     1 -> 504
        //        2 -> 12e     2 -> 305
        //        ........     ...........
        //        3 ->        3 ->
        //        1 -> 5a     1 -> 402
        //        ........     ..........
        addTeacherQueryToStringBuilder(mondayGrades, tuesdayGrades, wednesdayGrades, thursdayGrades, fridayGrades,
                mondayRooms, tuesdayRooms, wednesdayRooms, thursdayRooms, fridayRooms);
    }

    private void addTeacherQueryToStringBuilder(Map<Integer, Map<Integer, String>> mondayGrades,
                                                Map<Integer, Map<Integer, String>> tuesdayGrades,
                                                Map<Integer, Map<Integer, String>> wednesdayGrades,
                                                Map<Integer, Map<Integer, String>> thursdayGrades,
                                                Map<Integer, Map<Integer, String>> fridayGrades,
                                                Map<Integer, Map<Integer, Integer>> mondayRooms,
                                                Map<Integer, Map<Integer, Integer>> tuesdayRooms,
                                                Map<Integer, Map<Integer, Integer>> wednesdayRooms,
                                                Map<Integer, Map<Integer, Integer>> thursdayRooms,
                                                Map<Integer, Map<Integer, Integer>> fridayRooms) {

        for (int teacherId : mondayGrades.keySet()) {
            for (int i = 1; i < 15; i++) {
                String mondayGrade = "";
                int mondayRoom = 0;
                if (mondayGrades.containsKey(teacherId) && mondayRooms.containsKey(teacherId)) {
                    if (mondayGrades.get(teacherId).containsKey(i) && mondayRooms.get(teacherId).containsKey(i)) {
                        mondayGrade = mondayGrades.get(teacherId).get(i);
                        mondayRoom = mondayRooms.get(teacherId).get(i);
                    }
                }
                String tuesdayGrade = "";
                int tuesdayRoom = 0;
                if (tuesdayGrades.containsKey(teacherId) && tuesdayRooms.containsKey(teacherId)) {
                    if (tuesdayGrades.get(teacherId).containsKey(i) && tuesdayRooms.get(teacherId).containsKey(i)) {
                        tuesdayGrade = tuesdayGrades.get(teacherId).get(i);
                        tuesdayRoom = tuesdayRooms.get(teacherId).get(i);
                    }
                }
                String wednesdayGrade = "";
                int wednesdayRoom = 0;
                if (wednesdayGrades.containsKey(teacherId) && wednesdayRooms.containsKey(teacherId)) {
                    if (wednesdayGrades.get(teacherId).containsKey(i) && wednesdayRooms.get(teacherId).containsKey(i)) {
                        wednesdayGrade = wednesdayGrades.get(teacherId).get(i);
                        wednesdayRoom = wednesdayRooms.get(teacherId).get(i);
                    }
                }
                String thursdayGrade = "";
                int thursdayRoom = 0;
                if (thursdayGrades.containsKey(teacherId) && thursdayRooms.containsKey(teacherId)) {
                    if (thursdayGrades.get(teacherId).containsKey(i) && thursdayRooms.get(teacherId).containsKey(i)) {
                        thursdayGrade = thursdayGrades.get(teacherId).get(i);
                        thursdayRoom = thursdayRooms.get(teacherId).get(i);
                    }
                }
                String fridayGrade = "";
                int fridayRoom = 0;
                if (fridayGrades.containsKey(teacherId) && fridayRooms.containsKey(teacherId)) {
                    if (fridayGrades.get(teacherId).containsKey(i) && fridayRooms.get(teacherId).containsKey(i)) {
                        fridayGrade = fridayGrades.get(teacherId).get(i);
                        fridayRoom = fridayRooms.get(teacherId).get(i);
                    }
                }
                String query = String.format("UPDATE `%1$d` SET `%3$s` = '%4$d', `%5$s` = '%6$d', `%7$s` = '%8$d'" +
                                ", `%9$s` = '%10$d', `%11$s` = '%12$d', `%13$s` = '%14$s', `%15$s` = '%16$s'" +
                                ", `%17$s` = '%18$s', `%19$s` = '%20$s', `%21$s` = '%22$s' WHERE `%1$d`.`order_id` = %2$d;",
                        teacherId, i, MONDAY_ROOM_COLUMN, mondayRoom, TUESDAY_ROOM_COLUMN, tuesdayRoom,
                        WEDNESDAY_ROOM_COLUMN, wednesdayRoom, THURSDAY_ROOM_COLUMN, thursdayRoom,
                        FRIDAY_ROOM_COLUMN, fridayRoom, MONDAY_GRADE_COLUMN, mondayGrade,
                        TUESDAY_GRADE_COLUMN, tuesdayGrade, WEDNESDAY_GRADE_COLUMN, wednesdayGrade,
                        THURSDAY_GRADE_COLUMN, thursdayGrade, FRIDAY_GRADE_COLUMN, fridayGrade);

                this.queryStringBuilder.append(query);
            }
        }
    }

    private void addQueryShifts() {
        Map<String, Integer> shifts = this.gradesAndRoomsManager.getGradesShift();
        for (Map.Entry<String, Integer> entry : shifts.entrySet()) {
            //EXAMPLE 12g -> 1
            //EXAMPLE 10e -> 2
            String query = String.format("UPDATE `%1$s` SET `shift` = '%2$d' WHERE `%1$s`.`order_id` = '0';",
                    entry.getKey(), entry.getValue());
            this.queryStringBuilder.append(query);
        }
    }

    private void addQueryRoomsAndTeachers() {
        Map<String, Map<Integer, String>> mondayRooms = this.gradesAndRoomsManager.getMondayRooms();
        Map<String, Map<Integer, String>> tuesdayRooms = this.gradesAndRoomsManager.getTuesdayRooms();
        Map<String, Map<Integer, String>> wednesdayRooms = this.gradesAndRoomsManager.getWednesdayRooms();
        Map<String, Map<Integer, String>> thursdayRooms = this.gradesAndRoomsManager.getThursdayRooms();
        Map<String, Map<Integer, String>> fridayRooms = this.gradesAndRoomsManager.getFridayRooms();
        Map<String, Map<Integer, String>> mondayGrades = replaceTeacherIdsWithNames(this.gradesAndRoomsManager.getMondayGrades());
        Map<String, Map<Integer, String>> tuesdayGrades = replaceTeacherIdsWithNames(this.gradesAndRoomsManager.getTuesdayGrades());
        Map<String, Map<Integer, String>> wednesdayGrades = replaceTeacherIdsWithNames(this.gradesAndRoomsManager.getWednesdayGrades());
        Map<String, Map<Integer, String>> thursdayGrades = replaceTeacherIdsWithNames(this.gradesAndRoomsManager.getThursdayGrades());
        Map<String, Map<Integer, String>> fridayGrades = replaceTeacherIdsWithNames(this.gradesAndRoomsManager.getFridayGrades());
        //EXAMPLE 12g ->       12g ->
        //        1 -> 102     1 -> 'teacherName'
        //        2 -> 504     2 -> 'teacherName'
        //        ........     ...........
        //        12e ->       12е ->
        //        1 -> 305     1 -> 'teacherName'
        //        ........     ..........

        addQueryToStringBuilder(mondayRooms, tuesdayRooms, wednesdayRooms, thursdayRooms, fridayRooms,
                mondayGrades, tuesdayGrades, wednesdayGrades, thursdayGrades, fridayGrades);
    }

    private void addQueryToStringBuilder(Map<String, Map<Integer, String>> mondayRooms,
                                         Map<String, Map<Integer, String>> tuesdayRooms,
                                         Map<String, Map<Integer, String>> wednesdayRooms,
                                         Map<String, Map<Integer, String>> thursdayRooms,
                                         Map<String, Map<Integer, String>> fridayRooms,
                                         Map<String, Map<Integer, String>> mondayGrades,
                                         Map<String, Map<Integer, String>> tuesdayGrades,
                                         Map<String, Map<Integer, String>> wednesdayGrades,
                                         Map<String, Map<Integer, String>> thursdayGrades,
                                         Map<String, Map<Integer, String>> fridayGrades) {
        //iterate over the grades
        for (String grade : mondayRooms.keySet()) {
            for (int order = 0; order < 8; order++) {
                String mondayRoomValue = getValue(mondayRooms, grade, order);
                String tuesdayRoomValue = getValue(tuesdayRooms, grade, order);
                String wednesdayRoomValue = getValue(wednesdayRooms, grade, order);
                String thursdayRoomValue = getValue(thursdayRooms, grade, order);
                String fridayRoomValue = getValue(fridayRooms, grade, order);
                String mondayTeacherValue = getValue(mondayGrades, grade, order);
                String tuesdayTeacherValue = getValue(tuesdayGrades, grade, order);
                String wednesdayTeacherValue = getValue(wednesdayGrades, grade, order);
                String thursdayTeacherValue = getValue(thursdayGrades, grade, order);
                String fridayTeacherValue = getValue(fridayGrades, grade, order);
                String query = String.format("UPDATE `%1$s` SET `%3$s` = '%4$s', `%5$s` = '%6$s', `%7$s` = '%8$s'" +
                                ", `%9$s` = '%10$s', `%11$s` = '%12$s', `%13$s` = '%14$s', `%15$s` = '%16$s'" +
                                ", `%17$s` = '%18$s', `%19$s` = '%20$s', `%21$s` = '%22$s' WHERE `%1$s`.`order_id` = %2$d;",
                        grade, order, MONDAY_ROOM_COLUMN, mondayRoomValue, TUESDAY_ROOM_COLUMN, tuesdayRoomValue,
                        WEDNESDAY_ROOM_COLUMN, wednesdayRoomValue, THURSDAY_ROOM_COLUMN, thursdayRoomValue,
                        FRIDAY_ROOM_COLUMN, fridayRoomValue, MONDAY_TEACHER_COLUMN, mondayTeacherValue,
                        TUESDAY_TEACHER_COLUMN, tuesdayTeacherValue, WEDNESDAY_TEACHER_COLUMN, wednesdayTeacherValue,
                        THURSDAY_TEACHER_COLUMN, thursdayTeacherValue, FRIDAY_TEACHER_COLUMN, fridayTeacherValue);

                this.queryStringBuilder.append(query);
            }
        }
    }

    private String getValue(Map<String, Map<Integer, String>> day, String grade, int order) {
        if (day.get(grade).containsKey(order)) {
            return day.get(grade).get(order);
        }
        return "";
    }

    private Map<String, Map<Integer, String>> replaceTeacherIdsWithNames(Map<String, Map<Integer, String>> dayGrades) {
        for (Map.Entry<String, Map<Integer, String>> entry : dayGrades.entrySet()) {
            String key = entry.getKey();
            Map<Integer, String> orderTeacherId = entry.getValue();
            for (Map.Entry<Integer, String> innerEntry : orderTeacherId.entrySet()) {
                String teacherId = innerEntry.getValue();
                //teachers for this subject are not shown
                if (teacherId.contains("ТП")) {
                    continue;
                }
                String teacherName = "";
                String[] teacherIdArray = teacherId.split("\\s+");
                if (teacherIdArray.length == 2) {
                    //set two teacher ids to two names
                    int firstId = Integer.parseInt(teacherIdArray[0]);
                    int secondId = Integer.parseInt(teacherIdArray[1]);
                    teacherName = this.teacherIdAndName.get(firstId) + "/" + this.teacherIdAndName.get(secondId);
                } else if (teacherIdArray.length == 1) {
                    //set id to name
                    teacherName = this.teacherIdAndName.get(Integer.parseInt(teacherId.trim()));
                }
                //put set value to teacher name
                innerEntry.setValue(teacherName.trim());
            }
            dayGrades.replace(key, orderTeacherId);
        }
        return dayGrades;
    }

    private String setUpConnection(String url, String key, String query) throws IOException {
        URL u = new URL(url);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(key, query);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0; ) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    private void readJSON(String jsonString) {
        //example string [{"teacher_id":"1","name":"Елвира Габровска"},{"teacher_id":"2","name":"Поля Тевекелева"},......]
        JSONArray array = new JSONArray(jsonString);
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            int teacherId = jsonObject.getInt("teacher_id");
            String name = jsonObject.getString("name");
            this.teacherIdAndName.put(teacherId, name);
        }
    }
}
