package gr.codelearn.spring.kafka.config;

import gr.codelearn.spring.kafka.domain.Customer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String consumerGroupId;

	@Bean
	public ConsumerFactory<Long, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId + "-default");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "3000");
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<Long, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<Long, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(2);
		factory.getContainerProperties().setPollTimeout(10000);
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
		return factory;
	}

	@Bean
	public ConsumerFactory<Long, Customer> customerConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId + "-customer");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "3000");
		return new DefaultKafkaConsumerFactory<>(props, new LongDeserializer(), new JsonDeserializer<>(Customer.class));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<Long, Customer> customerKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<Long, Customer> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(customerConsumerFactory());
		factory.setConcurrency(1);
		// Poll for this amount of time
		factory.getContainerProperties().setPollTimeout(10000);
		// Stay idle for this amount of time and then poll
		factory.getContainerProperties().setIdleBetweenPolls(10000);
		// Define the Kafka template to use as the reply template
		factory.setReplyTemplate(kafkaTemplate());
		// In case we want to filter out specific object based specific value(s) in specific message attributes
		factory.setRecordFilterStrategy(
				rec -> List.of("ignore", "invalid", "skip").contains(rec.value().lastname().toLowerCase()));
		return factory;
	}

	@Bean
	public ProducerFactory<Long, String> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, "8192");
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, "5000");
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<Long, String> kafkaTemplate() {
		/*
		 * This template will be used as the reply template of the customerKafkaListenerContainerFactory via @SendTo
		 * annotation.
		 */
		return new KafkaTemplate<>(producerFactory());
	}
}
