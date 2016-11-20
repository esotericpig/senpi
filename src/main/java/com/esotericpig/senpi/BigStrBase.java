package com.esotericpig.senpi;

import java.util.Random;

/**
 * <pre>
 * 1) Parser for Integer/Decimal strings in user-specified bases.
 * 2) Random number strings in user-specified bases.
 * </pre>
 *
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class BigStrBase {
  public int[] digits;
  public boolean isDec;
  public boolean isTruncZero;
  public int length;
  public int offset;
  public int scale;
  public int sign;
  
  public BigStrBase(boolean isTruncZero,boolean isDec) {
    clear();
    
    this.isDec = isDec;
    this.isTruncZero = isTruncZero;
  }
  
  public void clear() {
    digits = null;
    length = 0;
    offset = 0;
    scale = 0;
    sign = 0;
  }
  
  public void parse(String s,int base) {
    clear();
  
    // Use Character.MIN_RADIX/MAX_RADIX instead?
    if(base < 2 || base > (Integer.MAX_VALUE / 2 + 1)) {
      // Unary not supported (because of 0, and 1s need to be tallies)
      throw new UnsupportedBaseException("Unsupported base: " + base);
    }
    
    // Calculate this.digits.length (store in this.length)
    int begIndex = -1;
    boolean hasDot = false;
    boolean hasInt = true;
    boolean hasNonZeroAfterDot = false;
    boolean hasZero = false;
    
    for(int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      
      if(c == '-' || c == '+') {
        if(this.sign == 0) {
          this.sign = (c == '-') ? -1 : 1;
        }
        else {
          throw new InvalidSignException("Invalid extra sign character(s)");
        }
      }
      else if(isDec && c == '.') {
        if(hasDot) {
          throw new InvalidDotException("Invalid extra decimal point(s)");
        }
        // .007 (instead of 0.007)
        if(begIndex == -1) {
          begIndex = i;
          hasInt = false;
          ++this.length; // Add a 0 in place of dot
        }
        hasDot = true;
      }
      else if(!Character.isWhitespace(c)) {
        if(c == '0') {
          if(hasDot) {
            ++this.length;
            ++this.scale;
          }
          else {
            hasZero = true;
            
            // 1000
            if(begIndex != -1) {
              ++this.length;
            }
            // 007
            else if(!isTruncZero) {
              ++this.length;
              ++this.offset;
            }
          }
        }
        else {
          if(hasDot) {
            hasNonZeroAfterDot = true;
            ++this.scale;
          }
          else if(begIndex == -1) {
            begIndex = i; // Found first non-zero digit (before dot)
          }
          ++this.length;
        }
      }
    }
    
    // Zero
    if((begIndex == -1 || !hasInt) && !hasNonZeroAfterDot) {
      // Don't allow size of 0
      if(this.length == 0) {
        this.length = 1;
      }
      
      this.digits = new int[this.length];
      this.sign = 0; // In case of "-0" or "+0" (which is allowed)
      
      // 000
      if(hasZero && !isTruncZero) {
        this.length = 1;
        this.offset = this.digits.length - this.scale - 1;
      }
      return;
    }
    
    // Convert s to digits
    this.digits = new int[this.length];
    
    if(!isTruncZero) {
      this.length -= this.offset; // Change len from 007 to match 7
    }
    if(this.sign == 0) {
      this.sign = 1; // Default to +#
    }
    
    for(int i = this.offset; begIndex < s.length(); ++begIndex) {
      char c = s.charAt(begIndex);
      
      if(isDec && c == '.') {
        // .007 (instead of 0.007)
        if(!hasInt) {
          this.digits[i++] = 0; // Add 0 in place of dot to make 0.007
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
          throw new InvalidDigitException("Invalid digit: " + d);
        }
        
        this.digits[i++] = d;
      }
    }
  }
  
  public static String randNumStr(int base,int minLen,int maxLen,boolean isPos) {
    return randNumStr(base,minLen,maxLen,isPos,false,new Random());
  }
  
  public static String randNumStr(int base,int minLen,int maxLen,boolean isPos,boolean allowZeroPad,Random rand) {
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
    StringBuilder sb = new StringBuilder(len + 1); // +1 for (potential) sign
    
    if(isPos) {
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
    for(int i = 0; i < len; ++i) {
      sb.append(Integer.toString(digit,base));
      digit = rand.nextInt(base);
    }
    return sb.toString();
  }
}
