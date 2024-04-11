package my.edu.utar.utardo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseViewHolder> {
    private List<Course> coursesList;

    public CoursesAdapter(List<Course> coursesList) {
        this.coursesList = coursesList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = coursesList.get(position);
        holder.textCourseName.setText(course.getDocumentId());
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView textCourseName;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourseName = itemView.findViewById(R.id.textCourseName);
        }
    }
}