package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.config.VNPayConfig;
import com.anhnhvcoder.spring_shopping_cart.dto.PaymentDTO;
import com.anhnhvcoder.spring_shopping_cart.request.OrderRequest;
import com.anhnhvcoder.spring_shopping_cart.utils.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig vnpayConfig;

    public PaymentDTO.VNPayResponse createVNPayPayment(OrderRequest orderRequest, HttpServletRequest request){
        try {
            long amount = (long) (Integer.parseInt(String.valueOf(orderRequest.getTotalPrice())) * 100L);
            Map<String, String> vnpParamsMap = vnpayConfig.getVnPayConfig();
            vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
            vnpParamsMap.put("vnp_BankCode", "NCB");
            vnpParamsMap.put("vnp_IpAddr", VNPayUtils.getIpAddress(request));
            String queryUrl = VNPayUtils.getPaymentUrl(vnpParamsMap, true);
            String hashData = VNPayUtils.getPaymentUrl(vnpParamsMap, false);
            String vnpSecureHash = VNPayUtils.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
            String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;

            return PaymentDTO.VNPayResponse.builder()
                    .code("OK")
                    .message("Success")
                    .paymentUrl(paymentUrl)
                    .build();

        }catch (Exception e){
            return PaymentDTO.VNPayResponse.builder()
                    .code("ERROR")
                    .message("Error")
                    .build();
        }
    }
}
