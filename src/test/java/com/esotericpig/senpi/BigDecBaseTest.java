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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Jonathan Bradley Whited (@esotericpig)
 */
public class BigDecBaseTest {
  public static final int TEST_COUNT = 100; // Number of tests to perform
  
  protected Random rand = null;
  
  @BeforeEach
  public void initEachTest() {
    rand = new Random();
  }
  
  @AfterEach
  public void finEachTest() {
    rand = null;
  }
  
  @Test
  public void testRandNumStrings() {
    final int base = 12;
    
    StringBuilder msg = new StringBuilder();
    String spatt = "^(\\-?)(\\+?0*)"; // Ignore padded 0s and plus sign for comparison
    
    for(int i = 0; i < TEST_COUNT; ++i) {
      String s = BigStrBase.randNumStr(base,1,100,rand.nextBoolean(),true,true,rand);
      BigDecBase bda = new BigDecBase(s,base);
      String bdas = bda.toString();
      
      msg.setLength(0);
      msg.append(s).append(" ~? ").append(bdas).append('\n');
      
      // $1 to keep negative sign
      s = s.replaceAll(spatt,"$1");
      bdas = bdas.replaceAll(spatt,"$1"); // Only for 0.~ really
      
      msg.append(s).append(" =? ").append(bdas).append('\n');
      String m = msg.toString();
      
      System.out.println(m);
      assertEquals(s,bdas,m);
    }
  }
}
