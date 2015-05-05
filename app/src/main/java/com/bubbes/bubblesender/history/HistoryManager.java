package com.bubbes.bubblesender.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.bubbes.bubblesender.PhoneEntry;
import com.bubbes.bubblesender.utils.Assertion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
    private final LinkedList<PhoneEntry> recentContacts;

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
     * @param context The context to use for retreivong shared preferences
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

    /**
     * Notify that the contact has sent bubbles
     * @param phoneEntry
     * @return
     */
    public boolean notifySender(PhoneEntry phoneEntry) {
        Assertion.assertIsNotMainThread();
        synchronized (recentContacts) {
            if (isFirst(phoneEntry)) {
                //This entry is already the first lets return
                return true;
            }else{
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

    }

    private boolean isFirst(PhoneEntry phoneEntry) {
        synchronized (recentContacts) {
            if (this.recentContacts.isEmpty()){
                return false;
            }else{
                PhoneEntry first = this.recentContacts.getFirst();
                return first != null && Objects.equals(first, phoneEntry);
            }
        }
    }

    public List<PhoneEntry> getRecentContacts() {
        Assertion.assertIsNotMainThread();
        synchronized (recentContacts) {
            return new ArrayList<>(this.recentContacts);
        }
    }


    //==============================================================================================
    // Private
    //==============================================================================================

    private LinkedList<PhoneEntry> deserializeContacts(String serializedContacts) {
        byte[] decode = Base64.decode(serializedContacts, Base64.DEFAULT);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(decode);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return (LinkedList<PhoneEntry>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new LinkedList<>();
        }
    }

    private String serializeContacts(LinkedList<PhoneEntry> contacts) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(contacts);
            objectOutputStream.flush();
            return new String(Base64.encode(byteArrayOutputStream.toByteArray(),Base64.DEFAULT));
        }
    }


    public void clear() {
        synchronized (this.recentContacts) {
            this.recentContacts.clear();
            this.sharedPreferences.edit().clear().apply();
        }
    }
}
