package cn.edu.xupt.acat.recruitment.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_evaluate_record")
@Document(indexName = "xupt-acat-recruitment-evaluate", type = "evaluate")
public class TbEvaluate implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nid;
    private String evaluateUser;
    private String content;
    private Integer score;
    private String workType;
    private Date createTime;
    private Date updateTime;
}
