/**
 * Copyright (C)2011, Shanda Corporation. All rights reserved.
 *
 * @file: ContactEdit.java
 *
 * @author: tangyaguang@snda.com 018386
 *
 * @date: 2011-08-21 22:30:07
 *
 * @doc: 编辑联系人信息
 */

package com.snda.ContactManager;

import android.app.Activity;

import android.os.Bundle;

public class ContactEdit extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_edit);
    }
}
