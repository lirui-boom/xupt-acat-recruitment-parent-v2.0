package cn.edu.xupt.acat.lib.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchPage<T> implements Serializable {
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private T data;

    public SearchPage(int pageNum, int pageSize, long total, T data) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }
}
