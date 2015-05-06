package com.bubbes.bubblesender.utils;

import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;

import com.bubbes.bubblesender.contacts.ContactActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;


public class DateUtilsTest extends ActivityInstrumentationTestCase2<ContactActivity> {
    //==============================================================================================
    // Constants
    //==============================================================================================

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat MY_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    //==============================================================================================
    // Constructor
    //==============================================================================================

    public DateUtilsTest() {
        super(ContactActivity.class);
    }

    //==============================================================================================
    // Tests
    //==============================================================================================

    public void testDateTheSameDayReturnTheHour() throws ParseException {
        String representation = createRepresentation("01/01/2010 12:00:00", "01/01/2010 09:38:00", Locale.US);
        assertThat(representation).isEqualTo("9:38 AM");
    }

    public void testDateTheSameDayReturnTheHourInFrench() throws ParseException {
        String representation = createRepresentation("01/01/2010 12:00:00", "01/01/2010 09:38:00", Locale.FRANCE);
        assertThat(representation).isEqualTo("09:38");
    }

    public void testDateIsYesterdayDisplayYesterday() throws ParseException {
        String representation = createRepresentation("01/01/2010 12:00:00", "31/12/2009 23:38:00", Locale.US);
        assertThat(representation).isEqualTo("Yesterday");
    }

    public void testDateIsYesterdayDisplayYesterdayInFrench() throws ParseException {
        String representation = createRepresentation("01/01/2010 12:00:00", "31/12/2009 23:38:00", Locale.US);
        //TODO after i18n assertThat(representation).isEqualTo("Hier");
    }

    public void testDateWhichIsMoreOldThanYesterdayDisplayDate() throws ParseException {
        String representation = createRepresentation("01/01/2010 12:00:00", "30/12/2009 23:38:00", Locale.US);
        assertThat(representation).isEqualTo("Dec 30, 2009");
    }

    public void testDateWhichIsMoreOldThanYesterdayDisplayDateInFrench() throws ParseException {
        String representation = createRepresentation("01/01/2010 12:00:00", "30/12/2009 23:38:00", Locale.FRANCE);
        assertThat(representation).isEqualTo("30 déc. 2009");
    }


    //==============================================================================================
    // Private
    //==============================================================================================

    private String createRepresentation(String now, String toDisplay, Locale locale) throws ParseException {
        return DateUtils.createDate(getTime(now), getTime(toDisplay), this.getActivity(), locale);
    }

    private long getTime(String formattedDate) throws ParseException {
        Date date = MY_FORMAT.parse(formattedDate);
        return date.getTime();
    }

}
