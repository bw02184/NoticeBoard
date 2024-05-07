package sws.NoticeBoard.trace.logtrace;

import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;
import sws.NoticeBoard.session.SessionConst;
import sws.NoticeBoard.trace.template.TraceId;
import sws.NoticeBoard.trace.template.TraceStatus;

@Slf4j
public class ThreadLocalLogTrace implements LogTrace {

	private static final String START_PREFIX = "-->";
	private static final String COMPLETE_PREFIX = "<--";
	private static final String EX_PREFIX = "<X-";

	private String message;

	@Override
	public TraceStatus begin(String message) {
		this.message = message;
		syncTraceId();
		String traceId = MDC.get("traceId");
		TraceId traceId1 = new TraceId(traceId.split("\\.")[1], Integer.parseInt(traceId.split("\\.")[0]));

		Long startTimeMs = System.currentTimeMillis();
		log.info("[{}] {}{}", traceId1.getId(), addSpace(START_PREFIX, traceId1.getLevel()), message);

		return new TraceStatus(traceId1, startTimeMs, message);
	}

	@Override
	public void end(TraceStatus status) {
		complete(status, null);
	}

	@Override
	public void exception(TraceStatus status, Exception e) {
		complete(status, e);
	}

	private void complete(TraceStatus status, Exception e) {
		Long stopTimeMs = System.currentTimeMillis();
		long resultTimeMs = stopTimeMs - status.getStartTimeMs();
		TraceId traceId = status.getTraceId();
		if (e == null) {
			log.info(
				"[{}] {}{} time={}ms",
				traceId.getId(),
				addSpace(COMPLETE_PREFIX, traceId.getLevel()),
				status.getMessage(),
				resultTimeMs);
		} else {
			log.info(
				"[{}] {}{} time={}ms ex={}",
				traceId.getId(),
				addSpace(EX_PREFIX, traceId.getLevel()),
				status.getMessage(),
				resultTimeMs,
				e.toString());
		}

		releaseTraceId();
	}

	private Object getSession() {
		ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		Object attribute = null;
		if (servletRequestAttribute != null) {
			attribute = servletRequestAttribute.getAttribute(SessionConst.LOGIN_MEMBER,
				RequestAttributes.SCOPE_SESSION);
		}
		return attribute;
	}

	private void syncTraceId() {
		String traceId = MDC.get("traceId");
		if (traceId == null) {
			TraceId traceId1 = new TraceId();
			MDC.put("traceId", traceId1.getMdcStr());
		} else {
			TraceId traceId1 = new TraceId(traceId.split("\\.")[1], Integer.parseInt(traceId.split("\\.")[0]));
			TraceId nextId = traceId1.createNextId();
			MDC.put("traceId", nextId.getMdcStr());
		}
	}

	private void releaseTraceId() {
		Object session = getSession();
		String traceId = MDC.get("traceId");
		TraceId traceId1 = new TraceId(traceId.split("\\.")[1], Integer.parseInt(traceId.split("\\.")[0]));
		if (traceId1.isFirstLevel()) {
			MDC.clear();
		} else {
			TraceId previousId = traceId1.createPreviousId();
			MDC.put("traceId", previousId.getMdcStr());
		}

	}

	private static String addSpace(String prefix, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append((i == level - 1) ? "|" + prefix : "|   ");
		}
		return sb.toString();
	}
}
