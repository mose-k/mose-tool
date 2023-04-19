package com.mose.util.cache.common;

import cn.hutool.core.util.StrUtil;

public class MoseIDUtil {
    private static String prefix;
    private static final String MOSE_KEYS="MoseTool:*";
    private static final String MOSE_CACHE_KEY="MoseTool:MoseCacheKey:";
    private static final String Mose_Lock_Key="MoseTool:MoseLockKey:";

    private static final String MOSE_DES_KEY="MoseTool:MoseDesKey";
    public static void setPrefix(String prefix) {
        MoseIDUtil.prefix = prefix;
    }
    public static String getMoseKeys() {
        return StrUtil.concat(true,prefix,MOSE_KEYS);
    }
    public static String getMoseCacheKey(String fp) {
        return StrUtil.concat(true,prefix,MOSE_CACHE_KEY,fp);
    }
    public static String getMoseDesKey() {
        return StrUtil.concat(true,prefix,MOSE_DES_KEY);
    }

    public static String getMoseLockKey(String fp) {
        return StrUtil.concat(true,prefix,Mose_Lock_Key,fp);
    }


}
