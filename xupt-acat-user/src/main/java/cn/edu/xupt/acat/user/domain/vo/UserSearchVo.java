package cn.edu.xupt.acat.user.domain.vo;

import cn.edu.xupt.acat.user.domain.entity.TbUser;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserSearchVo implements Serializable {
    private TbUser user;
    private int pageNum;
    private int pageSize;
}
