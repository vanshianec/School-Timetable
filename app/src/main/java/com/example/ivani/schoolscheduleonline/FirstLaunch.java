package com.example.ivani.schoolscheduleonline;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FirstLaunch extends AppCompatActivity {
    private List<SchoolItem> schoolItems;
    private Button studentButton;
    private Button teacherButton;
    private RequestQueue mQueue;
    private SharedPreferences sharedPreferences;
    private RequestManager requestManager;
    private ErrorManager errorManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);
        //display fullscreen background(no notification bar and action bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.studentButton = findViewById(R.id.school_student_view);
        this.teacherButton = findViewById(R.id.school_teacher_view);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.mQueue = Volley.newRequestQueue(getApplicationContext());
        this.errorManager = new ErrorManager(this);
        requestManager = new RequestManager(this, getApplicationContext(), errorManager);

        final String[] schoolNames = getIntent().getStringArrayExtra("school_names");
        String[] schoolLogos = getIntent().getStringArrayExtra("school_logos");
        setSchoolItems(schoolNames, schoolLogos);
        final AutoCompleteTextView editText = findViewById(R.id.actv);
        final AutoCompleteSchoolAdapter adapter = new AutoCompleteSchoolAdapter(this, this.schoolItems);
        editText.setAdapter(adapter);
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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    editText.setText(" ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editText.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
            @Override
            public void onDismiss() {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
            }
        });

        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateClick(editText, schoolNames, true);
            }
        });
        teacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateClick(editText, schoolNames, false);
            }
        });
    }


    private void validateClick(AutoCompleteTextView editText, String[] schoolNames, Boolean view) {
        String schoolName = editText.getText().toString().trim();
        if (schoolName.isEmpty()) {
            Toast.makeText(FirstLaunch.this, "Моля изберете училище!", Toast.LENGTH_SHORT).show();
        } else {
            if (!Arrays.asList(schoolNames).contains(schoolName)) {
                Toast.makeText(FirstLaunch.this, "Моля въведете валидно училище!", Toast.LENGTH_SHORT).show();
            } else {
                sharedPreferences.edit().putBoolean("studentView", view).apply();
                Intent intent = new Intent(FirstLaunch.this, MainActivity.class);
                intent.putExtra("BitmapLogo", schoolItems.get(getIndex(schoolItems, schoolName)).getSchoolImage());
                takeSelectedTableFromDatabase(schoolName, intent);
            }
        }
    }

    private int getIndex(List<SchoolItem> schoolItems, String schoolName) {
        for (SchoolItem item : schoolItems) {
            if (item.getSchoolName().equals(schoolName)) {
                return schoolItems.indexOf(item);
            }
        }
        return 0;
    }

    public void takeSelectedTableFromDatabase(final String value, final Intent intent) {
        //send request to the database
        String url = "https://schooltimetable.site/get_database_name.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        sharedPreferences.edit().putString("databaseName", response).apply();
                        sharedPreferences.edit().putBoolean("schoolList", false).apply();
                        startActivity(intent);
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
                MyData.put("androidSchoolName", value);
                return MyData;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
        requestManager.showLoadingDialogUntilResponse(mQueue);
    }

    //TODO SEE THIS SUPPRESS
    @SuppressLint("StaticFieldLeak")
    private void setSchoolItems(String[] schoolNames, String[] schoolLogos) {
        this.schoolItems = new ArrayList<>();
        List<Bitmap> logos = null;
        try {
            logos = new AsyncTask<String[], Void, List<Bitmap>>() {
                @Override
                protected List<Bitmap> doInBackground(String[]... params) {
                    try {
                        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
                        for (int i = 0; i < params[0].length; ++i) {
                            bitmaps.add(Glide.with(FirstLaunch.this).asBitmap().override(300, 320).load(params[0][i]).submit().get());
                        }
                        return bitmaps;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute(schoolLogos).get(7000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            //TODO
            e.printStackTrace();
        } catch (InterruptedException e) {
            //TODO
            e.printStackTrace();
        } catch (TimeoutException e) {
            //TODO
            e.printStackTrace();
        }
        int counter = 0;
        if (logos != null) {
            for (Bitmap logo : logos) {
                this.schoolItems.add(new SchoolItem(schoolNames[counter], logo));
                counter++;
            }
        } else {
            //TODO ADD ERROR
        }
    }
}
