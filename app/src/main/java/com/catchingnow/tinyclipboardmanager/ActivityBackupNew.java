package com.catchingnow.tinyclipboardmanager;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ActivityBackupNew extends MyActionBarActivity {
    private boolean isReverseSort = false;
    private boolean allItems = true;
    private final Calendar dateFrom = Calendar.getInstance();
    private final Calendar dateTo = Calendar.getInstance();
    private DatePicker datePickerFrom;
    private DatePicker datePickerTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_backup);
        super.onCreate(savedInstanceState);
        initExportView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_backup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
        initExportView();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private void initExportView() {
        //get started date
        long startedDate = dateFrom.getTimeInMillis();
        List<ClipObject> clipObjects = Storage.getInstance(this).getClipHistory();
        if (clipObjects.size() > 0) {
            startedDate = clipObjects.get(clipObjects.size() - 1).getDate().getTime();
        }
        dateFrom.setTimeInMillis(startedDate);

        while (dateFrom.after(dateTo)) {
            dateFrom.setTimeInMillis(dateFrom.getTimeInMillis() - 70000000);
        }

        //set the start date to the minimum
        dateFrom.set(Calendar.HOUR_OF_DAY, dateFrom.getMinimum(Calendar.HOUR_OF_DAY));
        dateFrom.set(Calendar.MINUTE, dateFrom.getMinimum(Calendar.MINUTE));
        dateFrom.set(Calendar.SECOND, dateFrom.getMinimum(Calendar.SECOND));
        dateFrom.set(Calendar.MILLISECOND, dateFrom.getMinimum(Calendar.MILLISECOND));
        //set the end date to the maximum
        dateTo.set(Calendar.HOUR_OF_DAY, dateTo.getMaximum(Calendar.HOUR_OF_DAY));
        dateTo.set(Calendar.MINUTE, dateTo.getMaximum(Calendar.MINUTE));
        dateTo.set(Calendar.SECOND, dateTo.getMaximum(Calendar.SECOND));
        dateTo.set(Calendar.MILLISECOND, dateTo.getMaximum(Calendar.MILLISECOND));

        Button buttonExport = findViewById(R.id.export_button);
        SwitchCompat switchReverseSort = findViewById(R.id.switch_reverse_sort);
        final SwitchCompat switchOnlyStarredItems =  findViewById(R.id.switch_only_starred_items);
        datePickerFrom = findViewById(R.id.date_picker_from);
        datePickerTo = findViewById(R.id.date_picker_to);
        buttonExport.setOnClickListener(v -> export());
        switchReverseSort.setOnCheckedChangeListener((buttonView, isChecked) -> isReverseSort = isChecked);
        switchOnlyStarredItems.setOnCheckedChangeListener((buttonView, isChecked) -> {
            allItems = isChecked;
            switchOnlyStarredItems.setText(
                    allItems ?
                            getString(R.string.switch_all_items)
                            :
                            getString(R.string.switch_only_starred_items)
            );
        });
        datePickerFrom.init(dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH), null);
        datePickerTo.init(dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH), null);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerFrom.setCalendarViewShown(false);
            datePickerTo.setCalendarViewShown(false);
        }

        long dateDiff = dateTo.getTimeInMillis() - dateFrom.getTimeInMillis();
        if (dateDiff > 80000000) {
            datePickerFrom.setMinDate(dateFrom.getTimeInMillis());
            datePickerTo.setMinDate(dateFrom.getTimeInMillis());
        }
        datePickerFrom.setMaxDate(dateTo.getTimeInMillis());
        datePickerTo.setMaxDate(dateTo.getTimeInMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            HorizontalScrollView datePickersScrollView = findViewById(R.id.date_pickers_scroll_view);
            datePickersScrollView.smoothScrollTo(0, 0);
        } else {
            ScrollView datePickersScrollView = findViewById(R.id.date_pickers_scroll_view);
            datePickersScrollView.smoothScrollTo(0, 0);
        }

    }


    private void export() {
        if (BackupExport.makeExport(
                this,
                new Date(datePickerFrom.getYear() - 1900, datePickerFrom.getMonth(), datePickerFrom.getDayOfMonth()),
                new Date(datePickerTo.getYear() - 1900, datePickerTo.getMonth(), datePickerTo.getDayOfMonth() + 1),
                isReverseSort,
                allItems
        )) {
            finish();
        }
    }
}
