package gr.codelearn.spring.kafka.produce;

import gr.codelearn.spring.kafka.base.BaseComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@RequiredArgsConstructor
public class SampleProducer extends BaseComponent {
	private final KafkaTemplate<Long, String> kafkaTemplate;

	public void sendMessage(String topic, Long key, String message) {
		var result = kafkaTemplate.send(topic, key, message);
		logger.debug("Produced key '{}' and message '{}' in topic '{}'.", key, message, topic);

		result.addCallback(new ListenableFutureCallback<SendResult<Long, String>>() {
			@Override
			public void onSuccess(final SendResult<Long, String> result) {
				logger.info("Message '{}' was delivered at partition {}.", message,
							result.getRecordMetadata().partition());
			}

			@Override
			public void onFailure(final Throwable ex) {
				logger.warn("Unable to deliver message '{}.", message);
			}
		});
	}
}
