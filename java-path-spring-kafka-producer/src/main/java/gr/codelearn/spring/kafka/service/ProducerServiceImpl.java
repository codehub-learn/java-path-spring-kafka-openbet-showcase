package gr.codelearn.spring.kafka.service;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import gr.codelearn.spring.kafka.base.BaseComponent;
import gr.codelearn.spring.kafka.domain.Customer;
import gr.codelearn.spring.kafka.produce.SampleProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl extends BaseComponent implements ProducerService {
	private final SampleProducer sampleProducer;

	@Value("${app.kafka.topic1}")
	private String topic1;
	@Value("${app.kafka.topic2}")
	private String topic2;
	@Value("${app.kafka.topic3}")
	private String topic3;

	private static final Integer NUM_OF_MESSAGES = 2;
	private static final Lorem generator = LoremIpsum.getInstance();
	private final AtomicLong sequence = new AtomicLong(1);

	@Override
	@Scheduled(cron = "0/15 * * * * ?")
	public void produceSampleMessages() {
		LongStream.range(0, NUM_OF_MESSAGES).forEach(i -> {
			sampleProducer.sendMessageWithKey(topic1, i, "String content " + i);
			logger.info("Produced {} messages to {}", NUM_OF_MESSAGES, topic1);
		});
	}

	@Override
	@Scheduled(cron = "0/1 * * * * ?")
	public void produceCustomers() {
		var customer = generateCustomer();
		sampleProducer.sendCustomer(topic2, customer);
		logger.info("Produced {} to {}", customer, topic2);
	}

	private Customer generateCustomer() {
		var firstname = generator.getFirstName();
		var lastname = generator.getLastName();
		var email = String.format("%s.%s@example.com", firstname, lastname);
		return Customer.builder().id(sequence.getAndIncrement()).firstname(firstname).lastname(lastname).email(email)
					   .age(ThreadLocalRandom.current().nextInt(18, 100)).build();
	}
}
