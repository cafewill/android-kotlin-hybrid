package com.demo.kotlin.hybrid

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.demo.java.hybrid.AlloGesture
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity (), AlloGesture.Listener {

    var web: WebView? = null

    override fun onSaveInstanceState (outState: Bundle) {
        Allo.i ("onSaveInstanceState $javaClass")
        super.onSaveInstanceState (outState)
    }

    override fun onRestoreInstanceState (savedInstanceState: Bundle) {
        Allo.i ("onRestoreInstanceState $javaClass")
        super.onRestoreInstanceState (savedInstanceState)
    }

    override fun onCreate (savedInstanceState: Bundle?) {
        Allo.i ("onCreate $javaClass")
        super.onCreate (savedInstanceState)
        
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
            window.requestFeature(Window.FEATURE_NO_TITLE)
            window.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            setContentView (R.layout.activity_main)

            web = findViewById <View> (R.id.web) as WebView
            // web.setWebContentsDebuggingEnabled (true); // 디버깅 (chrome://inspect)
            web!!.webViewClient = WebClient ()
            web!!.webChromeClient = ChromeClient ()
            web!!.settings.javaScriptEnabled = true
            web!!.settings.defaultTextEncodingName = Allo.CUBE_CHARSET
            web!!.settings.setSupportZoom (true)
            web!!.settings.useWideViewPort = true
            web!!.settings.databaseEnabled = true
            web!!.settings.domStorageEnabled = true
            web!!.settings.builtInZoomControls = false
            web!!.settings.loadWithOverviewMode = true
            web!!.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            web!!.settings.pluginState = WebSettings.PluginState.ON
            web!!.settings.allowFileAccess = true
            web!!.settings.allowContentAccess = true
            web!!.settings.allowFileAccessFromFileURLs = true
            web!!.settings.allowUniversalAccessFromFileURLs = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) // Android 5.0 Lollipop (API 21)
            {
                web!!.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            web!!.settings.setGeolocationEnabled (true)
            web!!.isLongClickable = false // long press disabled (이미지 다운로드 방지용)
            web!!.requestFocus (View.FOCUS_DOWN)
            web!!.setOnTouchListener (AlloGesture (this))
            if (null != savedInstanceState) web!!.restoreState (savedInstanceState)

            loadSite ()
            // rotateFirebase ()
            // enableNotificationPermission ()
        } catch (e: Exception) { e.printStackTrace () }
    }

    public override fun onStart () {
        Allo.i ("onStart $javaClass")
        super.onStart ()
    }

    public override fun onRestart () {
        Allo.i ("onRestart $javaClass")
        super.onRestart ()
    }

    override fun onResume () {
        Allo.i ("onResume $javaClass")
        super.onResume ()

        try {
            rotateNotification ()
        } catch (e: Exception) { e.printStackTrace () }
    }

    public override fun onPause () {
        Allo.i ("onPause $javaClass")
        super.onPause ()
    }

    public override fun onStop () {
        Allo.i ("onStop $javaClass")
        super.onStop ()
    }

    public override fun onDestroy () {
        Allo.i ("onDestroy $javaClass")
        super.onDestroy ()
    }

    override fun onNewIntent (intent: Intent) {
        Allo.i ("onNewIntent $javaClass")
        super.onNewIntent (intent)

        try {
            setIntent (intent)
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun enableNotificationPermission () {
        Allo.i ("enableNotificationPermission $javaClass")
        
        try {
            var enabled = false
            val managedPackages = NotificationManagerCompat.getEnabledListenerPackages (this)
            val packageName = packageName
            for (managedPackage in managedPackages) {
                if (packageName == managedPackage) {
                    enabled = true
                    break
                }
            }
            if (!enabled) {
                startActivity (Intent ("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            }
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun rotateNotification () {
        Allo.i ("rotateNotification $javaClass")

        try {
            val params = intent
            if (null != params) {
                if (null != params.getStringExtra (Allo.CUBE_LINK)) {
                    val link = params.getStringExtra (Allo.CUBE_LINK)
                    Allo.i ("Check link [$link]")
                    try {
                        URL (link) // 유효하지 않은 경우엔 오류서 스킵함
                        startActivity (Intent(Intent.ACTION_VIEW).setData (Uri.parse (link)))
                    } catch (x: Exception) { x.printStackTrace () }
                    // 푸시 알림 패러미터 재실행 방지를 위해 데이터 삭제요
                    // 예 : (데이터 삭제를 안하면) 띄워진 외부 링크 확인후 앱으로 넘어오면 다시 외부 링크를 띄움 (무한 반복)
                    params.removeExtra (Allo.CUBE_LINK)
                }
            }
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun rotateFirebase () {
        Allo.i ("rotateFirebase $javaClass")

        try {
            FirebaseMessaging.getInstance ().token
                .addOnCompleteListener (OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Allo.i ("getInstanceId failed " + task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result
                    Handler (Looper.getMainLooper ()).postDelayed ({ rotateToken (token) }, 100)
                })
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun rotateToken (token: String) {
        Allo.i ("rotateToken $javaClass")

        try {
            registDevice (token)
            val sharedPreferences = getPreferences (MODE_PRIVATE)
            val editor = sharedPreferences.edit ()
            editor.putString (Allo.CUBE_TOKEN, token)
            editor.commit ()
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun registDevice (token: String) {
        Allo.i ("registDevice $javaClass")

        try {
            object : Thread() {
                override fun run () {
                    // 필요시 로컬 및 리모트 서버 연동하여 저장함
                    Allo.i ("Check token [$token]")
                }
            }.start ()
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun loadSite () {
        Allo.i ("loadSite $javaClass")
        try {
            loadLink (Allo.CUBE_SITE)
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun loadLink (link: String) {
        Allo.i ("loadLink [$link] $javaClass")
        try {
            web!!.loadUrl (link)
        } catch (e: Exception) { e.printStackTrace () }
    }

    override fun onGesture (type: Int) {
        Allo.i ("onGesture [$type] $javaClass")
        
        try {
            when (type) {
                AlloGesture.SINGLE_SWIPE_LEFT -> actionNext ()
                AlloGesture.SINGLE_SWIPE_RIGHT -> actionPrev ()
            }
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun actionPrev() {
        Allo.i ("actionPrev [" + web!!.url + "] " + javaClass)
        try {
            if (web!!.isFocused && web!!.canGoBack ()) {
                web!!.goBack ()
            } else {
                val currentUrl = web!!.url
                if (!Allo.CUBE_SITE.equals (currentUrl)) loadSite ()
            }
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun actionNext() {
        Allo.i ("actionNext [" + web!!.url + "] " + javaClass)
        try {
            if (web!!.isFocused && web!!.canGoForward ()) web!!.goForward ()
        } catch (e: Exception) { e.printStackTrace () }
    }

    private inner class WebClient : WebViewClient () {
        override fun onPageStarted (view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted (view, url, favicon)
            Allo.i ("onPageStarted [$url] $javaClass")

            try {
                showIndicator ()
            } catch (e: Exception) { e.printStackTrace () }
        }

        override fun onPageFinished (view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Allo.i ("onPageFinished [$url] $javaClass")

            try {
                // hideIndicator ();
            } catch (e: Exception) { e.printStackTrace () }
        }

        override fun shouldOverrideUrlLoading (view: WebView?, url: String?): Boolean {
            Allo.i ("shouldOverrideUrlLoading [$url] $javaClass")

            return super.shouldOverrideUrlLoading (view, url)
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError (
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError (view, request, error)
            val uri = request.url
            handleError(view, error.errorCode, error.description.toString(), uri)
        }

        override fun onReceivedError (
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            super.onReceivedError (view, errorCode, description, failingUrl)
            val uri = Uri.parse(failingUrl)
            handleError(view, errorCode, description, uri)
        }

        private fun handleError (view: WebView, errorCode: Int, description: String, uri: Uri) {
            Allo.i ("handleError [$errorCode][$description] $javaClass")
        }
    }

    private inner class ChromeClient : WebChromeClient () {
        override fun onShowFileChooser (
            webView: WebView,
            filePathCallback: ValueCallback <Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            Allo.i ("onShowFileChooser [" + Arrays.toString(fileChooserParams.acceptTypes) + "] @" + this.javaClass)
            return true
        }
    }

    private fun showIndicator () {
        Allo.i ("showIndicator $javaClass")
        try {
            val progress = findViewById <View> (R.id.indicator) as ProgressBar
            if (!Objects.isNull (progress)) progress.visibility = View.VISIBLE
            Handler (Looper.getMainLooper ()).postDelayed ({ hideIndicator () }, 1500)
        } catch (e: Exception) { e.printStackTrace () }
    }

    private fun hideIndicator () {
        Allo.i ("hideIndicator $javaClass")
        try {
            val progress = findViewById <View> (R.id.indicator) as ProgressBar
            if (!Objects.isNull (progress)) progress.visibility = View.INVISIBLE
        } catch (e: Exception) { e.printStackTrace () }
    }
}