package com.example.ivani.schoolscheduleonline;

import android.content.Context;
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


public class Friday extends Fragment {
    private RecyclerView recyclerView;
    private List<TabRow> tabRowList;
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_JSON_STRING = "JSON_PARAM";

    private String jsonString;


    private OnFragmentInteractionListener mListener;

    public Friday() {
    }

    // TODO: Rename and change types and number of parameters
    public static Friday newInstance(String param1) {
        Friday fragment = new Friday();
        Bundle args = new Bundle();
        args.putString(ARG_JSON_STRING, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jsonString = getArguments().getString(ARG_JSON_STRING);
        }
        String fridayTeacherOrGrade = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getBoolean("studentView", true) ? "friday_teacher" : "friday_grade";
        JsonDataManager dataManager = new JsonDataManager(jsonString,fridayTeacherOrGrade,"friday_room",
                getClass().getSimpleName(),new RowDataManager());
        dataManager.parseJson();
        tabRowList = dataManager.getTabRowList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friday, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), tabRowList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
