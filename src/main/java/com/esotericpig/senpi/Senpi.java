/**
 * This file is part of senpi.
 * Copyright (c) 2016-2017 Jonathan Bradley Whited (@esotericpig)
 * 
 * senpi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * senpi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with senpi.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.esotericpig.senpi;

// TODO: when calculating pi, use base 10 to ensure algorithm is working
//       and then use base 12, as the number may be different than base 12
//       numbers on the internet, due to actually using a proper representation
/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class Senpi {
  public static void main(String[] args) {
    //BigIntBase.main(args);
    
    //123.456
    //BigDecBase a = new BigDecBase("000000123AB.456AB",12);
    //System.out.println(a);
    
    BigDecBase a = new BigDecBase("1A.1A");
    BigDecBase b = new BigDecBase("1B.1B");
    System.out.println(a.plus(b));
  }
}
