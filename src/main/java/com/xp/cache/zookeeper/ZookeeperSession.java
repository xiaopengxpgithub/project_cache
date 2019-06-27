package com.xp.cache.zookeeper;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * zookeeper管理类
 */
public class ZookeeperSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperSession.class);

    //计数栓
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private ZooKeeper zookeeper;

    public ZookeeperSession() {
        // 去连接zookeeper server,创建会话的时候,是异步去进行的
        // 所以要给一个监听器,说告诉我们什么时候才是真正完成了跟zk server的连接
        try {
            this.zookeeper = new ZooKeeper(
                    "192.168.1.121:2181,192.168.1.122:2181,192.168.1.123:2181",
                    50000,
                    new ZooKeeperWatcher());

            // 提示一个链接状态
            LOGGER.info(zookeeper.getState().name());

            try {
                // CountDownLatch计数栓:java多线程并发同步的一个工具类
                // 当某个线程执行await(),判断CountDownLatch的值,如果不是0,那么就卡住,等待

                // 其他的线程可以调用CountDownLatch的countDown(),减1
                // 如果数字减到0,那么之前所有在await的线程,都会逃出阻塞的状态,继续向下运行
                // 只要有一个线程成功链接了zookeeper,那么之后的线程就不会再等待了,直接执行后面的业务逻辑
                connectedSemaphore.await();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LOGGER.info("ZooKeeper session established......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取分布式锁/创建一个临时的node
     * @param productId
     */
    public void acquireDistributedLock(Integer productId) {
        String path = "/product-lock-" + productId;

        try {
            //根据商品ID创建临时的node
            zookeeper.create(path, "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            LOGGER.info("success to acquire lock for product[id=" + productId + "] ");

        } catch (Exception e) {
            // 如果商品对应的锁的node已经存在了,就是已经被别人加锁了,那么就这里就会报错"NodeExistsException"
            int count = 0;
            // 当前线程获取锁失败,在这里循环,等待
            while (true) {

                try {
                    //每隔1000毫秒尝试获取一次锁
                    Thread.sleep(1000);
                    zookeeper.create(path, "".getBytes(),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    count++;

                    //继续循环
                    continue;
                }

                LOGGER.info("success to acquire lock for product[id=" + productId + "] after " + count + " times try......");

                //当创建成功之后,跳出循环,结束该方法
                break;
            }
        }
    }

    /**
     * 释放掉一个分布式锁/删除之前根据商品ID创建的node
     *
     * @param productId
     */
    public void releaseDistributedLock(Integer productId) {
        String path = "/product-lock-" + productId;
        try {
            zookeeper.delete(path, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立zk session的watcher,监控zookeeper的链接状态,(只要一个线程)链接一次即可
     * @author Administrator
     */
    private class ZooKeeperWatcher implements Watcher {
        public void process(WatchedEvent event) {
            LOGGER.info("Receive watched event: " + event.getState());
            if (Event.KeeperState.SyncConnected == event.getState()) {
                //如果链接成功,则解除线程的阻塞状态
                connectedSemaphore.countDown();
            }
        }
    }

    /**
     * 封装单例的静态内部类
     */
    private static class Singleton {

        private static ZookeeperSession instance;

        static {
            instance = new ZookeeperSession();
        }

        public static ZookeeperSession getInstance() {
            return instance;
        }

    }

    /**
     * 获取单例
     *
     * @return
     */
    public static ZookeeperSession getInstance() {
        return Singleton.getInstance();
    }

    /**
     * 初始化单例的便捷方法
     */
    public static void init() {
        getInstance();
    }
}
