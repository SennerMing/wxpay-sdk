package com.github.wxpay.sdk.test;


import cn.hutool.json.JSONUtil;
import com.github.wxpay.sdk.WXPayUtil;
import com.github.wxpay.sdk.utils.HttpRequest;
import com.github.wxpay.sdk.utils.SnowflakeIdWorker;

import java.util.*;

public class TestWxPay {



    public static void main(String[] args) {


        String api_key = "上面在商户号中使用别人网站设置的API秘钥";
        String app_id = "在微信开放平台通过开发者账号申请的应用ID";
        String mch_id = "商户号ID";
        String unifiedorder_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        String pre_pay_callback = "https://www.yuqing.lescity.com.cn/order/prePay";//随便填写的公司地址


        Map<String, String> param = new HashMap<String, String>();
        param.put("appid",app_id);
        param.put("mch_id",mch_id);
        String nonce_str = WXPayUtil.generateNonceStr();
        param.put("nonce_str",nonce_str);
        param.put("body","竞标助手会员充值");
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        long id = idWorker.nextId();

        System.out.println("out_trade_no:"+id);
        param.put("out_trade_no",String.valueOf(id));
        param.put("total_fee","11");
        param.put("spbill_create_ip","127.0.0.1");
        param.put("notify_url", pre_pay_callback);
        param.put("trade_type","APP");

        // 统一下单 https://api.mch.weixin.qq.com/pay/unifiedorder


        String signxml = "";
        try {
            signxml = WXPayUtil.generateSignedXml(param, api_key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("sign:"+signxml);
        param.put("sign",signxml);


//        String xmlStr = HttpRequest.httpsRequest(unifiedorder_url, "POST", signxml);
        String xmlStr = cn.hutool.http.HttpUtil.post(unifiedorder_url, signxml);

        System.out.println(xmlStr);
        // 以下内容是返回前端页面的json数据
        String prepay_id = "";// 预支付id
        if (xmlStr.indexOf("SUCCESS") != -1) {
            Map<String, String> map = null;
            try {
                map = WXPayUtil.xmlToMap(xmlStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            prepay_id = (String) map.get("prepay_id");
        }

        Map<String, String> payMap = new HashMap<String, String>();
        payMap.put("appId", app_id);
        payMap.put("timeStamp", WXPayUtil.getCurrentTimestamp() + "");
        payMap.put("nonceStr", WXPayUtil.generateNonceStr());
        payMap.put("signType", "MD5");
        payMap.put("package", "prepay_id=" + prepay_id);
        String paySign = null;
        try {
            paySign = WXPayUtil.generateSignature(payMap, api_key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        payMap.put("paySign", paySign);

        System.out.println(JSONUtil.parse(payMap));
    }

}
