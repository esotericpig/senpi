package com.esotericpig.senpi;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

// TODO: same cache as MutBigIntBase

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class BigIntBase {
  private static final long serialVersionUID = 1L;
  
  protected int base = 0;
  protected int[] digits = null;
  protected int sign = 0;
  
  public BigIntBase() {
    this(MutBigIntBase.DEFAULT_BASE);
  }
  
  public BigIntBase(int base) {
    this.base = base;
    this.digits = new int[]{0};
    this.sign = 0;
  }
  
  public BigIntBase(BigIntBase bib) {
    this(bib,bib.sign);
  }
  
  protected BigIntBase(BigIntBase bib,int sign) {
    this.base = bib.base;
    this.digits = Arrays.copyOf(bib.digits,bib.digits.length);
    this.sign = sign;
  }
  
  public BigIntBase(MutBigIntBase mbib) {
    this.base = mbib.base;
    this.digits = Arrays.copyOfRange(mbib.digits,mbib.offset,mbib.digits.length);
    this.sign = mbib.sign;
  }
  
  public BigIntBase(String s) {
    this(s,MutBigIntBase.DEFAULT_BASE);
  }
  
  public BigIntBase(String s,int base) {
    MutBigIntBase mbib = new MutBigIntBase(s,base,true);
    
    this.base = base;
    this.digits = mbib.digits;
    this.sign = mbib.sign;
  }
  
  protected BigIntBase(int sign,int[] digits,int base) {
    this.base = base;
    this.digits = digits;
    this.sign = sign;
  }
  
  public BigIntBase abs() {
    return (sign == -1) ? new BigIntBase(this,1) : this;
  }
  
  public static int[] add(int[] x,int[] y,int base) {
    // Make top number (x) the longest (100 + 99)
    if(x.length < y.length) {
      int[] temp = x;
      x = y;
      y = temp;
    }
    
    int carry = 0;
    int[] result = new int[x.length];
    int xi = x.length;
    int yi = y.length;
    
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
        carry = sum / base; // left digit
        sum = sum % base; // right digit
      }
      
      result[xi] = sum;
    }
    
    // Add carry with potential growth
    if(carry != 0) {
      if(xi <= 0) {
        result = grow(result,1);
        xi = 0;
      }
      result[xi] = carry;
    }
    
    return result;
  }
  
  public static int compare(int[] x,int[] y) {
    if(x.length < y.length) {
      return -1;
    }
    if(x.length > y.length) {
      return 1;
    }
    for(int i = 0; i < x.length; ++i) {
      if(x[i] < y[i]) {
        return -1;
      }
      if(x[i] > y[i]) {
        return 1;
      }
    }
    return 0;
  }
  
  public int compareTo(BigIntBase bib) {
    if(bib.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign < bib.sign) {
      return -1;
    }
    if(sign > bib.sign) {
      return 1;
    }
    if(sign == 0 && bib.sign == 0) {
      return 0;
    }
    return compare(digits,bib.digits);
  }
  
  public static int[] grow(int[] x,int lengthToAdd) {
    int[] result = new int[x.length + lengthToAdd];

    for(int i = 1; i < result.length; ++i) {
      result[i] = x[i - 1];
    }
    return result;
  }
  
  public static int[] ltrim(int[] x) {
    if(x.length == 1) {
      return x;
    }
    
    int i = 0;
    
    while(i < x.length && x[i] == 0) {
      ++i;
    }
    if(i < 1) {
      return x;
    }
    if(i >= x.length) {
      return new int[]{0};
    }
    return Arrays.copyOfRange(x,i,x.length);
  }
  
  public BigIntBase minus(BigIntBase bib) {
    if(bib.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0) {
      return bib.negate();
    }
    if(bib.sign == 0) {
      return this;
    }
    // -4 - 4 = -8; 4 - -4 = 8
    if(sign != bib.sign) {
      // Addition
      return new BigIntBase(sign,add(digits,bib.digits,base),base);
    }
    
    int comparison = compare(digits,bib.digits);
    
    if(comparison == 0) {
      return new BigIntBase(base); // Same value
    }
    return new BigIntBase(sign * comparison,subtract(digits,bib.digits,base,comparison),base);
  }
  
  public BigIntBase mod(BigIntBase bib) {
    return overMod(bib)[1];
  }
  
  public static int[] multiply(int[] x,int[] y,int base) {
    int[] result = new int[x.length + y.length];
    
    for(int yi = y.length - 1; yi >= 0; --yi) {
      int carry = 0;
      
      for(int xi = x.length - 1,ri = x.length + yi; xi >= 0; --xi,--ri) {
        int product = result[ri] + y[yi] * x[xi] + carry;
        
        if(product < base) {
          carry = 0;
        }
        else {
          carry = product / base; // left digit
          product = product % base; // right digit
        }
        
        result[ri] = product;
      }
      
      if(carry > 0) {
        result[yi] = carry; // yi, so that in 111, then 1 => 10 => 100 place
      }
    }
    
    // Shrink down for overestimation
    result = ltrim(result);
    
    return result;
  }
  
  public BigIntBase negate() {
    return (sign != 0) ? new BigIntBase(this,-sign) : this;
  }
  
  public BigIntBase over(BigIntBase bib) {
    return overRem(bib)[0];
  }
  
  public BigIntBase[] overMod(BigIntBase bib) {
    BigIntBase[] result = overRem(bib);
    if(result[1].sign != 0) {
      result[1].sign = 1;
    }
    return result;
  }
  
  public BigIntBase[] overRem(BigIntBase bib) {
    MutBigIntBase x = new MutBigIntBase(this);
    MutBigIntBase y = new MutBigIntBase(bib);
    MutBigIntBase[] z = x.overRem(y);
    
    return new BigIntBase[] {
      new BigIntBase(z[0])
      ,new BigIntBase(z[1])
    };
  }
  
  public BigIntBase plus(BigIntBase bib) {
    if(bib.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0) {
      return bib;
    }
    if(bib.sign == 0) {
      return this;
    }
    if(sign != bib.sign) {
      // Subtraction
      int comparison = compare(digits,bib.digits);
      
      if(comparison == 0) {
        return new BigIntBase(base); // Same value
      }
      return new BigIntBase(sign * comparison,subtract(digits,bib.digits,base,comparison),base);
    }
    return new BigIntBase(sign,add(digits,bib.digits,base),base);
  }
  
  public BigIntBase rem(BigIntBase bib) {
    return overRem(bib)[1];
  }
  
  public static int[] subtract(int[] x,int[] y,int base,int comparison) {
    // Make top number (x) the longest (100 - 99)
    if(comparison < 0) {
      int[] temp = x;
      x = y;
      y = temp;
    }
    
    int borrow = 0;
    int[] result = new int[x.length];
    int xi = x.length;
    int yi = y.length;
    
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
      
      result[xi] = diff;
    }
    
    // Shrink down for borrow and/or 0s (1000 - 999 = 1 [not 001])
    return ltrim(result);
  }
  
  public BigIntBase times(BigIntBase bib) {
    if(bib.base != base) {
      throw new IncompatibleBaseException("Incompatible base");
    }
    if(sign == 0 || bib.sign == 0) {
      return new BigIntBase(base);
    }
    return new BigIntBase(sign * bib.sign,multiply(digits,bib.digits,base),base);
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
    StringBuilder sb = new StringBuilder(digits.length + 1);
    
    if(sign < 0) {
      sb.append('-');
    }
    for(int i = 0; i < digits.length; ++i) {
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
      
      BigIntBase a = new BigIntBase(parts[1],base);
      BigIntBase b = new BigIntBase(parts[3],base);
      BigIntBase c = null;
      
      // Internally, it uses base 2, but calling it 10 arbitrarily
      BigInteger a10 = new BigInteger(parts[1],base);
      BigInteger b10 = new BigInteger(parts[3],base);
      BigInteger c10 = null;
      
      switch(operator) {
        case '+':
          c = a.plus(b);
          c10 = a10.add(b10);
          break;
        case '-':
          c = a.minus(b);
          c10 = a10.subtract(b10);
          break;
        case '*':
          c = a.times(b);
          c10 = a10.multiply(b10);
          break;
        case '/':
          c = a.over(b);
          c10 = a10.divide(b10);
          break;
        case '%':
          c = a.mod(b);
          c10 = a10.mod(b10);
          break;
        case 'r':
          c = a.rem(b);
          c10 = a10.remainder(b10);
          break;
        default: throw new UnsupportedOperationException("Invalid operator: " + operator);
      }
      
      System.out.println("BigIntBase:");
      System.out.println("\t" + a + " " + operator + " " + b + " = " + c);
      System.out.println("BigInteger:");
      System.out.println("\t" + a10.toString(base) + " " + operator + " " + b10.toString(base) + " = " + c10.toString(base));
      System.out.println("\t" + a10 + " " + operator + " " + b10 + " = " + c10);
    }
  }
}
