package com.mose.util.cache.annotion;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD})
public @interface MoseCache {

    /**
     * 缓存时间 默认10分钟
     *
     * @return
     */
    long time() default 10l;

    /**
     * 缓存时间单位 分钟
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * 缓存数据 是否加密
     * 暂时为 简单的des加密
     * @return
     */
    boolean isEncrypted() default false;

    /**
     * 刷新缓存  等待的重试次数
     * @return
     */
    int retryCount() default 5;


    /**
     * 刷新缓存  重试的等待时间 默认0.5s
     * @return
     */
    long waitMillis() default 500;

}
