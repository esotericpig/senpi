package com.esotericpig.senpi;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class UnsupportedBaseException extends NumberFormatException {
  private static final long serialVersionUID = 1L;
  
  public UnsupportedBaseException() {
    super();
  }
  
  public UnsupportedBaseException(String s) {
    super(s);
  }
}
