package com.refuture.common;

import net.sf.json.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaTools {

    private static KafkaProducer<String, String> createProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");// 声明kafka
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<String, String>((properties));
    }

    public static void sendMessage(String topicName, String jsonMessage) {
        KafkaProducer<String, String> producer = createProducer();
        producer.send(new ProducerRecord<String, String>(topicName, jsonMessage));
        producer.close();
    }

    public static void sendMessage(String topicName, String... jsonMessages) throws InterruptedException {
        KafkaProducer<String, String> producer = createProducer();
        for (String jsonMessage : jsonMessages) {
            producer.send(new ProducerRecord<String, String>(topicName, jsonMessage));
        }
        producer.close();
    }

    public static void sendMessage(String topicName, List<Map<Object, Object>> mapMessageToJSONForArray) {
        KafkaProducer<String, String> producer = createProducer();
        for (Map<Object, Object> mapMessageToJSON : mapMessageToJSONForArray) {
            String array = JSONObject.fromObject(mapMessageToJSON).toString();
            producer.send(new ProducerRecord<String, String>(topicName, array));
        }
        producer.close();
    }

    public static void sendMessage(String topicName, Map<Object, Object> mapMessageToJSON) {
        KafkaProducer<String, String> producer = createProducer();
        String array = JSONObject.fromObject(mapMessageToJSON).toString();
        producer.send(new ProducerRecord<String, String>(topicName, array));
        producer.close();
    }


    public static void sendMessage(String topicName, Object object) {
        KafkaProducer<String, String> producer = createProducer();
        String json = JSONObject.fromObject(object).toString();
        producer.send(new ProducerRecord<String, String>(topicName, json));
        producer.close();
    }

    public static void main(String[] args) throws InterruptedException {
        // System.out.println(System.getProperty("file.encoding"));
        String[] s = new String[] { "{\"userName\":\"赵四31\",\"pwd\":\"lisi\",\"age\":13}",
                "{\"userName\":\"赵四41\",\"pwd\":\"lisi\",\"age\":14}",
                "{\"userName\":\"赵四51\",\"pwd\":\"lisi\",\"age\":15}" };
        // KafkaTools.sendMessage("logstest",
        // "{\"userName\":\"赵四\",\"pwd\":\"lisi\",\"age\":13}");
        /*
         * for (String a : s) { System.out.println(a); Thread.sleep(3000);
         * KafkaTools.sendMessage(topicName, jsonMessages); }
         */
        KafkaTools.sendMessage("ticket", s);
    }

}
