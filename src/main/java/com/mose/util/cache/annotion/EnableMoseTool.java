package com.mose.util.cache.annotion;

import com.mose.util.cache.config.MoseConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动注解
 * @author mose
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({MoseConfiguration.class})
@Documented
public @interface EnableMoseTool {

}
