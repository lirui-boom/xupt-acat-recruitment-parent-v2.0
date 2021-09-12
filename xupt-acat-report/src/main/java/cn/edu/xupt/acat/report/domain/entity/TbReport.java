package cn.edu.xupt.acat.report.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_report_record")
public class TbReport implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String opUser;
    private String conds;
    private Integer status;
    private String detail;
    private String download;
    private Date createTime;
    private Date updateTime;
}
