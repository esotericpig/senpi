package com.esotericpig.senpi;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class InvalidDotException extends NumberFormatException {
  private static final long serialVersionUID = 1L;
  
  public InvalidDotException() {
    super();
  }
  
  public InvalidDotException(String s) {
    super(s);
  }
}
