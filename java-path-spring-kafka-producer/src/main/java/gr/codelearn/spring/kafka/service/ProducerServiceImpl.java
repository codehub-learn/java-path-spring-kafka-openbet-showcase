package gr.codelearn.spring.kafka.service;

import gr.codelearn.spring.kafka.produce.SampleProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {
	private final SampleProducer producer;

	@Value("${app.kafka.topic1}")
	private String topic1;
	private static final Long NUM_OF_MESSAGES = 2L;

	@Override
	@Scheduled(cron = "0/10 * * * * ?")
	public void produceSampleMessages() {
		LongStream.range(0, NUM_OF_MESSAGES).forEach(i -> {
			producer.sendMessage(topic1, i, "String content no" + i);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
