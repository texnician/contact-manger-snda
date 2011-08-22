/**
 * Copyright (C)2011, Shanda Corporation. All rights reserved.
 *
 * @file: PersonColumns.java
 *
 * @author: tangyaguang@snda.com 018386
 *
 * @date: 2011-08-22 01:14:46
 *
 * @doc: Person Columns信息
 */

package com.snda.ContactManager;

import android.provider.ContactsContract.Data;

public interface PersonColumns
{
    /**
     * 存储自定义信息的MIME-type
     */
    public static final String SNDA_PERSON_TYPE =
        "vnd.android.cursor.item/vnd.snda.contactmanager.person";

    // 微博
    public static final String WEIBO = Data.DATA1;
    // 部门
    public static final String DEPARTMENT = Data.DATA2;
    // 职务
    public static final String POSITION = Data.DATA3;
}
