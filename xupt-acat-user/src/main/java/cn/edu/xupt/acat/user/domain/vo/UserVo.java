package cn.edu.xupt.acat.user.domain.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UserVo implements Serializable {
    private String username;
    private String password;
    private String nickName;
    private String email;
    private String code;
}
