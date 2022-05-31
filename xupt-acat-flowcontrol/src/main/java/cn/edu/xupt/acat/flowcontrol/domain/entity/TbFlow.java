package cn.edu.xupt.acat.flowcontrol.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_flow_record")
public class TbFlow implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nid;
    private String username;
    private String serviceLine;
    private Integer version;
    private String workType;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
