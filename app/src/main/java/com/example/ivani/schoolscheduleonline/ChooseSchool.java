package com.example.ivani.schoolscheduleonline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ChooseSchool extends AppCompatActivity {
    private List<SchoolItem> schoolItems;
    private AutoCompleteTextView editText;
    private Button studentButton;
    private Button teacherButton;
    private RequestQueue mQueue;
    private SharedPreferences sharedPreferences;
    private RequestManager requestManager;
    private ErrorManager errorManager;
    private SharedPreferences firstLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_school_activity);
        this.studentButton = findViewById(R.id.school_student_view);
        this.teacherButton = findViewById(R.id.school_teacher_view);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.firstLaunch = PreferenceManager.getDefaultSharedPreferences(this);
        this.mQueue = Volley.newRequestQueue(getApplicationContext());
        this.errorManager = new ErrorManager(this);
        requestManager = new RequestManager(this, getApplicationContext(), errorManager);
        //display fullscreen background(no notification bar and action bar)
        setFullscreenView();
        //get the school names and logos from the main activity
        final String[] schoolNames = getIntent().getStringArrayExtra("school_names");
        String[] schoolLogos = getIntent().getStringArrayExtra("school_logos");
        //create the customAutoCompleteTextView
        this.editText = createAutoCompleteTextView(schoolNames, schoolLogos);

        setOnTouchListener(this.editText);
        setOnItemClickListener(this.editText);
        setOnClickListener(schoolNames, this.editText);
        checkIfSuggestionsIsShowing(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        //if the application is launched for the first time and the user presses the back button we want to quit the app
        if (this.sharedPreferences.getBoolean("firstrun", true)) {
            moveTaskToBack(true);
        } else {
            //else return to the previous activity
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //check if the suggestions list is showing and save its state so we can retrieve it in the next on create method
        //so for example if the user rotates the device the suggestions list will still be showing
        savedInstanceState.putBoolean("dropdownNotPopped", this.sharedPreferences.getBoolean("firstLaunch", false));
        savedInstanceState.putBoolean("dropdownShowing", this.editText.isPopupShowing());
    }

    private void setFullscreenView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private AutoCompleteTextView createAutoCompleteTextView(String[] schoolNames, String[] schoolLogos) {
        setSchoolItems(schoolNames, schoolLogos);
        final CustomAutoCompleteTextView editText = findViewById(R.id.actv);
        final AutoCompleteSchoolAdapter adapter = new AutoCompleteSchoolAdapter(this, this.schoolItems);
        editText.setAdapter(adapter);
        return editText;
    }

    private void setSchoolItems(String[] schoolNames, String[] schoolLogos) {
        //create the school items list
        this.schoolItems = new ArrayList<>();
        //get the school logos from database
        List<Bitmap> logos = getLogosFromDatabase(schoolLogos);
        //for each school add a logo and a name to be displayed in the autocomplete suggestions list
        short counter = 0;
        if (logos != null) {
            for (Bitmap logo : logos) {
                this.schoolItems.add(new SchoolItem(schoolNames[counter], logo));
                counter++;
            }
        } else {
            Toast.makeText(this, "Грешка при обработването на данните. Моля, опитайте по-късно.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private List<Bitmap> getLogosFromDatabase(String[] schoolLogos) {
        final short width = 300;
        final short height = 320;
        List<Bitmap> logos = null;
        try {
            logos = new AsyncTask<String[], Void, List<Bitmap>>() {
                @Override
                protected List<Bitmap> doInBackground(String[]... params) {
                    try {
                        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
                        for (short i = 0; i < params[0].length; ++i) {
                            //use glide library to get all school logos from the database
                            bitmaps.add(Glide.with(ChooseSchool.this).asBitmap().override(width, height).load(params[0][i]).submit().get());
                        }
                        return bitmaps;
                    } catch (InterruptedException e) {
                        Toast.makeText(ChooseSchool.this, "Грешка при свързването със сървъра. Моля, опитайте по-късно.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        Toast.makeText(ChooseSchool.this, "Грешка при свързването със сървъра. Моля, опитайте по-късно.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute(schoolLogos).get(7000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Toast.makeText(this, "Грешка при свързването със сървъра. Моля, опитайте по-късно.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            Toast.makeText(this, "Грешка при свързването със сървъра. Моля, опитайте по-късно.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(this, "Времето за свързване изтече. Моля, проверете връзката си или опитайте по-късно.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return logos;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener(final AutoCompleteTextView editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (schoolItems.size() > 0) {
                    // show all suggestions
                    editText.showDropDown();
                }
                return false;
            }
        });
    }

    private void setOnItemClickListener(final AutoCompleteTextView editText) {
        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //hide the keyboard when the user selects a school from the suggestions list
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });
    }

    private void setOnClickListener(final String[] schoolNames, final AutoCompleteTextView editText) {
        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check whether the user has typed a valid school name
                validateClick(editText, schoolNames, true);
            }
        });
        teacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check whether the user has typed a valid school name
                validateClick(editText, schoolNames, false);
            }
        });
    }

    private void validateClick(AutoCompleteTextView editText, String[] schoolNames, Boolean view) {
        String schoolName = editText.getText().toString().trim();
        if (schoolName.isEmpty()) {
            Toast.makeText(ChooseSchool.this, "Моля изберете училище!", Toast.LENGTH_SHORT).show();
        } else {
            if (!Arrays.asList(schoolNames).contains(schoolName)) {
                Toast.makeText(ChooseSchool.this, "Моля въведете валидно училище!", Toast.LENGTH_SHORT).show();
            } else {
                sharedPreferences.edit().putBoolean("studentView", view).apply();
                Intent intent = new Intent(ChooseSchool.this, MainActivity.class);
                intent.putExtra("BitmapLogo", schoolItems.get(getIndex(schoolItems, schoolName)).getSchoolImage());
                getSchoolDatabaseNamesFromDatabase(schoolName, intent);
            }
        }
    }

    private int getIndex(List<SchoolItem> schoolItems, String schoolName) {
        //retrieve the selected index of the school selected from the suggestions list
        for (SchoolItem item : schoolItems) {
            if (item.getSchoolName().equals(schoolName)) {
                return schoolItems.indexOf(item);
            }
        }
        return 0;
    }

    private void getSchoolDatabaseNamesFromDatabase(final String value, final Intent intent) {
        //send request to the database
        String url = "https://schooltimetable.site/get_database_name.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //set up settings for the main activity and after that launch it
                        setUpMainActivitySettings(response, intent);
                    }
                }, new Response.ErrorListener() {
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
                MyData.put("androidSchoolName", value);
                return MyData;
            }
        };
        //add timeout of 7 seconds
        request.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
        //show loading dialog until there is a response from the server
        requestManager.showLoadingDialogUntilResponse(mQueue);
    }

    private void setUpMainActivitySettings(String response, Intent intent) {
        sharedPreferences.edit().putString("databaseName", response).apply();
        sharedPreferences.edit().putBoolean("schoolList", false).apply();
        if (sharedPreferences.getBoolean("firstrun", true)) {
            sharedPreferences.edit().putBoolean("firstrun", false).apply();
        }
        sharedPreferences.edit().putBoolean("studentFirstStart", true).apply();
        sharedPreferences.edit().putBoolean("teacherFirstStart", true).apply();
        startActivity(intent);
    }

    private void checkIfSuggestionsIsShowing(Bundle savedInstanceState) {
        //retrieve the state of the suggestions list
        boolean notShowing = savedInstanceState == null || savedInstanceState.getBoolean("dropdownNotPopped")
                || !savedInstanceState.getBoolean("dropdownShowing");
        if (!notShowing) {
            this.firstLaunch.edit().putBoolean("firstLaunch", false).apply();
        } else {
            this.firstLaunch.edit().putBoolean("firstLaunch", true).apply();
        }
    }

}
