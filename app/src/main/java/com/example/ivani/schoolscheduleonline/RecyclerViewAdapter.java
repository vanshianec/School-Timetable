package com.example.ivani.schoolscheduleonline;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<TabRow> data;

    public RecyclerViewAdapter(Context context, List<TabRow> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.textViewOrder.setText(data.get(i).getOrder() + "");
        viewHolder.textViewClock.setText(data.get(i).getClockText());
        viewHolder.textViewName.setText(data.get(i).getTeacherText());
        viewHolder.textViewRoom.setText(data.get(i).getRoomText());
        viewHolder.leftBorder.setBackgroundColor(Color.parseColor(data.get(i).getColor()));
        viewHolder.topBorder.setBackgroundColor(Color.parseColor(data.get(i).getColor()));
        //increase the border size by little when the color is not white so the user can see his next grade more easily
        if (!data.get(i).getColor().equals("#FFFFFF")) {
            viewHolder.leftBorder.setLayoutParams(new FrameLayout.LayoutParams(6, FrameLayout.LayoutParams.MATCH_PARENT));
            viewHolder.topBorder.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 6));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOrder;
        private TextView textViewClock;
        private TextView textViewName;
        private TextView textViewRoom;
        private FrameLayout leftBorder;
        private FrameLayout topBorder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrder = (TextView) itemView.findViewById(R.id.order_text);
            textViewClock = (TextView) itemView.findViewById(R.id.clock_text);
            textViewName = (TextView) itemView.findViewById(R.id.teacher_text);
            textViewRoom = (TextView) itemView.findViewById(R.id.room_text);
            leftBorder = (FrameLayout) itemView.findViewById(R.id.leftBorder);
            topBorder = (FrameLayout) itemView.findViewById(R.id.topBorder);
        }
    }

}
