package com.ebel_frank.activitycalendar.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.activity.AddNoteActivity;
import com.ebel_frank.activitycalendar.activity.SearchActivity;
import com.ebel_frank.activitycalendar.interfaces.setTitleSubtitleText;
import com.ebel_frank.activitycalendar.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NotesFragment extends Fragment {

    private Context context;
    private FloatingActionButton notesFAB;
    private RecyclerView notesRecyclerView;
    private boolean mHasReachedTopOnce = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notes, container, false);

        root.findViewById(R.id.search_bar).setOnClickListener(v -> startActivity(new Intent(getContext(), SearchActivity.class)));

        notesFAB = root.findViewById(R.id.notesFAB);
        notesFAB.setOnClickListener(v -> startActivity(new Intent(getContext(), AddNoteActivity.class)));
        ((RecyclerView) root.findViewById(R.id.notes_recycler_view)).addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0 && !mHasReachedTopOnce) {
                    mHasReachedTopOnce = true;
                    Utils.fadeAnimation(notesFAB, true);
                } else if (dy > 0  && mHasReachedTopOnce) {
                    mHasReachedTopOnce = false;
                    Utils.fadeAnimation(notesFAB, false);
                }
            }
        });

        notesRecyclerView = root.findViewById(R.id.notes_recycler_view);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new Utils.LoadData(requireContext(), notesRecyclerView, 2, null).execute(requireActivity());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((setTitleSubtitleText) context).setTitleTxt(R.string.title_notes);
        new Utils.LoadData(requireContext(), notesRecyclerView, 2, null).execute(requireActivity());
    }
}
