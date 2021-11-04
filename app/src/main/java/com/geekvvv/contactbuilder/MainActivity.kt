package com.geekvvv.contactbuilder

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.content.ContentUris
import android.content.ContentValues
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Data
import androidx.appcompat.app.AppCompatActivity
import com.geekvvv.contactbuilder.data.Contacts

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createList(createRawData())
    }

    private fun createRawData() :List<Contacts>{
        val contacts = arrayListOf<Contacts>()
        for (index in 1..10){
            contacts.add(Contacts("张三$index","1888888888${index-1}"))
        }
        return contacts
    }

    private fun createList(contacts:List<Contacts>){
        contacts.forEach {
            addContact(it)
        }
    }

    private fun addContact(contacts: Contacts) {
        val phone = contacts.phone

        // 创建一个空的ContentValues
        val values = ContentValues()
        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        val rawContactUri = contentResolver.insert(RawContacts.CONTENT_URI, values)
        val rawContactId = rawContactUri?.let { ContentUris.parseId(it) }
        values.clear()

        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId)
        // 内容类型
        values.put(ContactsContract.Contacts.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
        // 联系人名字
        values.put(StructuredName.GIVEN_NAME, contacts.name)
        // 向联系人URI添加联系人名字
        contentResolver.insert(Data.CONTENT_URI, values)
        values.clear()

        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId)
        values.put(ContactsContract.Contacts.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
        // 联系人的电话号码
        values.put(Phone.NUMBER, phone)
        // 电话类型
        values.put(Phone.TYPE, Phone.TYPE_MOBILE)
        // 向联系人电话号码URI添加电话号码
        contentResolver.insert(Data.CONTENT_URI, values)
        values.clear()
    }
}