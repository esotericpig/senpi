package com.esotericpig.senpi;

import java.math.BigInteger;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
  
  @Test
  public void testCache() {
    final int base = 12;
    
    BigIntBase b = new BigIntBase(base);
    
    testCache("ZERO",b.c().ZERO,new BigIntBase("0",base));
    testCache("ONE",b.c().ONE,new BigIntBase("1",base));
    testCache("TWO",b.c().TWO,new BigIntBase(Integer.toString(2,base),base));
    testCache("THREE",b.c().THREE,new BigIntBase(Integer.toString(3,base),base));
    testCache("FOUR",b.c().FOUR,new BigIntBase(Integer.toString(4,base),base));
    testCache("FIVE",b.c().FIVE,new BigIntBase(Integer.toString(5,base),base));
    testCache("SIX",b.c().SIX,new BigIntBase(Integer.toString(6,base),base));
    testCache("SEVEN",b.c().SEVEN,new BigIntBase(Integer.toString(7,base),base));
    testCache("EIGHT",b.c().EIGHT,new BigIntBase(Integer.toString(8,base),base));
    testCache("NINE",b.c().NINE,new BigIntBase(Integer.toString(9,base),base));
    testCache("TEN",b.c().TEN,new BigIntBase(Integer.toString(10,base),base));
    testCache("ELEVEN",b.c().ELEVEN,new BigIntBase(Integer.toString(11,base),base));
    testCache("TWELVE",b.c().TWELVE,new BigIntBase(Integer.toString(12,base),base));
    testCache("BB",b.c("bb"),new BigIntBase("bb",base));
    testCache("100b10",b.c10(100),new BigIntBase(Integer.toString(100,base),base));
  }
  
  public void testCache(String name,BigIntBase cacheVal,BigIntBase testVal) {
    assertNotNull("Null cache: " + name,cacheVal);
    
    String cvs = cacheVal.toString();
    String tvs = testVal.toString();
    String m = "Cache: " + name + ": " + cvs + " =? " + tvs;
    
    System.out.println(m);    
    assertEquals(m,cvs,tvs);
  }
  
  @Test
  public void testOps() {
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
        String numStrA = BigStrBase.randNumStr(base,1,100,rand.nextBoolean(),true,false,rand);
        String numStrB = BigStrBase.randNumStr(base,1,100,rand.nextBoolean(),true,false,rand);
        
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
            mb = bb.c().ONE.m();
            bb = bb.c().ONE;
            b = BigInteger.ONE;
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
          default: assertNotNull("Operation undefined: " + op,null);
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
        
        System.out.println(m);
        assertEquals(m,mas,cs);
        assertEquals(m,bcs,cs);
      }
    }
  }
}
