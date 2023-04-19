package com.mose.util.cache.common;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 简单 的des 加密
 */
public class DesUtil {

    private final static Logger logger = LoggerFactory.getLogger(DesUtil.class);

    private static String KEY;

    private static DES des;


    public static void initialize(String key) {
        KEY = key;
        des = SecureUtil.des(
                SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue(), key.getBytes()).getEncoded());
    }

    public static void refresh() {
        String moseDesKey = MoseIDUtil.getMoseDesKey();
        String desKey;
        if (RedisUtil.hasKey(moseDesKey)) {
            desKey = RedisUtil.get(moseDesKey).toString();
        } else {
            desKey = IdUtil.simpleUUID();
            boolean b = RedisUtil.setIfAbsent(moseDesKey, desKey, 30, TimeUnit.DAYS);
            int count = 0;
            while (!b) {
                try {
                    Thread.sleep(500);
                    b = RedisUtil.hasKey(moseDesKey);
                    if (b) {
                      return;
                    }
                } catch (InterruptedException e) {
                    logger.warn(ExceptionUtil.stacktraceToString(e));
                } finally {
                    count++;
                }
                if (count > 10) {
                    return;
                }
            }
            //refresh data

        }
        DesUtil.initialize(desKey);
    }

    /**
     * 根据KEY生成DES
     */


    /**
     * 获取加密后信息
     *
     * @param plainText 明文
     * @return 加密后信息
     */
    public static String getEncryptData(String plainText) {
        return des.encryptHex(plainText);
    }

    /**
     * 获取解密后信息
     *
     * @param cipherText 密文
     * @return 解密后信息
     */
    public static String getDecryptData(String cipherText) {
        return des.decryptStr(cipherText);
    }


}
