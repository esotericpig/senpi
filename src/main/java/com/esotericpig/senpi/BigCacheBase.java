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
public abstract class BigCacheBase<N extends BigNumBase> implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public final int BASE;
  
  public abstract N getCustom(String numberStr);
  
  public abstract N zero();
  public abstract N one();
  public abstract N two();
  public abstract N three();
  public abstract N four();
  public abstract N five();
  public abstract N six();
  public abstract N seven();
  public abstract N eight();
  public abstract N nine();
  public abstract N ten();
  public abstract N eleven();
  public abstract N twelve();
  
  public BigCacheBase(int base) {
    BASE = base;
  }
  
  public N getCustom10(int numberBase10) {
    return getCustom(Integer.toString(numberBase10,BASE));
  }
  
  public N cu(String numberStr) {
    return getCustom(numberStr);
  }
  
  public N cu10(int numberBase10) {
    return getCustom10(numberBase10);
  }
}
