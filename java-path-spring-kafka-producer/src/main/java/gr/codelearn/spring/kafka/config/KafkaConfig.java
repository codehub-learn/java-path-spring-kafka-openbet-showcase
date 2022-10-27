package gr.codelearn.spring.kafka.config;

import gr.codelearn.spring.kafka.base.BaseComponent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig extends BaseComponent {
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${app.kafka.topic1}")
	private String topic1;

	@Bean
	public ProducerFactory<Long, String> producerFactory() {
		Map<String, Object> configurationProperties = new HashMap<>();
		configurationProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configurationProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		configurationProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configurationProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, "8192");
		configurationProperties.put(ProducerConfig.LINGER_MS_CONFIG, "3000");

		return new DefaultKafkaProducerFactory<>(configurationProperties);
	}

	@Bean
	public KafkaTemplate<Long, String> kafkaTemplate() {
		var kafkaTemplate = new KafkaTemplate<>(producerFactory());
		kafkaTemplate.setProducerListener(new ProducerListener<Long, String>() {
			@Override
			public void onSuccess(final ProducerRecord<Long, String> producerRecord,
								  final RecordMetadata recordMetadata) {
				logger.debug("ACK received, key:{}, value:{}, partition:{}, offset:{}", producerRecord.key(),
							 producerRecord.value(), recordMetadata.partition(), recordMetadata.offset());
			}

			@Override
			public void onError(final ProducerRecord<Long, String> producerRecord, final RecordMetadata recordMetadata,
								final Exception exception) {
				logger.warn("Unable to produce message, key:{}, value:{}, partition:{}, offset:{}",
							producerRecord.key(), producerRecord.value(), recordMetadata.partition(),
							recordMetadata.offset());
			}
		});

		return kafkaTemplate;
	}

	@Bean
	public NewTopic newTopic() {
		return TopicBuilder.name(topic1).partitions(6).replicas(3).build();
	}
}
