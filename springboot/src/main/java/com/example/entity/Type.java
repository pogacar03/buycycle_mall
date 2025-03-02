package com.example.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 公告信息表
*/
public class Type implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 分类描述
     */
    private String description;
    /**
     * 分类图标
     */
    private String img;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<Type> getChildren() {
        return children;
    }

    public void setChildren(List<Type> children) {
        this.children = children;
    }

    /**
     * 父分类ID
     */
    private Integer parentId;
    /**
     * 子分类列表
     */
    private List<Type> children;

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
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    /**
     * ID
     */



}
