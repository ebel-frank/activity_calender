package com.ebel_frank.activitycalendar.adapter;

import static com.ebel_frank.activitycalendar.utils.Utils.getEventIcon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.activity.AddEventActivity;
import com.ebel_frank.activitycalendar.activity.EventActivity;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.model.EventData;
import com.ebel_frank.activitycalendar.utils.NotificationUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SavedEventsAdapter extends RecyclerView.Adapter<SavedEventsAdapter.EventHolder> {

    private static final byte EVENT_TYPE = 0;
    private static final byte TITLE_TYPE = 1;
    private static final byte INFO_TYPE = 2;

    private final List<Object> dataList;

    // The constructor
    public SavedEventsAdapter(List<Object> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==EVENT_TYPE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.saved_events_layout, parent, false);
        }else if (viewType==TITLE_TYPE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.saved_events_title, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.saved_events_info, parent, false);
        }
        return new EventHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getClass() == EventData.class) {
            return EVENT_TYPE;
        }
        /*
            I used less than 9 because the length of string in each element of
            info array is greater than 9
        */
        else if (dataList.get(position).toString().length() < 9) {
            return TITLE_TYPE;
        } else {
            return INFO_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        if (dataList.get(position).getClass() == EventData.class) {
            holder.bindEvent((EventData) dataList.get(position));
        } else {
            holder.bindEvent(dataList.get(position).toString());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class EventHolder extends RecyclerView.ViewHolder {

        private EventHolder(final View itemView) {
            super(itemView);
        }

        private void bindEvent(String title) {
            ((TextView) itemView.findViewById(R.id.event_txt)).setText(title);
        }

        private void bindEvent(EventData event) {
            TextView eventName, description, date;
            ImageView category_icon = itemView.findViewById(R.id.event_category_icon);
            itemView.findViewById(R.id.ic_arrow_right).setVisibility(View.GONE);    // set the arrow's visibility to gone
            eventName = itemView.findViewById(R.id.event_name);
            description = itemView.findViewById(R.id.event_description);
            date = itemView.findViewById(R.id.event_date);
            final int _id = event.get_id();
            category_icon.setImageResource(getEventIcon(event.getCategory()));

            Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
            long fromDate = event.getFromDate();
            long toDate = event.getToDate();
            try {
                //c2.setTime(new Date(toDate));
//                if ((value == 0 || value == 1) && (c1.getTime().getTime() > c2.getTime().getTime())) {
//                    completedCheckBox.setChecked(true);
//                }

                c2.setTime(new Date(fromDate));
            }catch (Exception e) {
                // do nothing
            }
            SimpleDateFormat sDF = new SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault());
            int c1_day = c1.get(Calendar.DAY_OF_YEAR), c2_day = c2.get(Calendar.DAY_OF_YEAR);
            int c1_year = c1.get(Calendar.YEAR), c2_year = c2.get(Calendar.YEAR);
            if  (( c1_day > c2_day || c1_day == c2_day || c1_day + 1 == c2_day || c1_day + 1 < c2_day)
                &&  c1_year == c2_year) {
                eventName.setText(event.getEventName());
                description.setText(event.getDescription());
                date.setText(String.format("%s - %s", sDF.format(fromDate), sDF.format(toDate)));

            } // don't attach the adapter; do nothing

            //*******************************************
            // This block of code is used to setOnClickListener for the saved events
            // such that the user can edit and delete events...
            CharSequence[] options = new CharSequence[]{"Edit", "Delete"};

            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setItems(options, (dialog, which) -> {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        // in the case of an EDIT, I used the original database ID
                        Intent intent = AddEventActivity.newIntent(itemView.getContext(), _id);
                        itemView.getContext().startActivity(intent);
                        break;
                    case 1:
                        new AlertDialog.Builder(itemView.getContext())
                                .setTitle("Delete this event?")
                                .setMessage(itemView.getContext().getString(R.string.delete_warning))
                                .setPositiveButton(R.string.delete, (dialog1, which1) -> {
                                    int position = getAdapterPosition();
                                    AppDatabase.getInstance(itemView.getContext()).appDao().delete((EventData)dataList.get(position));
                                    NotificationUtils.cancelNotification(itemView.getContext(), _id);
                                    dataList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, dataList.size());
                                })
                                .setNegativeButton(android.R.string.cancel, null)
                                .create()
                                .show();

                        break;
                }
            });

            Dialog dialog = builder.create();
            //*******************************************

            itemView.setOnLongClickListener(v -> {
                dialog.show();
                return false;
            });
            itemView.setOnClickListener(v -> {
                this.itemView.getContext().startActivity(
                        EventActivity.newIntent(this.itemView.getContext(), _id)
                );
            });
        }
    }
}