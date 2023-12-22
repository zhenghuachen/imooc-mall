package com.imooc.mall.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
// VO 是经过转换后返回给前端的类

/**
 * CategoryVO 类实现了 Serializable 接口，这意味着该类可以被序列化，
 * 从而在网络上传输或存储时，对象的属性能够正确地保存和恢复。
 * 序列化是指将对象的状态转换为字节流，以便在网络上传输或存储。实现了Serializable
 * 接口的类可以确保在序列化和反序列化过程中，对象的属性能够正确地保存和恢复。
 * 在实际应用中，如果一个对象需要被传输或存储，通常需要将其序列化为字节流，然后在另一端
 * 进行反序列化，以恢复原来的对象状态。
 * 此处需要被Redis缓存，故需要序列化。
 */
public class CategoryVO implements Serializable {
    private Integer id;

    private String name;

    private Integer type;

    private Integer parentId;

    private Integer orderNum;

    private Date createTime;

    private Date updateTime;

    private List<CategoryVO> childCategory = new ArrayList<>();

    public List<CategoryVO> getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(List<CategoryVO> childCategory) {
        this.childCategory = childCategory;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}