/**
 * Copyright (C)2011, Shanda Corporation. All rights reserved.
 *
 * @file: ContactManager.java
 *
 * @author: tangyaguang@snda.com 018386
 *
 * @date: 2011-08-22 00:55:25
 *
 * @doc: Contact Manager, 存储联系人信息和联系人相关的操作
 */

package com.snda.ContactManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Integer;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

public class ContactManager {
    // 所有联系人列表
    HashMap<Integer, Person> mPersonMap;

    // 唯一的PersonId
    Integer mNextPersonId;

    public ContactManager()
    {
        mNextPersonId = 0;
    }
    
    public List<Person> getPersonList()
    {
        ArrayList<Person> plist = new ArrayList<Person>(mPersonMap.values());
        return plist;
    }

    public boolean hasPerson(String name)
    {
        return false;
    }
    
    public Person makePerson()
    {
        Person person = new Person(mNextPersonId);
        mNextPersonId++;
        return person;
    }

	/**
     *  <code>addContact</code> 添加一个联系人
	 * @param parameterObject 参数对象
     */
    public void addContact(AddContactParameter parameterObject)
    {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

         ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                 .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                 .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                 .build());
        
         // 名字
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                           ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, parameterObject.first_name)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, parameterObject.last_name)
                .build());

        // 电话号码
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                           ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, parameterObject.phone_number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, parameterObject.phone_type)
                .build());

        // email
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                           ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, parameterObject.email)
                .build());

        // 添加自定义类型: 微博 
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        		.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
        		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
        		.withValue(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM)
                .withValue(ContactsContract.CommonDataKinds.Website.DATA, parameterObject.weibo)
                .build());

        
        // 单位
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                           ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, parameterObject.organization)
                .withValue(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, parameterObject.department)
                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, parameterObject.position)
                .build());

        // 地址
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                           ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, parameterObject.address)
                .build());

        // 备注
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                           ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Note.NOTE, parameterObject.note)
                .build());
        
        // 调用Contact provider 创建一个新的Contact
        Log.i(Constants.APP_TAG,"Creating contact: " + parameterObject.first_name + " " + parameterObject.last_name);
        try {
            parameterObject.resolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            // 显示警告信息
            Context ctx = parameterObject.context;
            CharSequence txt = ctx.getString(R.string.msg_contact_create_failture);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, txt, duration);
            toast.show();

            // Log exception
            Log.e(Constants.APP_TAG, "Inserting contact exception: " + e);
        }
	}
}
