package gr.codelearn.spring.kafka.bootstrap;

import gr.codelearn.spring.kafka.base.BaseComponent;
import gr.codelearn.spring.kafka.produce.SampleProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.LongStream;

@Component
@RequiredArgsConstructor
public class ProducerRunner extends BaseComponent implements CommandLineRunner {
	private final SampleProducer sampleProducer;

	@Override
	public void run(final String... args) throws Exception {
		LongStream.range(0, 10).forEach(i -> {
			sampleProducer.sendMessageWithKey("codelearn-1", i, "String content " + i);
			logger.info("Going to pause sending without keys...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
