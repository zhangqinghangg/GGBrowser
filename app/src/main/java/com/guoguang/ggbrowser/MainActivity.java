package com.guoguang.ggbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.guoguang.ggbrowser.R;
import com.iflytek.cloud.SpeechUtility;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.URLUtil;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.utils.v;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import Utlis.AudioUtils;
import Utlis.JSONTool;
import Utlis.PayHttpUtils;
import Utlis.X5WebView;
import Utlis.WebViewJavaScriptFunction;


public class MainActivity extends Activity {

    WebView xwv;
    WebViewClient homeWebViewClient;
    WebChromeClient homeWebChromeClient;
    String res;
    String re;
    String json = "idLevel：0001&idDev：Dev1";
    //后台数据请求码
    String json1 = "0001&Dev1";
    //实例网页
    String url_html = "file:///android_asset/demo2.html";
    //后台数据接口
    String url2 = "http://jeremyda.cn:8019/api/queue/get";
    String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏，要加在setContentView()之前才有效果
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        xwv = (X5WebView) findViewById(R.id.webview_a);
//        datathread();
        initwebview();


    }

    //访问后台数据，并返回json字符串
    private void datathread() {
        //访问后台接口url2，并返回后台数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                res = PayHttpUtils.GetSingleCabCollect(url2);
            }
        }).start();
    }

    private void initwebview() {
        //WebSettings类作用：对WebView进行配置和管理
        WebSettings webSettings = xwv.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //允许启用或禁用WebView访问文件数据
        webSettings.setAllowFileAccess(true);
        //支持插件
        webSettings.setPluginsEnabled(true);
        //设置自适应屏幕，两者合用
        //webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        //webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        //给WebView对象添加JS接口
        xwv.addJavascriptInterface(new WebViewJavaScriptFunction() {
            @Override
            public void onJsFunctionCalled(String tag) {

            }
        }, "Android");
        //通过WebView的addJavascriptInterface方法去注入一个我们自己写的interface（实例）
        xwv.addJavascriptInterface(new JsInterface(), "myspeech");
        xwv.addJavascriptInterface(new JsInterface(), "mydata");
        xwv.addJavascriptInterface(new JsInterface(), "myshutDown");
        xwv.addJavascriptInterface(new JsInterface(), "myconfig");
        // WebChromeClient类,辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等
        homeWebChromeClient = new WebChromeClient() {


            @Override
            //处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("带选择的对话框");
                //js中设置的对话框的内容message
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //根据js中的if(confirm("")访问location.href="http://www.ggzzrj.cn:8080/demo/";
                                result.confirm();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //根据js中的设置alert("你选择了不去！");
                                result.cancel();
                            }
                        });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }

            @Override
            // 处理javascript中的prompt
            // message为网页中对话框的提示内容
            // defaultValue在没有输入时，默认显示的内容
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, final JsPromptResult result) {
                // 自定义一个带输入的对话框由TextView和EditText构成
                LayoutInflater layoutInflater = LayoutInflater
                        .from(MainActivity.this);
                final View dialogView = layoutInflater.inflate(
                        R.layout.prom_dialog, null);

                // 设置TextView对应网页中的提示信息
                ((TextView) dialogView.findViewById(R.id.TextView_PROM))
                        .setText(message);
                // 设置EditText对应网页中的输入框
                ((EditText) dialogView.findViewById(R.id.EditText_PROM))
                        .setText(defaultValue);
                //构建一个Builder来显示网页中的对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //设置弹出框标题
                builder.setTitle("访问后台数据");
                //设置弹出框的布局
                builder.setView(dialogView);
                //设置按键的监听
                builder.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {


                                // 点击确定之后，取得输入的值，传给网页处理
                                String value = ((EditText) dialogView
                                        .findViewById(R.id.EditText_PROM))
                                        .getText().toString();
                                datathread();
                                if (value.equals(json)) {
                                    result.confirm(res);
                                } else {
                                    result.confirm("请求码有误，请输入正确的json请求码");
                                }
                            }

                        });

                builder.setNegativeButton(android.R.string.cancel,
                        new AlertDialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                result.cancel();
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        result.cancel();
                    }
                });
                builder.show();
                return true;
            }


        };


        //webview设置WebChromeClient
        xwv.setWebChromeClient(homeWebChromeClient);
        //加载页面
        xwv.loadUrl(url_html);

    }


    public class JsInterface {
        //映射js的文字转语音功能
        @JavascriptInterface
        public void speech(String text, String pitch, String volume) {
            // 初始化TextToSpeech对象
            SpeechUtility.createUtility(getApplicationContext(), "appid=5d355288");
            AudioUtils.getInstance().init(MainActivity.this, pitch, volume);
            // 播放语音
            AudioUtils.getInstance().speakText(text);

        }

        //映射js的访问后台数据功能
        @JavascriptInterface
        public String data(String code) {
            if (code.equals(json1)) {
                //访问后台数据，并返回json字符串
                //datathread();
                String str2 = "{\"noon\":1,\"serialNumber\":\"加9\",\"operate\":\"get\",\"success\":true,\"max_pm\":10,\"timeGet\":\"2019-08-13 10:29:14\",\"max_am\":5,\"idLevel\":\"0001\",\"detail\":\"{\\\"idLevel\\\":\\\"0001\\\",\\\"idDev\\\":\\\"Dev1\\\",\\\"DATE_WITH_MID_SEPERATOR\\\":\\\"2019-08-13\\\",\\\"DATE_WITHOUT_SEPERATOR\\\":\\\"20190813\\\",\\\"DATE_WITH_SEPERATOR\\\":\\\"2019_08_13\\\",\\\"id\\\":7849443396267212800}\",\"id\":\"7849443396267212800\",\"priority\":0}\"";
                String restr1 = str2.substring(0, str2.indexOf("detail"));
                String restr2 = str2.substring(str2.indexOf("id\":"));
                String restr3 = restr1 + restr2 + "";
                String re = JSONTool.stringToJSON(restr3);
                return "你的请求码是：" + code + "---返回的数据【" + re + "】";
            } else {
                return "错误请求码：" + code + "，请输入正确的请求码：0001&Dev1";

            }
        }

        //映射js的获取手机配置功能功能
        @JavascriptInterface
        public String config() {
            String con = getDeviceInfo();
            return "你的手机配置是：" + con;
        }

        //映射js的关机功能
        @JavascriptInterface
        public void guanji() {
            shutDown();
        }

        //关机功能
        private void shutDown() {
            String[] arrayShutDown = {"su","-c","reboot -p"};
            closePhone(MainActivity.this,arrayShutDown);
        }
        @SuppressWarnings("unused")
        private void closePhone(Context context,String[] shutdown){
            try {
                Process	 process = Runtime.getRuntime().exec(shutdown);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //获取手机配置功能
        private String getDeviceInfo() {
            StringBuffer sb = new StringBuffer();
            sb.append("1、主板：" + Build.BOARD);
            sb.append("\n2、系统启动程序版本号：" + Build.BOOTLOADER);
            sb.append("\n3、系统定制商：" + Build.BRAND);
            sb.append("\n4、cpu指令集：" + Build.CPU_ABI);
            sb.append("\n5、cpu指令集2：" + Build.CPU_ABI2);
            sb.append("\n6、设置参数：" + Build.DEVICE);
            sb.append("\n7、显示屏参数：" + Build.DISPLAY);
            sb.append("\n8、无线电固件版本：" + Build.getRadioVersion());
            sb.append("\n9、硬件识别码：" + Build.FINGERPRINT);
            sb.append("\n10、硬件名称：" + Build.HARDWARE);
            sb.append("\n11、HOST:" + Build.HOST);
            sb.append("\n12、修订版本列表：" + Build.ID);
            sb.append("\n13、硬件制造商：" + Build.MANUFACTURER);
            sb.append("\n14、版本：" + Build.MODEL);
            sb.append("\n15、硬件序列号：" + Build.SERIAL);
            sb.append("\n16、手机制造商：" + Build.PRODUCT);
            sb.append("\n17、描述Build的标签：" + Build.TAGS);
            sb.append("\n18、TIME:" + Build.TIME);
            sb.append("\n19、builder类型：" + Build.TYPE);
            sb.append("\n20、USER:" + Build.USER);
            return sb.toString();
        }
    }

    /**
     * 通过反射获取所有的字段信息
     *
     * @return
     */
    public String getDeviceInfo2() {
        StringBuilder sbBuilder = new StringBuilder();
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                sbBuilder.append("\n" + field.getName() + ":" + field.get(null).toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sbBuilder.toString();
    }

}


