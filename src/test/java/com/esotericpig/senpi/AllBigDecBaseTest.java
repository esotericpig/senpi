package com.esotericpig.senpi;

import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class AllBigDecBaseTest {
  public static final int TEST_COUNT = 100; // Number of tests to perform
  
  protected Random rand = null;
  
  @Before
  public void setUp() {
    rand = new Random();
  }
  
  @Test
  public void testRandNumStrings() {
    final int base = 12;
    
    StringBuilder msg = new StringBuilder();
    String spatt = "^(\\-?)(\\+?0*)"; // Ignore padded 0s for comparison
    
    for(int i = 0; i < TEST_COUNT; ++i) {
      String s = BigStrBase.randNumStr(base,1,100,rand.nextBoolean(),true,true,rand);
      BigDecBase bda = new BigDecBase(s,base);
      String bdas = bda.toString();
      
      System.out.println(s + " ~? " + bdas);
      
      s = s.replaceAll(spatt,"$1").toUpperCase();
      bdas = bdas.replaceAll(spatt,"$1"); // Only for 0.~ really
      
      msg.setLength(0);
      msg.append(s).append(" =? ").append(bdas);
      String m = msg.toString();
      
      System.out.println(m);
      assertEquals(m,s,bdas);
    }
  }
}
