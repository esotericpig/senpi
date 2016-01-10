package com.esotericpig.senpi;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

// TODO: create cache of most commonly used numbers, populate when used, per base
// TODO: allow user to initialize the cache and/or add new numbers to it, per base

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class MutBigIntBase {
  private static final long serialVersionUID = 1L;
  
  public static final int DEFAULT_BASE = 12;

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
  
  public MutBigIntBase(BigIntBase bib) {
    this.base = bib.base;
    this.digits = Arrays.copyOf(bib.digits,bib.digits.length);
    this.length = bib.digits.length;
    this.offset = 0;
    this.sign = bib.sign;
  }
  
  public MutBigIntBase(MutBigIntBase mbib) {
    this(mbib,mbib.sign);
  }
  
  protected MutBigIntBase(MutBigIntBase mbib,int sign) {
    this.base = mbib.base;
    this.digits = Arrays.copyOf(mbib.digits,mbib.digits.length);
    this.length = mbib.length;
    this.offset = mbib.offset;
    this.sign = sign;
  }
  
  public MutBigIntBase(String s) {
    this(s,DEFAULT_BASE);
  }
  
  public MutBigIntBase(String s,int base) {
    this(s,base,false);
  }
  
  public MutBigIntBase(String s,int base,boolean truncZero) {
    // Use Character.MIN_RADIX/MAX_RADIX instead?
    if(base < 2 || base > (Integer.MAX_VALUE / 2 + 1)) {
      // Unary not supported (because of 0 and 1s need to be tallies)
      throw new UnsupportedBaseException("Unsupported base: " + base);
    }
    
    this.base = base;
    
    // Calculate this.digits.length (store in this.length)
    int begIndex = -1;
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
      else if(!Character.isWhitespace(c)) {
        if(c == '0') {
          hasZero = true;
          
          // 1000
          if(begIndex != -1) {
            ++this.length;
          }
          // 007
          else if(!truncZero) {
            ++this.length;
            ++this.offset;
          }
        }
        else {
          if(begIndex == -1) {
            begIndex = i; // Found first non-zero digit
          }
          ++this.length;
        }
      }
    }
    
    // Zero
    if(begIndex == -1) {
      // For truncZero
      if(this.length == 0 && hasZero) {
        this.length = 1;
      }
      
      this.digits = new int[this.length];
      this.sign = 0; // In case of "-0" or "+0" (which is allowed)
      
      // 000
      if(!truncZero) {
        this.length = 1;
        this.offset = this.digits.length - 1;
      }
      return;
    }
    
    // Convert s to digits
    this.digits = new int[this.length];
    
    if(!truncZero) {
      this.length -= this.offset; // Convert this.length from this.digits.length
    }
    if(this.sign == 0) {
      this.sign = 1; // Default to +#
    }
    
    for(int i = this.offset; begIndex < s.length(); ++begIndex) {
      char c = s.charAt(begIndex);
      
      if(!Character.isWhitespace(c)) {
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
  
  protected MutBigIntBase(int sign,int[] digits,int base,int offset,int length) {
    this.base = base;
    this.digits = digits;
    this.length = length;
    this.offset = offset;
    this.sign = sign;
  }
  
  public static MutBigIntBase add(MutBigIntBase x,MutBigIntBase y) {
    final MutBigIntBase result = x;
    int[] rd = result.digits; // So that we don't overwrite x just yet
    
    // Make top number (x) the longest (100 - 99)
    if(x.length < y.length) {
      x = y;
      y = result;
      
      if(x.length > rd.length) {
        rd = new int[x.length + 1]; // +1 for potential growth
      }
    }
    
    int carry = 0;
    int ri = rd.length;
    int xi = x.digits.length;
    int yi = y.digits.length;
    
    // Add and bring down
    while(xi > x.offset) {
      int sum = x.digits[--xi] + carry;
      
      if(yi > y.offset) {
        sum += y.digits[--yi];
      }
      if(sum < result.base) {
        carry = 0;
      }
      else {
        carry = sum / result.base; // left digit
        sum = sum % result.base; // right digit
      }
      
      rd[--ri] = sum;
    }
    
    // Add carry with potential growth
    if(carry != 0) {
      if(ri <= 0) {
        rd = grow(rd,1);
        ri = 1;
      }
      rd[--ri] += carry;
    }
    
    // Create final result
    result.digits = rd;
    result.length = rd.length - ri;
    result.offset = ri;
    
    return x;
  }
  
  public MutBigIntBase abs() {
    if(sign == -1) {
      sign = 1;
    }
    return this;
  }
  
  public static int compare(MutBigIntBase x,MutBigIntBase y) {
    if(x.length < y.length) {
      return -1;
    }
    if(x.length > y.length) {
      return 1;
    }
    
    // Same length
    int xi = x.offset;
    int yi = y.offset;
    
    for(; xi < x.digits.length; ++xi,++yi) {
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
  
  public int compareTo(MutBigIntBase mbib) {
    if(mbib.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign < mbib.sign) {
      return -1;
    }
    if(sign > mbib.sign) {
      return 1;
    }
    if(sign == 0 && mbib.sign == 0) {
      return 0;
    }
    return compare(this,mbib);
  }
  
  public static MutBigIntBase[] divideRem(MutBigIntBase x,MutBigIntBase y) {
    // Uses Euclidean division:  x = yq + r
    
    int places = Math.max(x.length,y.length);
    MutBigIntBase quotient = new MutBigIntBase(0,new int[places],x.base,places - 1,1);
    MutBigIntBase remainder = new MutBigIntBase(x);
    
    // Slowly rotate each place value (digit) starting from the left like a combination lock
    while(places > 0) {
      divideRem(y,quotient,remainder,places--);
    }
    
    return new MutBigIntBase[]{quotient,remainder};
  }
  
  public static void divideRem(MutBigIntBase y,MutBigIntBase quotient,MutBigIntBase remainder,int places) {
    if(compare(remainder,y) < 0) {
      return;
    }
    
    int comparison = 0;
    MutBigIntBase inc = new MutBigIntBase(1,new int[places],y.base,0,places);
    inc.digits[0] = 1;
    MutBigIntBase dec = multiply(inc,y,null);
    
    while((comparison = compare(remainder,dec)) >= 0) {
      add(quotient,inc);
      subtract(remainder,dec,comparison);
      
      if(compare(remainder,y) < 0) {
        return;
      }
    }
  }
  
  public static int[] grow(int[] x,int lengthToAdd) {
    int[] result = new int[x.length + lengthToAdd];

    for(int i = 1; i < result.length; ++i) {
      result[i] = x[i - 1];
    }
    return result;
  }
  
  public MutBigIntBase ltrim() {
    if(digits.length < 1) {
      return this;
    }
    while(offset < digits.length && digits[offset] == 0) {
      --length;
      ++offset;
    }
    // For 0
    if(length <= 0 || offset >= digits.length) {
      length = 1;
      offset = digits.length - 1;
    }
    return this;
  }
  
  public MutBigIntBase ltrimFix() {
    length = digits.length;
    offset = 0;
    
    return ltrim();
  }
  
  public MutBigIntBase minus(MutBigIntBase mbib) {
    if(mbib.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0) {
      return set(mbib).negate();
    }
    if(mbib.sign == 0) {
      return this;
    }
    // -4 - 4 = -8; 4 - -4 = 8
    if(sign != mbib.sign) {
      return add(this,mbib);
    }
    
    int comparison = compare(this,mbib);
    
    if(comparison == 0) {
      return zero(); // Same value
    }
    return subtract(this,mbib,comparison);
  }
  
  public MutBigIntBase mod(MutBigIntBase mbib) {
    return set(overMod(mbib)[1]);
  }
  
  public static MutBigIntBase multiply(MutBigIntBase x,MutBigIntBase y,MutBigIntBase z) {
    int[] rd = null;
    int rl = x.length + y.length;
    
    // Assumes z is 0ed
    if(z != null) {
      if(z.digits.length >= rl) {
        rd = x.digits;
        rl = x.digits.length;
      }
      else {
        rd = new int[rl];
      }
    }
    else {
      rd = new int[rl];
      z = new MutBigIntBase(x.base); // Zero because z might not be null (above)
    }
    
    for(int yi = y.digits.length - 1; yi >= y.offset; --yi) {
      int carry = 0;
      int ryi = rl - (y.digits.length - yi); // Simulation of "x.length + yi"
      
      for(int xi = x.digits.length - 1,ri = ryi; xi >= x.offset; --xi,--ri) {
        // Assumes rd is 0ed (possibly from z)
        int product = rd[ri] + y.digits[yi] * x.digits[xi] + carry;
        
        if(product < z.base) {
          carry = 0;
        }
        else {
          carry = product / z.base; // left digit
          product = product % z.base; // right digit
        }
        
        rd[ri] = product;
      }
      
      if(carry > 0) {
        rd[ryi - x.length] = carry; // In 111, then 1 => 10 => 100 place
      }
    }
    
    z.digits = rd;
    z.length = rl;
    z.offset = 0;
    z.sign = x.sign * y.sign;
    
    return z.ltrim(); // ltrim() will fix offset and length
  }
  
  public MutBigIntBase negate() {
    sign = -sign;
    return this;
  }
  
  public MutBigIntBase over(MutBigIntBase mbib) {
    return set(overRem(mbib)[0]);
  }
  
  public MutBigIntBase[] overMod(MutBigIntBase mbib) {
    MutBigIntBase[] result = overRem(mbib);
    if(result[1].sign != 0) {
      result[1].sign = 1;
    }
    return result;
  }
  
  public MutBigIntBase[] overRem(MutBigIntBase mbib) {
    if(mbib.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(mbib.sign == 0) {
      throw new DivideByZeroException("Divide by zero");
    }
    if(sign == 0) {
      return new MutBigIntBase[]{new MutBigIntBase(base),new MutBigIntBase(base)};
    }
    
    MutBigIntBase[] result = divideRem(this,mbib);
    MutBigIntBase zero = new MutBigIntBase(base);
    
    result[0].sign = sign * mbib.sign * compare(result[0],zero);
    result[1].sign = sign * compare(result[1],zero);
    
    return result;
  }
  
  public MutBigIntBase plus(MutBigIntBase mbib) {
    if(mbib.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0) {
      return set(mbib);
    }
    if(mbib.sign == 0) {
      return this;
    }
    if(sign != mbib.sign) {
      // Subtraction
      int comparison = compare(this,mbib);
      
      if(comparison == 0) {
        return zero(); // Same value
      }
      return subtract(this,mbib,comparison);
    }
    return add(this,mbib);
  }
  
  public MutBigIntBase rem(MutBigIntBase mbib) {
    return set(overRem(mbib)[1]);
  }
  
  public MutBigIntBase set(MutBigIntBase mbib) {
    base = mbib.base;
    length = mbib.length;
    sign = mbib.sign;
    
    if(digits.length >= mbib.length) {
      offset = digits.length - mbib.length;
      
      for(int i = 0; i < mbib.length; ++i) {
        digits[offset + i] = mbib.digits[mbib.offset + i];
      }
    }
    else {
      digits = Arrays.copyOf(mbib.digits,mbib.digits.length);
      offset = mbib.offset;
    }
    
    return this;
  }
  
  public static MutBigIntBase subtract(MutBigIntBase x,MutBigIntBase y,int comparison) {
    final MutBigIntBase result = x;
    int[] rd = result.digits; // So that we don't overwrite x just yet
    
    // Make top number (x) the longest (100 - 99)
    if(comparison < 0) {
      x = y;
      y = result;
      
      if(x.length > rd.length) {
        rd = new int[x.length];
      }
    }
    
    int borrow = 0;
    int ri = rd.length;
    int xi = x.digits.length;
    int yi = y.digits.length;
    
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
        diff += result.base;
      }
      else {
        borrow = 0;
      }
      
      rd[--ri] = diff;
    }
    
    result.digits = rd;
    result.length = rd.length - ri;
    result.offset = ri; // Will be wrong if brought down all 0s with borrow
    result.sign *= comparison;
    
    return result.ltrim(); // ltrim() to fix offset and length
  }
  
  public MutBigIntBase times(MutBigIntBase mbib) {
    if(mbib.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0 || mbib.sign == 0) {
      return zero();
    }
    return set(multiply(this,mbib,null));
  }
  
  public MutBigIntBase zero() {
    Arrays.fill(digits,0);
    length = 1;
    offset = digits.length - 1;
    sign = 0;
    
    return this;
  }
  
  public int getBase() {
    return base;
  }
  
  public int getSign() {
    return sign;
  }
  
  public boolean isZero() {
    return sign == 0;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder(length + 1);
    
    if(sign < 0) {
      sb.append('-');
    }
    for(int i = offset; i < digits.length; ++i) {
      int d = digits[i];
      
      if(d < 10) {
        sb.append((char)('0' + d));
      }
      else {
        sb.append((char)('A' + (d - 10)));
      }
    }
    return sb.toString();
  }
  
  public static void main(String[] args) {
    System.out.println("<base#> <#> <op> <#>");
    System.out.println("  Ex: 12 2 + 2");
    
    Scanner stdin = new Scanner(System.in);
    
    while(true) {
      System.out.print("> ");
      String s = stdin.nextLine();
      
      if(s == null || s.length() < 7) {
        break;
      }
      
      String[] parts = s.trim().split("\\s+");
      
      if(parts.length < 4) {
        break;
      }
    
      int base = Integer.parseInt(parts[0]);
      char operator = parts[2].charAt(0);
      
      MutBigIntBase a = new MutBigIntBase(parts[1],base);
      MutBigIntBase b = new MutBigIntBase(parts[3],base);
      
      // Internally, it uses base 2, but calling it 10 arbitrarily
      BigInteger a10 = new BigInteger(parts[1],base);
      BigInteger b10 = new BigInteger(parts[3],base);
      BigInteger c10 = null;
      
      switch(operator) {
        case '+':
          a.plus(b);
          c10 = a10.add(b10);
          break;
        case '-':
          a.minus(b);
          c10 = a10.subtract(b10);
          break;
        case '*':
          a.times(b);
          c10 = a10.multiply(b10);
          break;
        case '/':
          a.over(b);
          c10 = a10.divide(b10);
          break;
        case '%':
          a.mod(b);
          c10 = a10.mod(b10);
          break;
        case 'r':
          a.rem(b);
          c10 = a10.remainder(b10);
          break;
        default: throw new UnsupportedOperationException("Invalid operator: " + operator);
      }
      
      System.out.println("MutBigIntBase:");
      System.out.println("\t" + a10.toString(base) + " " + operator + " " + b + " = " + a);
      System.out.println("BigInteger:");
      System.out.println("\t" + a10.toString(base) + " " + operator + " " + b10.toString(base) + " = " + c10.toString(base));
      System.out.println("\t" + a10 + " " + operator + " " + b10 + " = " + c10);
    }
  }
}
