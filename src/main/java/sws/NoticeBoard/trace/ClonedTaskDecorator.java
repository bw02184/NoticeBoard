package sws.NoticeBoard.trace;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class ClonedTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable task) {

        Map<String, String> callerThreadContext = MDC.getCopyOfContextMap();
        return () -> {
            // 아래 값이 null 인 케이스도 있을테니 null 처리
            if(callerThreadContext != null){
                MDC.setContextMap(callerThreadContext);
            }
            task.run();
        };
    }
}