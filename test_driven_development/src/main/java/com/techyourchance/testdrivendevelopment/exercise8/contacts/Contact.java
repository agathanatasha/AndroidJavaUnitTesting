package com.techyourchance.testdrivendevelopment.exercise8.contacts;

public class Contact {

    private final String mId;
    private final String mFullName;
    private final String mImageUrl;

    public Contact(String id, String fullName, String imageUrl) {
        mId = id;
        mFullName = fullName;
        mImageUrl = imageUrl;
    }

    public String getId() {
        return mId;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Contact contact = (Contact) o;

        if (!mId.equals(contact.mId)) {
            return false;
        }
        if (!mFullName.equals(contact.mFullName)) {
            return false;
        }
        return mImageUrl.equals(contact.mImageUrl);
    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + mFullName.hashCode();
        result = 31 * result + mImageUrl.hashCode();
        return result;
    }
}
