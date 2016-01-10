package com.esotericpig.senpi;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class InvalidDigitException extends NumberFormatException {
  private static final long serialVersionUID = 1L;
  
  public InvalidDigitException() {
    super();
  }
  
  public InvalidDigitException(String s) {
    super(s);
  }
}
