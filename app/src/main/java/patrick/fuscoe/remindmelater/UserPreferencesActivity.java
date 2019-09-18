package patrick.fuscoe.remindmelater;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import patrick.fuscoe.remindmelater.models.UserProfile;
import patrick.fuscoe.remindmelater.ui.dialog.TimePickerDialogFragment;
import patrick.fuscoe.remindmelater.ui.main.RemindersFragment;

public class UserPreferencesActivity extends AppCompatActivity
        implements TimePickerDialogFragment.OnTimeSetListener {

    public static final String TAG = "patrick.fuscoe.remindmelater.UserPreferencesActivity";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirebaseAuth auth;
    public static String userId;
    public static DocumentReference userDocRef;

    private UserProfile userProfile;

    private TextView viewTimeDisplay;
    private Button btnSetTime;
    private Button btnClearEmptyReminderCategories;
    private Button btnSave;
    private Button btnCancel;

    private int reminderHour;
    private int reminderMinute;


    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: Add button click behavior
            switch (v.getId())
            {
                case R.id.button_user_preferences_time:
                    openTimePicker();
                    return;

                case R.id.button_user_preferences_clear_empty_reminder_categories:
                    clearEmptyReminderCategories();
                    return;

                case R.id.button_user_preferences_save:
                    saveUserPrefs();
                    Toast.makeText(getApplicationContext(), userProfile.getDisplayName() + ": User Settings Updated", Toast.LENGTH_LONG).show();
                    onBackPressed();
                    return;

                case R.id.button_user_preferences_cancel:
                    Toast.makeText(getApplicationContext(), "Edit User Settings Cancelled", Toast.LENGTH_LONG).show();
                    onBackPressed();
                    return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();
        userDocRef = db.collection("users").document(userId);

        Intent intent = getIntent();
        Gson gson = new Gson();

        Type dataTypeUserProfile = new TypeToken<UserProfile>(){}.getType();
        String userProfileString = intent.getStringExtra(MainActivity.USER_PROFILE);
        Log.d(TAG, "userProfileString in intent: " + userProfileString);
        userProfile = gson.fromJson(userProfileString, dataTypeUserProfile);
        Log.d(TAG, "User Profile obtained from intent");

        viewTimeDisplay = findViewById(R.id.view_user_preferences_time_display);
        btnSetTime = findViewById(R.id.button_user_preferences_time);
        btnSetTime.setOnClickListener(btnClickListener);
        btnClearEmptyReminderCategories = findViewById(R.id.button_user_preferences_clear_empty_reminder_categories);
        btnClearEmptyReminderCategories.setOnClickListener(btnClickListener);
        btnSave = findViewById(R.id.button_user_preferences_save);
        btnSave.setOnClickListener(btnClickListener);
        btnCancel = findViewById(R.id.button_user_preferences_cancel);
        btnCancel.setOnClickListener(btnClickListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(userProfile.getDisplayName() + ":  Settings");
    }

    private void openTimePicker()
    {
        DialogFragment dialogFragment = new TimePickerDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Log.d(TAG, "onTimeSet: hourOfDay = " + hourOfDay + ". minute = " + minute);

        reminderHour = hourOfDay;
        reminderMinute = minute;
    }

    public void clearEmptyReminderCategories()
    {
        // TODO: Clear Empty Reminder Categories
        //loadRemindersFromCloud();
    }

    public void saveUserPrefs()
    {
        // TODO: Save User Prefs
    }

}
