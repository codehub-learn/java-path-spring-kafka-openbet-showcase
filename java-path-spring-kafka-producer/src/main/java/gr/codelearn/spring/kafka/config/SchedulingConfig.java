package gr.codelearn.spring.kafka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {
	/* In case we need to define task scheduler programmatically, uncomment the @Bean declaration and remove the
	 * scheduling configuration from application.yml
	 */
	//@Bean
	public TaskScheduler getTaskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(3);
		taskScheduler.setThreadNamePrefix("scheduler-");
		return taskScheduler;
	}
}
