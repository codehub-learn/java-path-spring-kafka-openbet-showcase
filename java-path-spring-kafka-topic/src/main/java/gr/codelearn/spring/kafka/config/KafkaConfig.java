package gr.codelearn.spring.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public KafkaAdmin newKafkaAdmin() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configuration.put(AdminClientConfig.CLIENT_ID_CONFIG, "local-admin-1");
		return new KafkaAdmin(configuration);
	}

	@Bean
	public NewTopic newTopic() {
		//@formatter:off
		return TopicBuilder.name("test-topic")
						   .partitions(3)
						   .replicas(2)
						   .config(TopicConfig.RETENTION_MS_CONFIG, "100000")
						   .config(TopicConfig.RETENTION_BYTES_CONFIG, "256000")
						   .build();
		//@formatter:on
	}
}
