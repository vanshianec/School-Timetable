package com.example.ivani.schoolscheduleonline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteSchoolAdapter extends ArrayAdapter<SchoolItem> {
    private List<SchoolItem> schoolListFull;

    public AutoCompleteSchoolAdapter(@NonNull Context context, @NonNull List<SchoolItem> schoolList) {
        super(context, 0, schoolList);
        schoolListFull = new ArrayList<>(schoolList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return schoolFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.school_autocomplete_row, parent, false
            );
        }
        //create custom school item text and image view
        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        ImageView imageViewFlag = convertView.findViewById(R.id.school_image);
        //get the item based on selected position from the autocomplete text view
        SchoolItem schoolItem = getItem(position);

        if (schoolItem != null) {
            //set the text and image view in the school item
            textViewName.setText(schoolItem.getSchoolName());
            imageViewFlag.setImageBitmap(schoolItem.getSchoolImage());
        }

        return convertView;
    }

    private Filter schoolFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            //create suggestion list to display in the show drop down menu
            List<SchoolItem> suggestions = new ArrayList<>();
            //add all school items if the user hasn't typed anything in the autocomplete text field
            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(schoolListFull);
            } else {
                //else add all matching school items based on the text the user has typed in the autocomplete text field
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SchoolItem item : schoolListFull) {
                    if (item.getSchoolName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //remove all elements from the list
            clear();
            //add all results that matched the filtering
            addAll((List) results.values);
            //refresh the view
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            //get the char sequence of all results that matched the filtering
            return ((SchoolItem) resultValue).getSchoolName();
        }
    };

}