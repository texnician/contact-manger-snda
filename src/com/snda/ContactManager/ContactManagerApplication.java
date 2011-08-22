package com.snda.ContactManager;

import android.app.Application;

public class ContactManagerApplication extends Application {
    private ContactManager mContactManager;

    public ContactManagerApplication()
    {
        super();
        mContactManager = new ContactManager();
    }
    
    public ContactManager getContactManager()
    {
        return mContactManager;
    }
}
