package cn.edu.xupt.acat.recruitment.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_recruitment_record")
public class TbRecruitment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String nid;
    private String workType;
    private Integer noticeStatus;
    private Date createTime;
}
