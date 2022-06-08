package com.ebel_frank.activitycalendar.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.model.NoteData;
import com.ebel_frank.activitycalendar.utils.Utils;

import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    private ImageView shareNote, deleteNote, saveNote;
    private EditText noteTitle, noteContent;
    private TextView noteDate;

    private static final String EXTRA_NOTE_ID = "note_id";
    private AppDatabase database;
    private NoteData note;

    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        database = AppDatabase.getInstance(this);   // instance of the database class

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assignVariables();

        int _id = getIntent().getIntExtra(EXTRA_NOTE_ID, 0);
        if (_id != 0) {
            shareNote.setVisibility(View.VISIBLE);
            deleteNote.setVisibility(View.VISIBLE);

            note = database.appDao().getNote(_id);
            noteTitle.setText(note.getTitle());
            date.setTime(note.getDate());
            noteContent.setText(note.getContent());
        }
        noteDate.setText(String.format("%s %s", Utils.convertDateToString(date.getTime()), Utils.convertTimeToString(date.getTime())));

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddNoteActivity.this)
                        .setTitle("Delete this note?")
                        .setMessage(getResources().getString(R.string.delete_warning))
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (note != null) {
                                    database.appDao().delete(note);
                                }
                                noteTitle.setText("");
                                noteContent.setText("");
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show();
            }
        });

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                shareNote.setVisibility(View.VISIBLE);
                deleteNote.setVisibility(View.VISIBLE);
            }
        });

        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                menuIconsVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        noteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                menuIconsVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void assignVariables() {
        shareNote = findViewById(R.id.share);
        deleteNote = findViewById(R.id.delete);
        saveNote = findViewById(R.id.save);
        noteTitle = findViewById(R.id.note_title);
        noteDate = findViewById(R.id.note_date);
        noteContent = findViewById(R.id.note_content);
    }

    void menuIconsVisibility() {
        if (noteTitle.getText().length() == 0 && noteContent.getText().length() == 0) {
            saveNote.setVisibility(View.GONE);
            shareNote.setVisibility(View.GONE);
            deleteNote.setVisibility(View.GONE);
        } else {
            saveNote.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent newIntent(Context context, int id) {
        Intent intent = new Intent(context, AddNoteActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, id);
        return intent;
    }

    @Override
    public void finish() {
        super.finish();
        String title = noteTitle.getText().toString().trim();
        String content = noteContent.getText().toString().trim();
        if (note == null && (!title.isEmpty() || !content.isEmpty())) {
            note = new NoteData(title, content, date.getTime(), new String[]{"Hmmm"});
            database.appDao().insert(note);
        } else if (note != null) {
            if (title.isEmpty() && content.isEmpty()) {
                database.appDao().delete(note);
                return;
            }
            note.setTitle(title);
            note.setContent(content);
            note.setDate(date.getTime());
            database.appDao().update(note);
        }

    }
}
