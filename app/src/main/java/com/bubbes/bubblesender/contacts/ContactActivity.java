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

import com.bubbes.bubblesender.R;
import com.bubbes.bubblesender.SendBubblesActivity;
import com.bubbes.bubblesender.PhoneEntry;
import com.bubbes.bubblesender.utils.Assertion;

import java.util.ArrayList;


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
        ContactAdapter adapter = this.initializeContactAdapter();
        this.initializeAutoCompletePhoneNumberView(adapter);
    }

    //==============================================================================================
    // Components initialization
    //==============================================================================================
    private void initializeAutoCompletePhoneNumberView(ContactAdapter mAdapter) {
        this.autoCompletePhoneNumberView = (AutoCompleteTextView) findViewById(R.id.auto_complete_text_view);
        this.autoCompletePhoneNumberView.setAdapter(mAdapter);
        this.autoCompletePhoneNumberView.setOnItemClickListener(this);
        this.autoCompletePhoneNumberView.requestFocus();
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

    private ContactAdapter initializeContactAdapter() {
        Assertion.assertIsMainThread();
        ArrayList<PhoneEntry> contactList = new ArrayList<>();
        ContactAdapter adapter = new ContactAdapter(this,R.layout.sample_custom_contact_view,contactList);
        initializeBackGroundContactLoading(adapter);
        return adapter;
    }

    private void initializeBackGroundContactLoading(final ContactAdapter adapter) {
        Assertion.assertIsMainThread();
        AsyncTask<Object, Void, Void> asyncTask = new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object[] params) {
                initializeAdapterContent(adapter);
                return null;
            }
        };
        //Let's load the contact list asynchronously
        asyncTask.execute();
    }

    private void initializeAdapterContent(final ContactAdapter adapter) {
        Assertion.assertIsNotMainThread();
        String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,ContactsContract.Contacts.PHOTO_URI};
        try (Cursor contactCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null)) {
            while (contactCursor.moveToNext()) {
                final ArrayList<PhoneEntry> phoneEntries = createPhoneEntriesForContact(contactCursor);
                if (!phoneEntries.isEmpty()) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addAll(phoneEntries);
                        }
                    });
                }
            }
        }
    }

    private ArrayList<PhoneEntry> createPhoneEntriesForContact(Cursor contactCursor) {
        Assertion.assertIsNotMainThread();
        ArrayList<PhoneEntry> phoneEntries = new ArrayList<>();
        String contactName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        int contactId = contactCursor.getInt(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
        String imageThumbnailURI = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
        String imageUri = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
        try (Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)}, null)) {
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int numberType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                CharSequence phoneType = this.getApplicationContext().getResources().getText(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(numberType));
                PhoneEntry object = new PhoneEntry(contactName, phoneNumber, phoneType.toString(), imageThumbnailURI, imageUri);
                phoneEntries.add(object);
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
        Intent intent = new Intent(this, SendBubblesActivity.class);
        intent.putExtra(SendBubblesActivity.EXTRA_PHONE_ENTRY, this.selectedPhoneEntry);
        this.startActivity(intent);
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
        if(aContactSelected){
            this.autoCompletePhoneNumberView.setText(this.selectedPhoneEntry.toDisplay());
        }
        this.changeButtonStatus(aContactSelected);
    }
}
