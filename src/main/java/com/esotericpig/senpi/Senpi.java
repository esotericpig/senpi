/**
 * This file is part of senpi.
 * Copyright (c) 2016-2017 Bradley Whited
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

// TODO: when calculating pi, use base 10 to ensure algorithm is working
//       and then use base 12, as the number may be different than base 12
//       numbers on the internet, due to actually using a proper representation

/**
 * @author Bradley Whited
 */
public class Senpi {
  public static void main(String[] args) {
    BigNumBaseApp.main(args);
  }
}
