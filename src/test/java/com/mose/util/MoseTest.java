package com.mose.util;


import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
//@SpringBootTest(classes = StartApplication.class)
public class MoseTest {
    private final static Logger logger = LoggerFactory.getLogger(GoodsService.class);
    @Autowired
    GoodsService goodsService;

    /**
     * 测试 @MoseCache
     */
    @Test
    public void  testMoseCache(){
        for (int i = 1; i <=10; i++) {
            System.out.println("------------- "+i+" --------------");
//            Goods goods = goodsService.getGoods();
//            System.out.println(goods);
//            goodsService.ex();
//            int code = goodsService.get(12);
//            System.out.println(code);
            System.out.println(goodsService.getGoods(new Goods("goods","02")));
        }

    }

    /**
     * 测试 @MoseCache
     */
    @Test
    public void  testThreadMoseCache() throws InterruptedException {
        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(10);
        for (int i = 1; i <=10; i++) {
            new Thread(()->{
                Goods goods = goodsService.getGoods();
                logger.info(goods.toString());
                goodsService.ex();
                logger.info(goodsService.get("good"));
                Goods g = goodsService.getGoods(new Goods("goods", "01"));
                logger.info(g.toString());
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();
    }

    /**
     * 测试  MoseLock
     * @throws InterruptedException
     */
    @Test
    public void  testThreadMoseLock() throws InterruptedException {
        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(10);
        for (int i = 1; i <=10; i++) {
            new Thread(()->{
                int res = goodsService.singleEx();
                logger.info(String.valueOf(res));
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
    }

    /**
     * 清除缓存
     */
    @Test
    public void  clear(){
        MoseToolUtil.clear();
    }

}
