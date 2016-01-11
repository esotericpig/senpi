package com.esotericpig.senpi;

import java.math.BigInteger;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class AllBigIntBaseTest {
  public static final int TEST_COUNT = 100; // Number of tests to perform

  protected Random rand = null;
  
  @Before
  public void setUp() {
    rand = new Random();
  }

  public static String createRandNumStr(int base,int minLen,int maxLen,boolean allowZeroPad,Random rand) {
    if(base < 2) {
      // Avoid infinite loop at !allowZeroPad
      throw new UnsupportedBaseException("Unsupported base: " + base);
    }
  
    int digit = 0;
    int len = minLen + rand.nextInt(maxLen);
    StringBuilder sb = new StringBuilder(len);
    
    // -#? (-0 is allowed)
    if(rand.nextBoolean()) {
      sb.append("-");
    }
    // Add "+" or not for +#? (+0 is allowed)
    else if(rand.nextBoolean()) {
      sb.append("+");
    }
    
    digit = rand.nextInt(base);
    
    // 007?
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

  @Test
  public void testAll() {
    // Currently, only tests:
    //   - +, -, *, /, % (modulus), r (remainder)
    //   - Base 12
    // (Called modulus because it is the absolute value, instead of modulo;
    //  however, it is kind of ambiguous.)
    final int base = 12;
    final char[] ops = {'+','-','*','/','%','r'};
    
    StringBuilder msg = new StringBuilder();
    
    for(char op: ops) {
      for(int t = 0; t < TEST_COUNT; ++t) {
        String numStrA = createRandNumStr(base,1,100,rand.nextBoolean(),rand);
        String numStrB = createRandNumStr(base,1,100,rand.nextBoolean(),rand);
        
        MutBigIntBase ma = new MutBigIntBase(numStrA,base);
        MutBigIntBase mb = new MutBigIntBase(numStrB,base);
        
        BigIntBase ba = new BigIntBase(numStrA,base);
        BigIntBase bb = new BigIntBase(numStrB,base);
        BigIntBase bc = null;
        
        BigInteger a = new BigInteger(numStrA,base);
        BigInteger b = new BigInteger(numStrB,base);
        BigInteger c = null;
        
        if(op == '/' || op == '%' || op == 'r') {
          if(b.signum() == 0) {
            // Prevent divide by 0
            mb = new MutBigIntBase("1",base);
            bb = new BigIntBase("1",base);
            b = new BigInteger("1",base);
          }
        }
        if(op == '%') {
          if(a.signum() == -1 || b.signum() == -1) {
            // Both #s must be positive because of differences in BigInteger
            ma.abs();
            mb.abs();
            ba = ba.abs();
            bb = bb.abs();
            a = a.abs();
            b = b.abs();
          }
        }
        
        switch(op) {
          case '+': ma.plus(mb); bc = ba.plus(bb); c = a.add(b); break;
          case '-': ma.minus(mb); bc = ba.minus(bb); c = a.subtract(b); break;
          case '*': ma.times(mb); bc = ba.times(bb); c = a.multiply(b); break;
          case '/': ma.over(mb); bc = ba.over(bb); c = a.divide(b); break;
          case '%': ma.mod(mb); bc = ba.mod(bb); c = a.mod(b); break;
          case 'r': ma.rem(mb); bc = ba.rem(bb); c = a.remainder(b); break;
          default: assertNull("Operation undefined: " + c,null);
        }
        
        String mas = ma.toString().toUpperCase();
        String bcs = bc.toString().toUpperCase();
        String as = a.toString(base).toUpperCase();
        String bs = b.toString(base).toUpperCase();
        String cs = c.toString(base).toUpperCase();
        
        msg.setLength(0);
        msg.append('\n');
        msg.append("MutBigIntBase:\n");
        msg.append('\t').append(as).append(' ').append(op).append(' ').append(mb).append(" = ").append(mas).append('\n');
        msg.append("BigIntBase:\n");
        msg.append('\t').append(ba).append(' ').append(op).append(' ').append(bb).append(" = ").append(bcs).append('\n');
        msg.append("BigInteger:\n");
        msg.append('\t').append(as).append(' ').append(op).append(' ').append(bs).append(" = ").append(cs).append('\n');
        msg.append('\t').append(a).append(' ').append(op).append(' ').append(b).append(" = ").append(c).append('\n'); // Base 10 (decimal)
        String m = msg.toString();
        
        assertEquals(m,mas,cs);
        assertEquals(m,bcs,cs);
      }
    }
  }
}
