package com.example.ivani.schoolscheduleonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class Monday extends Fragment {
    private RecyclerView recyclerView;
    private List<TabRow> tabRowList;

    private static final String ARG_JSON_STRING = "JSON_PARAM";

    private String jsonString;


    private OnFragmentInteractionListener mListener;

    public Monday() {
    }

    public static Monday newInstance(String jsonString) {
        Monday fragment = new Monday();
        Bundle args = new Bundle();
        args.putString(ARG_JSON_STRING, jsonString);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jsonString = getArguments().getString(ARG_JSON_STRING);
        }
        String mondayTeacherOrGrade = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getBoolean("studentView", true) ? "monday_teacher" : "monday_grade";
        JsonDataManager dataManager = new JsonDataManager(jsonString, mondayTeacherOrGrade, "monday_room",
                getClass().getSimpleName(), new RowDataManager());
        dataManager.parseJson();
        tabRowList = dataManager.getTabRowList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monday, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), tabRowList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
