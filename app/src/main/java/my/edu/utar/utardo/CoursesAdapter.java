// CourseAdapter
package my.edu.utar.utardo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseViewHolder> {
    private List<Course> courseList;
    private OnItemClickListener listener;

    public CoursesAdapter(List<Course> courseList) {
        this.courseList = courseList;
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
        Course course = courseList.get(position);
        holder.textCourseName.setText(course.getCourseName());

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(course); // Pass the selected course
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Course course);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView textCourseName;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourseName = itemView.findViewById(R.id.textCourseName);
        }
    }
}
