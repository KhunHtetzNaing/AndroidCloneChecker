package com.htetznaing.clonechecker;
import android.content.Context;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CloneChecker {
    private OnTaskCompleted onComplete;

    public CloneChecker(Context context,String IMEI) {
        WebView webView = new WebView(context);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByClassName('well')[0].innerText);");
            }
        });

        String url = "http://imei24.com/checking/";
        String postData = null;
        try {
            postData = "s=" + URLEncoder.encode(IMEI, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webView.postUrl(url,postData.getBytes());
    }

    class MyJavaScriptInterface{
        @SuppressWarnings("unused")
        @JavascriptInterface
        public void processHTML(String html) {
            if (html!=null) {
                html = html.toLowerCase();
                String brand = Build.MANUFACTURER.toLowerCase();
                String model = Build.MODEL.toLowerCase();
                if (html.contains("sorry") || html.contains("no") || html.contains("didn't find")){
                    onComplete.onTaskCompleted(false);
                }else{
                    if (html.contains(brand) || html.contains(model)) {
                        onComplete.onTaskCompleted(true);
                    }else{
                        onComplete.onTaskCompleted(false);
                    }
                }
            }else{
                onComplete.onError();
            }
        }
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean ogay);
        void onError();
    }

    public void onFinish(OnTaskCompleted onComplete) {
        this.onComplete = onComplete;
    }
}