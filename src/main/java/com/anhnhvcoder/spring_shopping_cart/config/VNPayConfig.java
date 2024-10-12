package com.anhnhvcoder.spring_shopping_cart.config;

import com.anhnhvcoder.spring_shopping_cart.utils.VNPayUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayConfig {

    @Getter
    String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    String vnp_TmnCode = "EZP507AR";
    @Getter
    String secretKey = "LZECX3PRZ6SZIVO34ZZEF0ZXYWUKPXUJ";
    String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    String vnp_ReturnUrl = "http://localhost:5173/payment-callback";
    String version = "2.1.0";
    String command = "pay";
    String vnp_OrderType = "other";

    public Map<String, String> getVnPayConfig(){
        Map<String, String> vnpParamsMap = new HashMap<>();

        vnpParamsMap.put("vnp_Version", version);
        vnpParamsMap.put("vnp_Command", command);
        vnpParamsMap.put("vnp_TmnCode", vnp_TmnCode);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef", VNPayUtils.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toán hóa đơn" + VNPayUtils.getRandomNumber(4));
        vnpParamsMap.put("vnp_OrderType", vnp_OrderType);
        vnpParamsMap.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnpParamsMap.put("vnp_BankCode", "NCB");

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = simpleDateFormat.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnp_CreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = simpleDateFormat.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);

        return vnpParamsMap;
    }


}
