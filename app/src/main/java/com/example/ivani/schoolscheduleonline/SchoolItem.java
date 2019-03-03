package com.example.ivani.schoolscheduleonline;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class SchoolItem {
    private String schoolName;
    private Bitmap schoolImage;

    public SchoolItem(String schoolName, Bitmap schoolImage) {
        this.schoolName = schoolName;
        this.schoolImage = schoolImage;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public Bitmap getSchoolImage() {
        return this.schoolImage;
    }
}