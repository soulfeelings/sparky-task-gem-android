package com.example.sparky_task_gem_android
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewWithInput()
        }
    }
}

@Composable
fun WebViewWithInput() {
    var url by remember { mutableStateOf("https://preview--sparky-task-gem.lovable.app/") }
    var currentUrl by remember { mutableStateOf(url) }
    Column() {
        Row(
            Modifier
                .padding(16.dp)
                .height(64.dp)) {
            TextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Введи URL, котик…") }
            )
            Button(onClick = { currentUrl = url }) {
                Text("Открыть")
            }
        }
        WebViewScreen(
            url = currentUrl,
            modifier = Modifier
                .background(Color.Gray)
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun WebViewScreen(url: String, modifier: Modifier = Modifier) {
    val isLoading = remember { mutableStateOf(true) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        isLoading.value = false
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
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    useWideViewPort = false
                    loadWithOverviewMode = false

                    setSupportZoom(false)
                    isVerticalScrollBarEnabled = true
                    isHorizontalScrollBarEnabled = true
                }
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                loadUrl(url)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
            isLoading.value = true
        },
        modifier = modifier
    )
    if (isLoading.value) {
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        }
    }
}