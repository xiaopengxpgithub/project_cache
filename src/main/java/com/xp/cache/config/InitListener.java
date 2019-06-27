package com.xp.cache.config;

import com.xp.cache.kafka.KafkaConsumerThread;
import com.xp.cache.rebuild.RebuildCacheThread;
import com.xp.cache.zookeeper.ZookeeperSession;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 系统初始化监听器
 */
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //获取spring容器,从而获取spring容器中的bean
        ServletContext sc = servletContextEvent.getServletContext();
        WebApplicationContext context =WebApplicationContextUtils.getWebApplicationContext(sc);
        SpringContext.setApplicationContext(context);

        //项目启动,开启kafka消费者线程,监听指定的topic接受消息
        new Thread(new KafkaConsumerThread("cache-messagecache-message")).start();

        //启动zookeeper session
        ZookeeperSession.init();

        //启动缓存重建线程
        new Thread(new RebuildCacheThread()).start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
