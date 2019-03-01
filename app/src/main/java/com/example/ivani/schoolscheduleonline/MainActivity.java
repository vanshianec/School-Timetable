package com.example.ivani.schoolscheduleonline;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String GET_SCHOOL_NAME_URL = "https://schooltimetable.site/get_school_name_and_logo.php";
    private static final String GET_TEACHERS_OR_GRADES_NAMES = "https://schooltimetable.site/get_teachers_or_grades_names.php";

    private String[] displaySchoolList;
    private String[] displaySchoolLogosURLs;
    private String[] displayTeachersList;
    private String[] displayGradesList;
    private RequestManager requestManager;
    private ErrorManager errorManager;
    private SharedPreferences sharedPreferences;
    private RequestQueue mQueue;
    private AlertDialog chooseDialog;
    private Button showTimetableBtn;
    private Button chooseBtn;
    private Button switchBtn;
    private ImageView changeSchool;
    private ImageView schoolLogo;
    private boolean isDialogCancelled;
    private View layout;
    private TransitionDrawable transitionDrawable;
    private long mLastClickTime;

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
        mLastClickTime = 0;

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
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("isDialogCancelled", isDialogCancelled);
        bundle.putStringArray("gradesList", displayGradesList);
        bundle.putStringArray("teachersList", displayTeachersList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.chooseDialog != null) {
            if (this.chooseDialog.isShowing()) {
                this.chooseDialog.dismiss();
            }
        }

    }

    @Override
    public void onBackPressed() {
        // exit app on back pressed when in main activity
        moveTaskToBack(true);
    }

    private void setViewBasedOnLaunch() {
        if (firstTimeLaunch()) {
            //the app is launching for the first time
            // using the following line to edit/commit prefs
            this.sharedPreferences.edit().putBoolean("firstrun", false).apply();
            //if the app is launching for the first time we have to display the choose school list
            loadChangeSchoolView();
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

    private void loadChangeSchoolView() {
        this.sharedPreferences.edit().putBoolean("schoolList", true).apply();
        takeNamesFromDatabaseAndDisplayThem(GET_SCHOOL_NAME_URL, "school");
    }

    private void setView(boolean studentView, boolean buttonClick) {
        //if studentView is true we set the view in student view
        if (studentView) {
            chooseBtn.setText("Избери клас");
            switchBtn.setText("Учителски изглед");
            if (buttonClick) {
                transitionDrawable.reverseTransition(1000);
            }
        }
        //else we set the view in teacherView
        else {
            chooseBtn.setText("Избери учител");
            switchBtn.setText("Ученически изглед");
            if (buttonClick) {
                transitionDrawable.startTransition(1000);
            } else {
                //since there is no button click that means the app was closed in teacher view so we instantly start the transition
                transitionDrawable.startTransition(0);
            }
        }
    }

    private void setBitmapLogo() {
        Bitmap logo = getIntent().getParcelableExtra("BitmapLogo");
        if (logo != null) {
            this.schoolLogo.setImageBitmap(logo);
            String encode = encodeToBase64(logo);
            this.sharedPreferences.edit().putString("bit", encode).apply();
        } else {
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
        isDialogCancelled = savedInstanceState == null || savedInstanceState.getBoolean("isDialogCancelled");
        if (!isDialogCancelled) {
            if (sharedPreferences.getBoolean("studentView", false)) {
                String[] gradesList = savedInstanceState.getStringArray("gradesList");
                if (gradesList != null) {
                    this.displayGradesList = gradesList;
                    displayChooseDialog(gradesList);
                }
            } else {
                String[] teacherList = savedInstanceState.getStringArray("teachersList");
                if (teacherList != null) {
                    this.displayTeachersList = teacherList;
                    displayChooseDialog(teacherList);
                }
            }
        }
    }

    private void displayChooseDialog(final String[] displayList) {
        AlertDialog.Builder builder = createBuilder(displayList);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        setAlertDialogSettings(dialog);
        this.chooseDialog = dialog;
        this.chooseDialog.show();
    }

    @NonNull
    private AlertDialog.Builder createBuilder(final String[] displayList) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.custom_text_view, displayList);
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                //take the selected grade info and send it to the database
                //after that the database will return the result from the selected grade and the shared preferences will save it
                //if we are in teachers view we need the teacher id else in students view we need the grade value
                if (!sharedPreferences.getBoolean("studentView", false)) {
                    int teacherId = index + 1;
                    takeSelectedTableFromDatabase(teacherId + "");
                } else {
                    takeSelectedTableFromDatabase(displayList[index]);
                }
            }
        });
        return builder;
    }

    public void takeSelectedTableFromDatabase(final String value) {
        //send request to the database
        String url = "https://schooltimetable.site/get_school_grade_or_teacher_data.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //save result from database in shared preferences
                        saveInSharedResponse(response);
                    }
                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                errorManager.displayErrorMessage(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //send key to php server to select given table
                //after that the php server will return a table from the database in JSON format
                Map<String, String> MyData = new HashMap<>();
                //androidSchoolName
                MyData.put("androidDatabase", sharedPreferences.getString("databaseName", ""));
                MyData.put("androidKey", value);
                return MyData;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
        requestManager.showLoadingDialogUntilResponse(mQueue);
    }

    private void saveInSharedResponse(String response) {
        if (sharedPreferences.getBoolean("studentView", true)) {
            this.sharedPreferences.edit().putString("Response", response).apply();
        } else {
            this.sharedPreferences.edit().putString("ResponseTeacher", response).apply();
        }
    }

    private void setAlertDialogSettings(AlertDialog dialog) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isDialogCancelled = true;
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                isDialogCancelled = false;
            }
        });
        ListView listView = dialog.getListView();
        listView.setDivider(new ColorDrawable(Color.parseColor("#D3D3D3")));
        listView.setDividerHeight(2);
        //remove last divider
        listView.setOverscrollFooter(new ColorDrawable(Color.TRANSPARENT));
    }

    public void takeNamesFromDatabaseAndDisplayThem(String url, final String view) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                saveNamesInSharedPreference(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorManager.displayErrorMessage(error);
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
        //show the teacher loading dialog and display the teachers on response
        showNamesListLoadingDialog(view);
    }

    private Map<String, String> getParams(String view) {
        //send key to php server to select given table
        //after that the php server will return a table from the database in JSON format
        Map<String, String> data = new HashMap<>();
        //androidSchoolName
        data.put("androidDatabase", sharedPreferences.getString("databaseName", ""));
        if (view.equals("school")) {
            return data;
        } else if (view.equals("student")) {
            data.put("grades_or_teachers", "grades");
        } else {
            data.put("grades_or_teachers", "teachers");
        }
        return data;
    }

    public void saveNamesInSharedPreference(String response) {
        //if we are in choose school list we need to store the response in the schools preferences
        if (this.sharedPreferences.getBoolean("schoolList", true)) {
            this.sharedPreferences.edit().putString("schools", response).apply();
        } else if (!this.sharedPreferences.getBoolean("studentView", false)) {
            //else we are in choose teacher list so we store the response in the teachers preferences
            this.sharedPreferences.edit().putString("teachers", response).apply();
        } else {
            this.sharedPreferences.edit().putString("students", response).apply();
        }
    }

    private void showNamesListLoadingDialog(final String view) {
        final Dialog loadingDialog = requestManager.createLoadingDialog();
        mQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (loadingDialog.isShowing()) {
                    //check if we are are in teacher view or school select view and display the names list
                    displayNames(view);
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void displayNames(String view) {
        try {
            if (view.equals("school")) {
                setSchoolDisplayLists();
                startChooseSchoolActivity();
            } else if (view.equals("student")) {
                setGradesDisplayList();
                displayChooseGradeDialog();
            } else {
                setTeachersDisplayList();
                displayChooseTeacherDialog();

            }
        } catch (JSONException e) {
            //TODO TOAST ERRRO !!;
        }
    }

    private void setSchoolDisplayLists() throws JSONException {
        String jsonString = this.sharedPreferences.getString("schools", "");
        JSONArray array = new JSONArray(jsonString);
        String name = "school_name";
        String logoURL = "logo_url";
        this.displaySchoolList = new String[array.length()];
        this.displaySchoolLogosURLs = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            this.displaySchoolList[i] = jsonObject.getString(name);
            this.displaySchoolLogosURLs[i] = jsonObject.getString(logoURL);
        }

    }

    private void setGradesDisplayList() throws JSONException {
        String jsonString = this.sharedPreferences.getString("students", "");
        JSONArray array = new JSONArray(jsonString);
        String name = "grade";
        this.displayGradesList = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            this.displayGradesList[i] = jsonObject.getString(name);
        }
    }

    private void setTeachersDisplayList() throws JSONException {
        String jsonString = this.sharedPreferences.getString("teachers", "");
        JSONArray array = new JSONArray(jsonString);
        String name = "name";
        this.displayTeachersList = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            this.displayTeachersList[i] = jsonObject.getString(name);
        }
    }

    private void displayChooseTeacherDialog() {
        if (displayTeachersList != null) {
            displayChooseDialog(displayTeachersList);
        } else {
            Toast.makeText(MainActivity.this, "Няма връзка със сървъра. Моля, опитайте по-късно",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void displayChooseGradeDialog() {
        if (displayGradesList != null) {
            displayChooseDialog(displayGradesList);
        } else {
            Toast.makeText(MainActivity.this, "Няма връзка със сървъра. Моля, опитайте по-късно",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void startChooseSchoolActivity() {
        if (displaySchoolList != null && displaySchoolLogosURLs != null) {
            Intent intent = new Intent(MainActivity.this, FirstLaunch.class);
            intent.putExtra("school_names", displaySchoolList);
            intent.putExtra("school_logos", displaySchoolLogosURLs);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Няма връзка със сървъра. Моля, опитайте по-късно",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setSwitchListener() {
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prevent the button from spamming
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                //save whether the app is in student or teacher view
                if (sharedPreferences.getBoolean("studentView", true)) {
                    sharedPreferences.edit().putBoolean("studentView", false).apply();
                } else {
                    sharedPreferences.edit().putBoolean("studentView", true).apply();
                }
                //set view with button click set on true
                setView(sharedPreferences.getBoolean("studentView", true), true);
            }
        });
    }

    private void setChooseListener() {
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean view = sharedPreferences.getBoolean("studentView", false);
                String[] displayList = view ? displayGradesList : displayTeachersList;
                if (displayList != null) {
                    displayChooseDialog(displayList);
                } else {
                    //get grades or teachers list from database depending on the current view
                    takeNamesFromDatabaseAndDisplayThem(GET_TEACHERS_OR_GRADES_NAMES, view ? "student" : "teacher");
                }
            }
        });
    }

    private void setChangeSchoolListener() {
        changeSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadChangeSchoolView();
            }
        });
    }

    private void setShowTimetableListener() {
        showTimetableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Timetable.class);
                startActivity(intent);
            }
        });
    }
}