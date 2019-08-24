package com.learnreactivespring.fluxandmonoplayground;

public class CustomException extends Throwable {

  private String errorMessage;

  public CustomException(Throwable e) {
    this.errorMessage = e.getMessage();
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
