package com.imooc.mall.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述： AddCategoryReq 请求类
 */
public class AddCategoryReq {
    @NotNull(message = "name不能为null")
    @Size(min = 2, max = 5)
    private String name;
    @NotNull(message = "type不能为null")
    @Max(3)    // 层级数不可超过3
    private Integer type;
    @NotNull(message = "parentId不能为null")   //增加message，会在校验是显示配置的提示
    private Integer parentId;
    @NotNull(message = "orderNum不能为null")
    private Integer orderNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
