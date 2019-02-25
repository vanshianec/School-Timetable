package com.example.ivani.schoolscheduleonline;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);
        this.studentButton = findViewById(R.id.school_student_view);
        this.teacherButton = findViewById(R.id.school_teacher_view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mQueue = Volley.newRequestQueue(getApplicationContext());
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


        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateClick(editText, schoolNames, true, editText.getListSelection());
            }
        });
        teacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateClick(editText, schoolNames, false, editText.getListSelection());
            }
        });
    }


    private void validateClick(AutoCompleteTextView editText, String[] schoolNames, Boolean view, int index) {
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
                displayErrorMessage(error);
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
        showLoadingDialogUntilResponse();
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

    @NonNull
    private Dialog createLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstLaunch.this);
        builder.setView(R.layout.progress_bar);
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
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
                            bitmaps.add(Glide.with(FirstLaunch.this).asBitmap().override(300,320).load(params[0][i]).submit().get());
                        }
                        return bitmaps;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public void onPostExecute(List<Bitmap> bitmaps) {
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
