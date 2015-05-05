package com.bubbes.bubblesender.contacts;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.bubbes.bubblesender.TimedPhoneEntry;
import com.bubbes.bubblesender.history.HistoryManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class HistoryTest extends ActivityInstrumentationTestCase2<ContactActivity> {
    //==============================================================================================
    // Attributes
    //==============================================================================================

    private HistoryManager historyManager;
    private TimedPhoneEntry phoneEntry1;
    private TimedPhoneEntry phoneEntry2;
    private TimedPhoneEntry phoneEntry3;
    private TimedPhoneEntry phoneEntry4;
    private TimedPhoneEntry phoneEntry5;
    private TimedPhoneEntry phoneEntry6;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public HistoryTest() {
        super(ContactActivity.class);
    }

    //==============================================================================================
    // Unit testing methods
    //==============================================================================================
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.historyManager = new FakeHistoryManager(getActivity());
        this.phoneEntry1 = new TimedPhoneEntry("name", "phone", "type", "uri1", null,12);
        this.phoneEntry2 = new TimedPhoneEntry("name2", "phone2", "type2", null, null,13);
        this.phoneEntry3 = new TimedPhoneEntry("name3", "phone2", "type2", null, null,15);
        this.phoneEntry4 = new TimedPhoneEntry("name4", "phone2", "type2", null, null,16);
        this.phoneEntry5 = new TimedPhoneEntry("name5", "phone2", "type2", null, null,17);
        this.phoneEntry6 = new TimedPhoneEntry("name6", "phone2", "type2", null, null,18);
    }

    @Override
    public void tearDown() throws Exception {
        this.historyManager.clear();
        super.tearDown();
    }

    //==============================================================================================
    // Tests
    //==============================================================================================

    public void testHistoryWithNoSaveReturnAnEmptyArray() {
        assertThat(this.historyManager.getRecentContacts()).isEmpty();
    }

    public void testAddAndGetOneContact() {
        addPhoneEntries(this.phoneEntry1);
        assertThat(this.historyManager.getRecentContacts()).containsOnly(this.phoneEntry1);
    }

    public void testaddTwoPhoneEntries() {
        addPhoneEntries(this.phoneEntry1, this.phoneEntry2);
        List<TimedPhoneEntry> recentContacts = this.historyManager.getRecentContacts();
        assertThat(recentContacts).containsExactly(this.phoneEntry2, this.phoneEntry1);
    }

    public void testAddTwoEntriesAndReReadItFromDisk() throws InterruptedException {
        addPhoneEntries(this.phoneEntry1, this.phoneEntry2);
        List<TimedPhoneEntry> recentContacts = new FakeHistoryManager(getActivity()).getRecentContacts();
        assertThat(recentContacts).containsExactly(this.phoneEntry2, this.phoneEntry1);
    }

    public void testNoMoreThan5ContactInTheCachedList() {
        addPhoneEntries(phoneEntry1, phoneEntry2, phoneEntry3, phoneEntry4, phoneEntry5, phoneEntry6);
        assertThat(this.historyManager.getRecentContacts())
                .containsExactly(this.phoneEntry6, this.phoneEntry5, this.phoneEntry4, this.phoneEntry3, this.phoneEntry2);
    }

    public void testNoMoreThan5ContactOnDisk() {
        addPhoneEntries(phoneEntry1, phoneEntry2, phoneEntry3, phoneEntry4, phoneEntry5, phoneEntry6);
        assertThat(new FakeHistoryManager(getActivity()).getRecentContacts())
                .containsExactly(this.phoneEntry6, this.phoneEntry5, this.phoneEntry4, this.phoneEntry3, this.phoneEntry2);
    }

    public void testPushUpAnExistingPhoneEntry() {
        addPhoneEntries(phoneEntry1, phoneEntry2, phoneEntry3, phoneEntry4,phoneEntry3);
        assertThat(new FakeHistoryManager(getActivity()).getRecentContacts())
                .containsExactly(this.phoneEntry3,this.phoneEntry4,this.phoneEntry2,this.phoneEntry1);
    }


    //==============================================================================================
    // Private methods / inner classes
    //==============================================================================================
    private void addPhoneEntries(TimedPhoneEntry... phoneEntry) {
        for (TimedPhoneEntry entry : phoneEntry) {
            this.historyManager.notifySender(entry);
        }
    }

    private class FakeHistoryManager extends HistoryManager {
        private FakeHistoryManager(Context context) {
            super(context);
        }
    }

}
