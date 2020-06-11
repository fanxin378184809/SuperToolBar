package com.fanxin.suppertoolbardemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fanxin.supertoobar.Action
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var imgAction = com.fanxin.supertoobar.ImageAction(supertoolbar,R.mipmap.ic_launcher).set()


        supertoolbar.addLeftAction(imgAction)
        supertoolbar.addLeftAction(com.fanxin.supertoobar.TextAction(supertoolbar,"测试测试").set())

        var imgAction2 = com.fanxin.supertoobar.ImageAction(supertoolbar,R.mipmap.ic_launcher).set()
        imgAction2.setNotification(10)

        supertoolbar.addRightAction(imgAction2)
        supertoolbar.addRightAction(com.fanxin.supertoobar.TextAction(supertoolbar,"测试测试").set())
        supertoolbar.setOnActionClickListener(object:com.fanxin.supertoobar.Action.OnActionClick{
            override fun onActionClick(index: Int, action: com.fanxin.supertoobar.Action) {
                Toast.makeText(this@MainActivity,"点击了===${index}",Toast.LENGTH_SHORT).show()
            }
        })

        supertoolbar.setOnActionClickListener(object : com.fanxin.supertoobar.Action.OnActionLeftClick{
            override fun onActionClick(index: Int, action: Action) {
                Toast.makeText(this@MainActivity,"左边点击了===${index}",Toast.LENGTH_SHORT).show()
            }

        })

    }
}
