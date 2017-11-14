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

import java.util.Random;

/**
 * <pre>
 * 1) Parser for Integer/Decimal strings in user-specified bases.
 * 2) Random number strings in user-specified bases.
 * </pre>
 *
 * @author Jonathan Bradley Whited (@esotericpig)
 */
public class BigStrBase implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public boolean isDecimal;
  public boolean shouldTruncZero;
  
  public BigStrBase(boolean shouldTruncZero,boolean isDecimal) {
    this.isDecimal = isDecimal;
    this.shouldTruncZero = shouldTruncZero;
  }
  
  public ParsedData parse(String numberStr,int base) {
    // TODO: Use Character.MIN_RADIX/MAX_RADIX instead?
    if(base < 2 || base > (Integer.MAX_VALUE / 2 + 1)) {
      // Unary not supported (because of 0, and 1s need to be tallies)
      throw new UnsupportedBaseException("Unsupported base: " + base);
    }
    
    ParsedData pd = new ParsedData();
    
    // Calculate digits.length (store in length)
    int begIndex = -1;
    boolean hasDot = false;
    boolean hasInt = true;
    boolean hasNonZeroAfterDot = false;
    boolean hasZero = false;
    
    for(int i = 0; i < numberStr.length(); ++i) {
      char c = numberStr.charAt(i);
      
      if(c == '-' || c == '+') {
        if(pd.sign == 0) {
          pd.sign = (c == '-') ? -1 : 1;
        }
        else {
          throw new InvalidSignException("Invalid extra sign(s)");
        }
        if(begIndex != -1 && !hasZero) {
          throw new InvalidSignException("Invalid sign after digit(s)");
        }
      }
      else if(isDecimal && c == '.') {
        if(hasDot) {
          throw new InvalidDotException("Invalid extra decimal point(s)");
        }
        // .007 (instead of 0.007)
        if(begIndex == -1) {
          begIndex = i;
          hasInt = false;
          ++pd.length; // Add a 0 in place of dot
        }
        hasDot = true;
      }
      else if(!Character.isWhitespace(c)) {
        if(c == '0') {
          if(hasDot) {
            ++pd.length;
            ++pd.scale;
          }
          else {
            hasZero = true;
            
            // 1000
            if(begIndex != -1) {
              ++pd.length;
            }
            // 007
            else if(!shouldTruncZero) {
              ++pd.length;
              ++pd.offset;
            }
          }
        }
        else {
          if(hasDot) {
            hasNonZeroAfterDot = true;
            ++pd.scale;
          }
          else if(begIndex == -1) {
            begIndex = i; // Found first non-zero digit (before dot)
          }
          ++pd.length;
        }
      }
    }
    
    // Zero
    if((begIndex == -1 || !hasInt) && !hasNonZeroAfterDot) {
      // Don't allow size of 0
      if(pd.length == 0) {
        pd.length = 1;
      }
      
      pd.digits = new int[pd.length];
      pd.sign = 0; // In case of "-0" or "+0" (which is allowed)
      
      // 000
      if(hasZero && !shouldTruncZero) {
        pd.length = 1;
        pd.offset = pd.digits.length - pd.scale - 1;
      }
      return pd;
    }
    
    // Convert numberStr to digits
    pd.digits = new int[pd.length];
    
    if(!shouldTruncZero) {
      pd.length -= pd.offset; // Change len from 007 to match 7
    }
    if(pd.sign == 0) {
      pd.sign = 1; // Default to +#
    }
    
    for(int i = pd.offset; begIndex < numberStr.length(); ++begIndex) {
      char c = numberStr.charAt(begIndex);
      
      if(isDecimal && c == '.') {
        // .007 (instead of 0.007)
        if(!hasInt) {
          pd.digits[i++] = 0; // Add 0 in place of dot to make 0.007
        }
      }
      else if(!Character.isWhitespace(c)) {
        int d = -1;
        
        if(c >= '0' && c <= '9') {
          d = c - '0';
        }
        else {
          c = Character.toUpperCase(c);
          d = c - 'A' + 10;
        }
        if(d < 0 || d >= base) {
          throw new InvalidDigitException("Invalid digit outside of base: " + d);
        }
        
        pd.digits[i++] = d;
      }
    }
    return pd;
  }
  
  public static String randNumStr(int base,int minLen,int maxLen) {
    Random rand = new Random();
    return randNumStr(base,minLen,maxLen,rand.nextBoolean(),rand.nextBoolean(),rand.nextBoolean(),rand);
  }
  
  public static String randNumStr(int base,int minLen,int maxLen,boolean isPositive,boolean allowZeroPad,boolean isDecimal,Random rand) {
    if(base < 2) {
      // Avoid infinite loop at !allowZeroPad
      throw new UnsupportedBaseException("Unsupported base: " + base);
    }
    
    // For min=0 and max=5: len = 0 + rand(6)[0-5]
    // For min=2 and max=5: len = 2 + rand(4)[0-3]
    // For min=5 and max=5: len = 5 + rand(1)[0]
    maxLen = maxLen - minLen + 1;
    int len = minLen + rand.nextInt(maxLen);
    
    if(len < 1) {
      return "0";
    }
    
    int digit = 0;
    StringBuilder sb = new StringBuilder(len + 2); // +2 for (potential) sign/dot
    
    if(isPositive) {
      // Add "+" or not for +#? (+0 is allowed)
      if(rand.nextBoolean()) {
        sb.append('+');
      }
    }
    // -#? (-0 is allowed)
    else {
      sb.append('-');
    }
    
    digit = rand.nextInt(base);
    
    // 007? (also, essentially, don't allow a 0#)
    if(!allowZeroPad) {
      while(digit == 0) {
        digit = rand.nextInt(base);
      }
    }
    
    int dotIndex = isDecimal ? rand.nextInt(len) : -1;
    
    for(int i = 0; i < len; ++i) {
      if(i == dotIndex) {
        sb.append('.');
      }
      sb.append(Integer.toString(digit,base));
      digit = rand.nextInt(base);
    }
    return sb.toString();
  }
  
  public static class ParsedData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int[] digits = null;
    public int length = 0;
    public int offset = 0;
    public int scale = 0;
    public int sign = 0;
  }
}
