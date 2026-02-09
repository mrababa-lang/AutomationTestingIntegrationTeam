package com.company.qic.core;

public enum Endpoint {
  GetQuotation("/qicservices/aggregator/GetQuotation", true, true),
  PurchasePolicy("/qicservices/aggregator/PurchasePolicy", true, true),
  UploadPolicyDocument("/qicservices/aggregator/UploadPolicyDocument", true, true),
  GetPolicyStatus("/qicservices/aggregator/GetPolicyStatus", false, false);

  private final String path;
  private final boolean basicAuth;
  private final boolean companyHeader;

  Endpoint(String path, boolean basicAuth, boolean companyHeader) {
    this.path = path;
    this.basicAuth = basicAuth;
    this.companyHeader = companyHeader;
  }

  public String path() {
    return path;
  }

  public boolean basicAuth() {
    return basicAuth;
  }

  public boolean companyHeader() {
    return companyHeader;
  }
}
