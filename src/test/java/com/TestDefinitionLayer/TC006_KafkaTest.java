package com.TestDefinitionLayer;

import com.eventing.Config;
import com.eventing.Kafka;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Properties;

public class TC006_KafkaTest {

    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.0"));

    final String topic = "second-topic";
    final String value = "hello kafka";

    Properties properties = new Properties();
    Kafka kafka = new Kafka();

    @BeforeTest
    public void beforeTest() {
        kafkaContainer.start();
        System.out.println("Starting kafka container");
    }

    @Test
    public void TestKafkaProducer() throws Exception {
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Create the producer
        Config conf = Config
                .builder()
                .Topic(topic)
                .Data(value)
                .Properties(properties)
                .build();
        kafka.createTopic(conf);
    }

    @AfterTest
    public void StopContainer() {
        kafkaContainer.stop();
        System.out.println("Stopping kafka container");
    }
}
