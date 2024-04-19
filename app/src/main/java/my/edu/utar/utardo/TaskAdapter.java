// TaskAdapter
package my.edu.utar.utardo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTaskTitle.setText(task.getTitle());
        // Set initial checkbox state
        holder.checkboxDone.setChecked(task.isDone());

        // Set listener to toggle checkbox state and delete task
        holder.checkboxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Remove the task from the list if checkbox is checke
            task.setDone(isChecked);
            if (isChecked) {
                taskList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

//    @Override
//    public int getItemCount() {
//        return taskList.size();
//    }

    @Override
    public int getItemCount() {
        // Ensure taskList is not null before getting its size
        return taskList != null ? taskList.size() : 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTaskTitle;
        public CheckBox checkboxDone;

        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewTaskTitle = itemView.findViewById(R.id.textViewTaskTitle);
            checkboxDone = itemView.findViewById(R.id.checkboxDone);
        }
    }

    public void setTasks(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

}
