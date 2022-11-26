package gr.codelearn.spring.kafka.consume;

import gr.codelearn.spring.kafka.base.BaseComponent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class StringConsumer extends BaseComponent {
	/* In case we need to read from a specific topic, partition(s) and offset(s). */
	//@formatter:off
//	@KafkaListener(topicPartitions = @TopicPartition(
//			topic = "${app.kafka.topic1}",
//			partitionOffsets = {
//					@PartitionOffset(partition = "0", initialOffset = "0")
//			})
//	)
	//@formatter:on
	@KafkaListener(id = "l1", idIsGroup = false, topics = "${app.kafka.topic1}")
	public void listen(ConsumerRecord<?, ?> consumerRecord, Acknowledgment ack,
					   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
					   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
		logger.trace("Received '{}' from {} @ partition {}.", consumerRecord.value(), topic, partition);
		// Manual acknowledgement
		ack.acknowledge();
	}
}
