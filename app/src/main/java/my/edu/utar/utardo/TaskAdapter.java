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

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
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
                taskList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, taskList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("TaskAdapter", "getItemCount called: " + taskList.size());
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

