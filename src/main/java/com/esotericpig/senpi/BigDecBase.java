package com.esotericpig.senpi;

import java.io.Serializable;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class BigDecBase implements Serializable {
  private static final long serialVersionUID = 1L;
  
  protected int scale = 0;
  protected MutBigIntBase value = null;
  
  public BigDecBase(String s) {
    this(s,MutBigIntBase.DEFAULT_BASE);
  }
  
  public BigDecBase(String s,int base) {
    this(s,base,new BigStrBase(false,true));
  }
  
  public BigDecBase(String s,int base,BigStrBase bsb) {
    BigStrBase.ParsedData pd = bsb.parse(s,base);
    
    this.scale = pd.scale;
    this.value = new MutBigIntBase(pd.sign,pd.digits,base,pd.offset,pd.length);
  }
  
  public String toString() {
    int dotIndex = value.digits.length - scale;
    StringBuilder sb = new StringBuilder(value.length + 2); // +2 for (potential) sign/dot
    
    if(value.sign < 0) {
      sb.append('-');
    }
    for(int i = value.offset; i < value.digits.length; ++i) {
      int d = value.digits[i];
      
      if(i == dotIndex) {
        sb.append('.');
      }
      if(d < 10) {
        sb.append((char)('0' + d));
      }
      else {
        sb.append((char)('A' + (d - 10)));
      }
    }
    return sb.toString();
  }
}
