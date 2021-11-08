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
import android.view.*
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.geekvvv.contactbuilder.data.Contacts
import com.geekvvv.contactbuilder.utils.StatusBarUtils
import com.geekvvv.contactbuilder.utils.dp
import com.permissionx.guolindev.PermissionX
import kotlin.random.Random
import kotlin.text.StringBuilder

class MainActivity : AppCompatActivity() {

    private val familyName =
        "陈林黄张李王吴刘蔡杨许郑谢洪郭邱曾廖赖徐周叶苏庄吕江何萧罗高周叶苏庄吕江何萧罗高潘简朱锺彭游詹胡施沈余卢梁赵颜柯翁魏孙戴范方宋邓杜傅侯曹薛丁卓马阮董唐温蓝蒋石古纪姚连冯欧程汤黄田康姜汪白邹尤巫钟黎涂龚严韩袁金童陆夏柳凃邵"
    private val girlName =
        "嘉琼桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊勤珍贞莉兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳"
    private val boyName =
        "辰士以建家致树炎德行时泰盛雄琛钧冠策腾伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发成康星光天达安岩中茂武新利清飞彬富顺信子杰楠榕风航弘"


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusBar(this, true, Color.WHITE, translucent = true)
        setContentView(R.layout.activity_main)
        val actionButton = findViewById<TextView>(R.id.action_button)
        val infoText = findViewById<TextView>(R.id.info_text)
        actionButton.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(0, 0, view?.width!!, view.height, 16.dp)
            }

        }
        infoText.text = "批量生成的手机号都为10位\n\n" +
                "待完成功能：\n" +
                "批量生成亲戚称呼\n" +
                "批量生成领导/老师/长辈称呼\n"
        actionButton.clipToOutline = true
        val editText = findViewById<EditText>(R.id.edit_text)

        actionButton.setOnClickListener {
            val text = editText.text
            if (text.isNullOrEmpty() || text.toString().toInt() <= 0) {
                Toast.makeText(this, "请输入大于0的整数", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            PermissionX.init(this).permissions(Manifest.permission.WRITE_CONTACTS)
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        createList(createRawData(text.toString().toInt()))
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
                    data = Uri.parse("https://blog.lagee.gay/about/")
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
                    "${familyName[Random.nextInt(0, familyName.length)]}${createName(index)}",
                    "${getPhoneRandom()}"
                )
            )
        }
        return contacts
    }

    private fun createList(contacts: List<Contacts>) {
        for (i in contacts.indices) {
            addContact(contacts[i])
            if (i == contacts.size - 1) {
                Toast.makeText(this, "生成联系人完成，请到通讯录查看（结果可能存在短暂延迟）", Toast.LENGTH_LONG).show()
            }
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


    private fun createName(index: Int): String {
        val builder = StringBuilder()
        val random = (0..10).random()
        val isGirl = index % 2 == 0  //随机切换性别
        appendName(isGirl, builder)
        if (random > 5) {       //随机切换两字/三字姓名

            appendName(isGirl, builder)
        }
        return builder.toString()
    }

    private fun appendName(isGirl: Boolean, builder: StringBuilder) {
        when {
            isGirl -> {
                builder.append(girlName[Random.nextInt(0, girlName.length)].toString())

            }
            else -> {
                builder.append(boyName[Random.nextInt(0, boyName.length)].toString())

            }
        }
    }


    private fun getPhoneRandom() = (1000000000..1999999999).random()
}