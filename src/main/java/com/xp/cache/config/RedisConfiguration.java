package com.xp.cache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedisConfiguration {

    @Configuration
    public class RedisConfig {

        @Bean
        public JedisCluster JedisClusterFactory() {
            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            //redis集群,6个节点,三台机器
            jedisClusterNodes.add(new HostAndPort("192.168.1.121", 6371));
            jedisClusterNodes.add(new HostAndPort("192.168.1.122", 6372));
            jedisClusterNodes.add(new HostAndPort("192.168.1.123", 6373));
            jedisClusterNodes.add(new HostAndPort("192.168.1.121", 6381));
            jedisClusterNodes.add(new HostAndPort("192.168.1.122", 6382));
            jedisClusterNodes.add(new HostAndPort("192.168.1.123", 6383));
            JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);

            return jedisCluster;
        }
    }
}
