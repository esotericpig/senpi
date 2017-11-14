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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jonathan Bradley Whited (@esotericpig)
 */
public class BigIntBase implements BigNumBase<BigIntBase>,CacheableBigNumBase<BigIntBase,BigIntBase.Cache> {
  private static final long serialVersionUID = 1L;
  
  protected static final HashMap<Integer,Cache> CACHES = new HashMap<Integer,Cache>();
  
  protected int base = 0;
  protected int[] digits = null;
  protected int sign = 0;
  
  public BigIntBase() {
    this(DEFAULT_BASE);
  }
  
  public BigIntBase(int base) {
    this.base = base;
    this.digits = new int[]{0};
    this.sign = 0;
  }
  
  protected BigIntBase(int sign,int[] digits,int base) {
    this.base = base;
    this.digits = digits;
    this.sign = sign;
  }
  
  public BigIntBase(BigIntBase value) {
    this(value,value.sign);
  }
  
  public BigIntBase(MutBigIntBase value) {
    this.base = value.base;
    this.digits = Arrays.copyOfRange(value.digits,value.offset,value.offset + value.length);
    this.sign = value.sign;
  }
  
  protected BigIntBase(BigIntBase value,int sign) {
    this.base = value.base;
    this.digits = Arrays.copyOf(value.digits,value.digits.length);
    this.sign = sign;
  }
  
  public BigIntBase(String valueStr) {
    this(valueStr,DEFAULT_BASE);
  }
  
  public BigIntBase(String valueStr,int base) {
    MutBigIntBase value = new MutBigIntBase(valueStr,base,true);
    
    this.base = base;
    this.digits = value.digits;
    this.sign = value.sign;
  }
  
  public BigIntBase(String valueStr,int base,BigStrBase bsb) {
    MutBigIntBase value = new MutBigIntBase(valueStr,base,bsb);
    
    this.base = base;
    this.digits = ltrim(value.digits); // #ltrim(...) because no offset stored
    this.sign = value.sign;
  }
  
  public BigIntBase abs() {
    return (sign <= -1) ? new BigIntBase(this,1) : this;
  }
  
  /**
   * <pre>
   * This doesn't check #base or the comparison for internal methods.
   * 
   * For those checks, use #plus(...) or #minus(...).
   * </pre>
   */
  protected static int[] add(int[] x,int[] y,int base) {
    // Make the top number (x) the longest (100 + 99) for z and the loop
    if(x.length < y.length) {
      int[] tmp = x;
      x = y;
      y = tmp;
    }
    
    int carry = 0;
    int xi = x.length;
    int yi = y.length;
    int[] z = new int[x.length];
    
    // Add and bring down
    while(xi > 0) {
      int sum = x[--xi] + carry;
      
      if(yi > 0) {
        sum += y[--yi];
      }
      if(sum < base) {
        carry = 0;
      }
      else {
        carry = sum / base; // Left digit
        sum = sum % base; // Right digit
      }
      
      z[xi] = sum;
    }
    
    // Add carry with potential growth
    if(carry != 0) {
      if(xi <= 0) {
        z = lgrow(z,1);
        xi = 0;
      }
      
      z[xi] = carry;
    }
    
    return z;
  }
  
  public BigIntBase clone() {
    return new BigIntBase(this);
  }
  
  protected static int compare(int[] x,int[] y) {
    if(x.length < y.length) {
      return -1;
    }
    if(x.length > y.length) {
      return 1;
    }
    
    // Same length
    for(int i = 0; i < x.length; ++i) {
      int xd = x[i];
      int yd = y[i];
      
      if(xd < yd) {
        return -1;
      }
      if(xd > yd) {
        return 1;
      }
    }
    
    return 0;
  }
  
  public int compareTo(BigIntBase y) {
    if(y.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign < y.sign) {
      return -1;
    }
    if(sign > y.sign) {
      return 1;
    }
    if(sign == 0 && y.sign == 0) {
      return 0;
    }
    
    return compare(digits,y.digits);
  }
  
  public BigIntBase floorMod(BigIntBase y) {
    return toMutBigIntBase().floorMod(y.toMutBigIntBase()).toBigIntBase();
  }
  
  protected static int[] lgrow(int[] x,int lengthToAdd) {
    int[] z = new int[x.length + lengthToAdd];

    for(int i = lengthToAdd,j = 0; i < z.length; ++i,++j) {
      z[i] = x[j];
    }
    
    return z;
  }
  
  public BigIntBase ltrim() {
    return this;
  }
  
  protected static int[] ltrim(int[] x) {
    if(x.length <= 1) {
      return x;
    }
    
    int i = 0;
    
    while(i < x.length && x[i] == 0) {
      ++i;
    }
    
    // For 0
    if(i >= x.length) {
      return new int[]{0};
    }
    
    return Arrays.copyOfRange(x,i,x.length);
  }
  
  public BigIntBase minus(BigIntBase y) {
    if(y.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0) {
      return y.negate();
    }
    if(y.sign == 0) {
      return this;
    }
    // -4 - 4 = -8; 4 - -4 = 8
    if(sign != y.sign) {
      return new BigIntBase(sign,add(digits,y.digits,base),base);
    }
    
    int comp = compare(digits,y.digits);
    
    if(comp == 0) {
      return getCache(base).ZERO; // Same value
    }
    
    return new BigIntBase(sign * comp,subtract(digits,y.digits,base,comp),base);
  }
  
  public BigIntBase mod(BigIntBase y) {
    return toMutBigIntBase().mod(y.toMutBigIntBase()).toBigIntBase();
  }
  
  protected static int[] multiply(int[] x,int[] y,int base) {
    int[] z = new int[x.length + y.length]; // Max length of product
    
    for(int yi = y.length - 1; yi >= 0; --yi) {
      int carry = 0;
      
      for(int xi = x.length - 1,zi = x.length + yi; xi >= 0; --xi,--zi) {
        int product = z[zi] + y[yi] * x[xi] + carry;
        
        if(product < base) {
          carry = 0;
        }
        else {
          carry = product / base; // Left digit
          product = product % base; // Right digit
        }
        
        z[zi] = product;
      }
      
      if(carry > 0) {
        z[yi] = carry; // yi, so that in 111, then 1 => 10 => 100 place
      }
    }
    
    return ltrim(z); // #ltrim(...) for overestimation
  }
  
  public BigIntBase negate() {
    return (sign != 0) ? new BigIntBase(this,-sign) : this;
  }
  
  public BigIntBase over(BigIntBase y) {
    return overRem(y).quotient;
  }
  
  public BigQuoBase<BigIntBase> overRem(BigIntBase y) {
    return toMutBigIntBase().overRem(y.toMutBigIntBase()).toBigIntBase();
  }
  
  public BigIntBase plus(BigIntBase y) {
    if(y.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0) {
      return y;
    }
    if(y.sign == 0) {
      return this;
    }
    if(sign != y.sign) {
      int comp = compare(digits,y.digits);
      
      if(comp == 0) {
        return getCache(base).ZERO; // Same value
      }
      
      return new BigIntBase(sign * comp,subtract(digits,y.digits,base,comp),base);
    }
    
    return new BigIntBase(sign,add(digits,y.digits,base),base);
  }
  
  public BigIntBase precise(int precisionToAddOrSub) {
    return setPrecision(digits.length + precisionToAddOrSub);
  }
  
  public BigIntBase rem(BigIntBase y) {
    return overRem(y).remainder;
  }
  
  public BigIntBase rtrim() {
    if(digits.length <= 1) {
      if(digits.length == 1 && digits[0] == 0) {
        sign = 0; // Mr. Justin Case
      }
      
      return this;
    }
    
    int lenMinus1 = digits.length - 1;
    int i = lenMinus1;
    
    while(i >= 0 && digits[i] == 0) {
      --i;
    }
    if(i == lenMinus1) {
      return this;
    }
    if(i < 0) {
      return getCache(base).ZERO;
    }
    
    return new BigIntBase(sign,Arrays.copyOf(digits,i + 1),base);
  }
  
  public BigIntBase scale(int scaleToAddOrSub) {
    return setScale(digits.length + scaleToAddOrSub);
  }
  
  public static int[] subtract(int[] x,int[] y,int base,int comparison) {
    // Make the top number (x) the largest value (99 - 88) for z and the loop
    if(comparison < 0) {
      int[] temp = x;
      x = y;
      y = temp;
    }
    
    int borrow = 0;
    int xi = x.length;
    int yi = y.length;
    int[] z = new int[x.length];
    
    // Subtract and borrow
    while(xi > 0) {
      int diff = x[--xi] - borrow;
      
      if(yi > 0) {
        diff -= y[--yi];
      }
      // In binary, 10 - 1 => 0 - 1 => -1 => -1 + 2 => 1 (borrow)
      // Also here because of 1000 - 1 to bring down
      if(diff < 0) {
        borrow = 1;
        diff += base;
      }
      else {
        borrow = 0;
      }
      
      z[xi] = diff;
    }
    
    return ltrim(z); // #ltrim(...) for borrow and/or 0s (1000 - 999 = 1 [not 001])
  }
  
  public BigIntBase times(BigIntBase y) {
    if(y.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0 || y.sign == 0) {
      return getCache(base).ZERO;
    }
    
    return new BigIntBase(sign * y.sign,multiply(digits,y.digits,base),base);
  }
  
  public BigIntBase trim() {
    // #ltrim() does nothing, but used in case it changes in future
    return ltrim().rtrim();
  }
  
  public BigIntBase under(BigIntBase y) {
    return underRem(y).quotient;
  }
  
  public BigQuoBase<BigIntBase> underRem(BigIntBase y) {
    return y.overRem(this);
  }
  
  public BigIntBase zero() {
    return getCache(base).ZERO;
  }
  
  public BigIntBase set(BigIntBase value) {
    return new BigIntBase(value);
  }
  
  public BigIntBase setPrecision(int precision) {
    // Even if greater than, return #this because BigIntBase doesn't store an offset
    if(precision >= digits.length) {
      return this;
    }
    if(precision < 1) {
      return getCache(base).ZERO;
    }
    
    int[] newDigits = ltrim(Arrays.copyOf(digits,precision));
    
    if(isZero(newDigits)) {
      return getCache(base).ZERO;
    }
    
    return new BigIntBase(sign,newDigits,base);
  }
  
  public BigIntBase setScale(int scale) {
    if(scale == digits.length) {
      return this;
    }
    if(scale < 1) {
      return getCache(base).ZERO;
    }
    
    return new BigIntBase(sign,Arrays.copyOf(digits,scale),base);
  }
  
  public int get(int index) {
    return digits[index];
  }
  
  public int getBase() {
    return base;
  }
  
  public Cache getCache() {
    return getCache(base);
  }
  
  public static Cache getCache(int base) {
    Cache result = CACHES.get(base);
    
    if(result == null) {
      CACHES.put(base,result = new Cache(base));
    }
    
    return result;
  }
  
  public static BigIntBase getCache(int base,String numberStr) {
    return getCache(base).getCustom(numberStr);
  }
  
  public BigIntBase getCache(String numberStr) {
    return getCache(base,numberStr);
  }
  
  public static BigIntBase getCache10(int base,int numberBase10) {
    return getCache(base).getCustom10(numberBase10);
  }
  
  public BigIntBase getCache10(int numberBase10) {
    return getCache10(base,numberBase10);
  }
  
  public int getDigit(int index) {
    return digits[index];
  }
  
  public int getLength() {
    return digits.length;
  }
  
  public int getOffset() {
    return 0;
  }
  
  public int getSign() {
    return sign;
  }
  
  public int getSize() {
    return getLength();
  }
  
  public boolean isZero() {
    return sign == 0;
  }
  
  /**
   * <pre>
   * This only checks value, not the length, and is important for #setPrecision(...).
   * </pre>
   */
  protected boolean isZero(int[] value) {
    for(int i: value) {
      if(i != 0) {
        return false;
      }
    }
    
    return true;
  }
  
  public BigDecBase toBigDecBase() {
    return null; // TODO: implement
  }
  
  public BigIntBase toBigIntBase() {
    return this;
  }
  
  public MutBigIntBase toMutBigIntBase() {
    return new MutBigIntBase(this);
  }
  
  public String toString() {
    return toString(true);
  }
  
  public String toString(boolean shouldDowncase) {
    char charNum = shouldDowncase ? 'a' : 'A';
    StringBuilder sb = new StringBuilder(digits.length + 1); // +1 for potential sign
    
    if(sign < 0) {
      sb.append('-');
    }
    for(int i = 0; i < digits.length; ++i) {
      int d = digits[i];
      
      if(d < 10) {
        sb.append((char)('0' + d));
      }
      else {
        sb.append((char)(charNum + (d - 10)));
      }
    }
    
    return sb.toString();
  }
  
  public Cache c() {
    return getCache();
  }
  
  public static Cache c(int base) {
    return getCache(base);
  }
  
  public static BigIntBase c(int base,String numberStr) {
    return getCache(base,numberStr);
  }
  
  public BigIntBase c(String numberStr) {
    return getCache(numberStr);
  }
  
  public static BigIntBase c10(int base,int numberBase10) {
    return getCache10(base,numberBase10);
  }
  
  public BigIntBase c10(int numberBase10) {
    return getCache10(numberBase10);
  }
  
  public BigDecBase bdb() {
    return toBigDecBase();
  }
  
  public BigIntBase bib() {
    return toBigIntBase();
  }
  
  public MutBigIntBase mbib() {
    return toMutBigIntBase();
  }
  
  public static class Cache extends BigCacheBase<BigIntBase> {
    private static final long serialVersionUID = 1L;
    
    public final BigIntBase ZERO;
    public final BigIntBase ONE;
    public final BigIntBase TWO;
    public final BigIntBase THREE;
    public final BigIntBase FOUR;
    public final BigIntBase FIVE;
    public final BigIntBase SIX;
    public final BigIntBase SEVEN;
    public final BigIntBase EIGHT;
    public final BigIntBase NINE;
    public final BigIntBase TEN;
    public final BigIntBase ELEVEN;
    public final BigIntBase TWELVE;
    
    // 13 for zero
    protected final Map<String,BigIntBase> CUSTOM = new HashMap<String,BigIntBase>(13);
    
    public Cache(int base) {
      super(base); // Nicki Minaj?
      
      ZERO = new BigIntBase(base);
      ONE = new BigIntBase("1",base);
      TWO = new BigIntBase(Integer.toString(2,base),base);
      THREE = new BigIntBase(Integer.toString(3,base),base);
      FOUR = new BigIntBase(Integer.toString(4,base),base);
      FIVE = new BigIntBase(Integer.toString(5,base),base);
      SIX = new BigIntBase(Integer.toString(6,base),base);
      SEVEN = new BigIntBase(Integer.toString(7,base),base);
      EIGHT = new BigIntBase(Integer.toString(8,base),base);
      NINE = new BigIntBase(Integer.toString(9,base),base);
      TEN = new BigIntBase(Integer.toString(10,base),base);
      ELEVEN = new BigIntBase(Integer.toString(11,base),base);
      TWELVE = new BigIntBase(Integer.toString(12,base),base);
    }
    
    public BigIntBase getCustom(String numberStr) {
      BigIntBase result = CUSTOM.get(numberStr);
      
      if(result == null) {
        CUSTOM.put(numberStr,result = new BigIntBase(numberStr,BASE));
      }
      
      return result;
    }
    
    public BigIntBase zero() {
      return ZERO;
    }
    
    public BigIntBase one() {
      return ONE;
    }
    
    public BigIntBase two() {
      return TWO;
    }
    
    public BigIntBase three() {
      return THREE;
    }
    
    public BigIntBase four() {
      return FOUR;
    }
    
    public BigIntBase five() {
      return FIVE;
    }
    
    public BigIntBase six() {
      return SIX;
    }
    
    public BigIntBase seven() {
      return SEVEN;
    }
    
    public BigIntBase eight() {
      return EIGHT;
    }
    
    public BigIntBase nine() {
      return NINE;
    }
    
    public BigIntBase ten() {
      return TEN;
    }
    
    public BigIntBase eleven() {
      return ELEVEN;
    }
    
    public BigIntBase twelve() {
      return TWELVE;
    }
  }
}
