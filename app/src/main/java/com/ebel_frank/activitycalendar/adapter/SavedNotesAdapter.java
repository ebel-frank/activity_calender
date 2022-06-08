package com.ebel_frank.activitycalendar.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.activity.AddNoteActivity;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.model.NoteData;
import com.ebel_frank.activitycalendar.utils.Utils;

import java.util.List;

public class SavedNotesAdapter extends RecyclerView.Adapter<SavedNotesAdapter.NotesHolder> {
    private final List<NoteData> notes;

    public SavedNotesAdapter(List<NoteData> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_notes_layout, parent, false);
        return new NotesHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NotesHolder extends RecyclerView.ViewHolder {

        private final TextView noteTitle, noteContent, noteDate, noteTime;
        private LinearLayout contentIcons;

        NotesHolder(@NonNull final View itemView) {
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
            //noteTime.setText(note.get_id()+"");

            itemView.setOnClickListener(v -> itemView.getContext().startActivity(
                    AddNoteActivity.newIntent(itemView.getContext(), note.get_id())));

            itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Delete this event?")
                        .setMessage(itemView.getContext().getString(R.string.delete_warning))
                        .setPositiveButton(R.string.delete, (dialog, which) -> {
                            int position = getAdapterPosition();
                            AppDatabase.getInstance(itemView.getContext()).appDao().delete(note);
                            notes.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, notes.size());
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show();
                return true;
            });
        }
    }

}
