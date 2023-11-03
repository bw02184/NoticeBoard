package sws.NoticeBoard.trace.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import sws.NoticeBoard.trace.logtrace.LogTrace;
import sws.NoticeBoard.trace.template.TraceStatus;

@Slf4j
@Aspect
public class LogTraceAspect {

  private final LogTrace logTrace;

  public LogTraceAspect(LogTrace logTrace) {
    this.logTrace = logTrace;
  }

  @Around(
      "execution(* sws.NoticeBoard.controller..*(..)) || execution(*"
          + " sws.NoticeBoard.service..*(..)) || execution(* sws.NoticeBoard.repository..*(..))")
  public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
    TraceStatus status = null;
    try {
      String message = joinPoint.getSignature().toShortString();
      status = logTrace.begin(message);
      // logic 호출
      Object result = joinPoint.proceed();
      logTrace.end(status);
      return result;
    } catch (Exception e) {
      logTrace.exception(status, e);
      throw e;
    }
  }
}
