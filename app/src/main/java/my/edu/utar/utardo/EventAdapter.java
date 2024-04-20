// EventAdapter
package my.edu.utar.utardo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private OnItemClickListener listener;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewEventTitle.setText(event.getEventName());

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(event);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewEventTitle;

        public EventViewHolder(View itemView) {
            super(itemView);
            textViewEventTitle = itemView.findViewById(R.id.textViewEventTitle);
        }
    }

    public void setEvents(List<Event> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }
}
