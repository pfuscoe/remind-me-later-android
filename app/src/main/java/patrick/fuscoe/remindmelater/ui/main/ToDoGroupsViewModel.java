package patrick.fuscoe.remindmelater.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import patrick.fuscoe.remindmelater.FirebaseQueryLiveData;
import patrick.fuscoe.remindmelater.MainActivity;

public class ToDoGroupsViewModel extends ViewModel {

    //private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    //private static final String userId = auth.getCurrentUser().getUid();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference toDoGroups = db.collection("todogroups");

    private final Query toDoGroupsQuery = toDoGroups.whereArrayContains("subscribers", MainActivity.userId);

    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(toDoGroupsQuery);

    @NonNull
    public LiveData<QuerySnapshot> getQuerySnapshotLiveData() {
        return liveData;
    }

}