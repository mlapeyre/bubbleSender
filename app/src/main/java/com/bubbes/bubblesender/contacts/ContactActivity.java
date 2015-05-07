package com.bubbes.bubblesender.contacts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.bubbes.bubblesender.PhoneEntry;
import com.bubbes.bubblesender.R;
import com.bubbes.bubblesender.SendBubblesActivity;
import com.bubbes.bubblesender.TimedPhoneEntry;
import com.bubbes.bubblesender.history.HistoryManager;
import com.bubbes.bubblesender.utils.Assertion;

import java.util.ArrayList;
import java.util.List;


public class ContactActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    //==============================================================================================
    // Constants used for state save/restore
    //==============================================================================================
    private static final String STATE_SELECTED_PHONE_ENTRY = "selectedPhoneEntry";

    //==============================================================================================
    // Attributes
    //==============================================================================================
    /**
     * The Text view where the contact name is typed
     */
    private AutoCompleteTextView autoCompletePhoneNumberView;
    /**
     * The phone number of the selected contact may be null if no contact is selected
     */
    private PhoneEntry selectedPhoneEntry;

    //==============================================================================================
    // Public
    //==============================================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Assertion.initializeStrictMode();
        this.setContentView(R.layout.activity_contact);
        //Load asynchronously the autocompletion list
        PhoneEntryAdapter adapter = this.initializeContactAdapter();
        this.initializeAutoCompletePhoneNumberView(adapter);
        this.initializeLastVictimList();
    }

    private void initializeLastVictimList() {
        ListView listView = (ListView) this.findViewById(R.id.victim_list);
        final TimedPhoneEntryAdapter phoneEntryAdapter = new TimedPhoneEntryAdapter(this, R.layout.timed_phone_entry_view, new ArrayList<TimedPhoneEntry>());
        listView.setAdapter(phoneEntryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                PhoneEntry phoneEntry = (PhoneEntry) adapterView.getItemAtPosition(position);
                goToSendBubbleActivity(phoneEntry);
            }
        });
        AsyncTask<Object, Void, List<TimedPhoneEntry>> asyncTask = new AsyncTask<Object, Void, List<TimedPhoneEntry>>() {
            @Override
            protected void onPostExecute(List<TimedPhoneEntry> phoneEntries) {
                super.onPostExecute(phoneEntries);
                findViewById(R.id.loading_recents_progressbar).setVisibility(View.GONE);
                if (phoneEntries.isEmpty()) {
                    findViewById(R.id.no_recent_contact_found).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.no_recent_contact_found).setVisibility(View.GONE);
                    phoneEntryAdapter.addAll(phoneEntries);
                }
            }

            @Override
            protected List<TimedPhoneEntry> doInBackground(Object[] params) {
                final HistoryManager instance = HistoryManager.getInstance(ContactActivity.this);
                return instance.getRecentContacts();
            }
        };
        findViewById(R.id.loading_recents_progressbar).setVisibility(View.VISIBLE);

        //Let's load the contact list asynchronously
        asyncTask.execute();
    }


    //==============================================================================================
    // Components initialization
    //==============================================================================================
    private void initializeAutoCompletePhoneNumberView(PhoneEntryAdapter mAdapter) {
        this.autoCompletePhoneNumberView = (AutoCompleteTextView) findViewById(R.id.auto_complete_text_view);
        this.autoCompletePhoneNumberView.setAdapter(mAdapter);
        this.autoCompletePhoneNumberView.setOnItemClickListener(this);
        this.autoCompletePhoneNumberView.setThreshold(1);
        //Listener used for resetting the entire text view if a character is removed
        this.autoCompletePhoneNumberView.addTextChangedListener(new TextWatcher() {
            private boolean deleteAll = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after < count && isAContactSelected()) {
                    this.deleteAll = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (this.deleteAll) {
                    this.deleteAll = false;
                    resetSelectedContact();
                }
            }
        });
    }

    private PhoneEntryAdapter initializeContactAdapter() {
        Assertion.assertIsMainThread();
        ArrayList<PhoneEntry> contactList = new ArrayList<>();
        PhoneEntryAdapter adapter = new PhoneEntryAdapter(this, R.layout.phone_entry_view, contactList);
        initializeBackGroundContactLoading(adapter);
        return adapter;
    }

    private void initializeBackGroundContactLoading(final PhoneEntryAdapter adapter) {
        Assertion.assertIsMainThread();
        AsyncTask<Object, Void, List<PhoneEntry>> asyncTask = new AsyncTask<Object, Void, List<PhoneEntry>>() {
            @Override
            protected List<PhoneEntry> doInBackground(Object[] params) {
                return retrievePhoneEntryList();
            }

            @Override
            protected void onPostExecute(List<PhoneEntry> phoneEntries) {
                super.onPostExecute(phoneEntries);
                adapter.addAll(phoneEntries);
            }
        };
        //Let's load the contact list asynchronously
        asyncTask.execute();
    }


    private List<PhoneEntry> retrievePhoneEntryList() {
        Assertion.assertIsNotMainThread();
        ArrayList<PhoneEntry> phoneEntries = new ArrayList<>();
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };
        try (Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null)) {
            //Retrieve column index once
            int displayNameIndex = phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int thumbnailIndex = phones.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
            int photoIndex = phones.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
            int phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int typeIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            //lets iterate now !!!
            while (phones.moveToNext()) {
                CharSequence phoneType = this.getApplicationContext().getResources().
                        getText(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(phones.getInt(typeIndex)));
                phoneEntries.add(new PhoneEntry(
                        phones.getString(displayNameIndex),
                        phones.getString(phoneIndex),
                        phoneType.toString(),
                        phones.getString(thumbnailIndex),
                        phones.getString(photoIndex)));
            }
        }
        return phoneEntries;
    }

    //==============================================================================================
    // View listeners/Management
    //==============================================================================================
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg) {
        PhoneEntry phoneEntry = (PhoneEntry) adapterView.getItemAtPosition(index);
        this.setSelectedContact(phoneEntry);
        findViewById(R.id.activity_contact_layout).requestFocus();
    }

    public void onButtonClick(View view) {
        assert this.selectedPhoneEntry != null;
        this.goToSendBubbleActivity(this.selectedPhoneEntry);
    }

    boolean isAContactSelected() {
        return this.selectedPhoneEntry != null;
    }

    void setSelectedContact(PhoneEntry entry) {
        this.autoCompletePhoneNumberView.setText(entry.toDisplay());
        this.selectedPhoneEntry = entry;
        this.changeButtonStatus(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autoCompletePhoneNumberView.getWindowToken(), 0);
    }

    private void resetSelectedContact() {
        this.selectedPhoneEntry = null;
        this.autoCompletePhoneNumberView.clearListSelection();
        this.autoCompletePhoneNumberView.setText("");
        this.changeButtonStatus(false);
    }

    private void changeButtonStatus(boolean enabled) {
        View viewById = this.findViewById(R.id.bt_bubble_them);
        viewById.setEnabled(enabled);
    }

    private void goToSendBubbleActivity(PhoneEntry phoneEntry) {
        assert phoneEntry != null;
        Intent intent = new Intent(this, SendBubblesActivity.class);
        intent.putExtra(SendBubblesActivity.EXTRA_PHONE_ENTRY, phoneEntry);
        this.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeLastVictimList();
    }

    //==============================================================================================
    // Save/Restore states
    //==============================================================================================
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_SELECTED_PHONE_ENTRY, this.selectedPhoneEntry);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.selectedPhoneEntry = (PhoneEntry) savedInstanceState.getSerializable(STATE_SELECTED_PHONE_ENTRY);
        boolean aContactSelected = isAContactSelected();
        if (aContactSelected) {
            this.autoCompletePhoneNumberView.setText(this.selectedPhoneEntry.toDisplay());
        }
        this.changeButtonStatus(aContactSelected);
    }
}
