package com.dct.model.dct;

import com.dct.model.dct.entity.EntityStringIdBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Charles
 * @date 2019/2/20
 * @description :
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_config_map", uniqueConstraints = {
        @UniqueConstraint(name = "unique_configName_itemName_itemValue", columnNames = { "config_name", "item_key", "item_value" }) })
public class AdminConfigMapModel extends EntityStringIdBase {

    @Column(name = "config_name")
    private String configName;

    @Column(name = "item_key")
    private String itemKey;

    @Column(name = "item_value")
    private String itemValue;

    @Column
    private String comment;
}
