package com.ctrip.framework.apollo.portal.entity.vo;

public class PageSetting {

  private String wikiAddress;
  private boolean canAppAdminCreatePrivateNamespace;
  private boolean closeEntry;
  private boolean showNotice;
  private String noticeContent;

  public String getWikiAddress() {
    return wikiAddress;
  }

  public void setWikiAddress(String wikiAddress) {
    this.wikiAddress = wikiAddress;
  }

  public boolean isCanAppAdminCreatePrivateNamespace() {
    return canAppAdminCreatePrivateNamespace;
  }

  public void setCanAppAdminCreatePrivateNamespace(boolean canAppAdminCreatePrivateNamespace) {
    this.canAppAdminCreatePrivateNamespace = canAppAdminCreatePrivateNamespace;
  }

  public boolean isCloseEntry() {
    return closeEntry;
  }

  public void setCloseEntry(boolean closeEntry) {
    this.closeEntry = closeEntry;
  }

  public boolean isShowNotice() {
    return showNotice;
  }

  public void setShowNotice(boolean showNotice) {
    this.showNotice = showNotice;
  }

  public String getNoticeContent() {
    return noticeContent;
  }

  public void setNoticeContent(String noticeContent) {
    this.noticeContent = noticeContent;
  }
}
