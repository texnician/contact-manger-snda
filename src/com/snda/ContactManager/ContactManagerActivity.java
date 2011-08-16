/**
 * Copyright (C) 2011, Shanda Corporation. All rights reserved.
 *
 * @file: ContactManagerActivity.java
 * 
 * @author: tangyaguang@snda.com 018386
 *
 * @date: 2011-08-16
 *
 * @doc:  ContactManager main Activity
 */

package com.snda.ContactManager;

import android.app.Activity;
import android.os.Bundle;

public class ContactManagerActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}