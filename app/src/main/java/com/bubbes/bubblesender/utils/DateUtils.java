package com.bubbes.bubblesender.utils;

import android.content.Context;

import com.bubbes.bubblesender.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {

    public static String createDate(long now,long dateToDisplay,Context context,Locale locale){
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(now);
        Calendar toDisplayCalendar = Calendar.getInstance();
        toDisplayCalendar.setTimeInMillis(dateToDisplay);
        if(areTheSameDay(nowCalendar, toDisplayCalendar)){
          return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(dateToDisplay);
        }else if(isYesterday(nowCalendar, toDisplayCalendar)){
            return context.getResources().getText(R.string.yesterday).toString();
        }else{
            return DateFormat.getDateInstance(DateFormat.MEDIUM,locale).format(dateToDisplay);
        }
    }

    private static boolean isYesterday(Calendar now, Calendar dateToTest){
        Calendar yesterday = (Calendar) now.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        return dateToTest.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR)
                && dateToTest.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR);
    }
    private static boolean areTheSameDay(Calendar nowCalendar, Calendar toDisplayCalendar) {
        return nowCalendar.get(Calendar.DAY_OF_YEAR)==toDisplayCalendar.get(Calendar.DAY_OF_YEAR)&&
                nowCalendar.get(Calendar.YEAR)==toDisplayCalendar.get(Calendar.YEAR);
    }
}
