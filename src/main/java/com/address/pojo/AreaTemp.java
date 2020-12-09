package com.address.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Created by Meng Kai
 * @Date 2020/11/21
 */
@Data
@Table(name = "area_temp")
public class AreaTemp {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private String name;
    private Integer parentId;
    private String prix;
    private String url;
    private Integer total;
    private Integer aId;
}
