package patrick.fuscoe.remindmelater.models;

import java.util.ArrayList;

import patrick.fuscoe.remindmelater.R;

public class CategoryIconSet {

    private ArrayList<Integer> categoryIconList;
    private ArrayList<Boolean> categoryIconListIsChecked;


    public CategoryIconSet()
    {
        categoryIconList = new ArrayList<>();

        populateList();
        buildCheckboxList();
    }

    private void populateList()
    {
        categoryIconList.add(R.drawable.category_account_card_details);
        categoryIconList.add(R.drawable.category_account_tie);
        categoryIconList.add(R.drawable.category_airplane);
        categoryIconList.add(R.drawable.category_ambulance);
        categoryIconList.add(R.drawable.category_archive);
        categoryIconList.add(R.drawable.category_atom);
        categoryIconList.add(R.drawable.category_axe);
        categoryIconList.add(R.drawable.category_baby_buggy);
        categoryIconList.add(R.drawable.category_balloon);
        categoryIconList.add(R.drawable.category_ballot);
        categoryIconList.add(R.drawable.category_bank);
        categoryIconList.add(R.drawable.category_basketball);
        categoryIconList.add(R.drawable.category_bed_empty);
        categoryIconList.add(R.drawable.category_bike);
        categoryIconList.add(R.drawable.category_briefcase);
        categoryIconList.add(R.drawable.category_cake_variant);
        categoryIconList.add(R.drawable.category_calculator_variant);

        // TODO: finish adding icons

    }

    private void buildCheckboxList()
    {
        categoryIconListIsChecked = new ArrayList<>();

        for (int i = 0; i < categoryIconList.size(); i++)
        {
            categoryIconListIsChecked.add(false);
        }
    }

    public ArrayList<Integer> getCategoryIconList() {
        return categoryIconList;
    }

    public ArrayList<Boolean> getCategoryIconListIsChecked() {
        return categoryIconListIsChecked;
    }
}
