package com.ebel_frank.activitycalendar.adapter;

import static com.ebel_frank.activitycalendar.utils.Utils.getEventIcon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.activity.EventActivity;
import com.ebel_frank.activitycalendar.interfaces.Comparator;
import com.ebel_frank.activitycalendar.model.EventData;
import com.ebel_frank.activitycalendar.model.TimetableData;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TodayTaskAdapter extends RecyclerView.Adapter<TodayTaskAdapter.TodayHolder> {

    private static final byte EVENT_TYPE = 0;
    private static final byte TIMETABLE_TYPE = 1;

    private List<Comparator> todayTask;
    private final View inform;

    public TodayTaskAdapter(List<Comparator> todayTask, View inform) {
        this.todayTask = todayTask;
        this.inform = inform;
    }

    @NonNull
    @Override
    public TodayTaskAdapter.TodayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==EVENT_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_events_layout, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_timetable_layout, parent, false);
        }
        return new TodayHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayTaskAdapter.TodayHolder holder, int position) {
        if (todayTask.get(position).getClass() == EventData.class) {
            holder.bind((EventData) todayTask.get(position));
        } else {
            holder.bind((TimetableData) todayTask.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (todayTask.get(position).getClass() == EventData.class) {
            return EVENT_TYPE;
        } else {
            return TIMETABLE_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        int total = todayTask.size();
        if (total == 0) {
            inform.setVisibility(View.VISIBLE);
        } else {
            inform.setVisibility(View.GONE);
        }
        return total;
    }

    static class TodayHolder extends RecyclerView.ViewHolder {

        TodayHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(EventData event) {
            ImageView category_icon = itemView.findViewById(R.id.event_category_icon);
            category_icon.setImageResource(getEventIcon(event.getCategory()));
            TextView eventName = itemView.findViewById(R.id.event_name);
            eventName.setText(event.getEventName());
            final TextView description = itemView.findViewById(R.id.event_description);
            description.setText(event.getDescription());

            SimpleDateFormat sDF = new SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault());
            TextView date = itemView.findViewById(R.id.event_date);
            date.setText(String.format("%s - %s", sDF.format(event.getFromDate()), sDF.format(event.getToDate())));

            itemView.setOnClickListener(v -> {
                this.itemView.getContext().startActivity(
                        EventActivity.newIntent(this.itemView.getContext(), event.get_id())
                );
            });
        }

        void bind(TimetableData timetable) {
            TextView time = itemView.findViewById(R.id.time);
            time.setText(new SimpleDateFormat("hh:mm", Locale.getDefault()).format(timetable.getFromDate()));
            ImageView icon = itemView.findViewById(R.id.timetable_icon);
            DrawableCompat.setTint(DrawableCompat.wrap(icon.getDrawable()), timetable.getColor());

            final TextView subject = itemView.findViewById(R.id.subject);
            subject.setText(timetable.getTitle());
        }
    }
}
