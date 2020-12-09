package com.address;

import com.address.mapper.AreaMapper;
import com.address.pojo.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @Created by Meng Kai
 * @Date 2020/11/21
 */
@Component
public class ReadCodeFromWeb2HTML {

    @Autowired
    private AreaMapper areaMapper;

    public static final String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2020/";
    //设置utf-8发现有部分字符有乱码
    public static final String CHARSET = "GBK";

    public static StringBuffer result = new StringBuffer();


    /**
     * 读省的信息
     *
     * @param provinceName
     * @throws Exception
     */
    public void readProvince(String provinceName) throws Exception {
        System.out.println();
        String url = baseUrl + "index.html";
        //如果需要设置代理
        //initProxy("10.10.13.200", "80");
        String str = getContent(url).toUpperCase();
        String[] arrs = str.split("<A");


        for (String s : arrs) {
            if (s.indexOf("HREF") != -1 && s.indexOf(".HTML") != -1) {
                String a = s.substring(7, s.indexOf("'>"));

                String name = s.substring(s.indexOf("'>") + 2, s.indexOf("<BR/>"));

                //省name
//                System.out.println("省 name:" + name);

                System.out.println(name);
                if (!provinceName.equals(name)) {
                    continue;
                }

                FileWriter fw = new FileWriter(new File("F:/全国地址" + name + ".html"));
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body><table border='1' bordercolor='#000000' style='border-collapse:collapse'><tr><td>代码</td><td>省</td><td>市</td><td>县</td><td>镇</td><td>城乡分类</td><td>村/街道</td></tr>");
                bw.newLine();
                bw.write("<tr><td></td><td>");
                bw.write(name);
                bw.write("</td><td></td><td></td><td></td><td></td><td></td></tr>");

                bw.newLine();

                System.out.println("爬取:" + name);

                readShi(a, bw);

                bw.newLine();
                bw.write("</table></body></html>");
                bw.flush();
                bw.close();
            }
        }
    }

    /**
     * 读市的数据
     *
     * @param bw
     * @throws Exception
     */
    public void readShi(String url, BufferedWriter bw) throws Exception {
        String content = getContent(baseUrl + url).toUpperCase();
        String[] citys = content.split("CITYTR");
        //'><TD><A HREF='11/1101.HTML'>110100000000</A></TD><TD><A HREF='11/1101.HTML'>市辖区</A></TD></td><TR CLASS='
        Area area = null;
        for (int c = 1, len = citys.length; c < len; c++) {
            String[] strs = citys[c].split("<A HREF='");
            String cityUrl = null;
            for (int si = 1; si < 3; si++) {
                if (si == 1) {//取链接和编码
                    cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                    String cityCode = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));

                    bw.write("<tr><td>");
                    bw.write(cityCode);
                    bw.write("</td>");
                } else {
                    bw.write("<td></td><td>");
                    bw.write(strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>")));
                    bw.write("</td><td></td><td></td><td></td><td></td></tr>");
                    String cityName = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));

//                    System.out.println("市 name:" + cityName);

                    System.out.println("爬取:" + strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>")));
                }
            }
            bw.newLine();
            readXian(cityUrl.substring(0, cityUrl.indexOf("/") + 1), cityUrl, bw);
        }
    }

    /**
     * 读县的数据
     *
     * @param url
     * @throws Exception
     */
    public void readXian(String prix, String url, BufferedWriter bw) throws Exception {
        String content = getContent(baseUrl + url).toUpperCase();
        String[] citys = content.split("COUNTYTR");
        Area area = null;
        for (int i = 1; i < citys.length; i++) {
            String cityUrl = null;

            //发现石家庄有一个县居然没超链接，特殊处理
            if (citys[i].indexOf("<A HREF='") == -1) {
                bw.write("<tr><td>");
                bw.write(citys[i].substring(6, 18));
                bw.write("</td>");

                bw.write("<td></td><td></td><td>");
                bw.write(citys[i].substring(citys[i].indexOf("</TD><TD>") + 9, citys[i].lastIndexOf("</TD>")));
                bw.write("</td><td></td><td></td><td></td></tr>");
            } else {
                String[] strs = citys[i].split("<A HREF='");
                for (int si = 1; si < 3; si++) {
                    if (si == 1) {//取链接和编码
                        cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                        String cityCode = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));

                        bw.write("<tr><td>");
                        bw.write(cityCode);
                        bw.write("</td>");
                    } else {
                        bw.write("<td></td><td></td><td>");

                        //县 name
//                        String name = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));
//                        System.out.println("县 name:" + name);

                        bw.write(strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>")));
                        bw.write("</td><td></td><td></td><td></td></tr>");
                    }
                }
            }
            bw.newLine();
            if (null != cityUrl) {
                readZhen(prix, cityUrl, bw);
            }
        }
    }

    /**
     * 读镇的数据
     *
     * @param url
     * @throws Exception
     */
    public void readZhen(String prix, String url, BufferedWriter bw) throws Exception {
        String content = getContent(baseUrl + prix + url).toUpperCase();
        String myPrix = (prix + url).substring(0, (prix + url).lastIndexOf("/") + 1);
        String[] citys = content.split("TOWNTR");
        Area area = null;
        for (int i = 1; i < citys.length; i++) {
            String[] strs = citys[i].split("<A HREF='");
            String cityUrl = null;
            for (int si = 1; si < 3; si++) {
                if (si == 1) {//取链接和编码
                    cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                    String cityCode = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));

                    bw.write("<tr><td>");
                    bw.write(cityCode);
                    bw.write("</td>");
                } else {
                    bw.write("<td></td><td></td><td></td><td>");

                    //镇 name
//                    String name = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));
//                    System.out.println("镇 name:" + name);

                    bw.write(strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>")));
                    bw.write("</td><td></td><td></td></tr>");
                }
            }
            bw.newLine();
            readCun(myPrix, cityUrl, bw);
        }
    }

    /**
     * 读村/街道的数据
     *
     * @param url
     * @throws Exception
     */
    public void readCun(String prix, String url, BufferedWriter bw) throws Exception {
        String content = getContent(baseUrl + prix + url).toUpperCase();
        String[] citys = content.split("VILLAGETR");
        for (int i = 1; i < citys.length; i++) {
            String[] strs = citys[i].split("<TD>");

            bw.write("<tr><td>");
            bw.write(strs[1].substring(0, strs[1].indexOf("</TD>")));
            bw.write("</td>");

            bw.write("<td></td><td></td><td></td><td></td>");
            bw.write("<td>");
            bw.write(strs[2].substring(0, strs[2].indexOf("</TD>")));
            bw.write("</td><td>");

            //村 街道 name
//            String name = strs[3].substring(0, strs[3].indexOf("</TD>"));
//            System.out.println("村 街道 name:" + name);

            bw.write(strs[3].substring(0, strs[3].indexOf("</TD>")));
            bw.write("</td></tr>");
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
