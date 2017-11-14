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
public interface CacheableBigNumBase<N extends BigNumBase,C extends BigCacheBase<N>> extends Serializable {
  public abstract C getCache();
  public abstract N getCache(String numberStr);
  public abstract N getCache10(int numberBase10);
  
  public abstract C c();
  public abstract N c(String numberStr);
  public abstract N c10(int numberBase10);
}
