package patrick.fuscoe.remindmelater.models;

import android.util.Log;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;

public class ReminderItem {

    private String title;
    private String nextOccurrence;  // LocalDate String format
    private String category;
    private int categoryIcon;
    private String description;

    //private Address address;
    private int recurrenceNum;
    private String recurrenceInterval;

    private Period recurrence;
    private String recurrenceString;
    private int daysAway;
    private boolean snoozed;
    private ArrayList<HistoryItem> historyItems;


    public ReminderItem() {

    }

    public ReminderItem(String title, int recurrenceNum, String recurrenceInterval, String nextOccurrence, String category, int categoryIcon, String description)
    {
        this.title = title;
        this.recurrenceNum = recurrenceNum;
        this.recurrenceInterval = recurrenceInterval;
        this.nextOccurrence = nextOccurrence;
        this.category = category;
        this.categoryIcon = categoryIcon;
        this.description = description;

        updateRecurrencePeriod();
        updateDaysAway(nextOccurrence);
        this.snoozed = false;
        this.historyItems = new ArrayList<>();
    }

    public void updateDaysAway(String nextOccurrence)
    {
        LocalDate now = LocalDate.now();
        LocalDate next = LocalDate.parse(nextOccurrence);
        Period diff = Period.between(now, next);

        daysAway = diff.getDays();
    }

    public void updateRecurrencePeriod()
    {
        switch (recurrenceInterval)
        {
            case "Days":
                recurrence = Period.ofDays(recurrenceNum);
                return;

            case "Weeks":
                recurrence = Period.ofWeeks(recurrenceNum);
                return;

            case "Months":
                recurrence = Period.ofMonths(recurrenceNum);
                return;

            case "Years":
                recurrence = Period.ofYears(recurrenceNum);
                return;
        }

        recurrenceString = recurrence.toString();
    }

    public void addToHistory(HistoryItem historyItem)
    {
        historyItems.add(historyItem);
    }

    /** Getters **/
    public String getTitle() {
        return title;
    }

    public int getRecurrenceNum() {
        return recurrenceNum;
    }

    public String getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public Period getRecurrence() {
        return recurrence;
    }

    public String getRecurrenceString() {
        return recurrenceString;
    }

    public String getNextOccurrence() {
        return nextOccurrence;
    }

    public String getCategory() {
        return category;
    }

    public int getCategoryIcon() {
        return categoryIcon;
    }

    public String getDescription() {
        return description;
    }

    public int getDaysAway() {
        return daysAway;
    }

    public boolean isSnoozed() {
        return snoozed;
    }

    public ArrayList<HistoryItem> getHistoryItems() {
        return historyItems;
    }


    /** Setters **/
    public void setTitle(String title) {
        this.title = title;
    }

    public void setRecurrenceNum(int recurrenceNum) {
        this.recurrenceNum = recurrenceNum;
    }

    public void setRecurrenceInterval(String recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

    public void setNextOccurrence(String nextOccurrence) {
        this.nextOccurrence = nextOccurrence;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCategoryIcon(int categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSnoozed(boolean snoozed) {
        this.snoozed = snoozed;
    }

}
