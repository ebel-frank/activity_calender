package com.ebel_frank.activitycalendar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.utils.Utils;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderHolder> {
    private final List<Integer> reminders;
    private final View view;

    public ReminderAdapter(final List<Integer> reminders, View view) {
        this.reminders = reminders;
        this.view = view;
        view.setOnClickListener(v -> {
            reminders.add(0, 0);
            notifyItemInserted(0);
        });
    }

    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_add_reminder, parent, false);
        return new ReminderHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReminderHolder holder, int position) {
        holder.reminderTime.setSelection(reminders.get(position));
        holder.reminderTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reminders.set(holder.getAdapterPosition(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.removeReminder.setOnClickListener(v -> {
            int position1 = holder.getAdapterPosition();
            reminders.remove(position1);
            notifyItemRemoved(position1);
        });
    }

    @Override
    public int getItemCount() {
        int total = reminders.size();
        if (total > 1) view.setVisibility(View.GONE);
        else view.setVisibility(View.VISIBLE);
        System.out.println(total);
        return total;
    }

    static class ReminderHolder extends RecyclerView.ViewHolder {
        private final Spinner reminderTime;
        private final ImageView removeReminder;

        ReminderHolder(@NonNull View itemView) {
            super(itemView);
            reminderTime = itemView.findViewById(R.id.reminder_time);
            reminderTime.setAdapter(new ArrayAdapter<>(
                    itemView.getContext(), android.R.layout.simple_list_item_1, Utils.getEntries()
            ));
            removeReminder = itemView.findViewById(R.id.remove_reminder);
        }

    }
}
