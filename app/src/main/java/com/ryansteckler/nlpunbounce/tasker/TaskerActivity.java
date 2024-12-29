package com.ryansteckler.nlpunbounce.tasker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ryansteckler.nlpunbounce.AlarmDetailFragment;
import com.ryansteckler.nlpunbounce.AlarmsFragment;
import com.ryansteckler.nlpunbounce.BaseDetailFragment;
import com.ryansteckler.nlpunbounce.R;
import com.ryansteckler.nlpunbounce.WakelockDetailFragment;
import com.ryansteckler.nlpunbounce.WakelocksFragment;
import com.ryansteckler.nlpunbounce.RegexDetailFragment;
import com.ryansteckler.nlpunbounce.RegexFragment;

public class TaskerActivity extends AppCompatActivity
        implements
        WakelocksFragment.OnFragmentInteractionListener,
        BaseDetailFragment.FragmentInteractionListener,
        AlarmsFragment.OnFragmentInteractionListener,
        RegexFragment.OnFragmentInteractionListener,
        TaskerWhichFragment.OnFragmentInteractionListener {

    public static final String EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";
    public static final String EXTRA_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB";
    public static final String BUNDLE_TYPE = "type";
    public static final String BUNDLE_NAME = "name";
    public static final String BUNDLE_SECONDS = "seconds";
    public static final String BUNDLE_ENABLED = "enabled";
    private boolean mIsPremium = true;

    Fragment mCurrentFragment = null;

    private static final String TAG = "NlpUnbounceTasker: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_tasker);

        Button save = (Button)findViewById(R.id.buttonTaskerSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
                Bundle taskerBundle = null;

                //Set the default tasker values
                taskerBundle = new Bundle();
                String blurb = "";

                WakelockDetailFragment wlFragment = (WakelockDetailFragment)fragmentManager.findFragmentByTag("wakelock_detail");
                AlarmDetailFragment alarmFragment;
                RegexDetailFragment regexFragment;
                if (wlFragment != null) {
                    taskerBundle.putString(BUNDLE_TYPE, "wakelock");
                    taskerBundle.putString(BUNDLE_NAME, wlFragment.getName());
                    taskerBundle.putLong(BUNDLE_SECONDS, wlFragment.getSeconds());
                    taskerBundle.putBoolean(BUNDLE_ENABLED, wlFragment.getEnabled());
                    blurb = wlFragment.getName() + " - " + (wlFragment.getEnabled() ?
                            getResources().getString(R.string.tasker_on) :
                            getResources().getString(R.string.tasker_off)) +
                            " - " + wlFragment.getSeconds();
                } else if ((alarmFragment = (AlarmDetailFragment)fragmentManager.findFragmentByTag("alarm_detail")) != null) {
                    taskerBundle.putString(BUNDLE_TYPE, "alarm");
                    taskerBundle.putString(BUNDLE_NAME, alarmFragment.getName());
                    taskerBundle.putLong(BUNDLE_SECONDS, alarmFragment.getSeconds());
                    taskerBundle.putBoolean(BUNDLE_ENABLED, alarmFragment.getEnabled());
                    blurb = alarmFragment.getName() + " - " + (alarmFragment.getEnabled() ?
                            getResources().getString(R.string.tasker_on) :
                            getResources().getString(R.string.tasker_off)) +
                            " - " + alarmFragment.getSeconds();
                } else if ((regexFragment = (RegexDetailFragment)fragmentManager.findFragmentByTag("regex_detail_alarm"))!= null) {
                    taskerBundle.putString(BUNDLE_TYPE, "regex_alarm");
                    taskerBundle.putString(BUNDLE_NAME, regexFragment.getName());
                    taskerBundle.putLong(BUNDLE_SECONDS, regexFragment.getSeconds());
                    taskerBundle.putBoolean(BUNDLE_ENABLED, regexFragment.getEnabled());
                    blurb = "Alarm: " + regexFragment.getName() + " - " + (regexFragment.getEnabled() ?
                            getResources().getString(R.string.tasker_on) :
                            getResources().getString(R.string.tasker_off)) +
                            " - " + regexFragment.getSeconds();
                } else {
                    regexFragment = (RegexDetailFragment)fragmentManager.findFragmentByTag("regex_detail_wakelock");
                    taskerBundle.putString(BUNDLE_TYPE, "regex_wakelock");
                    taskerBundle.putString(BUNDLE_NAME, regexFragment.getName());
                    taskerBundle.putLong(BUNDLE_SECONDS, regexFragment.getSeconds());
                    taskerBundle.putBoolean(BUNDLE_ENABLED, regexFragment.getEnabled());
                    blurb = "Wakelock: " + regexFragment.getName() + " - " + (regexFragment.getEnabled() ?
                            getResources().getString(R.string.tasker_on) :
                            getResources().getString(R.string.tasker_off)) +
                            " - " + regexFragment.getSeconds();
                }

                final Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_BUNDLE, taskerBundle);
                resultIntent.putExtra(EXTRA_BLURB, blurb);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        //If we have a bundle passed to us, we need to pre-populate that state in the detail page
        if (savedInstanceState != null) {
            Bundle savedBundle = getIntent().getBundleExtra(EXTRA_BUNDLE);
            if (savedBundle != null) {
                String type = savedBundle.getString(BUNDLE_TYPE);
                long seconds = savedBundle.getLong(BUNDLE_SECONDS);
                boolean enabled = savedBundle.getBoolean(BUNDLE_ENABLED);
                String name = savedBundle.getString(BUNDLE_NAME);

                //TODO:  Pass to detail fragment, and launch detail immediately (don't show "which" or "list")
            }

        }

    }

    public boolean isPremium() {
        return mIsPremium;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentFragment = TaskerWhichFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.container, mCurrentFragment)
                .commit();
    }

    @Override
    public void onWakelocksSetTitle(String title) {
    }

    @Override
    public void onWakelocksSetTaskerTitle(String title) {
        TextView text = (TextView)findViewById(R.id.textTaskerHeader);
        text.setText(title);
        Button save = (Button)findViewById(R.id.buttonTaskerSave);
        save.setVisibility(View.GONE);
    }

    @Override
    public void onDetailSetTitle(String title) {
    }

    @Override
    public void onDetailSetTaskerTitle(String title) {
        TextView text = (TextView)findViewById(R.id.textTaskerHeader);
        text.setText(title);
        Button save = (Button)findViewById(R.id.buttonTaskerSave);
        save.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAlarmsSetTitle(String title) {
    }

    @Override
    public void onAlarmsSetTaskerTitle(String title) {
        TextView text = (TextView)findViewById(R.id.textTaskerHeader);
        text.setText(title);
        Button save = (Button)findViewById(R.id.buttonTaskerSave);
        save.setVisibility(View.GONE);
    }

    @Override
    public void onRegexSetTitle(String title) {
    }

    @Override
    public void onRegexSetTaskerTitle(String title) {
        TextView text = (TextView)findViewById(R.id.textTaskerHeader);
        text.setText(title);
        Button save = (Button)findViewById(R.id.buttonTaskerSave);
        save.setVisibility(View.GONE);
    }

    @Override
    public void onTaskerResetSelected() {
        Bundle taskerBundle = new Bundle();
        taskerBundle.putString(BUNDLE_TYPE, "reset");
        String blurb = getResources().getString(R.string.tasker_reset_stats);

        final Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_BUNDLE, taskerBundle);
        resultIntent.putExtra(EXTRA_BLURB, blurb);
        setResult(RESULT_OK, resultIntent);
        finish();

    }

    @Override
    public void onTaskerWhichSetTitle(String title) {
        TextView text = (TextView)findViewById(R.id.textTaskerHeader);
        text.setText(title);
        Button save = (Button)findViewById(R.id.buttonTaskerSave);
        save.setVisibility(View.GONE);
    }
}
