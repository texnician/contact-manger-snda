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

import java.io.File;
import java.net.URI;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

public class ContactManagerActivity extends ListActivity implements AdapterView.OnItemClickListener {
	// private static final int MENU_CHANGE_CRITERIA = Menu.FIRST + 1;
    private static final int MENU_ADD_CONTACT = Menu.FIRST;
    private static final int MENU_CALL_LOG = Menu.FIRST + 1;
    private static final int MENU_FREQ = Menu.FIRST + 2;
    private static final int MENU_EXPORT_CONTACT = Menu.FIRST + 3;
    private static final int MENU_IMPORT_CONTACT = Menu.FIRST + 4;
    
    
    private static final int SEARCH_CONTACT_INTENT = 0;
    private static final int SELECT_EXPORT_DIR_INTENT = 1;
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

        // 设置ListView监听器为this
        getListView().setOnItemClickListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// 创建菜单项
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, 
                 ContactManagerActivity.MENU_ADD_CONTACT,
                 0,
                 R.string.menu_add_contact).setIcon(android.R.drawable.ic_menu_add);
    	menu.add(0, 
                 ContactManagerActivity.MENU_CALL_LOG,
                 0,
                 "通话记录").setIcon(android.R.drawable.sym_call_incoming);
    	menu.add(0, 
                 ContactManagerActivity.MENU_FREQ,
                 0,
                 "查看常用联系人").setIcon(android.R.drawable.star_big_on);
        menu.add(0, 
                 ContactManagerActivity.MENU_EXPORT_CONTACT,
                 0,
                 R.string.menu_export_contact);
        menu.add(0, 
                 ContactManagerActivity.MENU_IMPORT_CONTACT,
                 0,
                 R.string.menu_import_contact);
    	return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	// 响应菜单事件
    	super.onMenuItemSelected(featureId, item);
    	switch (item.getItemId()) {
        case MENU_ADD_CONTACT:
            Log.i(Constants.APP_TAG, "MENU_ADD_CONTACT selected");
            // 启动创建联系人的 activity
            Intent intent = new Intent(this, ContactEdit.class);
            startActivity(intent);
            return true;
        case MENU_CALL_LOG:
        	Log.i(Constants.APP_TAG, "MENU_CALL_LOG selected");
        	Intent tmp = new Intent(Intent.ACTION_VIEW);
            tmp.setType(CallLog.Calls.CONTENT_TYPE);
            startActivity(tmp);
        	return true;
        case MENU_FREQ:
        	Log.i(Constants.APP_TAG, "MENU_LIST_FREQUENT selected");
        	Intent it_log = new Intent("com.android.contacts.action.LIST_STREQUENT");
        	startActivity(it_log);
        	return true;
        case MENU_EXPORT_CONTACT:
            Log.i(Constants.APP_TAG, "MENU_EXPORT_CONTACT selected");
            exportContacts();
            return true;
        case MENU_IMPORT_CONTACT:
            Log.i(Constants.APP_TAG, "MENU_IMPORT_CONTACT selected");
            importContacts();
            return true;
        }
    	return true;
    }
    
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
    	// 处理联系人选择事件
    	if (mContacts.moveToPosition(position)) {
            int selectedId = mContacts.getInt(0); // _ID column
            Log.i(Constants.APP_TAG, "Contact with raw_contact_id =" + selectedId + " is selected.");
            
            Intent intent = new Intent(this, ContactDetail.class);
            intent.putExtra(Constants.SELECTED_CONTACT_ID, selectedId);
            intent.putExtra(Constants.DISPLAY_NAME, mContacts.getString(1));
            startActivity(intent);
    	}
    }
    
    @Override
    public boolean onSearchRequested() {
    	// TODO Auto-generated method stub
    	super.onSearchRequested();

        // 调用联系人搜索
    	Intent intent = new Intent();
    	intent.setAction("com.android.contacts.action.FILTER_CONTACTS");
    	intent.setType("vnd.android.cursor.dir/contact");
    	intent.addCategory("android.intent.category.DEFAULT");
    	startActivityForResult(intent, SEARCH_CONTACT_INTENT);
            	
    	// Intent intent = new Intent();
    	// intent.putExtra(SearchManager.QUERY, display_name_tv.getText().toString());
    	// intent.setAction(Intent.ACTION_SEARCH);
    	// intent.setPackage("com.android.contacts");
    	// startActivity(intent);
    	return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     	super.onActivityResult(requestCode, resultCode, data);
    	
    	Cursor c = null;
        
        if (requestCode == SEARCH_CONTACT_INTENT) {
            try{
                if (data != null) {
                    c = getContentResolver().query(data.getData(), null, null, null, null);
                }
                if (c != null) {
                    c.moveToFirst();
                    for(int i = 0; i < c.getColumnCount(); i++) {
                        String name = c.getColumnName(i);
                        String result = c.getString(i);
                        Log.i(Constants.APP_TAG, "ColName: " + name + " ColData: " + result);
                    }
                    c.close();
                }
            }
            catch (Exception e) {
                Log.e(Constants.APP_TAG, e.getMessage());
            }
        }
        else if (requestCode == SELECT_EXPORT_DIR_INTENT && resultCode == RESULT_OK) {
        	  Uri uri = data.getData();
        	  String type = data.getType();
        	  Log.i(Constants.APP_TAG, "Select export dir completed: "+ uri + " "+ type);
        	  if (uri != null)
        	  {
        		  String path = uri.toString();
        		  if (path.toLowerCase().startsWith("file://"))
        		  {
        			  // Selected file/directory path is below
        			  path = (new File(URI.create(path))).getAbsolutePath();
        			  Log.i(Constants.APP_TAG, "Absolute Path is: " + path);
        		  }
        	  }
        }
    }

    private void exportContacts() {
        ContactManagerApplication application = (ContactManagerApplication)getApplication();
        application.getContactManager().exportContacts(this, getContentResolver(), Constants.EXPORT_FILE_NAME);
    }

    private void importContacts() {
        ContactManagerApplication application = (ContactManagerApplication)getApplication();
        application.getContactManager().importContacts(this, getContentResolver(), Constants.EXPORT_FILE_NAME);
    }
}
