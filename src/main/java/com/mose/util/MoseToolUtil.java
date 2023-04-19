package com.mose.util;

import com.mose.util.cache.common.MoseIDUtil;
import com.mose.util.cache.common.RedisUtil;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class MoseToolUtil {
    private final static Logger logger = LoggerFactory.getLogger(MoseToolUtil.class);

    /**
     * 清除 MoseTool 缓存及配置
     * 使用了 keys 命令
     */
    public static void clear() {
        RedisUtil.removeAll(MoseIDUtil.getMoseKeys());
        logger.info("MoseToolUtil clearing succeeded");
    }

    /**
     * 打印对象内存大小
     *
     * @param o
     */
    public static BigDecimal printObjectSize(Object o) {
        logger.debug("--------------------------------Object Info start--------------------------------");
        logger.debug(ClassLayout.parseInstance(o).toPrintable());
        logger.debug("--------------------------------Object Info  end--------------------------------");
        double l = GraphLayout.parseInstance(o).totalSize();
        BigDecimal bigDecimal = new BigDecimal(l);
        bigDecimal = bigDecimal.divide(BigDecimal.valueOf(1024 * 1024))
                .setScale(4, BigDecimal.ROUND_HALF_UP);
        logger.debug("Object Info Memory Size: {} M", bigDecimal);
        return bigDecimal;
    }


}
