package com.address.mapper;

import com.address.pojo.AreaUrl;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @Created by Meng Kai
 * @Date 2020/11/21
 */
public interface AreaUrlMapper extends Mapper<AreaUrl>, IdListMapper<AreaUrl,Integer>, InsertListMapper<AreaUrl> {
}
