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

import java.io.Serializable;

/**
 * @author Jonathan Bradley Whited (@esotericpig)
 */
public interface BigNumBase<N extends BigNumBase> extends Comparable<N>,Serializable {
  public static final int DEFAULT_BASE = 12;
  
  public abstract N copy();
  
  public abstract N abs();
  public abstract N floorMod(N y);
  public abstract N ltrim();
  public abstract N minus(N y);
  public abstract N mod(N y);
  public abstract N negate();
  public abstract N over(N y);
  public abstract BigQuoBase<N> overRem(N y);
  public abstract N plus(N y);
  public abstract N precise(int precisionToAddOrSub);
  public abstract N rem(N y);
  public abstract N rtrim();
  public abstract N scale(int scaleToAddOrSub);
  public abstract N times(N y);
  public abstract N trim();
  public abstract N under(N y);
  public abstract BigQuoBase<N> underRem(N y);
  public abstract N zero();
  
  public abstract N set(N value);
  public abstract N setPrecision(int precision);
  public abstract N setScale(int scale);
  
  public abstract int get(int index);
  public abstract int getBase();
  public abstract int getDigit(int index);
  public abstract int getLength();
  public abstract int getOffset();
  public abstract int getSign();
  public abstract int getSize();
  public abstract boolean isZero();
  
  public abstract BigDecBase toBigDecBase();
  public abstract BigIntBase toBigIntBase();
  public abstract MutBigIntBase toMutBigIntBase();
  public abstract String toString(boolean shouldDowncase);
  
  public abstract BigDecBase bdb();
  public abstract BigIntBase bib();
  public abstract MutBigIntBase mbib();
}
