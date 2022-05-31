package cn.edu.xupt.acat.apply.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_apply_record")
@Document(indexName = "xupt-acat-recruitment-apply", type = "apply")
public class TbApply implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nid;
    private String username;
    private String snumber;
    private String realName;
    private String className;
    private Integer sex;
    private String phone;
    private String content;
    private Date createTime;
    private Date updateTime;
}
