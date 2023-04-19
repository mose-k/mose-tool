package com.mose.util;

import com.mose.util.cache.annotion.MoseCache;
import com.mose.util.cache.annotion.MoseLock;
import com.mose.util.cache.enums.MoseLockStrategyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GoodsService {
    private final static Logger logger = LoggerFactory.getLogger(GoodsService.class);

    @MoseCache(isEncrypted = true)
    public String get(String s){
       logger.info("----------GoodsService---get--------");
        return "test get {}"+s;
    }

    @MoseCache
    public int get(int code){
        logger.info("----------GoodsService---get---code-----");
        return code;
    }

    @MoseCache(isEncrypted = true)
    public Goods getGoods(){
        logger.info("----------GoodsService---getGoods--------");
        return new Goods("goods_name","25");
    }
    @MoseCache(isEncrypted = true)
    public Goods getGoods(Goods goods){
        logger.info("----------GoodsService---getGoods----goods----");
//        throw  new RuntimeException("err test");
        return goods;
    }

    @MoseCache(isEncrypted = true)
    public void ex(){
        logger.info("----------GoodsService---ex--------");
    }


    @MoseLock(strategy = MoseLockStrategyEnum.THROW_EXCEPTION)
    public int singleEx(){
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("----------GoodsService---singleEx--------");
        return 9527;
    }

}
