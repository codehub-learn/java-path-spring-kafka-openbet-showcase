package gr.codelearn.spring.kafka.produce;

import gr.codelearn.spring.kafka.base.BaseComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SampleProducer extends BaseComponent {
	private final KafkaTemplate<Long, String> kafkaTemplate;

	public void sendMessage(String topic, Long key, String message) {
		kafkaTemplate.send(topic, key, message);
		logger.debug("Produced key '{}' and message '{}' in topic '{}'.", key, message, topic);
	}
}
