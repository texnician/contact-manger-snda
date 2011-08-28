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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.Integer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;
import android.util.Xml;
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
    
    public Person makePerson(Context context, ContentResolver resolver, int contactId)
    {
        // 查询名字
        String [] selectionArgs = new String[] { "" + contactId };
        
        Cursor lookup = resolver.query(ContactsContract.Contacts.CONTENT_URI,
        		                       new String [] { ContactsContract.Contacts.LOOKUP_KEY },
        		                       ContactsContract.Contacts._ID + " =?", selectionArgs, null);
        
        Cursor name = resolver.query(ContactsContract.Data.CONTENT_URI,
                                     new String[] { CommonDataKinds.StructuredName.DISPLAY_NAME,
        		                                    CommonDataKinds.StructuredName.GIVEN_NAME,
        		                                    CommonDataKinds.StructuredName.FAMILY_NAME },
                                     ContactsContract.Data.CONTACT_ID + " =? AND " 
                                     + ContactsContract.Data.MIMETYPE + " =?",
                                     new String [] { "" + contactId, "" + CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE }, null);

        // 查询电话
        Cursor phone = resolver.query(CommonDataKinds.Phone.CONTENT_URI,
                                      new String[] { CommonDataKinds.Phone.NUMBER, CommonDataKinds.Phone.TYPE },
                                      ContactsContract.Data.CONTACT_ID + "=?", selectionArgs, null);
        
        // 查询email
        Cursor email = resolver.query(CommonDataKinds.Email.CONTENT_URI,
                                      new String[] { CommonDataKinds.Email.DATA },
                                      ContactsContract.Data.CONTACT_ID + " =?", selectionArgs, null);

        // 查询微博
        Cursor weibo = resolver.query(ContactsContract.Data.CONTENT_URI,
                                      new String [] { CommonDataKinds.Website.DATA },
                                      ContactsContract.Data.CONTACT_ID + "=? AND "
                                      + ContactsContract.CommonDataKinds.Website.TYPE + "=? AND "
                                      + ContactsContract.Data.MIMETYPE + "=?",
                                      new String[] { "" + contactId,
                                                     "" + ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM,
                                                     "" + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE },
                                      null);


        // 查询QQ
        Cursor qq = resolver.query(ContactsContract.Data.CONTENT_URI,
        						   new String [] { CommonDataKinds.Im.DATA },
        						   ContactsContract.Data.CONTACT_ID + "=? AND "
        						   + ContactsContract.CommonDataKinds.Im.PROTOCOL + "=? AND "
        						   + ContactsContract.Data.MIMETYPE + "=?",
        						   new String [] { "" + contactId,
        		                                   "" + ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ,
        		                                   "" + ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE },
        		                   null);
        
        // 查询MSN
        Cursor msn = resolver.query(ContactsContract.Data.CONTENT_URI,
                                    new String [] { CommonDataKinds.Im.DATA },
                                    ContactsContract.Data.CONTACT_ID + "=? AND "
                                    + ContactsContract.CommonDataKinds.Im.PROTOCOL + "=? AND "
                                    + ContactsContract.Data.MIMETYPE + "=?",
                                    new String [] { "" + contactId,
                                                    "" + ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN,
                                                    "" + ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE },
                                    null);
        
        // 单位
        Cursor organization = resolver.query(ContactsContract.Data.CONTENT_URI,
                                             new String [] { ContactsContract.CommonDataKinds.Organization.COMPANY,
                                                             ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
                                                             ContactsContract.CommonDataKinds.Organization.TITLE },
                                             ContactsContract.Data.CONTACT_ID + "=? AND "
                                             + ContactsContract.Data.MIMETYPE + "=?",
                                             new String [] { "" + contactId, "" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE }, null);

        
        		
        
        // 地址
        Cursor address = resolver.query(CommonDataKinds.StructuredPostal.CONTENT_URI,
                                        new String[] { CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS },
                                        ContactsContract.Data.CONTACT_ID + "=?", selectionArgs, null);
        
        // 备注
        Cursor note = resolver.query(ContactsContract.Data.CONTENT_URI,
        		                     new String [] { ContactsContract.CommonDataKinds.Note.NOTE },
        		                     ContactsContract.Data.CONTACT_ID + "=? AND "
                                     + ContactsContract.Data.MIMETYPE + "=?",
                                     new String [] { "" + contactId, "" + ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE }, null);
        
        Person person = new Person(contactId);

        Log.d(Constants.APP_TAG, "Get " + lookup.getCount() + " lookup keys");
        if (lookup.moveToFirst()) {
        	person.setLookupKey(lookup.getString(0));
        	Log.d(Constants.APP_TAG, "Lookup key: " + person.getLookupKey());
        }
        
        // 读取名字
        Log.d(Constants.APP_TAG, "Get " + name.getCount() + " names");
        if (name.moveToFirst()) {
        	person.setDisplayName(name.getString(0));
        	person.setFirstName(name.getString(1));
        	person.setLastName(name.getString(2));
        	Log.d(Constants.APP_TAG, 
        		  "DisplayName: " + person.getDisplayName() 
        		  + " FirstName: " + person.getFirstName()
        		  + " LastName: " + person.getLastName());
        }
        
        // 读取电话
        Log.d(Constants.APP_TAG, "Get " + phone.getCount() + " phones");
        if (phone.moveToFirst()) {
        	do {
        		String number = phone.getString(0);
        		int type = phone.getInt(1);
        		String typeName = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(),
                                                                                      type,
                                                                                      context.getString(R.string.undefined_phone_type_label)).toString();
        		person.addPhone(number);
        		Log.d(Constants.APP_TAG, "Phone: " + number + " Type: " + type + " TypeName: " + typeName);
        	} while (phone.moveToNext());
        }
        
        // 读取email
        Log.d(Constants.APP_TAG, "Get " + email.getCount() + " emails");
        if (email.moveToFirst()) {
        	do {
        		String tmp = email.getString(0);
        		person.addMail(tmp);
        		Log.d(Constants.APP_TAG, "Mail: " + tmp);
        	} while (email.moveToNext());
        }
        
        // 读取微博
        Log.d(Constants.APP_TAG, "Get " + weibo.getCount() + " weibo");
        if (weibo.moveToFirst()) {
        	person.setWeibo(weibo.getString(0));
        	Log.d(Constants.APP_TAG, "Weibo: " + person.getWeibo());
        }
        
        // 读取QQ
        Log.d(Constants.APP_TAG, "Get " + qq.getCount() + " QQ");
        if (qq.moveToFirst()) {
        	person.setQQ(qq.getString(0));
        	Log.d(Constants.APP_TAG, "QQ: " + person.getQQ());
        }
        
        // 读取MSN
        Log.d(Constants.APP_TAG, "Get " + msn.getCount() + " MSN");
        if (msn.moveToFirst()) {
        	person.setMSN(msn.getString(0));
        	Log.d(Constants.APP_TAG, "MSN: " + person.getMSN());
        }
        
        // 单位，部门，职务
        Log.d(Constants.APP_TAG, "Get " + organization.getCount() + " Org");
        if (organization.moveToFirst()) {
        	person.setOrg(organization.getString(0));
        	person.setDepartment(organization.getString(1));
        	person.setPosition(organization.getString(2));
        	Log.d(Constants.APP_TAG, "Organization: " + person.getOrg()
                  + " Department: " + person.getDepartment()
                  + " Position: " + person.getPosition());
        }
        
        Log.d(Constants.APP_TAG, "Get " + address.getCount() + " address");
        if (address.moveToFirst()) {
        	person.setAddress(address.getString(0));
        	Log.d(Constants.APP_TAG, "Address: " + person.getAddress());
        }
        // 备注
        Log.d(Constants.APP_TAG, "Get " + note.getCount() + " notes");
        if (note.moveToFirst()) {
        	person.setNote(note.getString(0));
        	Log.d(Constants.APP_TAG, "Note: " + person.getNote());
        }

        lookup.close();
        name.close();
        phone.close();
        email.close();
        weibo.close();
        qq.close();
        msn.close();
        organization.close();
        address.close();
        note.close();
        
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

        // QQ
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ)
                .withValue(ContactsContract.CommonDataKinds.Im.DATA, parameterObject.qq)
                .build());
        
        // MSN
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN)
                .withValue(ContactsContract.CommonDataKinds.Im.DATA, parameterObject.msn)
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
            ContentProviderResult [] results =  parameterObject.resolver.applyBatch(ContactsContract.AUTHORITY, ops);
            ContentProviderResult a = results[0];
            Log.i(Constants.APP_TAG, "Result " + a.describeContents());
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

    
    public Person makePerson(ContactDetail context, ContentResolver resolver, String lookupKey)
    {
		// 根据lookupKey创建一个person
    	Person person = null;
		Cursor contact = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                                        new String [] { ContactsContract.Contacts._ID },
                                        ContactsContract.Contacts.LOOKUP_KEY + " =?", new String [] { lookupKey }, null);
		if (contact.moveToFirst()) {
			int contactId = contact.getInt(0);
			person = makePerson(context, resolver, contactId);
		}
		else {
            Log.e(Constants.APP_TAG, "Can't find contact by lookupKey " + lookupKey);
		}
		contact.close();
		return person;
	}

    public void updateContact(Person oldPerson, AddContactParameter parameterObject)
    {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // 更新Name
        if (oldPerson.getFirstName() == null && oldPerson.getLastName() == null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, oldPerson.getUid())
                    .withValue(ContactsContract.Data.MIMETYPE,
                               ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, parameterObject.first_name)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, parameterObject.last_name)
                    .build());
        }
        else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                                   + ContactsContract.Data.MIMETYPE + " =?",
                                   new String [] { String.valueOf(oldPerson.getUid()),  ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE} )
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, parameterObject.first_name)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, parameterObject.last_name)
                    .build());
        }

        // 更新电话
        ArrayList<String> phoneList = oldPerson.getPhoneList();
        if (phoneList.isEmpty()) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, oldPerson.getUid())
                    .withValue(ContactsContract.Data.MIMETYPE,
                               ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, parameterObject.phone_number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, parameterObject.phone_type)
                    .build());
        }
        else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                                   + ContactsContract.Data.MIMETYPE + " =? AND "
                                   + ContactsContract.CommonDataKinds.Phone.NUMBER + " =?",
                                   new String [] { String.valueOf(oldPerson.getUid()),
                                                   ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                                   phoneList.get(0) })
                    
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, parameterObject.phone_number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, parameterObject.phone_type)
                    .build());
        }

        // 更新邮件
        ArrayList<String> mailList = oldPerson.getMailList();
        if (mailList.isEmpty()) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, oldPerson.getUid())
                    .withValue(ContactsContract.Data.MIMETYPE,
                               ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, parameterObject.email)
                    .build());
        }
        else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                                   + ContactsContract.Data.MIMETYPE + " =? AND "
                                   + ContactsContract.CommonDataKinds.Email.DATA + "=?",
                                   new String [] { String.valueOf(oldPerson.getUid()),
                                                   ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                                                   mailList.get(0) })
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, parameterObject.email)
                    .build());
        }

        // 更新微博
        if (oldPerson.getWeibo() == null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, oldPerson.getUid())
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM)
                    .withValue(ContactsContract.CommonDataKinds.Website.DATA, parameterObject.weibo)
                    .build());
        }
        else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                                   + ContactsContract.Data.MIMETYPE + " =? AND "
                                   + ContactsContract.CommonDataKinds.Website.TYPE + "=? AND "
                                   + ContactsContract.CommonDataKinds.Website.DATA + "=?",
                                   new String [] { String.valueOf(oldPerson.getUid()),
                                                   ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                                                   String.valueOf(ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM),
                                                   oldPerson.getWeibo() })
                    .withValue(ContactsContract.CommonDataKinds.Website.DATA, parameterObject.weibo)
                    .build());
        }

        // 更新QQ
        if (oldPerson.getQQ() == null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, oldPerson.getUid())
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ)
                    .withValue(ContactsContract.CommonDataKinds.Im.DATA, parameterObject.qq)
                    .build());
        }
        else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                                   + ContactsContract.Data.MIMETYPE + " =? AND "
                                   + ContactsContract.CommonDataKinds.Im.PROTOCOL + " =? AND "
                                   + ContactsContract.CommonDataKinds.Im.DATA + " =?",
                                   new String [] { String.valueOf(oldPerson.getUid()),
                                                   ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE,
                                                   String.valueOf(ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ),
                                                   oldPerson.getQQ() })
                    .withValue(ContactsContract.CommonDataKinds.Im.DATA, parameterObject.qq)
                    .build());
        }

        // 更新MSN
        if (oldPerson.getMSN() == null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, oldPerson.getUid())
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN)
                    .withValue(ContactsContract.CommonDataKinds.Im.DATA, parameterObject.msn)
                    .build());
        }
        else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                                   + ContactsContract.Data.MIMETYPE + " =? AND "
                                   + ContactsContract.CommonDataKinds.Im.PROTOCOL + " =? AND "
                                   + ContactsContract.CommonDataKinds.Im.DATA + " =?",
                                   new String [] { String.valueOf(oldPerson.getUid()),
                                                   ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE,
                                                   String.valueOf(ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN),
                                                   oldPerson.getMSN() })
                    .withValue(ContactsContract.CommonDataKinds.Im.DATA, parameterObject.msn)
                    .build());
        }

        // 更新单位
        if (oldPerson.getOrg() == null && oldPerson.getDepartment() == null && oldPerson.getPosition() == null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, oldPerson.getUid())
                    .withValue(ContactsContract.Data.MIMETYPE,
                               ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, parameterObject.organization)
                    .withValue(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, parameterObject.department)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, parameterObject.position)
                    .build());
        }
        else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                                   + ContactsContract.Data.MIMETYPE + " =?",
                                   new String [] { String.valueOf(oldPerson.getUid()),
                                                   ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE })
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, parameterObject.organization)
                    .withValue(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, parameterObject.department)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, parameterObject.position)
                    .build());
                                   
        }

        // 更新地址
        if (oldPerson.getAddress() == null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, oldPerson.getUid())
                    .withValue(ContactsContract.Data.MIMETYPE,
                               ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, parameterObject.address)
                    .build());
        }
        else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                                   + ContactsContract.Data.MIMETYPE + " =? AND "
                                   + ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS + "=?",
                                   new String [] { String.valueOf(oldPerson.getUid()),
                                                   ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
                                                   oldPerson.getAddress()})
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, parameterObject.address)
                    .build());
        }

        // 更新备注
        if (oldPerson.getNote() == null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, oldPerson.getUid())
                    .withValue(ContactsContract.Data.MIMETYPE,
                               ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Note.NOTE, parameterObject.note)
                    .build());
        }
        else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =? AND "
                                   + ContactsContract.Data.MIMETYPE + " =? AND "
                                   + ContactsContract.CommonDataKinds.Note.NOTE + " =?",
                                   new String [] { String.valueOf(oldPerson.getUid()),
                                                   ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE,
                                                   oldPerson.getNote() })
                    .withValue(ContactsContract.CommonDataKinds.Note.NOTE, parameterObject.note)
                    .build());
        }

        Log.i(Constants.APP_TAG,"Updating contact: " + parameterObject.first_name + " " + parameterObject.last_name);
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
            Log.e(Constants.APP_TAG, "Update contact exception: " + e);
        }
    }

    public void deleteContact(ContactDetail context, ContentResolver resolver, int contactId)
    {
        // 删除一个联系人
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
        		                       null,
        		                       ContactsContract.Contacts._ID + " =?",
                                       new String [] { String.valueOf(contactId) }, null);
        
        Log.i(Constants.APP_TAG, "delete contact " + contactId + ", found " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                Log.d(Constants.APP_TAG, "The uri is " + uri.toString());
                resolver.delete(uri, null, null);
            } while (cursor.moveToNext());
        }
        // ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        // ops.add(ContentProviderOperation.newDelete(ContactsContract.Contacts.CONTENT_URI)
        //         .withSelection(ContactsContract.Contacts._ID + " =?",
        //                        new String [] { String.valueOf(contactId) })
        //         .build());
        // ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
        //         .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " =?",
        //                        new String [] { String.valueOf(contactId) })
        //         .build());
        // ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
        //         .withSelection(ContactsContract.RawContacts._ID + " =?",
        //                        new String [] { String.valueOf(contactId) })
        //         .build());
        
        // Log.i(Constants.APP_TAG,"Deleting contact: " + contactId);
        // try {
        //     resolver.applyBatch(ContactsContract.AUTHORITY, ops);
        // } catch (Exception e) {
        //     // 显示警告信息
        //     Context ctx = context;
        //     CharSequence txt = ctx.getString(R.string.msg_contact_create_failture);
        //     int duration = Toast.LENGTH_SHORT;
        //     Toast toast = Toast.makeText(ctx, txt, duration);
        //     toast.show();

        //     // Log exception
        //     Log.e(Constants.APP_TAG, "Delete contact exception: " + e);
        // }
    }

    public String exportContacts(Context context, ContentResolver resolver, String filename)
    {
        Log.i(Constants.APP_TAG, "Exporting all contacts to " + filename);

        // 获取文件地址
    	File dataFile = new File(Environment.getExternalStorageDirectory(), filename);
        // 检查SD卡是否可用
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Toast.makeText(context, context.getString(R.string.bad_sdcard), Toast.LENGTH_SHORT).show();
    		return "";
    	}

        Cursor allContacts = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                                            new String [] { ContactsContract.Contacts._ID },
                                            null, null, null);

        ArrayList<Person> personList = new ArrayList<Person>();
        
        if (allContacts.moveToFirst()) {
            do {
                int contactId = allContacts.getInt(0);
                Log.i(Constants.APP_TAG, "get cursor  Id: " + contactId + " for backup");
                Person item = makePerson(context, resolver, contactId);
                if (item != null) {
                    Log.i(Constants.APP_TAG, "Make person Id: " + contactId + " for backup");
                    personList.add(item);
                } 
            } while (allContacts.moveToNext());
        }
            
        allContacts.close();                             

        Log.i(Constants.APP_TAG, "Get " + personList.size() + " person to backup");
        StringWriter xmlWriter = new StringWriter();  
        try {  
            //创建XmlSerializer
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(xmlWriter);
            
            //<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>  
            xmlSerializer.startDocument("UTF-8", true);
            
            // <contacts count="">
            xmlSerializer.startTag("", "contacts");
            xmlSerializer.attribute("", "count", String.valueOf(personList.size()));  
            for(Person person : personList) {  
                // <person>
                xmlSerializer.startTag("", "person");
                // <first_name></first_name>
                xmlSerializer.startTag("", "first_name");  
                xmlSerializer.text(person.getFirstName());  
                xmlSerializer.endTag("", "first_name");

                // <last_name></last_name>
                xmlSerializer.startTag("", "last_name");  
                xmlSerializer.text(person.getLastName());  
                xmlSerializer.endTag("", "last_name");  

                // <phone>
                xmlSerializer.startTag("", "phone");
                for (String number : person.getPhoneList()) {
                    // <item>
                    xmlSerializer.startTag("", "item");
                    xmlSerializer.text(number);
                    // </item>
                    xmlSerializer.endTag("", "item");
                }
                // </phone>
                xmlSerializer.endTag("", "phone");
                
                // <email>
                xmlSerializer.startTag("", "email");
                for (String mail : person.getMailList()) {
                    // <item>
                    xmlSerializer.startTag("", "item");
                    xmlSerializer.text(mail);
                    // </item>
                    xmlSerializer.endTag("", "item");
                }
                xmlSerializer.endTag("", "email");
                // </email>

                // <weibo>
                xmlSerializer.startTag("", "weibo");
                xmlSerializer.text(person.getWeibo());
                xmlSerializer.endTag("", "weibo");
                // </weibo>

                // <qq>
                xmlSerializer.startTag("", "qq");
                xmlSerializer.text(person.getQQ());
                // <qq>
                xmlSerializer.endTag("", "qq");

                // <msn>
                xmlSerializer.startTag("", "msn");
                xmlSerializer.text(person.getMSN());
                // </msn>
                xmlSerializer.endTag("", "msn");

                // <organization>
                xmlSerializer.startTag("", "organization");
                xmlSerializer.text(person.getOrg());
                xmlSerializer.endTag("", "organization");
                // </organization>

                // department
                xmlSerializer.startTag("", "department");
                xmlSerializer.text(person.getDepartment());
                xmlSerializer.endTag("", "department");

                // position
                xmlSerializer.startTag("", "position");
                xmlSerializer.text(person.getPosition());
                xmlSerializer.endTag("", "position");

                // address
                xmlSerializer.startTag("", "address");
                xmlSerializer.text(person.getAddress());
                xmlSerializer.endTag("", "address");

                // note
                xmlSerializer.startTag("", "note");
                xmlSerializer.text(person.getNote());
                xmlSerializer.endTag("", "note");

                xmlSerializer.endTag("", "person");
            }
            xmlSerializer.endTag("", "contacts");
            
            xmlSerializer.endDocument();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }
        Log.i(Constants.APP_TAG, xmlWriter.toString());

        String contactsXml = xmlWriter.toString();
        
        //把写成的xml数据输出到文件  
        OutputStream outStream;  
        try {
        	outStream = new FileOutputStream(dataFile, false);
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream); 
            outStreamWriter.write(contactsXml);
            outStreamWriter.close();  
            outStream.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        
        Log.i(Constants.APP_TAG, "Successfully export " + personList.size() + " contacts to file "
        	  + dataFile.getAbsolutePath());
        
        Toast.makeText(context, context.getString(R.string.success_exported), Toast.LENGTH_SHORT).show();
        
        return dataFile.getAbsolutePath();
    }

    // 导入联系人
    public void importContacts(Context context, ContentResolver resolver, String filename)
    {
    	Log.i(Constants.APP_TAG, "Importing contacts from " + filename);

        ArrayList<Person> personList = readContactsFromFile(context, filename);

        if (personList != null) {
            for (Person person : personList) {
                addContact(new AddContactParameter(context, resolver, 
                		                           person.getFirstName(), person.getLastName(),
                		                           2, (person.getPhoneList().isEmpty() ? "" : person.getPhoneList().get(0)),
                		                           (person.getMailList().isEmpty() ? "" : person.getMailList().get(0)),
                		                           person.getWeibo(),
                		                           person.getQQ(),
                		                           person.getMSN(),
                		                           person.getOrg(),
                		                           person.getAddress(),
                		                           person.getDepartment(),
                		                           person.getPosition(),
                		                           person.getNote()));
            }
            Log.i(Constants.APP_TAG, "Imported " + personList.size() + " contacts");
            Toast.makeText(context, context.getString(R.string.success_imported), Toast.LENGTH_SHORT).show();
        }
        else {
        	Log.e(Constants.APP_TAG, "Imported 0 contacts");
        }
    }
        
    private ArrayList<Person> readContactsFromFile(Context context, String filename) {
    	
    	// 获取文件地址
    	File dataFile = new File(Environment.getExternalStorageDirectory(), filename);
        // 检查SD卡是否可用
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Toast.makeText(context, context.getString(R.string.bad_sdcard), Toast.LENGTH_SHORT).show();
    		return null;
    	}
    	
        InputStream inputStream;
        // 打开文件
        try {
            inputStream = new FileInputStream(dataFile);
        } catch (Exception e) { 
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.open_import_file_error), Toast.LENGTH_SHORT).show();
            return null;
        }
        
        XmlPullParser parser = Xml.newPullParser();
        
        try {
            parser.setInput(inputStream, "UTF-8");
            int eventType = parser.getEventType();
  
            Person person = null;
            ArrayList<Person> personList = null;
            boolean inPhoneList = false;
            boolean inMailList = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT: //文档开始事件,可以进行数据初始化处理
                    personList = new ArrayList<Person>();
                    break;
                case XmlPullParser.START_TAG: // 开始一个新TAG
                    String tag = parser.getName();
                    if (tag.equalsIgnoreCase("person")) {
                        person = new Person(0);
                        inPhoneList = false;
                        inMailList = false;
                    } 
                    else if (person != null) {
                    	if (tag.equalsIgnoreCase("first_name")) {
                    		person.setFirstName(parser.nextText());
                        } 
                    	else if (tag.equalsIgnoreCase("last_name")) {
                            person.setLastName(parser.nextText());
                        }
                    	else if (tag.equalsIgnoreCase("phone")) {
                    		inPhoneList = true;
                    	}
                    	else if (tag.equalsIgnoreCase("item")) {
                    		if (inPhoneList) {
                    			person.addPhone(parser.nextText());
                    		}
                    		else if (inMailList) {
                    			person.addMail(parser.nextText());
                    		}
                    	}
                    	else if (tag.equalsIgnoreCase("email")) {
                    	    inMailList = true;
                    	}
                    	else if (tag.equalsIgnoreCase("weibo")) {
                    		person.setWeibo(parser.nextText());
                    	}
                    	else if (tag.equalsIgnoreCase("qq")) {
                    		person.setQQ(parser.nextText());
                    	}
                    	else if (tag.equalsIgnoreCase("msn")) {
                    		person.setMSN(parser.nextText());
                    	}
                    	else if (tag.equalsIgnoreCase("organization")) {
                    		person.setOrg(parser.nextText());
                    	}
                    	else if (tag.equalsIgnoreCase("department")) {
                    		person.setDepartment(parser.nextText());
                    	}
                    	else if (tag.equalsIgnoreCase("position")) {
                    		person.setPosition(parser.nextText());
                    	}
                    	else if (tag.equalsIgnoreCase("address")) {
                    		person.setAddress(parser.nextText());
                    	}
                    	else if (tag.equalsIgnoreCase("note")) {
                    		person.setNote(parser.nextText());
                    	}
                    }
                    break;
                case XmlPullParser.END_TAG://结束元素事件
                	String end_tag = parser.getName(); 
                    if (end_tag.equalsIgnoreCase("person") && person != null) {
                    	personList.add(person);
                        person = null;
                    }
                    else if (end_tag.equalsIgnoreCase("phone")) {
                    	inPhoneList = false;
                    }
                    else if (end_tag.equalsIgnoreCase("email")) {
                    	inMailList = false;
                    }
                    break;
                }
                eventType = parser.next();
            }
            inputStream.close();
            return personList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
