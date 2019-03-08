package com.example.ivani.schoolscheduleonline;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String GET_SCHOOL_NAME_URL = "https://schooltimetable.site/get_school_name_and_logo.php";
    private static final String GET_TEACHERS_OR_GRADES_NAMES = "https://schooltimetable.site/get_teachers_or_grades_names.php";

    private String[] displaySchoolList;
    private String[] displaySchoolLogosURLs;
    private String[] displayTeachersList;
    private String[] originalTeachersList;
    private String[] displayGradesList;
    private RequestManager requestManager;
    private ErrorManager errorManager;
    private SharedPreferences sharedPreferences;
    private RequestQueue mQueue;
    private AlertDialog networkRequireDialog;
    private AlertDialog chooseDialog;
    private Button showTimetableBtn;
    private Button chooseBtn;
    private Button switchBtn;
    private ImageView changeSchool;
    private ImageView schoolLogo;
    private boolean isDialogCancelled;
    private View layout;
    private TransitionDrawable transitionDrawable;
    private long lastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showTimetableBtn = findViewById(R.id.showProgram);
        chooseBtn = findViewById(R.id.chooseGrade);
        switchBtn = findViewById(R.id.switchBtn);
        changeSchool = findViewById(R.id.changeSchool);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        schoolLogo = findViewById(R.id.logoId);
        errorManager = new ErrorManager(this);
        requestManager = new RequestManager(this, getApplicationContext(), errorManager);
        layout = findViewById(R.id.mainlayout);
        transitionDrawable = (TransitionDrawable) layout.getBackground();
        lastClickTime = 0;

        setViewBasedOnLaunch();
        setBitmapLogo();
        setFullscreenView();
        checkIfDialogIsShowing(savedInstanceState);

        setShowTimetableListener();
        setChangeSchoolListener();
        setChooseListener();
        setSwitchListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("isDialogCancelled", isDialogCancelled);
        bundle.putStringArray("gradesList", displayGradesList);
        bundle.putStringArray("teachersList", displayTeachersList);
        bundle.putStringArray("originalTeachersList", originalTeachersList);
    }

    @Override
    protected void onPause() {
        //dismiss dialog (mainly on screen rotation) so we can use it again when the view changes
        if (this.chooseDialog != null) {
            if (this.chooseDialog.isShowing()) {
                this.chooseDialog.dismiss();
            }
        }
        if (this.networkRequireDialog != null) {
            if (this.networkRequireDialog.isShowing()) {
                this.networkRequireDialog.dismiss();
            }
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        // exit app on back pressed when in main activity
        moveTaskToBack(true);
    }

    private void setViewBasedOnLaunch() {
        //if the app is launching for the first time without internet connection create an alert dialog to notify the user
        if (firstTimeLaunch()) {
            if (!isNetworkAvailable()) {
                createNetworkRequireDialog();
            }
            //set the student and teacher first launch to true
            sharedPreferences.edit().putBoolean("studentFirstStart", true).apply();
            sharedPreferences.edit().putBoolean("teacherFirstStart", true).apply();
            //if the app is launching for the first time we have to display the choose school view
            loadChangeSchoolView();
        } else if (this.sharedPreferences.getBoolean("studentFirstStart", false)
                && this.sharedPreferences.getBoolean("studentView", false)) {
            //pass true in setView means that the grades will be displayed
            setView(true, false);
        } else if (this.sharedPreferences.getBoolean("teacherFirstStart", false)
                && !this.sharedPreferences.getBoolean("studentView", false)) {
            //pass false in setView means that the teachers will be displayed
            setView(false, false);
        } else {
            setView(this.sharedPreferences.getBoolean("studentView", true), false);
        }
    }

    private boolean firstTimeLaunch() {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (this.sharedPreferences.getBoolean("firstrun", true)) {
            return true;
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void createNetworkRequireDialog() {
        this.networkRequireDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
                .setMessage("Приложението изисква интернет връзка. Моля, включете Wi-Fi или мобилни данни.")
                .setPositiveButton("Продължи", null) //Set to null. We override the onclick
                .setNegativeButton("Откажи", null)
                .setCancelable(false)
                .create();
        setNetworkRequireOnShowListener(networkRequireDialog);
        networkRequireDialog.show();
    }

    private void setNetworkRequireOnShowListener(final AlertDialog dialog) {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button posButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                posButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isNetworkAvailable()) {
                            dialog.dismiss();
                            loadChangeSchoolView();
                        } else {
                            Toast.makeText(MainActivity.this, "Няма достъп до интернет.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Button negButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.this.finish();
                    }
                });
            }
        });
    }

    private void loadChangeSchoolView() {
        //set school list to true
        //this means that we will need the data for the choose school activity when selecting from the database
        this.sharedPreferences.edit().putBoolean("schoolList", true).apply();
        takeNamesFromDatabaseAndDisplayThem(GET_SCHOOL_NAME_URL, "school");
    }


    private void takeNamesFromDatabaseAndDisplayThem(String url, final String view) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                saveNamesInSharedPreference(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if the app is launched for the first time without connection the network dialog will be displayed
                //however if the app is not launched for the first time we need to notify the user for the connection problem
                if (networkRequireDialog != null) {
                    if (!networkRequireDialog.isShowing()) {
                        errorManager.displayErrorMessage(error);
                    }
                } else {
                    errorManager.displayErrorMessage(error);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return MainActivity.this.getParams(view);
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
        //show the loading dialog until the server response
        showNamesListLoadingDialog(view);
    }

    private void saveNamesInSharedPreference(String response) {
        //if the schoolList preferences is true we need to store the response in the schools preferences for future parsing
        if (this.sharedPreferences.getBoolean("schoolList", true)) {
            this.sharedPreferences.edit().putString("schools", response).apply();
        } else if (!this.sharedPreferences.getBoolean("studentView", false)) {
            //else we need teachers preferences
            this.sharedPreferences.edit().putString("teachers", response).apply();
        } else {
            //else the students
            this.sharedPreferences.edit().putString("students", response).apply();
        }
    }

    private Map<String, String> getParams(String view) {
        //send key to php server to select given table
        //after that the php server will return a table from the database in JSON format
        Map<String, String> data = new HashMap<>();
        data.put("androidDatabase", sharedPreferences.getString("databaseName", ""));
        //send key based on view (school , teacher, student)
        if (view.equals("school")) {
            return data;
        } else if (view.equals("student")) {
            data.put("grades_or_teachers", "grades");
        } else {
            data.put("grades_or_teachers", "teachers");
        }
        return data;
    }

    private void showNamesListLoadingDialog(final String view) {
        final Dialog loadingDialog = requestManager.createLoadingDialog();
        mQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (loadingDialog.isShowing()) {
                    //display the school, grades, or teacher names based on the current view
                    displayNames(view);
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void displayNames(String view) {
        try {
            if (view.equals("school")) {
                //save the school names and logos returned from the database
                setSchoolDisplayLists();
                //pass them to the choose school activity and launch it
                startChooseSchoolActivity();
            } else if (view.equals("student")) {
                //save the grades returned from the database and display them in the current activity
                setGradesDisplayList();
                displayChooseGradeDialog();
            } else {
                //save the teachers returned from the database and display them in the current activity
                setTeachersDisplayList();
                displayChooseTeacherDialog();

            }
        } catch (JSONException e) {
            //if the network require dialog is showing the user has to enable internet connection
            //if the dialog is not showing then there was an error in selecting the data from the database so we notify the user
            if (networkRequireDialog != null) {
                if (!networkRequireDialog.isShowing()) {
                    handleParsingError();
                }
            } else {
                handleParsingError();
            }
        }
    }

    private void setSchoolDisplayLists() throws JSONException {
        String jsonString = this.sharedPreferences.getString("schools", "");
        JSONArray array = new JSONArray(jsonString);
        String name = "school_name";
        String logoURL = "logo_url";
        this.displaySchoolList = new String[array.length()];
        this.displaySchoolLogosURLs = new String[array.length()];
        for (short i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            this.displaySchoolList[i] = jsonObject.getString(name);
            this.displaySchoolLogosURLs[i] = jsonObject.getString(logoURL);
        }

    }

    private void startChooseSchoolActivity() {
        if (displaySchoolList != null && displaySchoolLogosURLs != null) {
            Intent intent = new Intent(MainActivity.this, ChooseSchool.class);
            intent.putExtra("school_names", displaySchoolList);
            intent.putExtra("school_logos", displaySchoolLogosURLs);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Няма връзка със сървъра. Моля, опитайте по-късно",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setGradesDisplayList() throws JSONException {
        String jsonString = this.sharedPreferences.getString("students", "");
        JSONArray array = new JSONArray(jsonString);
        String name = "grade";
        this.displayGradesList = new String[array.length()];
        for (short i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            this.displayGradesList[i] = jsonObject.getString(name);
        }
        sortGrades();
    }

    private void sortGrades() {
        Comparator comparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                //example strings s1 = 12a , s2 = 9a
                //if we use default sort method then 9a will be after 12a because 1 is before 9
                //se we make custom comparator so we can compare 9 and 12 not 9 and 1
                byte num1 = Byte.parseByte(s1.replaceAll("[^0-9]", ""));
                byte num2 = Byte.parseByte(s2.replaceAll("[^0-9]", ""));
                return num1 - num2;
            }
        };
        Arrays.sort(this.displayGradesList, comparator);
    }

    private void displayChooseGradeDialog() {
        if (displayGradesList != null) {
            displayChooseDialog(displayGradesList);
        } else {
            Toast.makeText(MainActivity.this, "Няма връзка със сървъра. Моля, опитайте по-късно",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setTeachersDisplayList() throws JSONException {
        String jsonString = this.sharedPreferences.getString("teachers", "");
        JSONArray array = new JSONArray(jsonString);
        String name = "name";
        this.displayTeachersList = new String[array.length()];
        this.originalTeachersList = new String[array.length()];
        for (short i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            this.displayTeachersList[i] = jsonObject.getString(name);
            this.originalTeachersList[i] = jsonObject.getString(name);
        }
        Arrays.sort(this.displayTeachersList);
    }

    private void displayChooseTeacherDialog() {
        if (displayTeachersList != null) {
            displayChooseDialog(displayTeachersList);
        } else {
            Toast.makeText(MainActivity.this, "Няма връзка със сървъра. Моля, опитайте по-късно",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void displayChooseDialog(final String[] displayList) {
        AlertDialog.Builder builder = createBuilder(displayList);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        setAlertDialogSettings(dialog, displayList);
        this.chooseDialog = dialog;
        this.chooseDialog.show();
    }

    @NonNull
    private AlertDialog.Builder createBuilder(final String[] displayList) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.custom_text_view, displayList);
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setAdapter(arrayAdapter, null);
        return builder;
    }

    private void setAlertDialogSettings(final AlertDialog dialog, String[] displayList) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //if the dialog is dismissed set the local variable to true
                isDialogCancelled = true;
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //if the dialog is showing set the local variable to false
                isDialogCancelled = false;
            }
        });
        if (this.sharedPreferences.getBoolean("studentFirstStart", false)
                && this.sharedPreferences.getBoolean("studentView", false)) {
            //if the user enters the student view for the first time make him choose a grade
            dialog.setTitle("Избери клас");
            dialog.setCancelable(false);
        } else if (this.sharedPreferences.getBoolean("teacherFirstStart", false)
                && !this.sharedPreferences.getBoolean("studentView", false)) {
            //if the user enters the teacher view for the first time make him choose a teacher
            dialog.setTitle("Избери учител");
            dialog.setCancelable(false);
        }
        setDialogListViewSettings(dialog, displayList);
    }

    private void setDialogListViewSettings(AlertDialog dialog, final String[] displayList) {
        ListView listView = dialog.getListView();
        listView.setDivider(new ColorDrawable(Color.parseColor("#D3D3D3")));
        listView.setDividerHeight(2);
        //remove last divider in the list view
        listView.setOverscrollFooter(new ColorDrawable(Color.TRANSPARENT));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                //if we are in teachers view we need the teacher id else in students view we need the grade value
                if (isNetworkAvailable()) {
                    if (!sharedPreferences.getBoolean("studentView", false)) {
                        //select the teacher id from the original list not the sorted one
                        String teacherName = displayList[index];
                        int teacherId = 1;
                        for (short i = 0; i < originalTeachersList.length; i++) {
                            if (teacherName.equals(originalTeachersList[i])) {
                                teacherId = i + 1;
                                break;
                            }
                        }
                        //select the teacher table from the database
                        takeSelectedTableFromDatabase(teacherId + "");
                    } else {
                        //select the grade from the database
                        takeSelectedTableFromDatabase(displayList[index]);
                    }
                    chooseDialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Няма достъп до интернет.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void takeSelectedTableFromDatabase(final String value) {
        //send request to the database
        String url = "https://schooltimetable.site/get_school_grade_or_teacher_data.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        manageGradeOrTeacherResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //we need to close the activity if there is an error in selecting the table from the database so the user can try again later
                //also we notify him for the error with appropriate message
                errorManager.displayErrorMessage(error);
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //send key to php server to select given table
                //after that the php server will return a table from the database in JSON format
                Map<String, String> MyData = new HashMap<>();
                MyData.put("androidDatabase", sharedPreferences.getString("databaseName", ""));
                MyData.put("androidKey", value);
                return MyData;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
        //show loading dialog until the server response
        requestManager.showLoadingDialogUntilResponse(mQueue);
    }

    private void manageGradeOrTeacherResponse(String response) {
        //check if response contains sql error message
        if (response.toLowerCase().contains("error")) {
            //notify the user for the error
            Toast.makeText(MainActivity.this, "Грешка при обработването на данните. Моля, опитайте по-късно"
                    , Toast.LENGTH_SHORT).show();
            //close the activity so the user can try again later
            finish();
        } else {
            //save result from database in shared preferences
            saveInSharedResponse(response);
            if (sharedPreferences.getBoolean("studentFirstStart", false)
                    && sharedPreferences.getBoolean("studentView", false)) {
                sharedPreferences.edit().putBoolean("studentFirstStart", false).apply();
            } else if (sharedPreferences.getBoolean("teacherFirstStart", false)
                    && !sharedPreferences.getBoolean("studentView", false)) {
                sharedPreferences.edit().putBoolean("teacherFirstStart", false).apply();
            }
        }
    }

    private void saveInSharedResponse(String response) {
        if (sharedPreferences.getBoolean("studentView", true)) {
            this.sharedPreferences.edit().putString("Response", response).apply();
        } else {
            this.sharedPreferences.edit().putString("ResponseTeacher", response).apply();
        }
    }

    private void handleParsingError() {
        Toast.makeText(this, "Грешка при обработването на данните. Моля, опитайте по-късно.", Toast.LENGTH_SHORT).show();
        if (sharedPreferences.getBoolean("studentFirstStart", true)
                || sharedPreferences.getBoolean("teacherFirstStart", true)) {
            //app is entering this view for the first time so we return back to the choose school activity
            this.sharedPreferences.edit().putBoolean("firstrun", true).apply();
            finish();
        }
    }

    private void setView(boolean studentView, boolean buttonClick) {
        //if studentView is true we set the view in student view
        if (studentView) {
            chooseBtn.setText("Избери клас");
            switchBtn.setText("Учителски изглед");
            if (buttonClick) {
                //set transition when the user switches views
                transitionDrawable.reverseTransition(1000);
            }
            if (this.sharedPreferences.getBoolean("studentFirstStart", false)) {
                // if the user enters the student view for the first time open the choose grade list
                displayChooseList(true);
            }
        }
        //else we set the view in teacherView
        else {
            chooseBtn.setText("Избери учител");
            switchBtn.setText("Ученически изглед");
            if (buttonClick) {
                //set transition when the user switches views
                transitionDrawable.startTransition(1000);
            } else {
                //since there is no button click that means the app was closed in teacher view so we instantly start the transition
                transitionDrawable.startTransition(0);
            }
            if (this.sharedPreferences.getBoolean("teacherFirstStart", false)) {
                // if the user enters the teacher view for the first time open the choose teacher list
                displayChooseList(false);
            }
        }
    }

    private void displayChooseList(boolean view) {
        String[] displayList = view ? displayGradesList : displayTeachersList;
        if (displayList != null) {
            displayChooseDialog(displayList);
        } else {
            //get grades or teachers list from database depending on the current view
            takeNamesFromDatabaseAndDisplayThem(GET_TEACHERS_OR_GRADES_NAMES, view ? "student" : "teacher");
        }
    }

    private void setBitmapLogo() {
        //get the logo from the choose school activity
        Bitmap logo = getIntent().getParcelableExtra("BitmapLogo");
        //if we receive the logo store it in shared preferences for future usage
        if (logo != null) {
            this.schoolLogo.setImageBitmap(logo);
            String encode = encodeToBase64(logo);
            this.sharedPreferences.edit().putString("bit", encode).apply();
        } else {
            //else decode the stored logo and set it in the school logo image view
            Bitmap currentLogo = decodeBase64(this.sharedPreferences.getString("bit", ""));
            this.schoolLogo.setImageBitmap(currentLogo);
        }
    }

    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private void setFullscreenView() {
        //display fullscreen background(no notification bar and action bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void checkIfDialogIsShowing(Bundle savedInstanceState) {
        //if the activity is launched for the first time a choose dialog will appear so we need to check if...
        //...the activity is not launched for the first time and then check if the user has opened choose dialog manually
        if (!(sharedPreferences.getBoolean("studentView", true) && sharedPreferences.getBoolean("studentFirstStart", true)
                || (!sharedPreferences.getBoolean("studentView", true) && sharedPreferences.getBoolean("teacherFirstStart", true)))) {

            isDialogCancelled = savedInstanceState == null || savedInstanceState.getBoolean("isDialogCancelled");
            if (!isDialogCancelled) {
                if (sharedPreferences.getBoolean("studentView", false)) {
                    String[] gradesList = savedInstanceState.getStringArray("gradesList");
                    if (gradesList != null) {
                        this.displayGradesList = gradesList;
                        displayChooseDialog(gradesList);
                    }
                } else {
                    String[] teachersList = savedInstanceState.getStringArray("teachersList");
                    String[] originalTeachersList = savedInstanceState.getStringArray("originalTeachersList");
                    if (teachersList != null) {
                        this.displayTeachersList = teachersList;
                        this.originalTeachersList = originalTeachersList;
                        displayChooseDialog(teachersList);
                    }
                }
            }
        }

    }

    private void setShowTimetableListener() {
        showTimetableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Timetable.class);
                //get the logo image from the choose school activity
                Bitmap logo = getIntent().getParcelableExtra("BitmapLogo");
                //if the app was closed the image is null so we need to restore it
                if (logo == null) {
                    logo = decodeBase64(sharedPreferences.getString("bit", ""));
                }
                intent.putExtra("BitmapImage", logo);
                startActivity(intent);
            }
        });
    }

    private void setChangeSchoolListener() {
        changeSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    loadChangeSchoolView();
                } else {
                    Toast.makeText(MainActivity.this, "Няма достъп до интернет.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setChooseListener() {
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean view = sharedPreferences.getBoolean("studentView", false);
                displayChooseList(view);
            }
        });
    }

    private void setSwitchListener() {
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prevent the button from spamming
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                if (isNetworkAvailable()
                        || (sharedPreferences.getBoolean("studentView", true) && !sharedPreferences.getBoolean("teacherFirstStart", false))
                        || (!sharedPreferences.getBoolean("studentView", true) && !sharedPreferences.getBoolean("studentFirstStart", false))) {
                    //save whether the app is in student or teacher view
                    if (sharedPreferences.getBoolean("studentView", true)) {
                        sharedPreferences.edit().putBoolean("studentView", false).apply();
                    } else {
                        sharedPreferences.edit().putBoolean("studentView", true).apply();
                    }
                    //set view with button click set on true
                    setView(sharedPreferences.getBoolean("studentView", true), true);
                } else {
                    Toast.makeText(MainActivity.this, "Няма достъп до интернет.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}