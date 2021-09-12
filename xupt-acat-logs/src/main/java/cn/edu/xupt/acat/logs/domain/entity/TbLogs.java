package cn.edu.xupt.acat.logs.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_logs_record")
@Document(indexName = "xupt-acat-recruitment-logs", type = "logs")
public class TbLogs implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nid;
    private String username;
    private String opUser;
    private String opDetails;
    private String workType;
    private String opExt;
    private Date opTime;
}

