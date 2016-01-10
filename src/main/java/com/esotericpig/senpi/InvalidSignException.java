package com.esotericpig.senpi;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class InvalidSignException extends NumberFormatException {
  private static final long serialVersionUID = 1L;
  
  public InvalidSignException() {
    super();
  }
  
  public InvalidSignException(String s) {
    super(s);
  }
}
