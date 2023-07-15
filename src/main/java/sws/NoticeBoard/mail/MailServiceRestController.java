package sws.NoticeBoard.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MailServiceRestController {

  @Autowired RegisterMail registerMail;

  //  http://localhost:8080/login/mail/confirm.json?email={이메일주소}
  @PostMapping(value = "login/mail/confirm")
  public String mailConfirm(@RequestParam(name = "email") String email) throws Exception {
    String code = registerMail.sendSimpleMessage(email);
    log.info("사용자에게 발송한 인증코드 ==> ={}", code);

    return code;
  }
}
