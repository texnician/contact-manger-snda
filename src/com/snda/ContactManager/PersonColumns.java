/**
 * Copyright (C)2011, Shanda Corporation. All rights reserved.
 *
 * @file: PersonColumns.java
 *
 * @author: tangyaguang@snda.com 018386
 *
 * @date: 2011-08-22 01:14:46
 *
 * @doc: Person Columns��Ϣ
 */

package com.snda.ContactManager;

import android.provider.ContactsContract.Data;

public interface PersonColumns
{
    /**
     * �洢�Զ�����Ϣ��MIME-type
     */
    public static final String SNDA_PERSON_TYPE =
        "vnd.android.cursor.item/vnd.snda.contactmanager.person";

    // ΢��
    public static final String WEIBO = Data.DATA1;
    // ����
    public static final String DEPARTMENT = Data.DATA2;
    // ְ��
    public static final String POSITION = Data.DATA3;
}
