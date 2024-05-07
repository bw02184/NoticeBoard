package sws.NoticeBoard.trace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		int processors = Runtime.getRuntime().availableProcessors();
		taskExecutor.setCorePoolSize(processors);
		taskExecutor.setMaxPoolSize(processors * 2);
		taskExecutor.setQueueCapacity(100);
		taskExecutor.setMaxPoolSize(50);
		// 내가 만든 데코레이터 설정
		taskExecutor.setTaskDecorator(new ClonedTaskDecorator());
		taskExecutor.setThreadNamePrefix("async-task-");
		taskExecutor.setThreadGroupName("async-group");

		return taskExecutor;
	}

}