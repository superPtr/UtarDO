package my.edu.utar.utardo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Task implements Parcelable {
    private String tasktitle;
    private String taskDetails;
    private String taskStatus;
    private String reminderStatus;
    private String startDate;
    private String endDate;

    public Task(String title, String details, String taskStatus, String reminderStatus, String startDate, String endDate) {
        this.tasktitle = title;
        this.taskDetails = details;
        this.taskStatus = taskStatus;
        this.reminderStatus = reminderStatus;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    protected Task(Parcel in) {
        tasktitle = in.readString();
        taskDetails = in.readString();
        taskStatus = in.readString();
        reminderStatus = in.readString();
        startDate = in.readString();
        endDate = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getTitle() {
        return tasktitle;
    }

    public void setTitle(String title) {
        this.tasktitle = title;
    }

    public String getDetails() {
        return taskDetails;
    }

    public void setDetails(String details) {
        this.taskDetails = details;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getReminderStatus() {
        return reminderStatus;
    }

    public void setReminderStatus(String reminderStatus) {
        this.reminderStatus = reminderStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tasktitle);
        dest.writeString(taskDetails);
        dest.writeString(taskStatus);
        dest.writeString(reminderStatus);
        dest.writeString(startDate);
        dest.writeString(endDate);
    }
}
