package com.bubbes.bubblesender;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.bubbes.bubblesender.contacts.PhoneEntry;

import java.util.ArrayList;


public class ContactActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private AutoCompleteTextView autoCompletePhoneNumberView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ArrayList<PhoneEntry> contactList = createContactList();
        this.autoCompletePhoneNumberView = (AutoCompleteTextView) findViewById(R.id.auto_complete_text_view);
        SimpleAdapter mAdapter = new SimpleAdapter(this, contactList, R.layout.sample_custom_contact_view,
                new String[]{PhoneEntry.CONTACT_NAME, PhoneEntry.CONTACT_PHONE, PhoneEntry.CONTACT_PHONE_TYPE}, new int[]{
                R.id.ccontName, R.id.ccontNo, R.id.ccontType});
        this.autoCompletePhoneNumberView.setAdapter(mAdapter);
        this.autoCompletePhoneNumberView.setOnItemClickListener(this);
        this.autoCompletePhoneNumberView.requestFocus();
    }

    public ArrayList<PhoneEntry> createContactList() {
        ArrayList<PhoneEntry> contactList = new ArrayList<>();
        try (Cursor contactCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)) {
            while (contactCursor.moveToNext()) {
                contactList.addAll(createPhoneEntriesForContact(contactCursor));
            }
        }
        return contactList;
    }

    ArrayList<PhoneEntry> createPhoneEntriesForContact(Cursor contactCursor) {
        ArrayList<PhoneEntry> phoneEntries = new ArrayList<>();
        String contactName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        int contactId = contactCursor.getInt(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
        try (Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)) {
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int numberType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                CharSequence phoneType = this.getApplicationContext().getResources().getText(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(numberType));
                phoneEntries.add(new PhoneEntry(contactName, phoneNumber, phoneType.toString()));
            }
        }
        return phoneEntries;
    }


    public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg) {
        PhoneEntry phoneEntry = (PhoneEntry) adapterView.getItemAtPosition(index);
        autoCompletePhoneNumberView.setText(phoneEntry.toDisplay());
    }
}
