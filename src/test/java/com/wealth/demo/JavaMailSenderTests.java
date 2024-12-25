package com.wealth.demo;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JavaMailSenderTests {

    @Autowired
    private JavaMailSender mailSender;


    @Test
    public void sendSimpleMail() throws Exception {
            SimpleMailMessage message = new SimpleMailMessage();

            String code= "123";
            String message1= """
                    內容:%s
                    
                    
                    """.formatted(code);
            message.setFrom("收支管家<holodd0404@gmail.com>");
            message.setTo("holodd0404@gmail.com");
            message.setSubject("主旨：Hello World!!");
            message.setText(message1);

        mailSender.send(message);
    }


}
