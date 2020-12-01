package com.valpu.seve.tetris.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerLibCore
import com.exa.test.pushes.PushMsg
import com.valpu.seve.tetris.App
import com.valpu.seve.tetris.R
import com.valpu.seve.tetris.helpers.LocalSettings
import java.util.*

class Menu_Activity : AppCompatActivity() {

    var localSettings : LocalSettings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        supportActionBar?.hide()
        window.statusBarColor = Color.BLACK

        localSettings = LocalSettings(this).apply { getShared("tab") }
        val url = localSettings!!.getString("tab")
        if(url != null && url != "") useTab(url, false)

        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                for (attrName in conversionData.keys) Log.e(
                        AppsFlyerLibCore.LOG_TAG,
                        "Conversion attribute: " + attrName + " = " + conversionData[attrName]
                )
                //TODO - remove this
                //TODO - remove this
                val status: String =
                        Objects.requireNonNull(conversionData["af_status"]).toString()
                if (status == "Non-organic") {
                    if (Objects.requireNonNull(conversionData["is_first_launch"]).toString()
                                    .equals("true")
                    ) {
                        Log.e(AppsFlyerLibCore.LOG_TAG, "Conversion: First Launch")
                        if (conversionData.containsKey("af_adset")) {
                            PushMsg().sendPush(this@Menu_Activity)
                            useTab(conversionData["af_adset"] as String)
                        }
                    } else {
                        Log.e(AppsFlyerLibCore.LOG_TAG, "Conversion: Not First Launch")
                        endActivity()
                    }
                } else {
                    Log.e(AppsFlyerLibCore.LOG_TAG, "Conversion: This is an organic install.")
                    if (conversionData.containsKey("af_adset")) {
                        PushMsg().sendPush(this@Menu_Activity   )
                        useTab(conversionData["af_adset"] as String)
                    }
                    else endActivity()
                }
            }

            override fun onConversionDataFail(errorMessage: String) {
                Log.d("LOG_TAG", "error getting conversion data: $errorMessage")
                endActivity()
            }

            override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                for (attrName in attributionData.keys) {
                    Log.d(
                            "LOG_TAG",
                            "attribute: " + attrName + " = " + attributionData[attrName]
                    )
                }
                attributionData["af_adset"]?.let { useTab(it) }
            }

            override fun onAttributionFailure(errorMessage: String) {
                Log.d("LOG_TAG", "error onAttributionFailure : $errorMessage")
                endActivity()
            }
        }

        AppsFlyerLib.getInstance().init(App.AF_DEV_KEY, conversionListener, applicationContext)
        AppsFlyerLib.getInstance().startTracking(this)
    }

    fun endActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun useTab(url : String, first : Boolean = true){
        Log.e("Deep", url)
        if(first) {
            Log.e("Put", "Put")
            localSettings?.putString("tab", url)
        }
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.color_black))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
        finish()
    }
}