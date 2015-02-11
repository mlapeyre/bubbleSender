package com.bubbes.bubblesender.contacts;

import java.util.HashMap;

public class PhoneEntry extends HashMap<String, String> {

    //==============================================================================================
    // Contants
    //==============================================================================================
    public static final String CONTACT_NAME = "Name";
    public static final String CONTACT_PHONE = "Phone";
    public static final String CONTACT_PHONE_TYPE = "Type";

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public PhoneEntry(String name, String phone, String type) {
        super();
        this.put(CONTACT_NAME, name);
        this.put(CONTACT_PHONE, phone);
        this.put(CONTACT_PHONE_TYPE, type);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    public String toDisplay() {
        return this.get(CONTACT_NAME) + " <" + this.get(CONTACT_PHONE) + ">";
    }
}