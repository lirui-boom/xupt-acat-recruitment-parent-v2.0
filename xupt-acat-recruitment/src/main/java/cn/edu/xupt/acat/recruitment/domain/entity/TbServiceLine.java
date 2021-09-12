package cn.edu.xupt.acat.recruitment.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(indexName = "xupt-acat-recruitment-service_line", type = "service_line")
public class TbServiceLine implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String serviceLine;
    private Integer qps;
    private String workDetails;
    private Integer turns;
    private String authKey;
    private String descb;
    private Integer status;
    private Integer version;
    private Date createTime;
    private Date updateTime;
}
