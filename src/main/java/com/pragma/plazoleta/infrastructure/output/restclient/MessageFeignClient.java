package com.pragma.plazoleta.infrastructure.output.restclient;

import com.pragma.plazoleta.application.dto.response.NotificationResponse;
import com.pragma.plazoleta.application.dto.request.NotificationRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-message", url = "${ms-message.url}", configuration = FeignClientConfig.class)
public interface MessageFeignClient {
    @PostMapping("/notification/send")
    NotificationResponse sendMessage(@RequestBody NotificationRequest notificationRequest);
} 