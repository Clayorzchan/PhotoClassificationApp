package com.example.PhotoClassification;


import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.net.URLEncoder;




/**
 * 通用物体和场景识别
 */
public class AdvancedGeneral extends Thread{
    private static final String TAG = "AdvancedGeneral";
    String file;
    String result;
    Handler handler;

    public AdvancedGeneral(String file, Handler handler) {
        this.file = file;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        result = advancedGeneral(file);
        handler.sendEmptyMessage(1);
    }

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */


    //得到识别结果 result变为识别结果汉字形式
    public static String advancedGeneral(String filePath) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general";
        try {
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();

            //logcat中打印token
            Log.e(TAG, accessToken );
            String result = HttpUtil.post(url, accessToken, param);

            //
            JSONObject jsonObject1 =JSONObject.parseObject(result);
            JSONArray jsonArray= (JSONArray) jsonObject1.get("result");
            String s = "";
            //变为中文
            for(int i=0;i<jsonArray.size();i++)
            {
                s += jsonArray.getJSONObject(i).get("keyword")+"\n";
            }
            //

            //终端打印结果
            System.out.println(s);
            return s;
        } catch (Exception e) {
            System.out.println(e);
            Log.e(TAG, "Exception occurs");
            return null;
        }

    }
}

