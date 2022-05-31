package cn.edu.xupt.acat.logs.domain;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TbLogs implements Serializable {
    private Long id;
    private String nid;
    private String username;
    private String opUser;
    private String opDetails;
    private String workType;
    private String opExt;
    private Date opTime;
}

