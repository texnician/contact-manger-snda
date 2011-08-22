package com.snda.ContactManager;

import android.content.ContentResolver;
import android.content.Context;

public class AddContactParameter {
	public Context context;
	public ContentResolver resolver;
	public String first_name;
	public String last_name;
	public int phone_type;
	public String phone_number;
	public String email;
	public String weibo;
	public String qq;
	public String msn;
	public String organization;
	public String address;
	public String department;
	public String position;
	public String note;

	public AddContactParameter(Context context, ContentResolver resolver,
			String first_name, String last_name, int phone_type,
			String phone_number, String email, String weibo, String qq,
			String msn, String organization, String address, String department,
			String position, String note) {
		this.context = context;
		this.resolver = resolver;
		this.first_name = first_name;
		this.last_name = last_name;
		this.phone_type = phone_type;
		this.phone_number = phone_number;
		this.email = email;
		this.weibo = weibo;
		this.qq = qq;
		this.msn = msn;
		this.organization = organization;
		this.address = address;
		this.department = department;
		this.position = position;
		this.note = note;
	}
}