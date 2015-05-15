package com.bubbes.bubblesender.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.bubbes.bubblesender.TimedPhoneEntry;
import com.bubbes.bubblesender.utils.Assertion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * History manager used for store the phone entries for the last NB_CONTACTS
 * contacts which have been targeted by the app
 * Probably over synchronized, but there is no performance problems here no need for more optimi
 */
public class HistoryManager {
    //==============================================================================================
    // Constants
    //==============================================================================================
    /**
     * Lock for singleton creation
     */
    private static final Object MUTEX = new Object();
    /**
     * Key used for getting the right SharedPreferences object
     */
    private static final String SHARED_PREFERENCE_KEY = "shared_preference_key";
    /**
     * Key used to get the serialized recent contact list
     */
    private static final String HISTORY_KEY = "history_key";


    //==============================================================================================
    // Static instances
    //==============================================================================================
    private static HistoryManager historyManager;

    //==============================================================================================
    // Attributes
    //==============================================================================================
    private SharedPreferences sharedPreferences;
    private final LinkedList<TimedPhoneEntry> recentContacts;

    //==============================================================================================
    // Constructor
    //==============================================================================================
    protected HistoryManager(Context context) {
        Assertion.assertIsNotMainThread();
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
        //Initialize the contact list
        String serializedContacts = this.sharedPreferences.getString(HISTORY_KEY, "");
        //No need for synchronisation here we are in the MUTEX block
        this.recentContacts = this.deserializeContacts(serializedContacts);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Basic singleton pattern
     *
     * @param context The context to use for retrieving shared preferences
     * @return A HistoryManager instance
     */
    public static HistoryManager getInstance(Context context) {
        Assertion.assertIsNotMainThread();
        synchronized (MUTEX){
            if (historyManager == null) {
                historyManager = new HistoryManager(context);
            }
        }
        return historyManager;

    }


    public boolean notifySender(TimedPhoneEntry phoneEntry) {
        Assertion.assertIsNotMainThread();
        synchronized (recentContacts) {
                if(recentContacts.contains(phoneEntry)){
                    recentContacts.remove(phoneEntry);
                }
                recentContacts.addFirst(phoneEntry);
                if (recentContacts.size() > 5) {
                    recentContacts.removeLast();
                }

                try {
                    SharedPreferences.Editor editor = this.sharedPreferences.edit();
                    editor.putString(HISTORY_KEY, serializeContacts(this.recentContacts));
                    editor.apply();
                    return true;
                } catch (IOException e) {
                    //ignore me
                    return false;
            }
        }
    }

    public List<TimedPhoneEntry> getRecentContacts() {
        Assertion.assertIsNotMainThread();
        synchronized (this.recentContacts) {
            return new ArrayList<>(this.recentContacts);
        }
    }

    public void clear() {
        Assertion.assertIsNotMainThread();
        synchronized (this.recentContacts) {
            this.recentContacts.clear();
            this.sharedPreferences.edit().clear().apply();
        }
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    private LinkedList<TimedPhoneEntry> deserializeContacts(String serializedContacts) {
        byte[] decode = Base64.decode(serializedContacts, Base64.DEFAULT);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(decode);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return (LinkedList<TimedPhoneEntry>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new LinkedList<>();
        }
    }

    private String serializeContacts(LinkedList<TimedPhoneEntry> contacts) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(contacts);
            objectOutputStream.flush();
            return new String(Base64.encode(byteArrayOutputStream.toByteArray(),Base64.DEFAULT));
        }
    }



}
