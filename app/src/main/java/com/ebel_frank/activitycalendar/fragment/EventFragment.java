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
import com.ebel_frank.activitycalendar.activity.AddEventActivity;
import com.ebel_frank.activitycalendar.interfaces.setTitleSubtitleText;
import com.ebel_frank.activitycalendar.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private boolean mHasReachedTopOnce = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);

        final FloatingActionButton eventsFAB = root.findViewById(R.id.eventsFAB);
        eventsFAB.setOnClickListener(v -> startActivity(new Intent(getContext(), AddEventActivity.class)));

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0 && !mHasReachedTopOnce) {
                    mHasReachedTopOnce = true;
                    Utils.fadeAnimation(eventsFAB, true);
                } else if (dy > 0  && mHasReachedTopOnce) {
                    mHasReachedTopOnce = false;
                    Utils.fadeAnimation(eventsFAB, false);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new Utils.LoadData(requireContext(), recyclerView, 1, null).execute(requireActivity());

        return root;
    }

    // this method refreshes the page to check for any changes
    private void updateUI() {
        new Utils.LoadData(requireContext(), recyclerView, 1, null).execute(requireActivity());
    }

    @Override
    public void onResume() {
        updateUI();
        super.onResume();
        setTitleSubtitleText mSetTitleSubtitleText = (setTitleSubtitleText) context;
        mSetTitleSubtitleText.setTitleTxt(R.string.title_events);
    }

}