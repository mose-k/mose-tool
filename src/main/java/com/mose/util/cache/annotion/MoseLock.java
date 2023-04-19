package com.mose.util.cache.annotion;

import com.mose.util.cache.enums.MoseLockStrategyEnum;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 简单的分布式锁
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD})
public @interface MoseLock {
    /**
     * 锁过期时间 默认3分钟
     *
     * @return
     */
    long time() default 3l;

    /**
     * 锁过期时间单位 分钟
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * 执行策略
     * 默认  等待执行
     * THROW_EXCEPTION 抛出异常 默认抛出runtime异常 可修改异常提示信息
     * WAIT   等待重试 需设置重试次数与等待时间 超时执行抛出异常策略
     * RETURN_NUll  默认返回null值
     */
    MoseLockStrategyEnum strategy() default MoseLockStrategyEnum.WAIT;

    /**
     * 异常提示信息
     *
     * @return
     */
    String errInfo() default "The method is executing";

    /**
     * 策略为等待的时候  等待的重试次数
     * 默认等待时间 5s   10*0.5s
     *
     * @return
     */
    int retryCount() default 10;

    /**
     * 策略为等待的时候  重试的等待时间 默认0.5s
     *
     * @return
     */
    long waitMillis() default 500;

}
