## mose工具类
#### 默认配置

* 配置spring redistemplate
* @EnableMoseTool 开启注解

### 示例

    @EnableMoseTool
    @SpringBootApplication
    public class Application {
    }

### 使用redis缓存
#### @MoseCache 注解属性

        /**
         * 缓存时间 默认10分钟
         */
        long time() default 10l;
        /**
         * 缓存时间单位 分钟
         */
        TimeUnit timeUnit() default TimeUnit.MINUTES;
        /**
         * 缓存数据 是否加密
         * 暂时为 简单的des加密
         */
        boolean isEncrypted() default false;
        /**
         * 刷新缓存  等待的重试次数
         */
        int retryCount() default 5;
        /**
         * 刷新缓存  重试的等待时间 默认0.5s
         */
        long waitMillis() default 500;


##### 示例

    @MoseCache
    public Object ex(){
        logger.info("-------------ex----------");
        return null;
    }

### 使用redis分布式锁
#### @MoseLock 注解属性

      /**
        * 锁过期时间 默认3分钟
        */
      long time() default 3l;

      /**
       * 锁过期时间单位 分钟
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
       */
      String errInfo() default "The method is executing";

      /**
       * 策略为等待的时候  等待的重试次数
       * 默认等待时间 5s   10*0.5s
       */
      int retryCount() default 10;

      /**
       * 策略为等待的时候  重试的等待时间 默认0.5s
       */
      long waitMillis() default 500;

##### 示例

    @MoseLock
    public Object ex(){
        logger.info("-------------ex----------");
        return null;
    }