package com.address;

import com.address.mapper.AreaMapper;
import com.address.mapper.AreaUrlMapper;
import com.address.pojo.Area;
import com.address.pojo.AreaUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * 从国家统计局网站爬取2020年12位到村级别的行政区划代码
 */
@Component
public class ReadCodeFromWebUrl {

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private AreaUrlMapper areaUrlMapper;

    public static final String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2020/";
    //设置utf-8发现有部分字符有乱码
    public static final String CHARSET = "GBK";
//    public static final String CHARSET = "UTF-8";

    public static StringBuffer result = new StringBuffer();


    /**
     * 读省的信息
     *
     * @param provinceName
     * @throws Exception
     */
    public void readProvince(String provinceName) throws Exception {

        //获取本地数据
        AreaUrl areaUrlRecord = new AreaUrl();
        areaUrlRecord.setParentId(9527);
        List<AreaUrl> areaUrlList = areaUrlMapper.select(areaUrlRecord);
        if (areaUrlList == null || areaUrlList.size() == 0 || (areaUrlList.get(0).getTotal() != areaUrlList.size())) {
            //本地数据库数据不全
            String url = baseUrl + "index.html";
            //如果需要设置代理
//        initProxy("10.10.13.200", "80");
            String str = getContent(url).toUpperCase();
            String[] arrs = str.split("<A");
            List<AreaUrl> tempList = new ArrayList<>();

            for (String s : arrs) {
                if (s.indexOf("HREF") != -1 && s.indexOf(".HTML") != -1) {
                    String a = s.substring(7, s.indexOf("'>"));

                    String name = s.substring(s.indexOf("'>") + 2, s.indexOf("<BR/>"));

                    //省name
//                System.out.println("省 name:" + name);

                    //插入数据库
                    AreaUrl areaRecord = new AreaUrl();
                    areaRecord.setUrl(a);
                    areaRecord.setName(name);
                    areaRecord.setParentId(9527);
//                    areaRecord.setTotal(arrs.length);
//                    AreaUrl area = areaUrlMapper.selectOne(areaRecord);

                    tempList.add(areaRecord);
                    /*if (area == null) {
                        areaUrlMapper.insert(areaRecord);
                        area = areaRecord;
                    }

                    System.out.println(name);
                    if (!provinceName.equals(name)) {
                        continue;
                    }

                    System.out.println("爬取:" + name);

                    readShi(a, area.getId());*/

                }
            }
            for (AreaUrl areaUrl : tempList) {
                areaUrl.setTotal(tempList.size());
                System.out.println(areaUrl.getName());

            }
            if (areaUrlList == null || areaUrlList.size() == 0) {
                if (tempList.size() != 0) {
                    areaUrlMapper.insertList(tempList);
                }
            } else {
                for (AreaUrl areaUrl : tempList) {
                    AreaUrl area = areaUrlMapper.selectOne(areaUrl);
                    if (area != null) {
                        continue;
                    }
                    areaUrlMapper.insert(areaUrl);
                }
            }

            for (AreaUrl areaUrl : tempList) {
                /*if (!provinceName.equals(areaUrl.getName())) {
                    continue;
                }*/

                System.out.println("爬取:" + areaUrl.getName());

                readShi(areaUrl.getUrl(), areaUrl.getId());
            }
        } else {
//            System.out.println("省：  本地数据获取去成功");
//            System.out.println("size: " + areaUrlList.size() + ";   total: " + areaUrlList.get(0).getTotal());
            for (AreaUrl areaUrl : areaUrlList) {
                System.out.println("    local------------------------------------------------"+areaUrl.getName());
               /* if (!provinceName.equals(areaUrl.getName())) {
                    continue;
                }*/
                System.out.println("爬取:" + areaUrl.getName());
                readShi(areaUrl.getUrl(), areaUrl.getId());
            }
        }
    }

    /**
     * 读市的数据
     *
     * @throws Exception
     */
    public void readShi(String url, Integer parentId) throws Exception {
        //获取本地数据
        AreaUrl areaUrlRecord = new AreaUrl();
        areaUrlRecord.setParentId(parentId);
        List<AreaUrl> areaUrlList = areaUrlMapper.select(areaUrlRecord);
        if (areaUrlList == null || areaUrlList.size() == 0 || areaUrlList.get(0).getTotal() != areaUrlList.size()) {


            //本地数据不全
            String content = getContent(baseUrl + url).toUpperCase();
            String[] citys = content.split("CITYTR");
            //'><TD><A HREF='11/1101.HTML'>110100000000</A></TD><TD><A HREF='11/1101.HTML'>市辖区</A></TD></td><TR CLASS='
//            AreaUrl area = null;
            List<AreaUrl> tempList = new ArrayList<>();
            for (int c = 1, len = citys.length; c < len; c++) {
                String[] strs = citys[c].split("<A HREF='");
                String cityUrl = null;
                for (int si = 1; si < 3; si++) {
                    if (si == 1) {//取链接和编码
                        cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                        String cityCode = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));

                    } else {
                        String cityName = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));

//                    System.out.println("市 name:" + cityName);

                        //插入数据库
                        AreaUrl record = new AreaUrl();
                        record.setName(cityName);
                        record.setParentId(parentId);
                        record.setPrix(cityUrl.substring(0, cityUrl.indexOf("/") + 1));
                        record.setUrl(cityUrl);
                        tempList.add(record);

//                        System.out.println("爬取:" + strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>")));
                    }
                }
//                readXian(cityUrl.substring(0, cityUrl.indexOf("/") + 1), cityUrl, area.getId());
            }
            for (AreaUrl areaUrl : tempList) {
                areaUrl.setTotal(tempList.size());
            }
            if (areaUrlList == null || areaUrlList.size() == 0) {
                if (tempList.size() != 0) {
                    areaUrlMapper.insertList(tempList);
                }
            } else {
                for (AreaUrl areaUrl : tempList) {
                    AreaUrl area = areaUrlMapper.selectOne(areaUrl);
                    if (area != null) {
                        continue;
                    }
                    areaUrlMapper.insert(areaUrl);
                }
            }
            for (AreaUrl areaUrl : tempList) {
                System.out.println("爬取:" + areaUrl.getName());
                readXian(areaUrl.getPrix(), areaUrl.getUrl(), areaUrl.getId());
            }
        } else {
//            System.out.println("市：  本地数据获取去成功");
//            System.out.println("size: " + areaUrlList.size() + ";   total: " + areaUrlList.get(0).getTotal());
            for (AreaUrl areaUrl : areaUrlList) {
                System.out.println("    local------------------------------------------------"+areaUrl.getName());
                readXian(areaUrl.getPrix(), areaUrl.getUrl(), areaUrl.getId());
            }
        }

    }

    /**
     * 读县的数据
     *
     * @param url
     * @throws Exception
     */
    public void readXian(String prix, String url, Integer parentId) throws Exception {

        //获取本地数据
        AreaUrl areaUrlRecord = new AreaUrl();
        areaUrlRecord.setParentId(parentId);
        List<AreaUrl> areaUrlList = areaUrlMapper.select(areaUrlRecord);
        if (areaUrlList == null || areaUrlList.size() == 0 || areaUrlList.get(0).getTotal() != areaUrlList.size()) {
            String content = getContent(baseUrl + url).toUpperCase();
            String[] citys = content.split("COUNTYTR");
//            AreaUrl area = null;

            List<AreaUrl> tempList = new ArrayList<>();
            for (int i = 1; i < citys.length; i++) {
                String cityUrl = null;

                //发现石家庄有一个县居然没超链接，特殊处理
                if (citys[i].indexOf("<A HREF='") == -1) {

                } else {
                    String[] strs = citys[i].split("<A HREF='");
                    for (int si = 1; si < 3; si++) {
                        if (si == 1) {//取链接和编码
                            cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                            String cityCode = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));

                        } else {

                            //县 name
                            String name = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));
//                            System.out.println("爬取 县 name:" + name);

                            //插入数据库
                            AreaUrl record = new AreaUrl();
                            record.setName(name);
                            record.setParentId(parentId);
                            record.setPrix(prix);
                            record.setUrl(cityUrl);

                            tempList.add(record);
                        }
                    }
                }
               /* if (null != cityUrl) {
                    readZhen(prix, cityUrl, area.getId());
                }*/
            }
            /*for (AreaUrl areaUrl : tempList) {
                areaUrl.setTotal(tempList.size());
                AreaUrl area = areaUrlMapper.selectOne(areaUrl);
                if (area != null) {
                    continue;
                }
                areaUrlMapper.insert(areaUrl);
                *//*if (!provinceName.equals(areaUrl.getName())) {
                    continue;
                }*//*
//                System.out.println("爬取:" + areaUrl.getName());

            }*/
            for (AreaUrl areaUrl : tempList) {
                areaUrl.setTotal(tempList.size());
            }
            if (areaUrlList == null || areaUrlList.size() == 0) {
                if (tempList.size() != 0) {
                    areaUrlMapper.insertList(tempList);
                }
            } else {
                for (AreaUrl areaUrl : tempList) {
                    AreaUrl area = areaUrlMapper.selectOne(areaUrl);
                    if (area != null) {
                        continue;
                    }
                    areaUrlMapper.insert(areaUrl);
                }
            }
            for (AreaUrl areaUrl : tempList) {
                System.out.println("爬取:" + areaUrl.getName());
                if (null != areaUrl.getUrl()) {
                    readZhen(areaUrl.getPrix(), areaUrl.getUrl(), areaUrl.getId());
                }
            }
        } else {
//            System.out.println("爬取： 县 本地数据获取去成功");
//            System.out.println("size: " + areaUrlList.size() + ";   total: " + areaUrlList.get(0).getTotal());
            for (AreaUrl areaUrl : areaUrlList) {
                System.out.println("    local------------------------------------------------"+areaUrl.getName());
                if (areaUrl.getUrl() != null) {
                    readZhen(prix, areaUrl.getUrl(), areaUrl.getId());
                }
            }
        }

    }

    /**
     * 读镇的数据
     *
     * @param url
     * @throws Exception
     */
    public void readZhen(String prix, String url, Integer parentId) throws Exception {


        //获取本地数据
        AreaUrl areaUrlRecord = new AreaUrl();
        areaUrlRecord.setParentId(parentId);
        List<AreaUrl> areaUrlList = areaUrlMapper.select(areaUrlRecord);
        if (areaUrlList == null || areaUrlList.size() == 0 || areaUrlList.get(0).getTotal() != areaUrlList.size()) {
            //本地数据不全
            String content = getContent(baseUrl + prix + url).toUpperCase();
            String myPrix = (prix + url).substring(0, (prix + url).lastIndexOf("/") + 1);
            String[] citys = content.split("TOWNTR");
//            AreaUrl area = null;

            List<AreaUrl> tempList = new ArrayList<>();

            for (int i = 1; i < citys.length; i++) {
                String[] strs = citys[i].split("<A HREF='");
                String cityUrl = null;
                for (int si = 1; si < 3; si++) {
                    if (si == 1) {//取链接和编码
                        cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                        String cityCode = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));
                    } else {

                        //镇 name
                        String name = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));

                        //插入数据库
                        AreaUrl record = new AreaUrl();
                        record.setName(name);
                        record.setParentId(parentId);
                        record.setPrix(myPrix);
                        record.setUrl(cityUrl);

                        tempList.add(record);

                       /* area = areaUrlMapper.selectOne(record);
                        if (area == null) {
                            areaUrlMapper.insert(record);
                            area = record;
                        }*/
                    }
                }
//                readCun(myPrix, cityUrl, area.getId());
            }
            /*for (AreaUrl areaUrl : tempList) {
                areaUrl.setTotal(tempList.size());
                AreaUrl area = areaUrlMapper.selectOne(areaUrl);
                if (area != null) {
                    continue;
                }
                areaUrlMapper.insert(areaUrl);
                *//*if (!provinceName.equals(areaUrl.getName())) {
                    continue;
                }*//*
//                System.out.println("爬取:" + areaUrl.getName());

            }*/
            for (AreaUrl areaUrl : tempList) {
                areaUrl.setTotal(tempList.size());
            }
            if (areaUrlList == null || areaUrlList.size() == 0) {
                if (tempList.size() != 0) {
                    areaUrlMapper.insertList(tempList);
                }
            } else {
                for (AreaUrl areaUrl : tempList) {
                    AreaUrl area = areaUrlMapper.selectOne(areaUrl);
                    if (area != null) {
                        continue;
                    }
                    areaUrlMapper.insert(areaUrl);
                }
            }
            for (AreaUrl areaUrl : tempList) {
                readCun(areaUrl.getPrix(), areaUrl.getUrl(), areaUrl.getId());
            }
        } else {
//            System.out.println("镇：  本地数据获取去成功");
//            System.out.println("size: " + areaUrlList.size() + ";   total: " + areaUrlList.get(0).getTotal());

            for (AreaUrl areaUrl : areaUrlList) {
                System.out.println("    local------------------------------------------------"+areaUrl.getName());
                readCun(areaUrl.getPrix(), areaUrl.getUrl(), areaUrl.getId());
            }
        }

    }

    /**
     * 读村/街道的数据
     *
     * @param url
     * @throws Exception
     */
    public void readCun(String prix, String url, Integer parentId) throws Exception {
//        System.out.println("aaa");

        //获取本地数据
        AreaUrl areaUrlRecord = new AreaUrl();
        areaUrlRecord.setParentId(parentId);
        List<AreaUrl> areaUrlList = areaUrlMapper.select(areaUrlRecord);

        if (areaUrlList == null || areaUrlList.size() == 0 || areaUrlList.get(0).getTotal() != areaUrlList.size()) {
            String content = getContent(baseUrl + prix + url).toUpperCase();
            String[] citys = content.split("VILLAGETR");

            List<AreaUrl> tempList = new ArrayList<>();

            for (int i = 1; i < citys.length; i++) {
                String[] strs = citys[i].split("<TD>");

                //村 街道 name
                String name = strs[3].substring(0, strs[3].indexOf("</TD>"));
//            System.out.println("村 街道 name:" + name);

                //插入数据库
                AreaUrl record = new AreaUrl();
                record.setName(name);
                record.setParentId(parentId);

                tempList.add(record);
            }
            /*for (AreaUrl areaUrl : tempList) {
                areaUrl.setTotal(tempList.size());
                AreaUrl area = areaUrlMapper.selectOne(areaUrl);
                if (area != null) {
                    continue;
                }
                areaUrlMapper.insert(areaUrl);
                *//*if (!provinceName.equals(areaUrl.getName())) {
                    continue;
                }*//*
            }*/
            for (AreaUrl areaUrl : tempList) {
                areaUrl.setTotal(tempList.size());
            }
            if (areaUrlList == null || areaUrlList.size() == 0) {
                if (tempList.size() != 0) {
                    areaUrlMapper.insertList(tempList);
                }
            } else {
                for (AreaUrl areaUrl : tempList) {
                    AreaUrl area = areaUrlMapper.selectOne(areaUrl);
                    if (area != null) {
                        continue;
                    }
                    areaUrlMapper.insert(areaUrl);
                }
            }

        } else {
            for (AreaUrl areaUrl : areaUrlList) {
                System.out.println("    local------------------------------------------------"+areaUrl.getName());
            }
        }


    }


    //设置代理
    public void initProxy(String host, String port) {
        System.setProperty("http.proxyType", "4");
        System.setProperty("http.proxyPort", port);
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxySet", "true");
    }


    //获取网页的内容
    public String getContent(String strUrl) throws Exception {
        try {
            System.out.println(strUrl);
            URL url = new URL(strUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), Charset.forName(CHARSET)));
            String s = "";
            StringBuffer sb = new StringBuffer("");
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            br.close();
            return sb.toString();
        } catch (Exception e) {
            System.out.println("can't open url:" + strUrl);
            throw e;
        }
    }
}