package sws.NoticeBoard.controller.form;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInDto {
    private String username;
    private String password;
}
