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
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.content.DialogInterface;
import android.content.Intent;

public class ContactEdit extends Activity implements View.OnClickListener {
    private static final int SAVE = Menu.FIRST;
	private static final int CANCEL = Menu.FIRST + 1;
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
    private Button cancel_bt = null;
    private ArrayList<Integer> phone_types = null;
    private Spinner phone_types_sp = null;
    
    boolean isEditing = false;
    // 唯一的lookupKey, ContactId 可能会在编辑的时候改变
    String lookupKey = Constants.INVALID_LOOKUP_KEY;
    // 保存正在编辑的人物数据
    Person person;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.contact_edit);
    	
    	setEditText();
        setSpinner();
    	setButton();

        Log.d(Constants.APP_TAG, ContactEdit.class.getName() + " onCreate");
        
        Intent intent = getIntent();
        int contactId = intent.getIntExtra(Constants.SELECTED_CONTACT_ID, -1);
        if (contactId != -1) {
        	// 如果获取到contactId, 说明是一个编辑请求
        	isEditing = true;
        	ContactManagerApplication application = (ContactManagerApplication)getApplication();
            person = application.getContactManager().makePerson(this, getContentResolver(), contactId);
            lookupKey = person.getLookupKey();
            Log.i(Constants.APP_TAG, "Editing contact " + contactId + " with lookupKey: " + lookupKey);
            setInitialValues(person);
        }
        else {
        	Log.i(Constants.APP_TAG, "Adding a new contact");
        }
    }

    private void setInitialValues(Person person) {
		// 设置编辑的初始值
		first_name_et.setText(person.getFirstName());
        last_name_et.setText(person.getLastName());
        ArrayList<String> phoneList = person.getPhoneList();
        if (!phoneList.isEmpty()) {
        	phone_number_et.setText(phoneList.get(0));
        }
        ArrayList<String> emailList = person.getMailList();
        if (!emailList.isEmpty()) {
        	email_et.setText(emailList.get(0));
        }
        weibo_et.setText(person.getWeibo());
        qq_et.setText(person.getQQ());
        msn_et.setText(person.getMSN());
        organization_et.setText(person.getOrg());
        address_et.setText(person.getAddress());
        department_et.setText(person.getDepartment());
        position_et.setText(person.getPosition());
        note_et.setText(person.getNote());
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
        
        cancel_bt = (Button)findViewById(R.id.button_contacts_cancel_edit);
        cancel_bt.setOnClickListener(this);
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

    /**
     * <code>verifyForm</code> 校验表单数据合法性
     *
     * @return a <code>boolean</code> value
     */
    public boolean verifyForm()
    {
        String first_name = first_name_et.getText().toString();
        String last_name = last_name_et.getText().toString();
        if (first_name.equals("") && last_name.equals("")) {
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(getString(R.string.dialog_null_contact_name))
                .setPositiveButton(getString(R.string.Continue),
                                   new android.content.DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int arg1) {
                                           // 什么也不做，直接返回
                                       }
                                   })
                .show();
            return false;
        }
        return true;
    }
    
    @Override
    public void onClick(View v) {
    	// 响应按钮事件
    	switch (v.getId()) {
        case R.id.button_contacts_save:
        	saveContact();
            break;
        case R.id.button_contacts_cancel_edit:
            cancelEdit();
        	break;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, 
                 ContactEdit.SAVE,
                 0,
                 R.string.save).setIcon(android.R.drawable.ic_menu_save);
    	menu.add(0,
                 ContactEdit.CANCEL,
                 0,
                 R.string.cancel).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	// 响应菜单事件
    	super.onMenuItemSelected(featureId, item);
    	switch (item.getItemId()) {
    	case ContactEdit.SAVE:
    		saveContact();
    		return true;
    	case ContactEdit.CANCEL:
    		cancelEdit();
    		return true;
    	}
    	return true;
    }

	private void cancelEdit() {
		Log.i(Constants.APP_TAG, "Cancel edit contact");
        // 取消编辑
        Intent result2 = new Intent();
        setResult(RESULT_CANCELED, result2);
        finish();
	}

	private void saveContact() {
		Log.i(Constants.APP_TAG, "Saving Contact");
        // 校验数据合法性，名字和姓氏不能全部为空
        if (verifyForm() == false) {
            Log.i(Constants.APP_TAG, "Invalid Contact Form");
            return;
        }
            
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
            
        if (!isEditing) {
            // 添加联系人
            application.getContactManager().addContact(new AddContactParameter(this, getContentResolver(), first_name, last_name,
                                                                               phone_type, phone_number, email, weibo, qq, msn,
                                                                               organization, address, department, position, note));
            Log.i(Constants.APP_TAG, "Created a new contact");
        }
        else {
            // 更新联系人
            application.getContactManager().updateContact(person, new AddContactParameter(this, getContentResolver(), first_name, last_name,
                                                                               phone_type, phone_number, email, weibo, qq, msn,
                                                                               organization, address, department, position, note));
            Log.i(Constants.APP_TAG, "Edited contact LookupKey: " + lookupKey);
        }

        Intent result = new Intent();
        // 返回lookup key
        result.putExtra(Constants.LOOKUP_KEY, lookupKey);
        setResult(RESULT_OK, result);
        finish();
	}
}
