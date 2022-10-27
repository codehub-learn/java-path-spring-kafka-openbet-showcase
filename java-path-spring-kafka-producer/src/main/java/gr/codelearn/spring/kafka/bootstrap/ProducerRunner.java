package gr.codelearn.spring.kafka.bootstrap;

import gr.codelearn.spring.kafka.produce.SampleProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.LongStream;

@Component
@RequiredArgsConstructor
public class ProducerRunner implements CommandLineRunner {
	private final SampleProducer producer;

	@Value("${app.kafka.topic1}")
	private String topic1;

	@Override
	public void run(final String... args) throws Exception {
		LongStream.range(0, 10).forEach(i -> {
			producer.sendMessage(topic1, i, "String content no" + i);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
