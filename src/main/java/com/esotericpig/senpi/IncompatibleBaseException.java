package com.esotericpig.senpi;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class IncompatibleBaseException extends IllegalArgumentException {
  private static final long serialVersionUID = 1L;
  
  public IncompatibleBaseException() {
    super();
  }
  
  public IncompatibleBaseException(String s) {
    super(s);
  }
  
  public IncompatibleBaseException(String s,Throwable t) {
    super(s,t);
  }
  
  public IncompatibleBaseException(Throwable t) {
    super(t);
  }
}
