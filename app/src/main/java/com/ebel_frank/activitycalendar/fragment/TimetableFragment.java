package com.ebel_frank.activitycalendar.fragment;

import static com.ebel_frank.activitycalendar.utils.Utils.convertTimeToString;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;

import com.ebel_frank.activitycalendar.R;
import com.ebel_frank.activitycalendar.database.AppDatabase;
import com.ebel_frank.activitycalendar.interfaces.DialogCloseListener;
import com.ebel_frank.activitycalendar.interfaces.setTitleSubtitleText;
import com.ebel_frank.activitycalendar.model.TimetableData;
import com.ebel_frank.activitycalendar.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimetableFragment extends Fragment implements DialogCloseListener {

    private Guideline time_guideline, monday_guideline, tuesday_guideline,
            wednesday_guideline, thursday_guideline, friday_guideline, saturday_guideline;
    private ConstraintLayout constraintLayout_mainScreen;
    private AppDatabase database;
    private Context context;
    private HashMap<String, Integer> btnHeight;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = AppDatabase.getInstance(getContext());   // instance of the database class

        btnHeight = new HashMap<>();
        btnHeight.put("12 AM",R.id.line12am);
        btnHeight.put("1 AM", R.id.line1am);
        btnHeight.put("2 AM", R.id.line2am);
        btnHeight.put("3 AM", R.id.line3am);
        btnHeight.put("4 AM", R.id.line4am);
        btnHeight.put("5 AM", R.id.line5am);
        btnHeight.put("6 AM", R.id.line6am);
        btnHeight.put("7 AM", R.id.line7am);
        btnHeight.put("8 AM", R.id.line8am);
        btnHeight.put("9 AM", R.id.line9am);
        btnHeight.put("10 AM", R.id.line10am);
        btnHeight.put("11 AM", R.id.line11am);
        btnHeight.put("12 PM", R.id.line12pm);
        btnHeight.put("1 PM", R.id.line1pm);
        btnHeight.put("2 PM", R.id.line2pm);
        btnHeight.put("3 PM", R.id.line3pm);
        btnHeight.put("4 PM", R.id.line4pm);
        btnHeight.put("5 PM", R.id.line5pm);
        btnHeight.put("6 PM", R.id.line6pm);
        btnHeight.put("7 PM", R.id.line7pm);
        btnHeight.put("8 PM", R.id.line8pm);
        btnHeight.put("9 PM", R.id.line9pm);
        btnHeight.put("10 PM", R.id.line10pm);
        btnHeight.put("11 PM", R.id.line11pm);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timetable, container, false);

        final FloatingActionButton timetableFAB = root.findViewById(R.id.timetableFAB);
        timetableFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTimetableFragment addTimetableDialog = new AddTimetableFragment(TimetableFragment.this);
                addTimetableDialog.show(getParentFragmentManager(), "addTimetable");
            }
        });
        root.findViewById(R.id.timetable_scroll_view).setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Utils.fadeAnimation(timetableFAB, scrollY <= oldScrollY);

            }
        });

        constraintLayout_mainScreen = root.findViewById(R.id.timetable_layout);

        time_guideline = root.findViewById(R.id.time_guideline);
        monday_guideline = root.findViewById(R.id.monday_guideline);
        tuesday_guideline = root.findViewById(R.id.tuesday_guideline);
        wednesday_guideline = root.findViewById(R.id.wednesday_guideline);
        thursday_guideline = root.findViewById(R.id.thursday_guideline);
        friday_guideline = root.findViewById(R.id.friday_guideline);
        saturday_guideline = root.findViewById(R.id.saturday_guideline);

        updateUI(database.appDao().getTimetableList());

        return root;
    }

    private void updateUI(final List<TimetableData> timetables) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                // doInBackground
                final ConstraintSet set = new ConstraintSet();
                set.clone(constraintLayout_mainScreen);

                for (int i=0, n=timetables.size(); i < n; i++) {
                    final TimetableData timetable = timetables.get(i);
                    final String title = timetable.getTitle();
                    final String day = timetable.getDay();
                    String fromTime = convertTimeToString(timetable.getFromDate());
                    String toTime = convertTimeToString(timetable.getToDate());

                    GradientDrawable shape = new GradientDrawable();
                    shape.setCornerRadius(15);
                    shape.setColor(timetable.getColor());

                    final Button table = new Button(getActivity());
                    table.setId(View.generateViewId());           // <-- Important
                    table.setText(title);
                    table.setTextColor(Color.BLACK);
                    table.setTextSize(9.5f);
                    table.setElevation(10);
                    table.setBackground(shape);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            constraintLayout_mainScreen.addView(table);
                        }
                    });

                    String timeTxt = fromTime.substring(0,fromTime.length()-6)+fromTime.substring(fromTime.length()-3);
                    if (fromTime.contains(":30")) {
                        set.connect(table.getId(), ConstraintSet.TOP, btnHeight.get(timeTxt), ConstraintSet.BOTTOM, 60);
                    } else {
                        set.connect(table.getId(), ConstraintSet.TOP, btnHeight.get(timeTxt), ConstraintSet.BOTTOM, 0);
                    }
                    timeTxt = toTime.substring(toTime.length()-3);
                    if (toTime.contains(":30")) {
                        timeTxt = (Integer.parseInt(toTime.substring(0,toTime.length()-6))+1)+timeTxt;
                        set.connect(table.getId(), ConstraintSet.BOTTOM, btnHeight.get(timeTxt), ConstraintSet.TOP, 60);
                    } else {
                        timeTxt = toTime.substring(0,toTime.length()-6) + timeTxt;
                        set.connect(table.getId(), ConstraintSet.BOTTOM, btnHeight.get(timeTxt), ConstraintSet.TOP, 0);
                    }
                    switch (day) {
                        case "Sun":
                            set.connect(table.getId(), ConstraintSet.RIGHT, monday_guideline.getId(), ConstraintSet.RIGHT, 4);
                            set.connect(table.getId(), ConstraintSet.LEFT, time_guideline.getId(), ConstraintSet.LEFT, 4);
                            break;
                        case "Mon":
                            set.connect(table.getId(), ConstraintSet.RIGHT, tuesday_guideline.getId(), ConstraintSet.RIGHT, 4);
                            set.connect(table.getId(), ConstraintSet.LEFT, monday_guideline.getId(), ConstraintSet.LEFT, 4);
                            break;
                        case "Tue":
                            set.connect(table.getId(), ConstraintSet.RIGHT, wednesday_guideline.getId(), ConstraintSet.RIGHT, 4);
                            set.connect(table.getId(), ConstraintSet.LEFT, tuesday_guideline.getId(), ConstraintSet.LEFT, 4);
                            break;
                        case "Wed":
                            set.connect(table.getId(), ConstraintSet.RIGHT, thursday_guideline.getId(), ConstraintSet.RIGHT, 4);
                            set.connect(table.getId(), ConstraintSet.LEFT, wednesday_guideline.getId(), ConstraintSet.LEFT, 4);
                            break;
                        case "Thu":
                            set.connect(table.getId(), ConstraintSet.RIGHT, friday_guideline.getId(), ConstraintSet.RIGHT, 4);
                            set.connect(table.getId(), ConstraintSet.LEFT, thursday_guideline.getId(), ConstraintSet.LEFT, 4);
                            break;
                        case "Fri":
                            set.connect(table.getId(), ConstraintSet.RIGHT, saturday_guideline.getId(), ConstraintSet.RIGHT, 4);
                            set.connect(table.getId(), ConstraintSet.LEFT, friday_guideline.getId(), ConstraintSet.LEFT, 4);
                            break;
                        default:
                            set.connect(table.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 4);
                            set.connect(table.getId(), ConstraintSet.LEFT, saturday_guideline.getId(), ConstraintSet.LEFT, 4);
                            break;
                    }

                    table.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Delete this subject?")
                                    .setMessage(getResources().getString(R.string.delete_warning))
                                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            timetables.remove(timetable);
                                            database.appDao().delete(timetable);
                                            constraintLayout_mainScreen.removeView(table);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .create()
                                    .show();
                            return false;
                        }
                    });
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            set.applyTo(constraintLayout_mainScreen);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((setTitleSubtitleText) context).setTitleTxt(R.string.title_timetable);
    }

    @Override
    public void reloadTimetable(int id) {
        List<TimetableData> timetableData = new ArrayList<>();
        timetableData.add(database.appDao().getTimetable(id));
        updateUI(timetableData);
    }
}