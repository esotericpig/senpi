package com.esotericpig.senpi;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class DivideByZeroException extends ArithmeticException {
  private static final long serialVersionUID = 1L;

  public DivideByZeroException() {
    super();
  }
  
  public DivideByZeroException(String s) {
    super(s);
  }
}
