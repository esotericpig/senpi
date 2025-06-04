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

import java.util.Arrays;

/**
 * <pre>
 * This was primarily made to just make division faster.
 * </pre>
 * 
 * @author Bradley Whited
 */
public class MutBigIntBase implements BigNumBase<MutBigIntBase> {
  private static final long serialVersionUID = 1L;
  
  protected int base = 0;
  protected int[] digits = null;
  protected int length = 0;
  protected int offset = 0;
  protected int sign = 0;
  
  public MutBigIntBase() {
    this(DEFAULT_BASE);
  }
  
  public MutBigIntBase(int base) {
    this.base = base;
    this.digits = new int[]{0};
    this.length = 1;
    this.offset = 0;
    this.sign = 0;
  }
  
  protected MutBigIntBase(int sign,int[] digits,int base,int offset,int length) {
    this.base = base;
    this.digits = digits;
    this.length = length;
    this.offset = offset;
    this.sign = sign;
  }
  
  public MutBigIntBase(BigIntBase value) {
    this.base = value.base;
    this.digits = Arrays.copyOf(value.digits,value.digits.length);
    this.length = value.digits.length;
    this.offset = 0;
    this.sign = value.sign;
  }
  
  public MutBigIntBase(MutBigIntBase value) {
    this(value,value.sign);
  }
  
  protected MutBigIntBase(MutBigIntBase value,int sign) {
    this.base = value.base;
    this.digits = Arrays.copyOf(value.digits,value.digits.length);
    this.length = value.length;
    this.offset = value.offset;
    this.sign = sign;
  }
  
  public MutBigIntBase(String valueStr) {
    this(valueStr,DEFAULT_BASE);
  }
  
  public MutBigIntBase(String valueStr,int base) {
    this(valueStr,base,false);
  }
  
  public MutBigIntBase(String valueStr,int base,BigStrBase bsb) {
    BigStrBase.ParsedData pd = bsb.parse(valueStr,base);
    
    this.base = base;
    this.digits = pd.digits;
    this.length = pd.length;
    this.offset = pd.offset;
    this.sign = pd.sign;
  }
  
  public MutBigIntBase(String valueStr,int base,boolean shouldTruncZero) {
    this(valueStr,base,new BigStrBase(shouldTruncZero,false));
  }
  
  public MutBigIntBase copy() {
    return new MutBigIntBase(this);
  }
  
  public MutBigIntBase abs() {
    sign &= 1;
    return this;
  }
  
  /**
   * <pre>
   * This doesn't check #base, #sign, or the comparison for internal methods.
   * 
   * For those checks, use #plus(...) or #minus(...).
   * </pre>
   */
  protected static MutBigIntBase add(MutBigIntBase x,MutBigIntBase y,MutBigIntBase z) {
    int zBase = x.base;
    int zSign = x.sign;
    
    // Make the top number (x) the longest (100 + 99) for z and the loop
    if(x.length < y.length) {
      MutBigIntBase tmp = x;
      x = y;
      y = tmp;
    }
    
    if(z != null) {
      // Make z.digits be >= the longest number
      if((z.offset + z.length) < x.length) {
        z.setPrecision(x.length + 1); // +1 for potential growth from carry
      }
      
      z.sign = zSign;
    }
    else {
      int zLen = x.length + 1; // +1 for potential growth from carry
      z = new MutBigIntBase(zSign,new int[zLen],zBase,zLen - 1,zLen);
    }
    
    int zOffsetLen = z.offset + z.length;
    
    int carry = 0;
    int xi = x.offset + x.length;
    int yi = y.offset + y.length;
    int zi = zOffsetLen;
    
    // Add and bring down
    while(xi > x.offset) {
      int sum = x.digits[--xi] + carry;
      
      if(yi > y.offset) {
        sum += y.digits[--yi];
      }
      if(sum < x.base) {
        carry = 0;
      }
      else {
        carry = sum / z.base; // Left digit
        sum = sum % z.base; // Right digit
      }
      
      z.digits[--zi] = sum;
    }
    
    z.length = zOffsetLen - zi;
    z.offset = zi;
    
    // Add carry with potential growth
    if(carry != 0) {
      if(zi <= 0) {
        z.precise(1);
        zi = 1;
      }
      
      z.digits[--zi] += carry;
      z.length += 1;
      z.offset = zi;
    }
    
    return z;
  }
  
  /**
   * <pre>
   * This doesn't check #base or #sign for internal methods.
   * 
   * For those checks, use #compareTo(...).
   * </pre>
   */
  protected static int compare(MutBigIntBase x,MutBigIntBase y) {
    if(x.length < y.length) {
      return -1;
    }
    if(x.length > y.length) {
      return 1;
    }
    
    // Same length
    int len = x.offset + x.length;
    int xi = x.offset;
    int yi = y.offset;
    
    for(; xi < len; ++xi,++yi) {
      int xd = x.digits[xi];
      int yd = y.digits[yi];
      
      if(xd < yd) {
        return -1;
      }
      if(xd > yd) {
        return 1;
      }
    }
    
    return 0;
  }
  
  public int compareTo(MutBigIntBase y) {
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
    
    return compare(this,y);
  }
  
  /**
   * <pre>
   * This doesn't check #base or #sign for internal methods.
   * 
   * For those checks, use #compareTo(new MutBigIntBase(int)).
   * 
   * This always returns a value of 0 or 1 and is important for #divideRem(...).
   * </pre>
   */
  protected static int compareZero(MutBigIntBase x) {
    // "y" is 0
    
    if(x.length < 1) {
      return 0;
    }
    if(x.length > 1) {
      return 1;
    }
    
    int len = x.offset + x.length;
    int xi = x.offset;
    
    for(; xi < len; ++xi) {
      int xd = x.digits[xi];
      
      if(xd != 0) {
        return 1;
      }
    }
    
    return 0;
  }
  
  /**
   * <pre>
   * This doesn't check #base, #sign, or the comparison for internal methods.
   * 
   * For those checks, use #overRem(...) or #underRem(...).
   * </pre>
   */
  protected static BigQuoBase<MutBigIntBase> divideRem(MutBigIntBase x,MutBigIntBase y) {
    // Uses Euclidean division:  x = yq + r
    
    int places = Math.max(x.length,y.length);
    MutBigIntBase quotient = new MutBigIntBase(0,new int[places],x.base,places - 1,1);
    MutBigIntBase remainder = new MutBigIntBase(x);
    
    // Slowly rotate each place value (digit) starting from the left like a combination lock
    for(; places > 0; --places) {
      if(!divideRem(y,quotient,remainder,places)) {
        break;
      }
    }
    
    BigQuoBase<MutBigIntBase> z = new BigQuoBase<MutBigIntBase>(quotient,remainder);
    
    // We have to #compareZero(...) for the sign, else it will result in -0, instead of just 0.
    // #compareZero(...) doesn't check #sign, so a negative number will still result in 1, so the result will
    //   always be 0 or 1.
    z.quotient.sign = x.sign * y.sign * compareZero(z.quotient);
    z.remainder.sign = x.sign * compareZero(z.remainder);
    
    return z;
  }
  
  protected static boolean divideRem(MutBigIntBase y,MutBigIntBase quotient,MutBigIntBase remainder,int places) {
    // If remainder (x) is < y (ignore #sign), then the quotient is < 0, which is always 0 for ints
    if(compare(remainder,y) < 0) {
      return false;
    }
    
    // 1000...(places)
    MutBigIntBase inc = new MutBigIntBase(1,new int[places],remainder.base,0,places);
    inc.digits[0] = 1;
    
    // dec = y * inc
    int decLen = y.length + (places - 1);
    MutBigIntBase dec = new MutBigIntBase(1,Arrays.copyOfRange(y.digits,y.offset,y.offset + decLen),remainder.base,0,decLen);
    
    int comp = 0;
    
    while((comp = compare(remainder,dec)) >= 0) {
      // Use internal methods to ignore #sign
      
      add(quotient,inc,quotient);
      subtract(remainder,dec,remainder,comp);
      
      // See above comment at top of method
      if(compare(remainder,y) < 0) {
        return false;
      }
    }
    
    return true;
  }
  
  public MutBigIntBase floorMod(MutBigIntBase y) {
    BigQuoBase<MutBigIntBase> result = overRem(y);
    
    if(result.remainder.sign != 0 && sign != y.sign) {
      result.quotient.minus(new MutBigIntBase(1,new int[]{1},base,0,1)); // --quotient;
    }
    
    // Use #negate() and #plus(..) instead of reversing the order because this class is mutable, else we'd
    //   have to create a new instance.
    // Equivalent to:  (this.copy()).minus(result.quotient.times(y))
    return result.quotient.times(y).negate().plus(this);
  }
  
  public MutBigIntBase ltrim() {
    if(digits.length <= 1 || length <= 1) {
      if(digits.length >= 1 && digits[offset] == 0) {
        sign = 0;
      }
      
      return this;
    }
    
    int offsetLen = offset + length;
    
    while(offset < offsetLen && digits[offset] == 0) {
      --length;
      ++offset;
    }
    
    // For 0
    if(length <= 0 || offset >= offsetLen) {
      length = 1;
      offset = offsetLen - 1;
      sign = 0;
    }
    
    return this;
  }
  
  public MutBigIntBase minus(MutBigIntBase y) {
    if(y.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0) {
      return set(y).negate();
    }
    if(y.sign == 0) {
      return this;
    }
    // -4 - 4 = -8; 4 - -4 = 8
    if(sign != y.sign) {
      return add(this,y,this);
    }
    
    int comp = compare(this,y);
    
    if(comp == 0) {
      return zero(); // Same value
    }
    
    return subtract(this,y,this,comp);
  }
  
  public MutBigIntBase mod(MutBigIntBase y) {
    MutBigIntBase result = overRem(y).remainder;
    return set((result.sign >= 0) ? result : result.plus(y));
  }
  
  /**
   * <pre>
   * This doesn't check #base or #sign for internal methods.
   * 
   * For those checks, use #times(...).
   * </pre>
   */
  protected static MutBigIntBase multiply(MutBigIntBase x,MutBigIntBase y,MutBigIntBase z) {
    int zBase = x.base;
    int zOffsetLen = x.length + y.length; // Max length of product
    int zSign = x.sign * y.sign;
    
    if(z != null) {
      // Assumes z is 0ed
      
      int zol = z.offset + z.length;
      
      if(zol >= zOffsetLen) {
        // Set offset and length for #ltrim()
        z.length = zOffsetLen;
        z.offset = zol - zOffsetLen;
      }
      else {
        z.setPrecision(zOffsetLen);
        
        // Set offset and length for #ltrim()
        z.length = zOffsetLen;
        z.offset = 0;
      }
      
      z.sign = zSign;
    }
    else {
      // Offset is 0 for #ltrim()
      z = new MutBigIntBase(zSign,new int[zOffsetLen],zBase,0,zOffsetLen);
    }
    
    int xOffsetLen = x.offset + x.length;
    int yOffsetLen = y.offset + y.length;
    
    for(int yi = yOffsetLen - 1; yi >= y.offset; --yi) {
      int carry = 0;
      final int zyi = zOffsetLen - (yOffsetLen - yi); // z.offset + x.length + (yi - y.offset)
      
      for(int xi = xOffsetLen - 1,zi = zyi; xi >= x.offset; --xi,--zi) {
        // Assumes z is 0ed
        
        int product = z.digits[zi] + y.digits[yi] * x.digits[xi] + carry;
        
        if(product < z.base) {
          carry = 0;
        }
        else {
          carry = product / z.base; // Left digit
          product = product % z.base; // Right digit
        }
        
        z.digits[zi] = product;
      }
      
      if(carry > 0) {
        z.digits[zyi - x.length] = carry; // In 111, then 1 => 10 => 100 place
      }
    }
    
    return z.ltrim(); // #ltrim() will fix offset and length
  }
  
  public MutBigIntBase negate() {
    sign = -sign;
    return this;
  }
  
  public MutBigIntBase over(MutBigIntBase y) {
    return set(overRem(y).quotient);
  }
  
  /**
   * #this and y will not be modified, immutable.
   */
  public BigQuoBase<MutBigIntBase> overRem(MutBigIntBase y) {
    if(y.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(y.sign == 0) {
      throw new DivideByZeroException("Divide by zero");
    }
    if(sign == 0) {
      return new BigQuoBase<MutBigIntBase>(new MutBigIntBase(base),new MutBigIntBase(base));
    }
    
    int comp = compare(this,y); // Don't use #compareTo(...); ignore #sign
    
    if(comp < 0) {
      // For ints, an x (this) value < y (ignore #sign) always has a quotient of 0
      return new BigQuoBase<MutBigIntBase>(new MutBigIntBase(base),new MutBigIntBase(this));
    }
    if(comp == 0) {
      // Same value; return 1 w/ appropriate sign
      return new BigQuoBase<MutBigIntBase>(new MutBigIntBase(sign * y.sign,new int[]{1},base,0,1),new MutBigIntBase(base));
    }
    
    return divideRem(this,y);
  }
  
  public MutBigIntBase plus(MutBigIntBase y) {
    if(y.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0) {
      return set(y);
    }
    if(y.sign == 0) {
      return this;
    }
    if(sign != y.sign) {
      int comp = compare(this,y);
      
      if(comp == 0) {
        return zero(); // Same value
      }
      
      return subtract(this,y,this,comp);
    }
    
    return add(this,y,this);
  }
  
  /**
   * @see #setPrecision(int)
   */
  public MutBigIntBase precise(int precisionToAddOrSub) {
    return setPrecision(length + precisionToAddOrSub);
  }
  
  public MutBigIntBase rem(MutBigIntBase y) {
    return set(overRem(y).remainder);
  }
  
  public MutBigIntBase rtrim() {
    if(digits.length <= 1 || length <= 1) {
      if(digits.length >= 1 && digits[offset] == 0) {
        sign = 0;
      }
      
      return this;
    }
    
    for(int i = offset + length - 1; i >= offset && digits[offset] == 0; --i) {
      --length;
    }
    
    // For 0
    if(length <= 0) {
      length = 1;
    }
    
    return this;
  }
  
  /**
   * @see #setScale(int)
   */
  public MutBigIntBase scale(int scaleToAddOrSub) {
    return setScale(length + scaleToAddOrSub);
  }
  
  /**
   * <pre>
   * This doesn't check #base, #sign, or the comparison for internal methods.
   * 
   * For those checks, use #minus(...) or #plus(...).
   * </pre>
   */
  protected static MutBigIntBase subtract(MutBigIntBase x,MutBigIntBase y,MutBigIntBase z,int comparison) {
    int zSign = x.sign * comparison;
    
    // Make the top number (x) the largest value (99 - 88) for z and the loop
    if(comparison < 0) {
      MutBigIntBase tmp = x;
      x = y;
      y = tmp;
    }
    
    if(z != null) {
      // Make z.digits be >= the longest number
      if((z.offset + z.length) < x.length) {
        z.setPrecision(x.length);
      }
      
      z.sign = zSign;
    }
    else {
      z = new MutBigIntBase(zSign,new int[x.length],x.base,x.length - 1,x.length);
    }
    
    int zOffsetLen = z.offset + z.length;
    
    int borrow = 0;
    int xi = x.offset + x.length;
    int yi = y.offset + y.length;
    int zi = zOffsetLen;
    
    // Subtract and borrow
    while(xi > x.offset) {
      int diff = x.digits[--xi] - borrow;
      
      if(yi > y.offset) {
        diff -= y.digits[--yi];
      }
      // In binary, 10 - 1 => 0 - 1 => -1 => -1 + 2 => 1 (borrow)
      // Also here because of 1000 - 1 to bring down
      if(diff < 0) {
        borrow = 1;
        diff += z.base;
      }
      else {
        borrow = 0;
      }
      
      z.digits[--zi] = diff;
    }
    
    z.length = zOffsetLen - zi;
    z.offset = zi; // Will be wrong if brought down all 0s with borrow
    
    return z.ltrim(); // #ltrim() to fix offset and length (1000 - 999 = 1 [not 001])
  }
  
  public MutBigIntBase times(MutBigIntBase y) {
    if(y.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0 || y.sign == 0) {
      return zero();
    }
    
    return set(multiply(this,y,null));
  }
  
  public MutBigIntBase trim() {
    if(digits.length <= 1 || length <= 1) {
      if(digits.length >= 1 && digits[offset] == 0) {
        sign = 0;
      }
      
      return this;
    }
    
    int offsetLen = offset + length;
    
    // ltrim
    while(offset < offsetLen && digits[offset] == 0) {
      --length;
      ++offset;
    }
    
    // rtrim
    for(int i = offsetLen - 1; i >= offset && digits[offset] == 0; --i) {
      --length;
    }
    
    // For 0
    if(length <= 0 || offset >= offsetLen) {
      length = 1;
      offset = offsetLen - 1;
      sign = 0;
    }
    
    return this;
  }
  
  public MutBigIntBase under(MutBigIntBase y) {
    return set(underRem(y).quotient);
  }
  
  /**
   * #this and y will not be modified, immutable.
   */
  public BigQuoBase<MutBigIntBase> underRem(MutBigIntBase y) {
    return y.overRem(this);
  }
  
  public MutBigIntBase zero() {
    if(sign == 0) {
      return this;
    }
    
    if(digits.length > 0) {
      int offsetLen = offset + length;
      
      Arrays.fill(digits,offset,offsetLen,0);
      
      offset = offsetLen - 1;
      length = 1;
    }
    else {
      offset = 0;
      length = 0;
    }
    
    sign = 0;
    
    return this;
  }
  
  public MutBigIntBase set(MutBigIntBase value) {
    if(digits.length >= value.length) {
      Arrays.fill(digits,offset,offset + length,0);
      
      int valueOffsetLen = value.offset + value.length;
      
      offset = digits.length - value.length;
      
      for(int i = offset,j = value.offset; j < valueOffsetLen; ++i,++j) {
        digits[i] = value.digits[j];
      }
    }
    else {
      digits = Arrays.copyOf(value.digits,value.digits.length);
      offset = value.offset;
    }
    
    base = value.base;
    length = value.length;
    sign = value.sign;
    
    return this;
  }
  
  /**
   * <pre>
   * This is a bit untraditional, and as the opposite of #scale(int), it will
   * increase or decrease the size of #digits on the left side with 0s.
   * 
   * This is useful for #add(...), #multiply(...), #subtract(...), etc.
   * 
   * If there is room in #digits on the left side, it won't modify anything.
   * 
   * If there is room in #digits on the right side, it will shift the digits
   * right.
   * 
   * Else, it will expand the array #digits on the left side.
   * </pre>
   */
  public MutBigIntBase setPrecision(int precision) {
    if(precision == length) {
      return this;
    }
    if(precision < 1) {
      return zero();
    }
    
    if(precision > length) {
      int precDiff = precision - length;
      
      // Is there NO room on the left side?
      if(precDiff > offset) {
        // Is there NO room on the right side?
        if(precision > (digits.length - offset)) {
          // Expand the array on the left side
          int[] newDigits = new int[digits.length + precDiff];
          
          int j = offset;
          int offsetLen = j + length;
          
          offset += precDiff;
          
          int i = offset;
          
          for(; j < offsetLen; ++i,++j) {
            newDigits[i] = digits[j];
          }
          
          digits = newDigits;
        }
        else {
          // Shift to the right
          int j = offset + length - 1;
          
          offset += precDiff;
          
          int i = offset + precision - 1;
          
          for(; j >= 0; --i,--j) {
            digits[i] = digits[j];
          }
          for(; i >= 0; --i) {
            digits[i] = 0;
          }
        }
      }
    }
    else {
      // Fill the left side with 0s up to the new length
      int precDiff = length - precision;
      
      Arrays.fill(digits,offset,offset + precDiff,0);
      
      length = precision; // This is the only time #length is set, by design
      offset += precDiff;
    }
    
    return this;
  }
  
  /**
   * <pre>
   * The decimal point is always at the end of an int, so this will increase or
   * decrease the size of #digits on the right side with 0s.
   * 
   * If there is room in #digits on the right side, it will modify #length and
   * not modify #digits.
   * 
   * If there is room in #digits on the left side, it will shift the digits
   * left.
   * 
   * Else, it will expand the array #digits on the right side.
   * </pre>
   */
  public MutBigIntBase setScale(int scale) {
    if(scale == length) {
      return this;
    }
    if(scale < 1) {
      return zero();
    }
    
    if(scale > length) {
      // Is there NO room on the right side?
      if(scale > (digits.length - offset)) {
        int scaleDiff = scale - length;
        
        // Is there NO room on the left side?
        if(scaleDiff > offset) {
          // Expand the array on the right side
          // - Arrays#copyOf(...) will increase the size and pad with trailing 0s
          digits = Arrays.copyOf(digits,digits.length + scaleDiff);
        }
        else {
          // Shift to the left
          int j = offset;
          int offsetLen = j + length;
          
          offset = offset - scaleDiff;
          
          int i = offset;
          int offsetScale = i + scale;
          
          for(; j < offsetLen; ++i,++j) {
            digits[i] = digits[j];
          }
          for(; i < offsetScale; ++i) {
            digits[i] = 0;
          }
        }
      }
    }
    else {
      // Fill the right side with 0s up to the new length
      Arrays.fill(digits,offset + scale,offset + length,0);
    }
    
    length = scale;
    
    return this;
  }
  
  public int get(int index) {
    return digits[index];
  }
  
  public int getBase() {
    return base;
  }
  
  public int getDigit(int index) {
    return digits[offset + index];
  }
  
  public int getLength() {
    return length;
  }
  
  public int getOffset() {
    return offset;
  }
  
  public int getSign() {
    return sign;
  }
  
  public int getSize() {
    return digits.length;
  }
  
  public boolean isZero() {
    return sign == 0;
  }
  
  public BigDecBase toBigDecBase() {
    return null; // TODO: implement
  }
  
  public BigIntBase toBigIntBase() {
    return new BigIntBase(this);
  }
  
  public MutBigIntBase toMutBigIntBase() {
    return this;
  }
  
  public String toString() {
    return toString(true);
  }
  
  public String toString(boolean shouldDowncase) {
    char charNum = shouldDowncase ? 'a' : 'A';
    int offsetLen = offset + length;
    StringBuilder sb = new StringBuilder(length + 1); // +1 for potential sign
    
    if(sign < 0) {
      sb.append('-');
    }
    for(int i = offset; i < offsetLen; ++i) {
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
  
  public BigDecBase bdb() {
    return toBigDecBase();
  }
  
  public BigIntBase bib() {
    return toBigIntBase();
  }
  
  public MutBigIntBase mbib() {
    return toMutBigIntBase();
  }
}
