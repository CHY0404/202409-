package com.wealth.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserLoginDTO {

    private String username;
    private String password; //用於登入密碼
    private String captcha;

}
