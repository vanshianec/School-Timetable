package com.example.ivani.schoolscheduleonline;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String[] DISPLAY_GRADES_LIST = {"5а", "5б", "6а", "6б", "7а", "7б", "8а", "8б", "8в", "8г", "8д", "8е",
            "9а", "9б", "9в", "9г", "9д", "9е", "10а", "10б", "10в", "10г", "10д", "10е",
            "11а", "11б", "11в", "11г", "11д", "11е", "12а", "12б", "12в", "12г", "12д", "12е"};

    private String[] displayTeachersList;
    private SharedPreferences sharedPreferences;
    private RequestQueue mQueue;
    private Button showTimetableBtn;
    private Button chooseBtn;
    private Button switchBtn;
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

        layout = findViewById(R.id.mainlayout);
        transitionDrawable = (TransitionDrawable) layout.getBackground();
        mLastClickTime = 0;

        if (firstTimeLaunch()) {
            System.out.println();
            //set sharedPreferences for student view
            this.sharedPreferences.edit().putBoolean("studentView", true).apply();
        } else {
            //set the view without button click
            setView(this.sharedPreferences.getBoolean("studentView", true), false);
        }

        //display fullscreen background(no notification bar and action bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        checkIfDialogIsShowing(savedInstanceState);

        mQueue = Volley.newRequestQueue(getApplicationContext());
        showTimetableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Timetable.class);
                startActivity(intent);
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sharedPreferences.getBoolean("studentView", false)) {
                    if (displayTeachersList != null) {
                        //the teachers names where loaded before so we don't need to get them from the database
                        displayChooseDialog(displayTeachersList);
                    } else {
                        //take teachers names and display them for the first time
                        takeTeachersNamesFromDatabaseAndDisplayThem();
                    }
                } else {
                    displayChooseDialog(DISPLAY_GRADES_LIST);
                }
            }
        });

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

    @Override
    public void onBackPressed() {
        // exit app on back pressed when in main activity
        moveTaskToBack(true);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("isDialogCancelled", isDialogCancelled);
    }

    private boolean firstTimeLaunch() {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (this.sharedPreferences.getBoolean("firstrun", true)) {
            //the app is launching for the first time
            // using the following line to edit/commit prefs
            this.sharedPreferences.edit().putBoolean("firstrun", false).apply();
            return true;
        }
        return false;
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

    private void checkIfDialogIsShowing(Bundle savedInstanceState) {
        isDialogCancelled = savedInstanceState == null || savedInstanceState.getBoolean("isDialogCancelled");
        if (!isDialogCancelled) {
            displayChooseDialog(DISPLAY_GRADES_LIST);
        }
    }

    private void displayChooseDialog(final String[] displayList) {
        AlertDialog.Builder builder = createBuilder(displayList);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        setAlertDialogSettings(dialog);
        dialog.show();
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

    public void takeTeachersNamesFromDatabaseAndDisplayThem() {
        String url = "https://liverpoolynwa.000webhostapp.com/get_teachers_names.php";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                saveTeachersInSharedPreference(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayErrorMessage(error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
        //show the teacher loading dialog and display the teachers on response
        showTeacherLoadingDialog();
    }

    public void saveTeachersInSharedPreference(String response) {
        this.sharedPreferences.edit().putString("teachers", response).apply();
    }

    public String[] getTeachersNames() throws JSONException {
        String jsonString = this.sharedPreferences.getString("teachers", "");
        JSONArray array = new JSONArray(jsonString);
        String[] displayTeacherList = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            displayTeacherList[i] = jsonObject.getString("name");
        }
        return displayTeacherList;
    }

    public void takeSelectedTableFromDatabase(final String value) {
        //send request to the database
        String url = "https://liverpoolynwa.000webhostapp.com/get_data.php";
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
                displayErrorMessage(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //send key to php server to select given table
                //after that the php server will return a table from the database in JSON format
                Map<String, String> MyData = new HashMap<>();
                MyData.put("androidKey", value);
                return MyData;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
        showLoadingDialogUntilResponse();
    }

    private void displayErrorMessage(VolleyError error) {
        String message = null;
        if (error instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (error instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (error instanceof NoConnectionError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showLoadingDialogUntilResponse() {
        final Dialog loadingDialog = createLoadingDialog();
        mQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void showTeacherLoadingDialog() {
        final Dialog loadingDialog = createLoadingDialog();
        mQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (loadingDialog.isShowing()) {
                    //display the teachers on response
                    if (!sharedPreferences.getBoolean("studentView", false)) {
                        displayTeachersNames();
                    }
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void displayTeachersNames() {
        try {
            displayTeachersList = getTeachersNames();
            if (displayTeachersList != null) {
                displayChooseDialog(displayTeachersList);
            } else {
                Toast.makeText(MainActivity.this, "Няма връзка със сървъра. Моля, опитайте по-късно",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private Dialog createLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(R.layout.progress_bar);
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    private void saveInSharedResponse(String response) {
        if (sharedPreferences.getBoolean("studentView", true)) {
            this.sharedPreferences.edit().putString("Response", response).apply();
        } else {
            this.sharedPreferences.edit().putString("ResponseTeacher", response).apply();
        }
    }

}
