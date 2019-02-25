package com.example.ivani.schoolscheduleonline.Contracts;

import com.example.ivani.schoolscheduleonline.TabRow;

import java.util.List;

public interface JsonData {
    void parseJson();
    void addResult(TabRow item);
    List<TabRow> getResultList();
    void printError(Exception e);
}
