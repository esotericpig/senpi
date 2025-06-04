/**
 * This file is part of senpi.
 * Copyright (c) 2016-2017 Bradley Whited
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

// TODO: implement BigNumBase
// TODO: plus, minus, times, over
// TODO: abs, compareTo, negate
// TODO: cache, sqrt, pow
// TODO: Goal: 1/3=0.4 (instead of 0.333...); 1/6=0.2; 1/9=0.14

/**
 * @author Bradley Whited
 */
public class BigDecBase implements Serializable {
  private static final long serialVersionUID = 1L;
  
  protected int scale = 0;
  protected MutBigIntBase value = null;
  
  public BigDecBase(BigDecBase value) {
    this(value.value,value.scale);
  }
  
  protected BigDecBase(MutBigIntBase value,int scale) {
    this.scale = scale;
    this.value = new MutBigIntBase(value);
  }
  
  public BigDecBase(String valueStr) {
    // TODO: remove BigNumBase when implement it in future
    this(valueStr,BigNumBase.DEFAULT_BASE);
  }
  
  public BigDecBase(String valueStr,int base) {
    this(valueStr,base,new BigStrBase(false,true));
  }
  
  public BigDecBase(String valueStr,int base,BigStrBase bsb) {
    BigStrBase.ParsedData pd = bsb.parse(valueStr,base);
    
    this.scale = pd.scale;
    this.value = new MutBigIntBase(pd.sign,pd.digits,base,pd.offset,pd.length);
  }
  
  public BigDecBase minus(BigDecBase y) {
    if(value.getSign() == 0) {
      return y;
    }
    else if(y.value.getSign() == 0) {
      return this;
    }
    
    // #this is copied inside the method called, and potentially y
    BigDecBase[] scaled = scaleForAddOrSubtract(this,y);
    
    scaled[0].value.minus(scaled[1].value);
    
    return scaled[0];
  }
  
  public BigDecBase plus(BigDecBase y) {
    if(value.getSign() == 0) {
      return y;
    }
    else if(y.value.getSign() == 0) {
      return this;
    }
    
    // #this is copied inside the method called, and potentially y
    BigDecBase[] scaled = scaleForAddOrSubtract(this,y);
    
    scaled[0].value.plus(scaled[1].value);
    
    return scaled[0];
  }
  
  public BigDecBase scale(int scale) {
    if(this.scale == scale) {
      return this;
    }
    
    BigDecBase result = new BigDecBase(this);
    
    // precision + new scale
    result.value.scale((result.value.getLength() - result.scale) + scale);
    result.scale = scale;
    
    return result;
  }
  
  public static BigDecBase[] scaleForAddOrSubtract(BigDecBase x,BigDecBase y) {
    x = new BigDecBase(x); // Work on new copy
    
    int addScale = 0;
    BigDecBase z = null;
    
    //     1.23  (x)
    // +/- 1.234 (y)
    if(x.scale < y.scale) {
      z = x;
      addScale = y.scale - x.scale;
    }
    //     1.234 (x)
    // +/- 1.23  (y)
    else if(x.scale > y.scale) {
      y = new BigDecBase(y); // Work on new copy
      z = y;
      addScale = x.scale - y.scale;
    }
    
    if(z != null && addScale > 0) {
      z.scale += addScale;
      z.value.scale(z.value.getLength() + addScale);
    }
    
    return new BigDecBase[]{x,y};
  }
  
  public String toString() {
    return toString(true);
  }
  
  public String toString(boolean shouldDowncase) {
    char charNum = shouldDowncase ? 'a' : 'A';
    int offsetLen = value.getOffset() + value.getLength();
    int dotIndex = offsetLen - scale;
    StringBuilder sb = new StringBuilder(value.getLength() + 2); // +2 for potential sign/dot
    
    if(value.getSign() < 0) {
      sb.append('-');
    }
    for(int i = value.getOffset(); i < offsetLen; ++i) {
      int d = value.get(i);
      
      if(i == dotIndex) {
        sb.append('.');
      }
      if(d < 10) {
        sb.append((char)('0' + d));
      }
      else {
        sb.append((char)(charNum + (d - 10)));
      }
    }
    
    return sb.toString();
  }
}
