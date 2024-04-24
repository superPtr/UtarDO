package my.edu.utar.utardo;

import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = filterNonCompletedTasks(taskList);
    }

    // Helper method to filter out completed tasks
    private List<Task> filterNonCompletedTasks(List<Task> tasks) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return tasks.stream()
                    .filter(task -> !"Completed".equals(task.getTaskStatus()))
                    .collect(Collectors.toList());
        } else {
            List<Task> filteredTasks = new ArrayList<>();
            for (Task task : tasks) {
                if (!"Completed".equals(task.getTaskStatus())) {
                    filteredTasks.add(task);
                }
            }
            return filteredTasks;
        }
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = filterNonCompletedTasks(taskList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("TaskAdapter", "onCreateViewHolder called");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Log.d("TaskAdapter", "onBindViewHolder called for position " + position);
        Task task = taskList.get(position);
        holder.textViewTaskTitle.setText(task.getTitle());
        holder.textViewTaskDetails.setText(task.getDetails());
        holder.textViewTaskStatus.setText(task.getTaskStatus());

        holder.checkboxDone.setOnCheckedChangeListener(null);
        holder.checkboxDone.setChecked(false);

        holder.checkboxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                task.setTaskStatus("Completed");
                int currentPosition = holder.getAdapterPosition();
                taskList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, taskList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTaskTitle;
        public TextView textViewTaskDetails;
        public TextView textViewTaskStatus;
        public CheckBox checkboxDone;

        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewTaskTitle = itemView.findViewById(R.id.textViewTaskTitle);
            textViewTaskDetails = itemView.findViewById(R.id.textViewTaskDetails);
            textViewTaskStatus = itemView.findViewById(R.id.textViewTaskStatus);
            checkboxDone = itemView.findViewById(R.id.checkboxDone);
        }
    }
}