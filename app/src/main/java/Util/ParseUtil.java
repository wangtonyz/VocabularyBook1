package Util;


import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.Context.MODE_PRIVATE;


public class ParseUtil {
    private final static String TAG = "金山解析工具";

    public static boolean isEnglish(String content){
        if(content == null){                    //获取内容为空则返回false
            return false;
        }
        content = content.replace(" ","");      //去掉内容中的空格
        return content.matches("^[a-zA-Z]*");   //判断是否是全英文，是则返回true，反之返回false

    }



    public static void sendOkHttpRequest(String address,okhttp3.Callback callback) {
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建Request对象，装上地址
        Request request = new Request.Builder().url(address).build();
        //发送请求，返回数据需要自己重写回调方法
        client.newCall(request).enqueue(callback);

    }



}
