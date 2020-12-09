package com.address;

import com.address.mapper.AreaMapper;
import com.address.service.TrimService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Created by Meng Kai
 * @Date 2020/11/21
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ReadCodeFromWebTest {

    @Autowired
    private ReadCodeFromWeb readCodeFromWeb;

    @Autowired
    private ReadCodeFromWebUrl readCodeFromWebUrl;

    @Autowired
    private TrimService trimService;

   /* @Test
    public void readProvince() throws Exception {
        readData();
    }

    public void readData() {
        try {
            readCodeFromWeb.readProvince("内蒙古自治区");
        } catch (Exception e) {
            readData();
        }
    }*/


    @Test
    public void readProvinceUrl() throws Exception {
        readDataUrl();
    }

    public void readDataUrl() throws Exception {
        readCodeFromWebUrl.readProvince("新疆维吾尔自治区");
       /* try {
            readCodeFromWebUrl.readProvince("河北省");
        } catch (Exception e) {
            readDataUrl();
        }*/
    }

    @Test
    public void stringTest() {
        String str = "123456789";
        String substring = str.substring(1,2);
        System.out.println(substring);
    }

    @Test
    public void trimTest() {
        trimService.trimEnd();
    }
}