package cn.edu.xupt.acat.lib.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class FlowModel implements Serializable {
    private String flowName;
    private String next;

    public FlowModel(String flowName, String next) {
        this.flowName = flowName;
        this.next = next;
    }
}
