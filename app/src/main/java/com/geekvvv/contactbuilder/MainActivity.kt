package com.geekvvv.contactbuilder

import android.Manifest
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.Outline
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Data
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.*
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.geekvvv.contactbuilder.data.Contacts
import com.geekvvv.contactbuilder.data.DataFactory
import com.geekvvv.contactbuilder.data.DataFactory.familyName
import com.geekvvv.contactbuilder.utils.StatusBarUtils
import com.geekvvv.contactbuilder.utils.dp
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ui.MoreActionDialog
import kotlin.random.Random
import kotlin.text.StringBuilder

class MainActivity : AppCompatActivity() {

    private var isCreating = false
    private lateinit var actionButton: TextView
    private lateinit var moreActionButton: TextView
    var setting = "111"

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusBar(this, true, Color.WHITE, translucent = true)
        setContentView(R.layout.activity_main)
        actionButton = findViewById(R.id.action_button)
        moreActionButton = findViewById(R.id.more_action_button)
        val infoText = findViewById<TextView>(R.id.info_text)
        val footInfo = findViewById<TextView>(R.id.foot_info)
        actionButton.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(0, 0, view?.width!!, view.height, 28.dp)
            }

        }
        moreActionButton.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(0, 0, view?.width!!, view.height, 28.dp)
            }

        }
        infoText.text = "批量生成的手机号都为10位\n\n" +
                "务必在系统分身或备用机上测试!!!"
        actionButton.clipToOutline = true
        moreActionButton.clipToOutline = true

        footInfo.apply {
            text = Html.fromHtml("仅供娱乐  by <a href=\"https://blog.lagee.gay/about\">酷安小黄</a>", 0)
            movementMethod = LinkMovementMethod.getInstance()
        }

        val editText = findViewById<EditText>(R.id.edit_text)

        moreActionButton.setOnClickListener {
            val dialog = MoreActionDialog()
            dialog.show(supportFragmentManager, null)
        }

        actionButton.setOnClickListener {
            actionButton.text = "生成中..."
            val text = editText.text
            if (text.isNullOrEmpty() || text.toString().toInt() <= 0) {
                Toast.makeText(this, "请输入大于0的整数", Toast.LENGTH_SHORT).show()
                GlobalScope.launch(Dispatchers.Main) {
                    actionButton.text = "完成"
                }
                return@setOnClickListener
            }
            PermissionX.init(this).permissions(Manifest.permission.WRITE_CONTACTS)
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        GlobalScope.launch(Dispatchers.IO) {
                            createList(
                                createRawData(
                                    text.toString().toInt()
                                ) as MutableList<Contacts>
                            )
                        }
                    }
                }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                Intent().apply {
                    action = "android.intent.action.VIEW"
                    data = Uri.parse("https://blog.lagee.gay/about")
                    startActivity(this)
                }
            }
        }
        return true
    }

    private fun createRawData(size: Int): List<Contacts> {
        val contacts = arrayListOf<Contacts>()
        for (index in 1..size) {
            contacts.add(
                Contacts(
                    "${familyName[Random.nextInt(0, familyName.length)]}${
                        DataFactory.createName(index)
                    }",
                    "${DataFactory.getRandomPhoneNumber()}"
                )
            )
        }
        return contacts
    }

    private fun createList(contacts: MutableList<Contacts>) {
        if (isCreating) {
            return
        }
        isCreating = true
        val isOtherRelation = setting[2].toString() == "1"
        val isWorkRelation = setting[1].toString() == "1"
        val isBestRelation = setting[0].toString() == "1"
        if (isBestRelation) {
            contacts.addAll(DataFactory.createBestRelativeData())
        }
        if (isWorkRelation) {
            contacts.addAll(DataFactory.createWorkRelativeData())
        }
        if (isOtherRelation) {
            contacts.addAll(DataFactory.createOtherRelativeData())
        }

        for (i in contacts.indices) {
            DataFactory.addContact(contacts[i], this)
            if (i == contacts.size - 1) {
                isCreating = false
                val activity = this
                GlobalScope.launch(Dispatchers.Main) {
                    actionButton.text = "完成"
                    Toast.makeText(activity, "生成联系人完成，请到通讯录查看（结果可能存在短暂延迟）", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}