package com.esotericpig.senpi;

import java.io.Serializable;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class BigDecBase implements Serializable {
  private static final long serialVersionUID = 1L;
  
  protected BigIntBase intVal;
  protected int scale;
  
  protected BigDecBase(BigIntBase intVal,int scale) {
    this.intVal = intVal;
    this.scale = scale;
  }
  
  public BigDecBase plus(BigDecBase bdb) {
    return new BigDecBase(intVal.plus(bdb.intVal),scale);
  }
}
