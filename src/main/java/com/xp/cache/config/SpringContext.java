package com.xp.cache.config;

import org.springframework.context.ApplicationContext;

/**
 * spring上下文/spring容器,在项目的任何地方都可以获取到spring容器对象
 */
public class SpringContext {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.applicationContext = applicationContext;
    }
}
