package com.ebel_frank.activitycalendar.fragment;

import static com.ebel_frank.activitycalendar.activity.SettingsActivity.PREFS;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.interfaces.setTitleSubtitleText;
import com.ebel_frank.activitycalendar.model.QuoteData;
import com.ebel_frank.activitycalendar.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AppDatabase database = AppDatabase.getInstance(getContext());
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        MobileAds.initialize(requireContext(), initializationStatus -> {});
        List<String> testDeviceIds = Collections.singletonList("33BE2250B43518CCDA7DE426D04EE231");
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds)
                        .build());

        AdView adView = root.findViewById(R.id.ad_view);
        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        SharedPreferences settings = context.getSharedPreferences(PREFS, 0);
        int lastDay = settings.getInt("day", 0);
        if (currentDay != lastDay) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("day", currentDay);

            editor.putInt("quote", new Random().nextInt(65)+1);    // sets the quotes on the screen.
            editor.apply();
        }
        QuoteData quoteData = database.appDao().getQuote(settings.getInt("quote", 0));

        TextView quote = root.findViewById(R.id.quote);
        quote.setText(quoteData.getQuote());
        TextView quoteAuthor = root.findViewById(R.id.quote_author);
        quoteAuthor.setText(quoteData.getAuthor());

        LinearLayout quoteLayout = root.findViewById(R.id.quote_layout);

        quoteLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        quoteLayout.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) v, new AutoTransition());
            if(quote.getVisibility() == View.VISIBLE) {
                quote.setVisibility(View.GONE);
                quoteAuthor.setVisibility(View.GONE);
            } else {
                quote.setVisibility(View.VISIBLE);
                quoteAuthor.setVisibility(View.VISIBLE);
            }
        });

        View defaultView = root.findViewById(R.id.default_view);

        RecyclerView todayTasks = root.findViewById(R.id.today_task_recycler_view);
        todayTasks.setLayoutManager(new LinearLayoutManager(getActivity()));
        new Utils.LoadData(requireContext(), todayTasks, 0, defaultView).execute(requireActivity());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleSubtitleText mSetTitleSubtitleText = (setTitleSubtitleText) context;
        mSetTitleSubtitleText.setTitleTxt(R.string.hello);
        if (context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean("darkTheme", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}