package com.budget.common.exceptions;

public class CriticalIncidentException extends RuntimeException {
  public CriticalIncidentException(String message, Throwable cause) {
    super(message, cause);
  }
}
