package patrick.fuscoe.remindmelater.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import patrick.fuscoe.remindmelater.MainActivity;
import patrick.fuscoe.remindmelater.R;
import patrick.fuscoe.remindmelater.ReminderDetailsActivity;
import patrick.fuscoe.remindmelater.models.CategoryIconSet;

public class AddCategoryDialogFragment extends DialogFragment {

    public static final String TAG = "patrick.fuscoe.remindmelater.AddCategoryDialogFragment";

    private List<Integer> categoryIconList;
    private List<Boolean> categoryIconListIsChecked;
    private int selectedIcon;
    private int selectedIconPos;

    private RecyclerView categoryIconRecycler;
    private RecyclerView.LayoutManager categoryIconRecyclerLayoutManager;
    private RecyclerView.Adapter categoryIconRecyclerAdapter;

    public interface CategoryIconClickListener {
        void onIconClicked(View v, int position);
    }

    private CategoryIconClickListener categoryIconClickListener = new CategoryIconClickListener() {
        @Override
        public void onIconClicked(View v, int position) {
            int oldPos = selectedIconPos;
            selectedIconPos = position;

            if (oldPos != -1)
            {
                categoryIconListIsChecked.set(oldPos, false);
            }

            if (oldPos == position)
            {
                //categoryIconListIsChecked.set(position, false);
                selectedIcon = -1;
                selectedIconPos = -1;
            }
            else
            {
                categoryIconListIsChecked.set(position, true);
                selectedIcon = categoryIconList.get(position);

                // notify old pos changed
                categoryIconRecyclerAdapter.notifyItemChanged(oldPos);
            }

            // notify new pos changed
            categoryIconRecyclerAdapter.notifyItemChanged(position);
        }
    };

    AddCategoryDialogListener listener;

    public interface AddCategoryDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof ReminderDetailsActivity)
            {
                listener = (AddCategoryDialogListener) context;
            }
            else if (context instanceof MainActivity)
            {
                listener = (AddCategoryDialogListener) getTargetFragment();
            }
            else
            {
                Log.d(TAG, ": Unable to setup listener due to unrecognized context");
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Host must implement AddCategoryDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_category_edit, null);

        CategoryIconSet categoryIconSet = new CategoryIconSet();
        categoryIconList = categoryIconSet.getCategoryIconList();
        categoryIconListIsChecked = categoryIconSet.getCategoryIconListIsChecked();

        selectedIcon = -1;
        selectedIconPos = -1;

        //Bundle bundle = getArguments();

        EditText viewCategoryName = v.findViewById(R.id.dialog_category_edit_name);

        categoryIconRecycler = v.findViewById(R.id.dialog_category_edit_recycler);
        categoryIconRecycler.setHasFixedSize(true);

        categoryIconRecyclerLayoutManager = new GridLayoutManager(getContext(), 5);
        categoryIconRecycler.setLayoutManager(categoryIconRecyclerLayoutManager);

        categoryIconRecyclerAdapter = new AddCategoryIconSelectAdapter(categoryIconList,
                categoryIconListIsChecked, getContext(), categoryIconClickListener);
        categoryIconRecycler.setAdapter(categoryIconRecyclerAdapter);

        builder.setView(v)
                .setTitle(R.string.dialog_add_category_title)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(AddCategoryDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(AddCategoryDialogFragment.this);
                    }
                });
        return builder.create();
    }

    public int getSelectedIconId() {
        return selectedIcon;
    }
}