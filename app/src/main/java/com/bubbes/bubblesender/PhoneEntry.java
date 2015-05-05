package com.bubbes.bubblesender;

import java.io.Serializable;

public class PhoneEntry implements Serializable {

    //==============================================================================================
    // Attributes
    //==============================================================================================

    private final String name;
    private final String phone;
    private final String type;
    private final String imageThumbUri;
    private final String imageUri;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public PhoneEntry(String name, String phone, String type, String imageThumbUri, String imageUri) {
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.imageThumbUri = imageThumbUri;
        this.imageUri = imageUri;
    }

    //==============================================================================================
    // Public/getters
    //==============================================================================================

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getType() {
        return type;
    }

    public String getImageThumbUri() {
        return this.imageThumbUri;
    }

    public String getImageUri() {
        return this.imageUri;
    }

    public String toDisplay() {
        return this.name + " <" + this.phone + ">";
    }
}
