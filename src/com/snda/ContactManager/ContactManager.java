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
     *
     * @param first_name a <code>String</code> value
     * @param last_name a <code>String</code> value
     * @param phone_type an <code>int</code> value
     * @param phone_number a <code>String</code> value
     * @param email a <code>String</code> value
     * @param weibo a <code>String</code> value
     * @param qq a <code>String</code> value
     * @param msn a <code>String</code> value
     * @param organization a <code>String</code> value
     * @param address a <code>String</code> value
     * @param department a <code>String</code> value
     * @param position a <code>String</code> value
     * @param note a <code>String</code> value
     */
    public void addContact(String first_name, String last_name, int phone_type,
			String phone_number, String email, String weibo, String qq,
			String msn, String organization, String address, String department,
			String position, String note)
    {

		
	}
}
