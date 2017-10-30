/**
 * This file is part of senpi.
 * Copyright (c) 2016-2017 Jonathan Bradley Whited (@esotericpig)
 * 
 * senpi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * senpi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with senpi.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.esotericpig.senpi;

import java.io.Serializable;
import java.math.RoundingMode;

// TODO: plus, minus, times, over
// TODO: abs, compareTo, negate
// TODO: cache, sqrt, pow
// TODO: Goal: 1/3=0.4 (instead of 0.333...); 1/6=0.2; 1/9=0.14
/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class BigDecBase implements Serializable {
  private static final long serialVersionUID = 1L;
  
  protected int scale = 0;
  protected BigIntBase value = null;
  
  public BigDecBase(BigDecBase bdb) {
    this.scale = bdb.scale;
    this.value = bdb.value;
  }
  
  public BigDecBase(String s) {
    this(s,BigStrBase.DEFAULT_BASE);
  }
  
  public BigDecBase(String s,int base) {
    this(s,base,new BigStrBase(true,true));
  }
  
  public BigDecBase(String s,int base,BigStrBase bsb) {
    BigStrBase.ParsedData pd = bsb.parse(s,base);
    
    this.scale = pd.scale;
    this.value = new BigIntBase(new MutBigIntBase(pd.sign,pd.digits,base,pd.offset,pd.length));
  }
  
  protected BigDecBase(BigIntBase value,int scale) {
    this.scale = scale;
    this.value = value;
  }
  
  public BigDecBase plus(BigDecBase bdb) {
    return null;
  }
  
  public BigDecBase scale(int newScale,RoundingMode mode) {
    if(newScale == scale) {
      return this;
    }
    
    // TODO: if(value.getSign() == 0) { return new BigDecBase(0,newScale); /*or zero(newScale);*/ }
    
    if(newScale > scale) {
    }
    
    return null;
  }
  
  public String toString() {
    int dotIndex = value.getSize() - scale;
    StringBuilder sb = new StringBuilder(value.getLength() + 2); // +2 for (potential) sign/dot
    
    if(value.getSign() < 0) {
      sb.append('-');
    }
    for(int i = value.getOffset(); i < value.getSize(); ++i) {
      int d = value.getDigit(i);
      
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
