package com.za.caretogether.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.za.caretogether.BuildConfig

import com.za.caretogether.R
import com.za.caretogether.utils.Storage
import com.za.caretogether.utils.ViewUtil
import com.za.caretogether.utils.isInternetAvailable
import com.za.caretogether.utils.progressdialog
import kotlinx.android.synthetic.main.fragment_noti2.*
import java.util.*


class Noti2Fragment : Fragment() {
    val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_noti2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var local = Storage.make(context).locale
        val config =
            context!!.resources.configuration
        val locale = Locale(local)
        Locale.setDefault(locale)
        config.locale = locale
        context!!.resources.updateConfiguration(
            config,
            context!!.resources.displayMetrics
        )

        if(context!!.isInternetAvailable()){

            require_news_internet.visibility = View.GONE
            webview_news.visibility = View.VISIBLE

            initWebView()
            setWebClient()
            handlePullToRefresh()
            var locale = Storage.make(context).locale
            loadUrl("https://ct.zacompany.dev/webview/news/texts"+"?language="+locale+"&v="+ BuildConfig.VERSION_NAME)

        }else {

            require_news_internet.visibility = View.VISIBLE
            webview_news.visibility = View.GONE

        }



        webview_news.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webview_news.canGoBack()) {
                    webview_news.goBack()
                    return@OnKeyListener true
                }
            }
            false
        })

    }

    private fun handlePullToRefresh() {
    }



    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webview_news.settings.javaScriptEnabled = true
        webview_news.settings.loadWithOverviewMode = true
        webview_news.settings.useWideViewPort = true
        webview_news.settings.domStorageEnabled = true
        webview_news.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                if (url.toString().startsWith("tel:")) {

                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context!! as Activity,
                            arrayOf(
                                Manifest.permission.CALL_PHONE
                            ),
                            100
                        )
                    } else {

                        var intent = Intent(
                            Intent.ACTION_CALL,
                            Uri.parse(url.toString())
                        )

                        startActivity(intent)
                    }

                    return true

                }
                else  if (!url.toString().startsWith("https:ct.zacompany.dev")) {

                    var intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )

                    startActivity(intent)

                    return true

                }
                else return false
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request!!.url.toString().startsWith("tel:")) {

                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context!! as Activity,
                            arrayOf(
                                Manifest.permission.CALL_PHONE
                            ),
                            100
                        )
                    } else {

                        var intent = Intent(
                            Intent.ACTION_CALL,
                            Uri.parse(request!!.url.toString())
                        )

                        startActivity(intent)
                    }

                    return true

                }
                else  if (!request!!.url.toString().startsWith("https://ct.zacompany.dev")) {

                    var intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(request!!.url.toString())
                    )

                    startActivity(intent)

                    return true

                }
                else return false
            }
            override
            fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }

    }

    private fun setWebClient() {

        activity!!.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        webview_news.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(
                view: WebView,
                newProgress: Int
            ) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    loading_indicator.visibility = View.GONE
                    activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    private fun loadUrl(pageUrl: String) {
        webview_news.loadUrl(pageUrl)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            Noti2Fragment().apply {

            }
    }
}
