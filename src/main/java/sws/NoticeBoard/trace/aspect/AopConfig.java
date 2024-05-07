package sws.NoticeBoard.trace.aspect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sws.NoticeBoard.trace.logtrace.LogTrace;
import sws.NoticeBoard.trace.logtrace.ThreadLocalLogTrace;

@Configuration
public class AopConfig {
	@Bean
	public LogTrace logTrace() {
		return new ThreadLocalLogTrace();
	}

	@Bean
	public LogTraceAspect logTraceAspect(LogTrace logTrace) {
		return new LogTraceAspect(logTrace);
	}

}
