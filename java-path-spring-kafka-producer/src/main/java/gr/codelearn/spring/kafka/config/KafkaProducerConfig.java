package gr.codelearn.spring.kafka.config;

import gr.codelearn.spring.kafka.base.BaseComponent;
import gr.codelearn.spring.kafka.domain.Customer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class KafkaProducerConfig extends BaseComponent {
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public ProducerFactory<Long, String> producerFactory() {
		Map<String, Object> configProps = getDefaultConfigurationProperties();
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<Long, String> kafkaTemplate() {
		var kafkaTemplate = new KafkaTemplate<>(producerFactory());
		kafkaTemplate.setProducerListener(new ProducerListener<>() {
			@Override
			public void onSuccess(ProducerRecord<Long, String> producerRecord, RecordMetadata recordMetadata) {
				logger.debug("ACK received, key:{}, message:{} at offset:{}", producerRecord.key(),
							 producerRecord.value(), recordMetadata.offset());
			}

			@Override
			public void onError(ProducerRecord<Long, String> producerRecord, RecordMetadata recordMetadata,
								Exception exception) {
				logger.debug("Unable to produce message from ProducerListener key:{}, message:{} at offset:{}",
							 producerRecord.key(), producerRecord.value(), recordMetadata.offset(), exception);
			}
		});
		return kafkaTemplate;
	}

	@Bean
	public ProducerFactory<Long, Customer> customerProducerFactory() {
		return new DefaultKafkaProducerFactory<>(getDefaultConfigurationProperties());
	}

	@Bean
	public KafkaTemplate<Long, Customer> customerKafkaTemplate() {
		return new KafkaTemplate<>(customerProducerFactory());
	}

	@Bean
	public NewTopic firstTopic() {
		return TopicBuilder.name("codelearn-1").partitions(6).replicas(3).build();
	}

	@Bean
	public NewTopic secondTopic() {
		return TopicBuilder.name("codelearn-2").partitions(6).replicas(3).build();
	}

	@Bean
	public NewTopic thirdTopic() {
		return TopicBuilder.name("codelearn-3").partitions(6).replicas(3).build();
	}

	//@Bean
	public RoutingKafkaTemplate routingTemplate(GenericApplicationContext context) {
		/*
		 * Showcasing multiple producers with different configurations where we want to select
		 * producer at runtime based on the topic name.
		 */

		// ProducerFactory with Bytes serializer
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
		DefaultKafkaProducerFactory<Object, Object> bytesProducerFactory = new DefaultKafkaProducerFactory<>(props);
		context.registerBean(DefaultKafkaProducerFactory.class, "bytesProducerFactory", bytesProducerFactory);

		// ProducerFactory with String serializer
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		DefaultKafkaProducerFactory<Object, Object> stringProducerFactory = new DefaultKafkaProducerFactory<>(props);

		Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
		map.put(Pattern.compile(".*-bytes"), bytesProducerFactory);
		map.put(Pattern.compile(".*-text"), stringProducerFactory);
		return new RoutingKafkaTemplate(map);
	}

	private Map<String, Object> getDefaultConfigurationProperties() {
		/* Custom producer factory used to produce custom objects via their corresponding Kafka template. */
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		// For custom object, we use JsonSerializer as value serializer
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, "8192");
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, "5000");
		return configProps;
	}
}
