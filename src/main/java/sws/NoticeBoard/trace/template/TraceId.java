package sws.NoticeBoard.trace.template;

import java.util.UUID;

public class TraceId {

  private String id;
  private int level;
  private String mdcStr;

  public TraceId() {
    this.id = createId();
    this.level = 0;
    this.mdcStr = level+"."+id;
  }

  public TraceId(String id, int level) {
    this.id = id;
    this.level = level;
    this.mdcStr = level+"."+id;
  }

  private String createId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  public TraceId createNextId() {
    return new TraceId(id, level + 1);
  }

  public TraceId createPreviousId() {
    return new TraceId(id, level - 1);
  }

  public boolean isFirstLevel() {
    return level == 0;
  }

  public String getId() {
    return id;
  }

  public int getLevel() {
    return level;
  }

  public String getMdcStr() {
    return mdcStr;
  }
}
