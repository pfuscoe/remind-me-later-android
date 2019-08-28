package patrick.fuscoe.remindmelater.models;

import java.time.LocalDate;
import java.util.Calendar;

public class ReminderAlarmItem {

    private String title;
    private String nextOccurrence;
    private int iconId;
    private int broadcastId;
    private Calendar alarmCalendarObj;


    public ReminderAlarmItem()
    {

    }

    public ReminderAlarmItem(String title, String nextOccurrence, int iconId, int broadcastId, int hour, int minute)
    {
        this.title = title;
        this.nextOccurrence = nextOccurrence;
        this.iconId = iconId;
        this.broadcastId = broadcastId;

        buildAlarmCalendarObject(hour, minute);
    }

    private void buildAlarmCalendarObject(int hour, int minute)
    {
        LocalDate alarmDate = LocalDate.parse(nextOccurrence);
        int year = alarmDate.getYear();
        int month = alarmDate.getMonthValue();
        int day = alarmDate.getDayOfMonth();

        alarmCalendarObj = Calendar.getInstance();
        alarmCalendarObj.setTimeInMillis(System.currentTimeMillis());
        alarmCalendarObj.set(year, month - 1, day);
        alarmCalendarObj.set(Calendar.HOUR_OF_DAY, hour);
        alarmCalendarObj.set(Calendar.MINUTE, minute);
    }

    public String getTitle() {
        return title;
    }

    public String getNextOccurrence() {
        return nextOccurrence;
    }

    public int getIconId() {
        return iconId;
    }

    public int getBroadcastId() {
        return broadcastId;
    }

    public Calendar getAlarmCalendarObj() {
        return alarmCalendarObj;
    }

}