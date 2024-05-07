package sws.NoticeBoard.trace.logtrace;

import sws.NoticeBoard.trace.template.TraceStatus;

public interface LogTrace {

	TraceStatus begin(String message);

	void end(TraceStatus status);

	void exception(TraceStatus status, Exception e);
}
