package com.snda.ContactManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ContactDetail extends Activity implements View.OnClickListener {
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
		
		Log.i(Constants.APP_TAG, "Start activity with selectedId =" + selectedId + ", display Name =" + displayName);
		
		// Gather email data from email table
        Cursor email = getContentResolver().query(CommonDataKinds.Email.CONTENT_URI,
                                                  new String[] { CommonDataKinds.Email.DATA },
                                                  ContactsContract.Data.CONTACT_ID + " = " + selectedId, null, null);
        // Gather phone data from phone table
        Cursor phone = getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                                                  new String[] { CommonDataKinds.Phone.NUMBER },
                                                  ContactsContract.Data.CONTACT_ID + " = " + selectedId, null, null);
        // Gather addresses from address table
        Cursor address = getContentResolver().query(CommonDataKinds.StructuredPostal.CONTENT_URI,
                                                    new String[] { CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS },
                                                    ContactsContract.Data.CONTACT_ID + " = " + selectedId, null, null);
        //Build the dialog message
        StringBuilder sb = new StringBuilder();
        sb.append(email.getCount() + " Emails\n");
        if (email.moveToFirst()) {
            do {
                sb.append("Email: " + email.getString(0));
                sb.append('\n');
            } while (email.moveToNext());
            sb.append('\n');
        }
        sb.append(phone.getCount() + " Phone Numbers\n");
        if (phone.moveToFirst()) {
            do {
                sb.append("Phone: " + phone.getString(0));
                sb.append('\n');
            } while (phone.moveToNext());
            sb.append('\n');
        }
        sb.append(address.getCount() + " Addresses\n");
        if (address.moveToFirst()) {
            do {
                sb.append("Address:\n" + address.getString(0));
            } while (address.moveToNext());
            sb.append('\n');
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(displayName); // Display name
        builder.setMessage(sb.toString());
        builder.setPositiveButton("OK", null);
        builder.create().show();

        display_name_tv.setText(displayName);
        
        if (phone.getCount() > 0) {
            phone.moveToFirst();
            phone_number_tv.setText(phone.getString(0));
        }
        if (email.getCount() > 0) {
            email.moveToFirst();
            email_tv.setText(email.getString(0));
        }
        email.close();
        phone.close();
        address.close();
	}
    
	@Override
	public void onClick(View v) {
        // 响应按钮事件
        switch (v.getId()) {
        case R.id.bt_call:
            Log.i(Constants.APP_TAG, "Phone Button Clicked");
            break;
        case R.id.bt_sms:
            Log.i(Constants.APP_TAG, "SMS Button Clicked");
            break;
        case R.id.bt_email:
            Log.i(Constants.APP_TAG, "Email Button Clicked");
            break;
        case R.id.bt_weibo:
            Log.i(Constants.APP_TAG, "Weibo Button Clicked");
            break;
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
}
