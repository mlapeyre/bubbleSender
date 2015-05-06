package com.bubbes.bubblesender;


public class TimedPhoneEntry extends PhoneEntry {
    //==============================================================================================
    // Attributes
    //==============================================================================================
    private final long time;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public TimedPhoneEntry(String name, String phone, String type, String imageThumbUri, String imageUri, long time) {
        super(name, phone, type, imageThumbUri, imageUri);
        this.time = time;
    }

    public TimedPhoneEntry(PhoneEntry phoneEntry, long time) {
        super(phoneEntry.getName(), phoneEntry.getPhone(), phoneEntry.getType(), phoneEntry.getImageThumbUri(), phoneEntry.getImageUri());
        this.time = time;
    }

    public long getTime() {
        return time;
    }

}
