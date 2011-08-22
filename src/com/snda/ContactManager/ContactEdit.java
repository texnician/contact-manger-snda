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

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.os.Bundle;
import android.provider.ContactsContract;

public class ContactEdit extends Activity implements View.OnClickListener {
    private EditText first_name_et = null;
    private EditText last_name_et = null;
    private EditText phone_number_et = null;
    private EditText email_et = null; 
    private EditText weibo_et = null; 
    private EditText qq_et = null; 
    private EditText msn_et = null; 
    private EditText organization_et = null; 
    private EditText address_et = null; 
    private EditText department_et = null; 
    private EditText position_et = null; 
    private EditText note_et = null; 
    private Button save_bt = null;
    private ArrayList<Integer> phone_types = null;
    private Spinner phone_types_sp = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.contact_edit);
    	
    	setEditText();
        setSpinner();
    	setButton();

        Log.d(Constants.APP_TAG, ContactEdit.class.getName() + " onCreate");
    }

    /**
     *  <code>setSpinner</code> 设置phone type spinner
     *
     */
    private void setSpinner()
    {
        // 设置默认的phone type列表
        phone_types = new ArrayList<Integer>();
        phone_types.add(ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        phone_types.add(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        phone_types.add(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        phone_types.add(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Iterator<Integer> iter;
        iter = phone_types.iterator();
        while (iter.hasNext()) {
            adapter.add(ContactsContract.CommonDataKinds.Phone.getTypeLabel(this.getResources(),
                                                                            iter.next(),
                                                                            getString(R.string.undefined_phone_type_label)).toString());
        }

        phone_types_sp = (Spinner)findViewById(R.id.spinner_phone_type);
        phone_types_sp.setAdapter(adapter);
        phone_types_sp.setPrompt(getString(R.string.spinner_select_label));
    }
    
    /**
     * <code>setButton</code> 设置Button控件
     *
     */
    private void setButton()
    {
        save_bt = (Button)findViewById(R.id.button_contacts_save);
        save_bt.setOnClickListener(this);
    }
    
    /**
     * <code>setEditText</code>设置EditText控件
     *
     */
    private void setEditText()
    {
        first_name_et = (EditText)findViewById(R.id.edit_text_first_name);
        last_name_et = (EditText)findViewById(R.id.edit_text_last_name);
        phone_number_et = (EditText)findViewById(R.id.edit_text_phone);
        email_et = (EditText)findViewById(R.id.edit_text_email);
        weibo_et = (EditText)findViewById(R.id.edit_text_weibo);
        qq_et = (EditText)findViewById(R.id.edit_text_qq);
        msn_et = (EditText)findViewById(R.id.edit_text_msn);
        organization_et = (EditText)findViewById(R.id.edit_text_organization);
        address_et = (EditText)findViewById(R.id.edit_text_address);
        department_et = (EditText)findViewById(R.id.edit_text_department);
        position_et = (EditText)findViewById(R.id.edit_text_position);
        note_et = (EditText)findViewById(R.id.edit_text_note);
    }
    
    // String SELECTION = RawContacts.CONTACT_ID + "=? OR " + RawContacts.CONTACT_ID + "=?";
    
    @Override
    public void onClick(View v) {
    	// 响应按钮事件
        Log.i(Constants.APP_TAG, "Button Clicked");
    	switch (v.getId()) {
        case R.id.button_contacts_save:
            Log.i(Constants.APP_TAG, "Save Contact");
            // 搜集UI上的数据
            String first_name = first_name_et.getText().toString();
            String last_name = last_name_et.getText().toString();
            String phone_number = phone_number_et.getText().toString();
            int phone_type = phone_types.get(phone_types_sp.getSelectedItemPosition());
            String email = email_et.getText().toString();
            String weibo = weibo_et.getText().toString();
            String qq = qq_et.getText().toString();
            String msn = msn_et.getText().toString();
            String organization = organization_et.getText().toString();
            String address = address_et.getText().toString();
            String department = department_et.getText().toString();
            String position = position_et.getText().toString();
            String note = note_et.getText().toString();
            
            Log.i(Constants.APP_TAG, "Contact FirstName: " + first_name
                  + " LastName: " + last_name + " PhoneType: " + phone_type + " Phone: " + phone_number + " Email: " + email
                  + " Weibo: " + weibo + " QQ: " + qq + " MSN: " + msn + " Org: " + organization
                  + " Address: " + address + " Department: " + department + " Position: " + position
                  + " Note: " + note);
            
            // 存储数据到数据库中
            ContactManagerApplication application = (ContactManagerApplication)getApplication();
            application.getContactManager().addContact(first_name, last_name, phone_type, phone_number,
                                                       email, weibo, qq, msn, organization, address,
                                                       department, position, note);
            // 刷新列表?
            break;
        }
    }
}
