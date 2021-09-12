package cn.edu.xupt.acat.user.service;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.user.domain.vo.UserVo;

public interface UserRegisterService {
    public abstract R register(UserVo input);
    public abstract R getCheckCode(UserVo input);
}
