package com.xp.cache.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * kafka消费者线程
 */
public class KafkaConsumerThread implements Runnable {

    private String topic;
    private static Properties properties = null;

    //初始化
    static {
        //配置信息
        properties = new Properties();
        //kafka集群地址
        properties.put("bootstrap.servers", "192.168.1.121:9092");
        //消费者组ID
        properties.put("group.id", "test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //消息的offset自动提交
        properties.put("enable.auto.commit", "true");
        //读取数据之后等多长时间再自动提交offset
        properties.put("auto.commit.interval.ms", "1000");
        //kv的反序列化
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    }

    public KafkaConsumerThread(String topic) {
        this.topic = topic;
    }

    @Override
    public void run() {
        //创建kafka消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        //指定要订阅的topic,可以订阅多个
        consumer.subscribe(Arrays.asList(topic));

        while (true) {
            //获取数据,指定拉取数据的时间间隔
            ConsumerRecords<String, String> consumerRecords = consumer.poll(100);
            new Thread(new KafkaMessageProcessor(consumerRecords)).start();
        }
    }

}
