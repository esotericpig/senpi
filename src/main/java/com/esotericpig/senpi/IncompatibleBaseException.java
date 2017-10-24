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

/**
 * @author Jonathan Bradley Whited, @esotericpig
 */
public class IncompatibleBaseException extends IllegalArgumentException {
  private static final long serialVersionUID = 1L;
  
  public IncompatibleBaseException() {
    super();
  }
  
  public IncompatibleBaseException(String s) {
    super(s);
  }
  
  public IncompatibleBaseException(String s,Throwable t) {
    super(s,t);
  }
  
  public IncompatibleBaseException(Throwable t) {
    super(t);
  }
}
