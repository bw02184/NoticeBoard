package sws.NoticeBoard.controller.form;

import java.time.LocalDateTime;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MemberSaveForm {
    @NotEmpty
    @Length(min = 4, max = 20)
    private String loginId;
    @NotEmpty
    private String realName;
    @NotEmpty
    @Length(min = 4, max = 20)
    private String password;
    @NotEmpty
    @Length(min = 4, max = 20)
    private String password2;
    @NotEmpty
    @Email
    private String email;
}
