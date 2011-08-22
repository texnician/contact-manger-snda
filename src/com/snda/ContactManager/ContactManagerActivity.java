/**
 * Copyright (C)2011, Shanda Corporation. All rights reserved.
 *
 * @file: ContactManagerActivity.java
 *
 * @author: tangyaguang@snda.com 018386
 *
 * @date: 2011-08-16 15:57:21
 *
 * @doc: ContactManager main Activity
 */

package com.snda.ContactManager;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class ContactManagerActivity extends ListActivity {
	// private static final int MENU_CHANGE_CRITERIA = Menu.FIRST + 1;
    private static final int MENU_ADD_CONTACT = Menu.FIRST;
    
    /** Called when the activity is first created. */
	Cursor mContacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                                             ContactsContract.Contacts.DISPLAY_NAME };
        mContacts = managedQuery(ContactsContract.Contacts.CONTENT_URI,
                                 projection, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        // 在ListView中显示所有联系人信息
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,
                                                               android.R.layout.simple_list_item_1, 
                                                               // R.layout.contact_entry,
                                                               mContacts,
                                                               new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                                                               new int[] { android.R.id.text1 });
                                                               //new int[] { R.id.contact_entry_text });
        setListAdapter(mAdapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// 创建菜单项
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, 
                 ContactManagerActivity.MENU_ADD_CONTACT,
                 0,
                 R.string.menu_add_contact).setIcon(android.R.drawable.ic_menu_more);
    	return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	// 响应菜单事件
    	super.onMenuItemSelected(featureId, item);
    	switch (item.getItemId()) {
            case MENU_ADD_CONTACT:
                // 启动创建联系人的 activity
            	Intent intent = new Intent(this, ContactEdit.class);
            	startActivity(intent);
                return true;
        }
    	return true;
    }
}
