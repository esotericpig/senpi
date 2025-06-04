/**
 * This file is part of senpi.
 * Copyright (c) 2018 Bradley Whited
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

import java.math.BigInteger;

/**
 * @author Bradley Whited
 */
public class BigIntWrap /*implements BigNumBase<BigIntWrap>*/ {
  protected BigInteger value;
  
  public BigIntWrap() {
    this(BigInteger.ZERO);
  }
  
  public BigIntWrap(BigInteger value) {
    this.value = value;
  }
  
  public BigIntWrap(BigIntWrap value) {
    this(value.value);
  }
  
  public BigIntWrap copy() {
    return new BigIntWrap(this);
  }
  
  public BigIntWrap abs() {
    return valueOf(value.abs());
  }
  
  public BigIntWrap minus(BigIntWrap y) {
    return valueOf(value.subtract(y.value));
  }
  
  protected BigIntWrap valueOf(BigInteger newValue) {
    return (newValue == value) ? this : new BigIntWrap(newValue);
  }
}
