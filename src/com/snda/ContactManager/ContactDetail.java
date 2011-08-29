/**
 * Copyright (C)2011, Shanda Corporation. All rights reserved.
 *
 * @file: ContactDetail.java
 *
 * @author: tangyaguang@snda.com 018386
 *
 * @date: 2011-08-27 00:27:46
 *
 * @doc: 联系人详细信息 Activity
 */

package com.snda.ContactManager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ContactDetail extends Activity implements View.OnClickListener {
    private static final int GROUP_ACTION = 0, GROUP_EDIT = 1;
    private static final int MENU_EDIT_CONTACT = Menu.FIRST;
    private static final int MENU_DEL_CONTACT = Menu.FIRST + 1;
    private static final int MENU_CALL = Menu.FIRST + 2;
    private static final int MENU_SMS = Menu.FIRST + 3;
    private static final int MENU_MAIL = Menu.FIRST + 4;
    private static final int MENU_WEIBO = Menu.FIRST + 5;
	private static final int EDIT_CONTACT_ACTIVITY = 0;
    
    // TextViews
    private TextView display_name_tv = null;
    private TextView phone_number_tv = null;
    private TextView email_tv = null; 
    private TextView weibo_tv = null; 
    private TextView qq_tv = null; 
    private TextView msn_tv = null; 
    private TextView organization_tv = null; 
    private TextView address_tv = null; 
    private TextView department_tv = null; 
    private TextView position_tv = null; 
    private TextView note_tv = null;

    // Buttons
    private ImageButton call_bt = null;
    private ImageButton sms_bt = null;
    private ImageButton email_bt = null;
    private ImageButton weibo_bt = null;
    
    private String displayName = null;
    private int selectedId = 0;
    
    // Person
    Person person = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 查看联系人详细信息
		super.onCreate(savedInstanceState);
		
		Log.i(Constants.APP_TAG, "View Contact Detail Info Activity onCreate");
		
		setContentView(R.layout.contact_detail);

        setTextViews();
        setButtons();
        
		Intent intent = getIntent();
		selectedId = intent.getIntExtra(Constants.SELECTED_CONTACT_ID, 0);
		displayName = intent.getStringExtra(Constants.DISPLAY_NAME);
        if (selectedId != 0) {
            Log.i(Constants.APP_TAG, "Start activity with selectedId =" + selectedId + ", display Name =" + displayName);
        }
        else {
            Uri data = intent.getData();
            String authority = data.getAuthority();
            Uri lookupUri = null;
            if (ContactsContract.AUTHORITY.equals(authority)) {
                lookupUri = data;
            } else if (android.provider.Contacts.AUTHORITY.equals(authority)) {
                final long rawContactId = ContentUris.parseId(data);
                lookupUri = RawContacts.getContactLookupUri(getContentResolver(),
                                                             ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId));
            }
            selectedId = (int) ContentUris.parseId(lookupUri);
            Log.i(Constants.APP_TAG, "Start activity by VIEW intent with selectedId = " + selectedId + " uri: " + lookupUri.toString());
        }
        // 显示人物信息
		ContactManagerApplication application = (ContactManagerApplication)getApplication();
        person = application.getContactManager().makePerson(this, getContentResolver(), selectedId);
        displayPersion();
	}
    
	@Override
	public void onClick(View v) {
        // 响应按钮事件
        switch (v.getId()) {
        case R.id.bt_call:
            Log.i(Constants.APP_TAG, "Phone Button Clicked");
            callContact();
            break;
        case R.id.bt_sms:
            Log.i(Constants.APP_TAG, "SMS Button Clicked");
            sendSMS();
            break;
        case R.id.bt_email:
            Log.i(Constants.APP_TAG, "Email Button Clicked");
            sendMail();
            break;
        case R.id.bt_weibo:
            Log.i(Constants.APP_TAG, "Weibo Button Clicked");
            break;
        }
	}
    
    private void callContact()
    {
        String number = phone_number_tv.getText().toString();

        if (!number.equals("")) {
            Log.i(Constants.APP_TAG, "Calling " + number);
            Intent intent = new Intent(Intent.ACTION_CALL,
                                       Uri.parse("tel:" + number));
            startActivity(intent);
        }
    }
    
    /**
     * <code>setTextViews</code> 设置TextViews
     *
     */
    private void setTextViews()
    {
        display_name_tv = (TextView)findViewById(R.id.display_name_tv);
        phone_number_tv = (TextView)findViewById(R.id.phone_number_tv);
        email_tv = (TextView)findViewById(R.id.email_tv);
        weibo_tv = (TextView)findViewById(R.id.weibo_tv);
        qq_tv = (TextView)findViewById(R.id.qq_tv);
        msn_tv = (TextView)findViewById(R.id.msn_tv);
        organization_tv = (TextView)findViewById(R.id.organization_tv);
        address_tv = (TextView)findViewById(R.id.address_tv);
        department_tv = (TextView)findViewById(R.id.department_tv);
        position_tv = (TextView)findViewById(R.id.position_tv);
        note_tv = (TextView)findViewById(R.id.note_tv);
    }

    /**
     * <code>setButtons</code> 设置界面上的button
     *
     */
    private void setButtons()
    {
        call_bt = (ImageButton)findViewById(R.id.bt_call);
        call_bt.setOnClickListener(this);
        
        sms_bt = (ImageButton)findViewById(R.id.bt_sms);
        sms_bt.setOnClickListener(this);
        
        email_bt = (ImageButton)findViewById(R.id.bt_email);
        email_bt.setOnClickListener(this);
        
        weibo_bt = (ImageButton)findViewById(R.id.bt_weibo);
        weibo_bt.setOnClickListener(this);
    }

    /**
     * <code>displayPersion</code> 在界面上显示人物信息
     * @param  
     *
     */
    private void displayPersion()
    {
        display_name_tv.setText(person.getDisplayName());
        
        ArrayList<String> phones = person.getPhoneList();
        if (phones.size() > 0) {
        	phone_number_tv.setText(phones.get(0));
        }
        
        ArrayList<String> emails = person.getMailList();
        if (emails.size() > 0) {
        	email_tv.setText(emails.get(0));
        }
        
        weibo_tv.setText(person.getWeibo());
        qq_tv.setText(person.getQQ());
        msn_tv.setText(person.getMSN());
        organization_tv.setText(person.getOrg());
        address_tv.setText(person.getAddress());
        department_tv.setText(person.getDepartment());
        position_tv.setText(person.getPosition());
        note_tv.setText(person.getNote());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// 创建菜单项
    	super.onCreateOptionsMenu(menu);
    	menu.add(ContactDetail.GROUP_ACTION, 
                 ContactDetail.MENU_CALL,
                 0,
                 R.string.menu_call_contact).setIcon(android.R.drawable.ic_menu_call);
    	menu.add(ContactDetail.GROUP_ACTION,
                 ContactDetail.MENU_SMS,
                 0,
                 R.string.menu_sms_contact).setIcon(android.R.drawable.sym_action_chat);
    	menu.add(ContactDetail.GROUP_ACTION,
                 ContactDetail.MENU_MAIL,
                 0,
                 R.string.menu_mail_contact).setIcon(android.R.drawable.ic_dialog_email);
    	menu.add(ContactDetail.GROUP_ACTION,
                 ContactDetail.MENU_WEIBO,
                 0,
                 R.string.menu_weibo_contact).setIcon(R.drawable.weibo_64x64);
    	
    	menu.add(ContactDetail.GROUP_EDIT,
                 ContactDetail.MENU_EDIT_CONTACT,
                 0,
                 R.string.edit_contact).setIcon(android.R.drawable.ic_menu_edit);
    	
    	menu.add(ContactDetail.GROUP_EDIT,
    			 ContactDetail.MENU_DEL_CONTACT,
    			 0,
    			 R.string.delete).setIcon(android.R.drawable.ic_menu_delete);
    	return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	// 响应菜单事件
    	super.onMenuItemSelected(featureId, item);
    	switch (item.getItemId()) {
            case MENU_CALL:
                // 呼叫
            	Log.i(Constants.APP_TAG, "Menu call contact selected");
                callContact();
                return true;
            case MENU_SMS:
            	// 短信
            	Log.i(Constants.APP_TAG, "Menu sms contact selected");
                sendSMS();
            	return true;
            case MENU_MAIL:
            	// 邮件
            	Log.i(Constants.APP_TAG, "Menu mail contact selected");
                sendMail();
            	return true;
            case MENU_WEIBO:
            	// 微博
            	Log.i(Constants.APP_TAG, "Menu weibo contact selected");
            	return true;
            case MENU_EDIT_CONTACT:
            	Log.i(Constants.APP_TAG, "Menu edit contact selected");
                editContact();
            	return true;
            case MENU_DEL_CONTACT:
            	Log.i(Constants.APP_TAG, "Menu delete contact selected");
            	deleteContact();
            	return true;
        }
    	return true;
    }

    private void deleteContact() {
		ContactManagerApplication application = (ContactManagerApplication)getApplication();
		application.getContactManager().deleteContact(this, getContentResolver(), person.getUid());

        CharSequence txt = this.getString(R.string.msg_contact_deleted);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, txt, duration);
        toast.show();
        
		finish();
	}

	private void editContact()
    {
        // Start edit activity for result
        Intent intent = new Intent(this, ContactEdit.class);
        intent.putExtra(Constants.SELECTED_CONTACT_ID, person.getUid());
        intent.putExtra(Constants.LOOKUP_KEY, person.getLookupKey());
        startActivityForResult(intent, EDIT_CONTACT_ACTIVITY);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent result)
    {
    	if (requestCode == EDIT_CONTACT_ACTIVITY && resultCode == RESULT_OK) {
    		Log.i(Constants.APP_TAG, "EditActivity finished OK");
    		String lookupKey = result.getStringExtra(Constants.LOOKUP_KEY);
    		if (lookupKey != Constants.INVALID_LOOKUP_KEY) {
    			// 刷新person的信息
    			ContactManagerApplication application = (ContactManagerApplication)getApplication();
    			person = application.getContactManager().makePerson(this, getContentResolver(), lookupKey);
    			if (person != null) {
    				displayPersion();
    			}
    			else {
    				finish();
    			}
    		}
    	}
    	else {
    		Log.i(Constants.APP_TAG, "EditActivity CANCELED");
    	}
    }

    private void sendSMS() {
          Uri smsToUri = Uri.parse("smsto:" + phone_number_tv.getText().toString());
          Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
          intent.putExtra("address", phone_number_tv.getText().toString());
          startActivity(intent);
    }

    private void sendMail()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String [] { email_tv.getText().toString() });
        intent.putExtra("address", email_tv.getText().toString());
        intent.setType("text/plain");
        startActivity(intent);
    }
}
