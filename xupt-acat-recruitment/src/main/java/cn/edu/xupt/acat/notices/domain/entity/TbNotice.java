package cn.edu.xupt.acat.notices.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_notice_record")
public class TbNotice implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nid;
    private String workType;
    private String sendUser;
    private String receiveUser;
    private String receiveAddress;
    private String title;
    private String content;
    private Integer status;
    private Date sendTime;
}
