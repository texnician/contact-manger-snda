/**
 * Copyright (C)2011, Shanda Corporation. All rights reserved.
 *
 * @file: Person.java
 *
 * @author: tangyaguang@snda.com 018386
 *
 * @date: 2011-08-21 16:51:13
 *
 * @doc: 联系人
 */

package com.snda.ContactManager;

import java.util.ArrayList;
import java.lang.String;

public class Person {
    // 名
    private String firstName;
    // 姓
    private String lastName;
    // 电话
    private ArrayList<String> phoneList;
    // 邮件
    private ArrayList<String> mailList;
    // 新浪围脖帐号
    private String weibo;
    // Qq号
    private String qq;
    // msn号码
    private String msn;
    // 单位
    private String organization;
    // 地址
    private String address;
    // 部门
    private String department;
    // 职务
    private String position;
    // 备注
    private String note;
    // 唯一的id
	private Integer uid;

    public Person(Integer id) {
		setUid(id);
	}

	public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String val)
    {
        firstName = val;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String val)
    {
    	lastName = val;
    }

    public ArrayList<String> getPhoneList()
    {
        return phoneList;
    }

    public void addPhone(String val)
    {
        phoneList.add(val);
    }

    public ArrayList<String> getMailList()
    {
        return mailList;
    }

    public void addMail(String val)
    {
        mailList.add(val);
    }

    public String getWeibo()
    {
        return weibo;
    }

    public void setWeibo(String val)
    {
        weibo = val;
    }

    public String getQQ()
    {
        return qq;
    }

    public void setQQ(String val)
    {
        qq = val;
    }

    public String getMSN()
    {
        return msn;
    }

    public void setMSN(String val)
    {
        msn = val;
    }

    public String getOrg()
    {
        return organization;
    }

    public void setOrg(String val)
    {
        organization = val;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String val)
    {
        address = val;
    }
    
    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String val)
    {
        department  = val;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String val)
    {
        position = val;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String val)
    {
        note = val;
    }

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
}
