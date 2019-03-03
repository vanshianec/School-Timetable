package com.example.ivani.schoolscheduleonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

public class CustomAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {

    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    public CustomAutoCompleteTextView(Context context) {
        super(context);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        //when back button is pressed hide keyboard first then suggestions
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager.hideSoftInputFromWindow(findFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)) {
                return true;
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public boolean enoughToFilter() {
        //if the activity is just launching don't filter the results in the text field
        if (this.sharedPreferences.getBoolean("firstLaunch", false)) {
            return false;
        }
        //else the user clicked on the text field so we show him the results from the filter and the item list
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        //show all suggestions when no text is entered
        if (focused && getAdapter() != null) {
            performFiltering(getText(), 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the user clicked on the text field so we need to remove to set the firstLaunch to false in the shared preferences
        this.sharedPreferences.edit().putBoolean("firstLaunch", false).apply();
        //show suggestions that match the current text entered when the user touches the search bar
        if (getAdapter() != null) {
            performFiltering(getText(), 0);
        }
        return super.onTouchEvent(event);
    }
}
