package com.esotericpig.senpi;

import java.io.Serializable;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class BigDecBase implements Serializable {
  private static final long serialVersionUID = 1L;
  
  protected int scale = 0;
  protected MutBigIntBase value = null;
  
  protected BigDecBase(String s,int base) {
    BigStrBase bsb = new BigStrBase(false,true);
    
    this.value = new MutBigIntBase(s,base,bsb);
    this.scale = bsb.scale;
  }
}
