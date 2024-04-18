package my.edu.utar.utardo;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String eventName;
    private String eventDetails;
    private String eventDate;

    public Event(String eventName, String eventDetails, String eventDate) {
        this.eventName = eventName;
        this.eventDetails = eventDetails;
        this.eventDate = eventDate;
    }

    protected Event(Parcel in) {
        eventName = in.readString();
        eventDetails = in.readString();
        eventDate = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(eventDetails);
        dest.writeString(eventDate);
    }
}
