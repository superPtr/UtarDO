package my.edu.utar.utardo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Task implements Parcelable {
    private String title;
    private String details;
    private Date endDate;
    private boolean done;

    public Task(String title, String details, boolean done, Date endDate) {
        this.title = title;
        this.details = details;
        this.endDate = endDate;
        this.done = done;
    }

    protected Task(Parcel in) {
        title = in.readString();
        details = in.readString();
        endDate = new Date(in.readLong());
        done = in.readByte() != 0;
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(details);
        dest.writeLong(endDate.getTime());
        dest.writeByte((byte) (done ? 1 : 0));
    }
}
