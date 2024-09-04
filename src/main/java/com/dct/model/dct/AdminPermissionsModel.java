package com.dct.model.dct;

import com.dct.model.dct.entity.EntityStringIdBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.List;
import java.util.Objects;

/**
 * AdminPermissionsModel
 *
 * @author Vic on 2019/1/7
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_permissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "path", "parent_id" }) })
public class AdminPermissionsModel extends EntityStringIdBase {

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "name", nullable = false)
    private String name;

    /** 权限标题 */
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "icon", nullable = false)
    private String icon;

    /** 权限URL */
    @Column(name = "path")
    private String path;

    /** 权限类型 */
    @Column(name = "is_leaf", nullable = false)
    private boolean isLeaf;

    /** 排序索引 */
    @Column(name = "level", nullable = false)
    private Integer level;

    /** 排序索引 排序索引依次递增 */
    @Column(name = "sort_index", nullable = false, updatable = false)
    private Integer sortIndex;

    /** 目录不隐藏,其余均隐藏 */
    @Column(name = "hidden", nullable = false)
    private boolean hidden;

    /** 目录都为 redirect */
    @Column(name = "redirect", columnDefinition = "varchar(255) default null")
    private String redirect;

    /**
     * 权限类型，1.路径    2.按钮
     */
    @Column(name = "permission_type" ,columnDefinition = "int(2)")
    private Integer permissionType;

    /**
     * 操作权限.
     */
    @Column(name = "operator", columnDefinition = "varchar(50) default null")
    private String operator;

    /**
     * 操作权限路径.
     */
    @Transient
    private  String operatorPath;

    /** 次级权限 */
    @Transient
    private List<AdminPermissionsModel> subPermissions;

    public AdminPermissionsModel(){
    }

    public AdminPermissionsModel(String id, String parentId, String name, String title, String icon, String path, boolean isLeaf, Integer sortIndex, boolean hidden){
        this.setId(id);
        this.parentId = parentId;
        this.title = title;
        this.path = path;
        this.sortIndex = sortIndex;
        this.name = name;
        this.icon = icon;
        this.isLeaf = isLeaf;
        this.hidden = hidden;
    }

    public boolean isParent(AdminPermissionsModel other) {
        Objects.requireNonNull(other);

        if (other.getParentId() == null) {
            return false;
        }

        if (other.getParentId().equals(getId())) {
            return true;
        }

        return false;
    }
}
