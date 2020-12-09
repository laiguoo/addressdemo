package com.address.service;

import com.address.mapper.AreaUrlMapper;
import com.address.pojo.AreaUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Meng Kai
 * @Date 2020/11/25
 */
@Service
public class TrimService {

    @Autowired
    private AreaUrlMapper areaUrlMapper;

    public void trimEnd() {
        List<AreaUrl> provinceList = new ArrayList<>();
        AreaUrl provinceRecord = new AreaUrl();
        provinceRecord.setParentId(9527);
        provinceList = areaUrlMapper.select(provinceRecord);
        if (provinceList.size() != 0) {
            for (AreaUrl province : provinceList) {
                AreaUrl shiRecord = new AreaUrl();
                shiRecord.setParentId(province.getId());
                List<AreaUrl> shiList = new ArrayList<>();
                shiList = areaUrlMapper.select(shiRecord);
                if (shiList.size() != 0) {
                    for (AreaUrl shi : shiList) {
                        AreaUrl xianRecord = new AreaUrl();
                        xianRecord.setParentId(shi.getId());
                        List<AreaUrl> xianList = new ArrayList<>();
                        xianList = areaUrlMapper.select(xianRecord);
                        if (xianList.size() != 0) {
                            for (AreaUrl xian : xianList) {
                                AreaUrl zhenRecord = new AreaUrl();
                                zhenRecord.setParentId(xian.getId());
                                List<AreaUrl> zhenList = new ArrayList<>();
                                zhenList = areaUrlMapper.select(zhenRecord);
                                if (zhenList.size() != 0) {
                                    for (AreaUrl zhen : zhenList) {
                                        AreaUrl cunRecord = new AreaUrl();
                                        cunRecord.setParentId(zhen.getId());
                                        List<AreaUrl> cunList = new ArrayList<>();
                                        cunList = areaUrlMapper.select(cunRecord);
                                        if (cunList.size() != 0) {
                                            for (AreaUrl cun : cunList) {
                                                String name = cun.getName();
                                                int index = name.length() - 1;
                                                /*String subNameOne = name.substring(index - 2);
                                                String subNameTwo = name.substring(index - 1);*/
                                                if (name.endsWith("居委会")) {
//                                                    System.out.println("居委会：    " + name);
                                                } else if (name.endsWith("村村委会")) {
//                                                    System.out.println("村村委会：   " + name);
                                                } else if (name.endsWith("村委会")) {
//                                                    System.out.println("村委会：   " + name);
                                                }else if (name.endsWith("村")) {
//                                                    System.out.println("村：   " + name);
                                                }else if (name.endsWith("居民委员会")) {
//                                                    System.out.println("居民委员会：   " + name);
                                                }else if (name.endsWith("委会一委")) {
//                                                    System.out.println("委会一委：   " + name);
                                                }else if (name.endsWith("居委")) {
//                                                    System.out.println("居委：   " + name);
                                                }else if (name.endsWith("居民委会")) {
//                                                    System.out.println("居民委会：   " + name);
                                                }else if (name.endsWith("委员会")) {
//                                                    System.out.println("委员会：   " + name);
                                                }else if (name.endsWith("居委会筹备组及社区工作站")
                                                        ||name.endsWith("居委会筹备组和社区工作站")
                                                        ||name.endsWith("居委会筹备组和工作站")||name.endsWith("筹备组")) {
//                                                    System.out.println("居委会筹备组及社区工作站：   " + name);
                                                }else if (name.endsWith("居委会筹备组")) {
//                                                    System.out.println("居委会筹备组：   " + name);
                                                }else if (name.endsWith("农工商联合公司（村委会）")) {
//                                                    System.out.println("农工商联合公司（村委会）：   " + name);
                                                }else if (name.endsWith("社区")) {
//                                                    System.out.println("社区：   " + name);
                                                }else if (name.endsWith("生活区")) {
//                                                    System.out.println("生活区：   " + name);
                                                }else if (name.endsWith("居民委员会和工作站")) {
//                                                    System.out.println("居民委员会和工作站：   " + name);
                                                } else {
                                                    System.out.println("未包含：   " + name);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
