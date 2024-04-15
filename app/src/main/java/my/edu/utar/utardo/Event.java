package my.edu.utar.utardo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Event {
    private String title;

    public Event(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}