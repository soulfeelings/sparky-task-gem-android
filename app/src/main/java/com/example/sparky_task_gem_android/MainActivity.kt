package com.example.sparky_task_gem_android

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.TextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import android.webkit.WebChromeClient
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebView.setWebContentsDebuggingEnabled(true)
        setContent {
            WebViewWithInput()
        }
    }
}

@Composable
fun WebViewWithInput() {
    var url by remember { mutableStateOf("https://yandex.ru") }
    var currentUrl by remember { mutableStateOf(url) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(8.dp)) {
            TextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Введи URL, котик…") }
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { currentUrl = url }) {
                Text("Открыть")
            }
        }
        Box(Modifier.weight(1f)) {
            WebViewScreen(url = currentUrl)
        }
    }
}

@Composable
fun WebViewScreen(url: String) {
    var isLoading by remember { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.d(
                                "WebViewConsole",
                                "${consoleMessage?.messageLevel()} @${consoleMessage?.sourceId()}:${consoleMessage?.lineNumber()} — ${consoleMessage?.message()}"
                            )
                            return true
                        }
                    }
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.javaScriptCanOpenWindowsAutomatically = true
                    settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                    loadUrl(url)
                }
            },
            update = { webView ->
                webView.loadUrl(url)
                isLoading = true
            }
        )
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
            }
        }
    }
}

