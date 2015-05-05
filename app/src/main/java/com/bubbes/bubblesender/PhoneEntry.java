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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneEntry that = (PhoneEntry) o;

        if (!name.equals(that.name)) return false;
        if (!phone.equals(that.phone)) return false;
        if (!type.equals(that.type)) return false;

        if (imageThumbUri != null ? !imageThumbUri.equals(that.imageThumbUri) : that.imageThumbUri != null)
            return false;
        return !(imageUri != null ? !imageUri.equals(that.imageUri) : that.imageUri != null);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + phone.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (imageThumbUri != null ? imageThumbUri.hashCode() : 0);
        result = 31 * result + (imageUri != null ? imageUri.hashCode() : 0);
        return result;
    }
}
