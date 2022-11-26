package gr.codelearn.spring.kafka.produce;

import gr.codelearn.spring.kafka.base.BaseComponent;
import gr.codelearn.spring.kafka.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@RequiredArgsConstructor
public class SampleProducer extends BaseComponent {
	private final KafkaTemplate<Long, String> kafkaTemplate;
	private final KafkaTemplate<Long, Customer> customerKafkaTemplate;

	public void sendMessageWithoutKey(String topic, String message) {
		kafkaTemplate.send(topic, message);
		logger.debug("Sent a keyless message {} in topic {}", message, topic);
	}

	public void sendMessageWithKey(String topic, Long key, String message) {
		var future = kafkaTemplate.send(topic, key, message);

		logger.debug("Sent key '{}' and message '{}' in topic '{}'.", key, message, topic);

		future.addCallback(new ListenableFutureCallback<SendResult<Long, String>>() {
			@Override
			public void onSuccess(SendResult<Long, String> result) {
				logger.info("Message '{}' delivered at offset {}.", message, result.getRecordMetadata().offset());
			}

			@Override
			public void onFailure(Throwable ex) {
				logger.warn("Unable to deliver message '{}'.", message, ex);
			}
		});
	}

	public void sendCustomer(String topic, Customer customer) {
		customerKafkaTemplate.send(topic, customer.getId(), customer);
		logger.debug("Sent key '{}' and customer '{}' in topic '{}'.", customer.getId(), customer, topic);
	}
}
