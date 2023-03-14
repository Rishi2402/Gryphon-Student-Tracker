package com.example.studenttracker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GryphAdapter extends RecyclerView.Adapter<GryphAdapter.MyViewHolder>
{
    public final RecyclerViewCourseInterface recyclerViewCourseInterface;
    ArrayList<CourseModel> courseList;


    public GryphAdapter(ArrayList<CourseModel> courseList, RecyclerViewCourseInterface recyclerViewCourseInterface) {
        this.courseList = courseList;
        this.recyclerViewCourseInterface = recyclerViewCourseInterface;
    }

       @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_layout,parent, false);
        return new MyViewHolder(view,recyclerViewCourseInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.courseButton.setText(courseList.get(position).getCourse());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        Button courseButton;
        RecyclerView recyclerView;
        public ImageView enterImage;
        public MyViewHolder(@NonNull View itemView, RecyclerViewCourseInterface recyclerViewCourseInterface) {
            super(itemView);
            courseButton= itemView.findViewById(R.id.courseButton);
            recyclerView = itemView.findViewById(R.id.courseContainer);
            enterImage = itemView.findViewById(R.id.enterButton);
            Log.d("coursebuttonbefore","before onClick called!!");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    Log.d("MyViewHolderonClick","onClick called!!");
                    if(recyclerViewCourseInterface != null)
                    {
                        int position = getAdapterPosition();
                        Log.d("positionMyView", String.valueOf(position));
                        if(position != RecyclerView.NO_POSITION)
                        {
                            recyclerViewCourseInterface.onItemClick(position);
                        }
                    }
                }
            });
            enterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewCourseInterface != null)
                    {
                        int position = getAdapterPosition();
                        Log.d("positionMyView", String.valueOf(position));
                        if(position != RecyclerView.NO_POSITION)
                        {
                            recyclerViewCourseInterface.onImageClick(position);
                        }
                    }
                }
            });
        }
    }
}
