package com.example.ivani.schoolscheduleonline;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class ErrorManager {

    private Context context;

    public ErrorManager(Context context) {
        this.context = context;
    }

    public void displayErrorMessage(VolleyError error) {
        String message = null;
        if (error instanceof NetworkError) {
            message = "Няма достъп до интернет. Моля, проверете връзката си.";
        } else if (error instanceof ServerError) {
            message = "Проблем при свързването със съръра. Моля, опитайте по-късно.";
        } else if (error instanceof AuthFailureError) {
            message = "Няма достъп до интернет. Моля, проверете връзката си.";
        } else if (error instanceof ParseError) {
            message = "Проблем при обработването на данните. Моля, опитайте по-късно.";
        } else if (error instanceof TimeoutError) {
            message = "Времето за свързване изтече! Моля, проверете интернет връзката си.";
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
