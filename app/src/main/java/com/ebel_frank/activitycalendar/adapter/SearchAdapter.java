package com.ebel_frank.activitycalendar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.activity.AddNoteActivity;
import com.ebel_frank.activitycalendar.model.NoteData;
import com.ebel_frank.activitycalendar.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> implements Filterable {

    private final List<NoteData> notes, fullNotes;

    public SearchAdapter(List<NoteData> fullNotes) {
        this.notes = new ArrayList<>();
        this.fullNotes = fullNotes;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_notes_layout, parent, false);
        return new SearchHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(@NonNull CharSequence constraint) {
            List<NoteData> filteredList = new ArrayList<>();
            if (constraint.length() != 0) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (NoteData item : fullNotes) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {    // it can also be item.getContent().toLowerCase().contains(filterPattern)
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();
            notes.addAll((Collection<? extends NoteData>) results.values);
            notifyDataSetChanged();
        }
    };

    static class SearchHolder extends RecyclerView.ViewHolder {

        private final TextView noteTitle, noteContent, noteDate, noteTime;
        private LinearLayout contentIcons;

        SearchHolder(@NonNull final View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteContent = itemView.findViewById(R.id.note_content);
            noteDate = itemView.findViewById(R.id.note_date);
            noteTime = itemView.findViewById(R.id.note_time);
            contentIcons = itemView.findViewById(R.id.content_icons);
        }

        void bind(final NoteData note) {
            noteTitle.setText(note.getTitle());
            noteContent.setText(note.getContent());
            noteDate.setText(Utils.convertDateToString(note.getDate()));
            noteTime.setText(Utils.convertTimeToString(note.getDate()));

            itemView.setOnClickListener(v ->
                    itemView.getContext().startActivity(
                    AddNoteActivity.newIntent(itemView.getContext(), note.get_id())));
        }
    }

}
