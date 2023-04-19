package com.mose.util.cache.aop;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.mose.util.cache.annotion.MoseLock;
import com.mose.util.cache.common.DesUtil;
import com.mose.util.cache.common.MoseIDUtil;
import com.mose.util.cache.common.RedisUtil;
import com.mose.util.cache.enums.MoseCacheTimeEnum;
import com.mose.util.cache.enums.MoseLockStrategyEnum;
import com.mose.util.cache.enums.RetryEnum;
import com.mose.util.cache.annotion.MoseCache;
import com.mose.util.cache.exception.ExecuteException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Aspect
@Component
public class MoseCacheExecutor {
    private final static Logger logger = LoggerFactory.getLogger(MoseCacheExecutor.class);

    @Pointcut("@annotation(com.mose.util.cache.annotion.MoseCache)")
    private void cache() {
    }

    @Pointcut("@annotation(com.mose.util.cache.annotion.MoseLock)")
    private void moseLock() {
    }

    @Around("moseLock()")
    public Object moseLockAround(ProceedingJoinPoint point) {
        Object proceed = null;
        MoseLock moseLock = null;
        try {
            Signature signature = point.getSignature();
            if (signature instanceof MethodSignature) {
                MethodSignature methodSignature = (MethodSignature) signature;
                Method method = methodSignature.getMethod();
                moseLock = method.getAnnotation(MoseLock.class);
                Type genericReturnType = method.getGenericReturnType();
                String key = generateKey(method, point.getArgs(), genericReturnType);
                boolean flag = RedisUtil.setIfAbsent(key, "1", moseLock.time(),
                        moseLock.timeUnit());
                if (flag) {
                    try {
                        proceed = point.proceed();
                    } catch (Throwable e) {
                        throw new ExecuteException(e);
                    }finally {
                        RedisUtil.del(key);
                    }
                    return proceed;
                } else {
                    MoseLockStrategyEnum strategy = moseLock.strategy();
                    switch (strategy) {
                        case WAIT:
                            int count = 0;
                            do {
                                Thread.sleep(moseLock.waitMillis());
                                count++;
                                if (count >= moseLock.retryCount()) {
                                    throw new ExecuteException(moseLock.errInfo());
                                }
                            } while (!RedisUtil.setIfAbsent(key, "1", MoseCacheTimeEnum.FIVE_MINUTES.getTime(),
                                    MoseCacheTimeEnum.FIVE_MINUTES.getTimeUnit()));
                            try {
                                proceed = point.proceed();
                            } catch (Throwable e) {
                                throw new ExecuteException(e);
                            } finally {
                                RedisUtil.del(key);
                            }
                            return proceed;
                        case THROW_EXCEPTION:
                            throw new ExecuteException(moseLock.errInfo());
                        case RETURN_NUll:
                            if (method.getReturnType().isPrimitive()){
                                throw new ExecuteException("The execution strategy is RETURN_NUll, and the method return type not supports the basic type!");
//                                throw new ExecuteException("执行策略为 RETURN_NUll，方法返回值不支持基本类型!");
                            }
                            return null;
                    }
                }


            }
        } catch (ExecuteException e){
            throw e;
        } catch (Exception e) {
            logger.error("@MoseCache error :{}",
                    ExceptionUtil.stacktraceToString(e));
        }
        return proceed;
    }

    @Around("cache()")
    public Object moseCacheAround(ProceedingJoinPoint point) {
        Object proceed = null;
        MoseCache moseCache = null;
        try {
            Signature signature = point.getSignature();
            if (signature instanceof MethodSignature) {
                MethodSignature methodSignature = (MethodSignature) signature;
                Method method = methodSignature.getMethod();
                moseCache = method.getAnnotation(MoseCache.class);
                Type genericReturnType = method.getGenericReturnType();
                if (Void.TYPE == TypeUtil.getReturnType(method)) {
                    logger.warn(" @MoseCache are invalid in the void method ({}.{}())!", method.getDeclaringClass().getName(), method.getName());
                } else {
                    String key = generateKey(method, point.getArgs(), genericReturnType);
                    String retryKey = key + "retry";
                    if (moseCache.isEncrypted()) {
                        proceed = RedisUtil.get(key);
                        if (!StringUtils.isEmpty(proceed)) {
                            String decryptData = DesUtil.getDecryptData(proceed.toString());
                            try {
                                return JSONObject.parseObject(decryptData, genericReturnType);
                            } catch (Exception e) {
                                RedisUtil.del(key);
                                DesUtil.refresh();
                            }
                        }
                        RetryEnum retry = retry(key, retryKey, moseCache.retryCount(), moseCache.waitMillis());
                        switch (retry) {
                            case FAIL:
                                break;
                            case SUCCEED:
                                try {
                                    proceed = point.proceed();
                                } catch (Throwable e) {
                                    RedisUtil.del(retryKey);
                                    throw new ExecuteException(e);
                                }
                                String encryptData = DesUtil.getEncryptData(JSONObject.toJSONString(proceed));
                                RedisUtil.set(key, encryptData,
                                        moseCache.time(), moseCache.timeUnit());
                                RedisUtil.del(retryKey);
                                return proceed;
                            case EXECUTED:
                                proceed = RedisUtil.get(key);
                                try {
                                    JSONObject jsonObject = JSONObject.parseObject(
                                            DesUtil.getDecryptData(proceed.toString()));
                                    Object parse = jsonObject.toJavaObject(genericReturnType);
                                    return parse;
                                } catch (Exception e) {
                                    RedisUtil.del(key);
                                    DesUtil.refresh();
                                }
                                break;
                        }
                    } else {
                        proceed = RedisUtil.get(key);
                        if (proceed != null) {
                            return proceed;
                        }
                        RetryEnum retry = retry(key, retryKey, moseCache.retryCount(), moseCache.waitMillis());
                        switch (retry) {
                            case FAIL:
                                break;
                            case SUCCEED:
                                try {
                                    proceed = point.proceed();
                                } catch (Throwable e) {
                                    RedisUtil.del(retryKey);
                                    throw new ExecuteException(e);
                                }
                                RedisUtil.set(key, proceed, moseCache.time(), moseCache.timeUnit());
                                RedisUtil.del(retryKey);
                                return proceed;
                            case EXECUTED:
                                proceed = RedisUtil.get(key);
                                if (proceed != null) {
                                    return proceed;
                                }
                                break;
                        }
                    }
                }
            }
        }catch (ExecuteException e){
            throw e;
        } catch (Exception e) {
            logger.error("@MoseCache error :{}",
                    ExceptionUtil.stacktraceToString(e));
        }
        try {
            proceed = point.proceed();
            return proceed;
        } catch (Throwable e) {
            throw new ExecuteException(e);
        }

    }

    private String generateKey(Method method, Object[] args, Type genericReturnType) {
        logger.debug("method: {}", JSONObject.toJSONString(method));
        logger.debug("args: {}", JSONObject.toJSONString(args));
        logger.debug("genericReturnType: {}", JSONObject.toJSONString(genericReturnType));
        String key = MoseIDUtil.getMoseCacheKey(SecureUtil.md5(
                StrUtil.concat(true,
                        JSONObject.toJSONString(method.getDeclaringClass()),
                        JSONObject.toJSONString(method.getName()),
                        JSONObject.toJSONString(method.getParameterTypes()),
                        JSONObject.toJSONString(method.getAnnotations()),
                        JSONObject.toJSONString(genericReturnType),
                        JSONObject.toJSONString(args)
                )
        ));
        return key;
    }


    private RetryEnum retry(String key, String retryKey, int retryCount, long waitMillis) {
        boolean b = RedisUtil.setIfAbsent(retryKey, "1", MoseCacheTimeEnum.THREE_MINUTES.getTime(),
                MoseCacheTimeEnum.THREE_MINUTES.getTimeUnit());
        int count = 0;
        while (!b) {
            try {
                Thread.sleep(waitMillis);
                b = RedisUtil.hasKey(key);
                if (b) {
                    return RetryEnum.EXECUTED;
                }
            } catch (InterruptedException e) {
                logger.warn(ExceptionUtil.stacktraceToString(e));
            } finally {
                count++;
            }
            if (count > retryCount) {
                return RetryEnum.FAIL;
            }
        }
        return RetryEnum.SUCCEED;
    }


}
