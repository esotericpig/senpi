/**
 * This file is part of senpi.
 * Copyright (c) 2017 Jonathan Bradley Whited (@esotericpig)
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

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.Scanner;

// TODO: Add BigDecimal & BigDecBase

/**
 * <pre>
 * This ignores unchecked warnings in order to write less code that can use both BigInteger and BigNumBase.
 * </pre>
 * 
 * @author Jonathan Bradley Whited (@esotericpig)
 */
@SuppressWarnings("unchecked")
public class BigNumBaseApp {
  protected final int BIG_NUMS_COUNT = 9; // Must be a multiple of 3
  
  protected Object[] bigNums = new Object[BIG_NUMS_COUNT];
  protected String[] bigNumStrs = new String[BIG_NUMS_COUNT + 3]; // +3 for BigInteger conversion to base
  protected int[] maxLens = new int[3]; // x, y, z
  
  public static void main(String[] args) {
    BigNumBaseApp app = new BigNumBaseApp();
    app.runLoop(args);
  }
  
  public void runLoop(String[] args) {
    System.out.println("<base#> <#> <op> <#>");
    System.out.println("  <op>:     +, -, *, /, % (mod), r (rem), f (floor mod)");
    System.out.println("  Exit:     <any other value>");
    System.out.println("  Example:  12 2 + 2");
    System.out.println();
    
    Scanner stdin = new Scanner(System.in);
    
    while(true) {
      System.out.print("> ");
      String s = stdin.nextLine();
      System.out.println();
      
      // 7 for "2 1 + 1"
      if(s == null || s.length() < 7) {
        System.out.println("Invalid input; exiting...");
        break;
      }
      
      String[] parts = s.trim().split("\\s+");
      
      if(parts.length < 4) {
        System.out.println("Invalid input; exiting...");
        break;
      }
      
      int base = Integer.parseInt(parts[0]);
      char operator = parts[2].charAt(0);
      String xStr = parts[1];
      String yStr = parts[3];
      
      bigNums[0] = new BigInteger(xStr,base);
      bigNums[1] = new BigInteger(yStr,base);
      bigNums[2] = null;
      
      bigNums[3] = new BigIntBase(xStr,base);
      bigNums[4] = new BigIntBase(yStr,base);
      bigNums[5] = null;
      
      bigNums[6] = new MutBigIntBase(xStr,base);
      bigNums[7] = new MutBigIntBase(yStr,base);
      bigNums[8] = null;
      
      doOperators(operator);
      genBigNumStrs(base);
      printBigNumStrs(operator);
      System.out.println();
    }
  }
  
  public void doOperators(char operator) {
    for(int i = 0; i < bigNums.length; ++i) {
      Object x = bigNums[i];
      Object y = bigNums[++i];
      
      ++i; // z
      
      // Clone MutBigIntBase because it's mutable
      if(x instanceof MutBigIntBase) {
        x = ((BigNumBase)x).clone();
      }
      
      // If an operator doesn't exist, use ZERO for #genBigNumStrs(...)
      
      switch(operator) {
        case '+':
          if(x instanceof BigInteger) {
            bigNums[i] = ((BigInteger)x).add((BigInteger)y);
          }
          else if(x instanceof BigNumBase) {
            bigNums[i] = ((BigNumBase)x).plus((BigNumBase)y);
          }
          break;
        
        case '-':
          if(x instanceof BigInteger) {
            bigNums[i] = ((BigInteger)x).subtract((BigInteger)y);
          }
          else if(x instanceof BigNumBase) {
            bigNums[i] = ((BigNumBase)x).minus((BigNumBase)y);
          }
          break;
        
        case '*':
          if(x instanceof BigInteger) {
            bigNums[i] = ((BigInteger)x).multiply((BigInteger)y);
          }
          else if(x instanceof BigNumBase) {
            bigNums[i] = ((BigNumBase)x).times((BigNumBase)y);
          }
          break;
        
        case '/':
          if(x instanceof BigInteger) {
            bigNums[i] = ((BigInteger)x).divide((BigInteger)y);
          }
          else if(x instanceof BigNumBase) {
            bigNums[i] = ((BigNumBase)x).over((BigNumBase)y);
          }
          break;
        
        case '%':
          if(x instanceof BigInteger) {
            // BigInteger#mod(...) can't accept -#s for y so do #abs()
            bigNums[i] = ((BigInteger)x).mod(((BigInteger)y).abs());
          }
          else if(x instanceof BigNumBase) {
            bigNums[i] = ((BigNumBase)x).mod((BigNumBase)y);
          }
          break;
        
        case 'r':
          if(x instanceof BigInteger) {
            bigNums[i] = ((BigInteger)x).remainder((BigInteger)y);
          }
          else if(x instanceof BigNumBase) {
            bigNums[i] = ((BigNumBase)x).rem((BigNumBase)y);
          }
          break;
        
        case 'f':
          if(x instanceof BigInteger) {
            bigNums[i] = BigInteger.ZERO;
          }
          else if(x instanceof BigNumBase) {
            bigNums[i] = ((BigNumBase)x).floorMod((BigNumBase)y);
          }
          break;
        
        default: throw new UnsupportedOperationException("Invalid operator: " + operator);
       }
    }
  }
  
  public void genBigNumStrs(int base) {
    for(int i = 0; i < maxLens.length; ++i) {
      maxLens[i] = 0;
    }
    
    for(int i = 0,j = 0; i < BIG_NUMS_COUNT; ++i,++j) {
      int xi = i;
      int yi = ++i;
      int zi = ++i;
      
      int sxi = j;
      int syi = ++j;
      int szi = ++j;
      
      bigNumStrs[sxi] = bigNums[xi].toString();
      bigNumStrs[syi] = bigNums[yi].toString();
      bigNumStrs[szi] = bigNums[zi].toString();
      
      maxLens[0] = Math.max(maxLens[0],bigNumStrs[sxi].length());
      maxLens[1] = Math.max(maxLens[1],bigNumStrs[syi].length());
      maxLens[2] = Math.max(maxLens[2],bigNumStrs[szi].length());
      
      if(bigNums[xi] instanceof BigInteger) {
        sxi = ++j;
        syi = ++j;
        szi = ++j;
        
        bigNumStrs[sxi] = ((BigInteger)bigNums[xi]).toString(base);
        bigNumStrs[syi] = ((BigInteger)bigNums[yi]).toString(base);
        bigNumStrs[szi] = ((BigInteger)bigNums[zi]).toString(base);
        
        maxLens[0] = Math.max(maxLens[0],bigNumStrs[sxi].length());
        maxLens[1] = Math.max(maxLens[1],bigNumStrs[syi].length());
        maxLens[2] = Math.max(maxLens[2],bigNumStrs[szi].length());
      }
    }
  }
  
  public void printBigNumStrs(char operator) {
    String opStr = (operator != '%') ? String.valueOf(operator) : "%%";
    String fmtStr = String.format("  %%%ds %s %%%ds = %%%ds%n",maxLens[0],opStr,maxLens[1],maxLens[2]);
    String[] titles = {"BigInteger",null,"BigIntBase","MutBigIntBase"};
    
    int i = 0;
    
    for(String title: titles) {
      if(title != null) {
        System.out.println(title);
      }
      
      System.out.printf(fmtStr,bigNumStrs[i++],bigNumStrs[i++],bigNumStrs[i++]);
    }
  }
}
