package patrick.fuscoe.remindmelater.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import patrick.fuscoe.remindmelater.MainActivity;
import patrick.fuscoe.remindmelater.R;
import patrick.fuscoe.remindmelater.ReminderCategoriesActivity;
import patrick.fuscoe.remindmelater.ReminderDetailsActivity;
import patrick.fuscoe.remindmelater.models.ReminderCategory;
import patrick.fuscoe.remindmelater.models.ReminderItem;
import patrick.fuscoe.remindmelater.models.UserProfile;
import patrick.fuscoe.remindmelater.ui.reminder.ReminderCategorySpinnerAdapter;

public class RemindersFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = "patrick.fuscoe.remindmelater.RemindersFragment";
    public static final String REMINDER_ITEM = "patrick.fuscoe.remindmelater.REMINDERS";
    public static final String REMINDERS_DOC_ID = "patrick.fuscoe.remindmelater.REMINDERS_DOC_ID";
    public static final String USER_PROFILE = "patrick.fuscoe.remindmelater.USER_PROFILE";
    public static final String REMINDER_CATEGORIES_USED = "patrick.fuscoe.remindmelater.REMINDER_CATEGORIES_USED";
    public static final String REMINDER_ITEMS = "patrick.fuscoe.remindmelater.REMINDER_ITEMS";

    //private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference remindersCollectionRef = db.collection("reminders");

    //private final String userId = auth.getUid();
    //private final DocumentReference userDocRef = db.collection("users").document(userId);
    private String remindersDocId;

    private Spinner viewCategorySpinner;
    private RecyclerView remindersRecyclerView;
    private RecyclerView.Adapter remindersAdapter;
    private RecyclerView.LayoutManager remindersLayoutManager;
    private FrameLayout viewRemindersTips;
    private WebView viewRemindersTipsWebView;

    private RemindersViewModel remindersViewModel;
    private UserProfileViewModel userProfileViewModel;

    private UserProfile userProfile;
    private List<ReminderItem> reminderItemList;

    private MenuItem tipsMenuItem;

    private String selectedCategoryName = "All";
    private boolean editMode;
    private boolean isTipsOn;


    private ReminderClickListener reminderClickListener = new ReminderClickListener() {
        @Override
        public void reminderClicked(View v, int position) {
            Log.d(TAG, ": Reminder " + position + " clicked");
            ReminderItem reminderItem = reminderItemList.get(position);

            // Open details activity for reminder item clicked
            if (!editMode)
            {
                Intent intent = new Intent(getContext(), ReminderDetailsActivity.class);
                Gson gson = new Gson();
                String reminderItemString = gson.toJson(reminderItem);
                String userProfileString = gson.toJson(userProfile);
                intent.putExtra(REMINDER_ITEM, reminderItemString);
                intent.putExtra(REMINDERS_DOC_ID, remindersDocId);
                intent.putExtra(USER_PROFILE, userProfileString);
                startActivity(intent);
            }
        }
    };

    public interface ReminderClickListener {
        void reminderClicked(View v, int position);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.view_reminders_spinner_filter)
        {
            ReminderCategory reminderCategory = (ReminderCategory) parent.getItemAtPosition(position);
            selectedCategoryName = reminderCategory.getCategoryName();
            //String categoryName = reminderCategory.getCategoryName();
            //int iconId = reminderCategory.getIconId();

            List<ReminderItem> filteredReminderItemList =
                    filterReminderListByCategory(selectedCategoryName);
            Collections.sort(filteredReminderItemList);

            updateRemindersDisplay(filteredReminderItemList);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static RemindersFragment newInstance(int index) {
        RemindersFragment fragment = new RemindersFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reminderItemList = new ArrayList<>();

        editMode = false;
        isTipsOn = false;

        /*
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        */
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reminders, container, false);

        setHasOptionsMenu(true);

        // Setup filter
        viewCategorySpinner = root.findViewById(R.id.view_reminders_spinner_filter);
        //updateCategorySelectSpinner();

        viewRemindersTips = root.findViewById(R.id.view_reminders_tips);
        viewRemindersTipsWebView = root.findViewById(R.id.view_reminders_tips_webview);
        viewRemindersTipsWebView.loadUrl("file:///android_asset/tips_reminders.html");

        // Setup recycler view
        remindersRecyclerView = root.findViewById(R.id.view_reminders_recycler);
        remindersRecyclerView.setHasFixedSize(true);

        remindersLayoutManager = new LinearLayoutManager(getContext());
        remindersRecyclerView.setLayoutManager(remindersLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(remindersRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        remindersRecyclerView.addItemDecoration(dividerItemDecoration);

        remindersAdapter = new RemindersAdapter(reminderItemList, getContext(), reminderClickListener);
        remindersRecyclerView.setAdapter(remindersAdapter);

        /*
        // Show Tips if Reminders List Empty
        if (reminderItemList.isEmpty() && !isTipsOn)
        {
            viewRemindersTips.setVisibility(View.VISIBLE);
            isTipsOn = true;
        }
        */

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        remindersViewModel = ViewModelProviders.of(this).get(RemindersViewModel.class);
        LiveData<QuerySnapshot> remindersLiveData = remindersViewModel.getQuerySnapshotLiveData();

        remindersLiveData.observe(getViewLifecycleOwner(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(@Nullable QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null)
                {
                    // Update UI views with data from snapshot
                    List<ReminderItem> reminderListFromDoc = new ArrayList<>();

                    //Log.d(TAG, ": queryDocumentSnapshots size: " + queryDocumentSnapshots.size());

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                    {
                        MainActivity.remindersDocRef = doc.getReference();
                        remindersDocId = doc.getId();
                        //Log.d(TAG, ": remindersDocId: " + remindersDocId);

                        Map<String, Object> docMap = doc.getData();

                        for (Map.Entry<String, Object> entry : docMap.entrySet())
                        {
                            if (!entry.getKey().contentEquals("userId"))
                            {
                                String title = entry.getKey();
                                HashMap<String, Object> reminderItemMap = (HashMap<String, Object>) entry.getValue();

                                boolean isRecurring = (boolean) reminderItemMap.get("isRecurring");
                                int recurrenceNum = Math.toIntExact((long) reminderItemMap.get("recurrenceNum"));
                                //Log.d(TAG, ": recurrenceNum: " + recurrenceNum);
                                String recurrenceInterval = (String) reminderItemMap.get("recurrenceInterval");

                                String nextOccurrence = (String) reminderItemMap.get("nextOccurrence");

                                String category = (String) reminderItemMap.get("category");
                                String categoryIconName = (String) reminderItemMap.get("categoryIconName");
                                //Log.d(TAG, ": categoryIconName: " + categoryIconName);

                                String description = (String) reminderItemMap.get("description");

                                boolean isSnoozed = (boolean) reminderItemMap.get("isSnoozed");
                                boolean isHibernating = (boolean) reminderItemMap.get("isHibernating");
                                Map<String, String> history = (Map<String, String>) reminderItemMap.get("history");

                                ReminderItem reminderItem = new ReminderItem(title, isRecurring,
                                        recurrenceNum, recurrenceInterval, nextOccurrence, category,
                                        categoryIconName, description, isSnoozed, isHibernating, history);

                                reminderListFromDoc.add(reminderItem);
                            }
                        }
                    }

                    reminderItemList = reminderListFromDoc;
                    //Log.d(TAG, ": reminderItemList size: " + reminderItemList.size());

                    if (tipsMenuItem != null)
                    {
                        if (reminderItemList.isEmpty() && !isTipsOn)
                        {
                            toggleTips();
                        }

                        if (!reminderItemList.isEmpty() && isTipsOn)
                        {
                            toggleTips();
                        }
                    }
                    else
                    {
                        if (reminderItemList.isEmpty() && !isTipsOn)
                        {
                            toggleTipsNoMenu();
                        }

                        if (!reminderItemList.isEmpty() && isTipsOn)
                        {
                            toggleTipsNoMenu();
                        }
                    }

                    Collections.sort(reminderItemList);
                    updateRemindersDisplay(reminderItemList);
                }
            }
        });

        userProfileViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        LiveData<DocumentSnapshot> userProfileLiveData = userProfileViewModel.getDocumentSnapshotLiveData();

        userProfileLiveData.observe(getViewLifecycleOwner(), new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null)
                {
                    Map<String, Object> docMap = documentSnapshot.getData();

                    String id = documentSnapshot.getId();
                    String displayName = documentSnapshot.getString("displayName");

                    ArrayList<String> subscriptionsList = (ArrayList<String>) docMap.get("subscriptions");

                    //Log.d(TAG, "subscriptionsList: " + subscriptionsList);

                    String[] subscriptions = new String[subscriptionsList.size()];
                    subscriptions = subscriptionsList.toArray(subscriptions);

                    /*
                    for (int i = 0; i < subscriptions.length; i++) {
                        Log.d("subscriptions item: ", subscriptions[i]);
                    }
                    */

                    Map<String, String> reminderCategories =
                            (Map<String, String>) documentSnapshot.get("reminderCategories");

                    MainActivity.reminderTimeHour = Math.toIntExact((long) docMap.get("reminderHour"));
                    MainActivity.reminderTimeMinute = Math.toIntExact((long) docMap.get("reminderMinute"));

                    int hibernateLength = Math.toIntExact((long) docMap.get("hibernateLength"));

                    ArrayList<String> friendsList = (ArrayList<String>) docMap.get("friends");
                    String[] friends;
                    friends = friendsList.toArray(new String[0]);

                    userProfile = new UserProfile(id, displayName, subscriptions, reminderCategories,
                            MainActivity.reminderTimeHour, MainActivity.reminderTimeMinute,
                            hibernateLength, friends);

                    Log.d(TAG, "UserProfile loaded");

                    ((MainActivity) getActivity()).setActionBarTitle("Hello, " + userProfile.getDisplayName() + "!");

                    updateCategorySelectSpinner();
                }
            }
        });
    }

    public void updateRemindersDisplay(List<ReminderItem> reminderItems)
    {
        remindersAdapter = new RemindersAdapter(reminderItems, getContext(), reminderClickListener);
        remindersRecyclerView.setAdapter(remindersAdapter);

        remindersAdapter.notifyDataSetChanged();
    }

    public void updateCategorySelectSpinner()
    {
        Log.d(TAG, "updateCategorySelectSpinner called");

        ReminderCategorySpinnerAdapter reminderCategorySpinnerAdapter = new ReminderCategorySpinnerAdapter(
                getContext(), userProfile.getReminderCategories());
        viewCategorySpinner.setAdapter(reminderCategorySpinnerAdapter);
        //setCategorySpinnerSelection(reminderCategorySpinnerAdapter);
        viewCategorySpinner.setOnItemSelectedListener(this);
    }

    private List<ReminderItem> filterReminderListByCategory(String categoryName)
    {
        if (categoryName.equals("All"))
        {
            return reminderItemList;
        }

        List<ReminderItem> filteredReminderItemList = new ArrayList<>();

        for (ReminderItem reminderItem : reminderItemList)
        {
            if (reminderItem.getCategory().equals(categoryName))
            {
                filteredReminderItemList.add(reminderItem);
            }
        }

        return filteredReminderItemList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.removeItem(R.id.menu_main_edit);
        menu.removeItem(R.id.menu_main_reorder);

        tipsMenuItem = menu.findItem(R.id.menu_main_tips);

        if (isTipsOn)
        {
            tipsMenuItem.setTitle(R.string.hide_tips);
        }
        else
        {
            tipsMenuItem.setTitle(R.string.show_tips);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_main_add:
                Log.d(TAG, ": Add Button pressed");
                addReminder();
                return true;

            case R.id.menu_main_user_settings:
                Log.d(TAG, ": Menu item selected: " + item.getItemId());
                return true;

            case R.id.menu_main_edit_reminder_categories:
                openEditReminderCategories();
                return true;

            case R.id.menu_main_tips:
                toggleTips();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addReminder()
    {
        String title = "";
        int recurrenceNum = 7;
        String recurrenceInterval = "Days";

        LocalDate nextOccurrenceLocalDate = LocalDate.now().plusDays(recurrenceNum);
        String nextOccurrence = nextOccurrenceLocalDate.toString();

        String category = "Main";
        //int categoryIcon = R.drawable.category_note;
        String categoryIconName = MainActivity.DEFAULT_REMINDER_CATEGORY_ICON_NAME;
        String description = "";
        boolean isSnoozed = false;
        boolean isRecurring = true;
        boolean isHibernating = false;
        Map<String, String> history = new HashMap<>();

        ReminderItem reminderItem = new ReminderItem(title, isRecurring, recurrenceNum,
                recurrenceInterval, nextOccurrence, category, categoryIconName, description,
                isSnoozed, isHibernating, history);

        //reminderItemList.add(reminderItem);

        Intent intent = new Intent(getContext(), ReminderDetailsActivity.class);
        Gson gson = new Gson();
        String reminderItemString = gson.toJson(reminderItem);
        String userProfileString = gson.toJson(userProfile);
        intent.putExtra(REMINDER_ITEM, reminderItemString);
        intent.putExtra(REMINDERS_DOC_ID, remindersDocId);
        intent.putExtra(USER_PROFILE, userProfileString);
        startActivity(intent);
    }

    private void openEditReminderCategories()
    {
        Intent intent = new Intent(getContext(), ReminderCategoriesActivity.class);
        Gson gson = new Gson();
        String userProfileString = gson.toJson(userProfile);
        String reminderItemListString = gson.toJson(reminderItemList);
        intent.putExtra(MainActivity.USER_PROFILE, userProfileString);
        intent.putExtra(REMINDER_ITEMS, reminderItemListString);
        startActivity(intent);
    }

    private void toggleTips()
    {
        if (isTipsOn)
        {
            viewRemindersTips.setVisibility(View.INVISIBLE);
            remindersRecyclerView.setVisibility(View.VISIBLE);
            tipsMenuItem.setTitle(R.string.show_tips);
            isTipsOn = false;
        }
        else
        {
            viewRemindersTips.setVisibility(View.VISIBLE);
            remindersRecyclerView.setVisibility(View.INVISIBLE);
            tipsMenuItem.setTitle(R.string.hide_tips);
            isTipsOn = true;
        }
    }

    private void toggleTipsNoMenu()
    {
        if (isTipsOn)
        {
            viewRemindersTips.setVisibility(View.INVISIBLE);
            remindersRecyclerView.setVisibility(View.VISIBLE);
            isTipsOn = false;
        }
        else
        {
            viewRemindersTips.setVisibility(View.VISIBLE);
            remindersRecyclerView.setVisibility(View.INVISIBLE);
            isTipsOn = true;
        }
    }
}