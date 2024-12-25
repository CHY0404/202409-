package com.wealth.demo.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class CaptchaController {

    private final DefaultKaptcha captchaProducer;

    public CaptchaController(DefaultKaptcha captchaProducer) {
        this.captchaProducer = captchaProducer;
    }

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 設定回應標頭
        response.setContentType("image/jpeg");

        // 生成驗證碼文字
        String captchaText = captchaProducer.createText();

        // 將驗證碼文字儲存在 session 中以便驗證
        request.getSession().setAttribute("captcha", captchaText);

        // 創建驗證碼圖片
        BufferedImage captchaImage = captchaProducer.createImage(captchaText);

        // 將圖片寫入回應輸出流
        ImageIO.write(captchaImage, "jpg", response.getOutputStream());
    }

}
