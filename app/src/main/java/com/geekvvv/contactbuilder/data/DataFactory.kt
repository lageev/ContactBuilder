package com.geekvvv.contactbuilder.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import com.geekvvv.contactbuilder.MainActivity
import com.geekvvv.contactbuilder.data.DataFactory.relativeTitles
import kotlin.random.Random

object DataFactory {

    const val familyName =
        "陈林黄张李王吴刘蔡杨许郑谢洪郭邱曾廖赖徐周叶苏庄吕江何萧罗高周叶苏庄吕江何萧罗高潘简朱锺彭游詹胡施沈余卢梁赵颜柯翁魏孙戴范方宋邓杜傅侯曹薛丁卓马阮董唐温蓝蒋石古纪姚连冯欧程汤黄田康姜汪白邹尤巫钟黎涂龚严韩袁金童陆夏柳凃邵"
    private const val girlName =
        "嘉琼桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊勤珍贞莉兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳"
    private const val boyName =
        "辰士以建家致树炎德行时泰盛雄琛钧冠策腾伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发成康星光天达安岩中茂武新利清飞彬富顺信子杰楠榕风航弘"

    private val relativeTitles = arrayOf(
        "爸爸",
        "妈妈",
        "爷爷",
        "奶奶",
        "叔",
        "伯伯",
        "伯母",
        "大爷",
        "大娘",
        "哥哥",
        "姐姐",
        "弟弟",
        "妹妹",
        "姑姑",
        "姑父",
        "舅舅",
        "舅妈",
        "表哥",
        "表姐",
        "婶子",
        "姨",
        "姨夫",
        "堂姐",
        "堂哥"
    )

    private val workRelativeTitles = arrayOf("总", "主任", "经理", "工")

    private val normalRelativeTitles = arrayOf("爷爷", "奶奶", "叔叔", "阿姨")

    fun createBestRelativeData(): List<Contacts> {
        val contacts = arrayListOf<Contacts>()
        for (index in 1 until relativeTitles.size) {
            contacts.add(
                Contacts(
                    relativeTitles[index],
                    "${getRandomPhoneNumber()}"
                )
            )
        }
        return contacts
    }

    fun createWorkRelativeData(): List<Contacts> {
        val contacts = arrayListOf<Contacts>()
        for (index in 1 until workRelativeTitles.size) {
            contacts.add(
                Contacts(
                    "${familyName[Random.nextInt(0, familyName.length)]}${workRelativeTitles[index]}",
                    "${getRandomPhoneNumber()}"
                )
            )
        }
        return contacts
    }

    fun createOtherRelativeData(): List<Contacts> {
        val contacts = arrayListOf<Contacts>()
        for (index in 1 until normalRelativeTitles.size) {
            contacts.add(
                Contacts(
                    "${familyName[Random.nextInt(0, familyName.length)]}${normalRelativeTitles[index]}",
                    "${getRandomPhoneNumber()}"
                )
            )
        }
        return contacts
    }

    fun addContact(contacts: Contacts, context: Context) {
        val phone = contacts.phone

        // 创建一个空的ContentValues
        val values = ContentValues()
        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        val rawContactUri =
            context.contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values)
        val rawContactId = rawContactUri?.let { ContentUris.parseId(it) }
        values.clear()

        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId)
        // 内容类型
        values.put(
            ContactsContract.Contacts.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
        )
        // 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contacts.name)
        // 向联系人URI添加联系人名字
        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
        values.clear()

        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId)
        values.put(
            ContactsContract.Contacts.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )
        // 联系人的电话号码
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
        // 电话类型
        values.put(
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
        )
        // 向联系人电话号码URI添加电话号码
        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
        values.clear()
    }

    fun getRandomPhoneNumber(): Int {
        return (1000000000..1999999999).random()
    }

    fun createName(index: Int): String {
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
}

