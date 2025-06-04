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

/**
 * @author Bradley Whited
 */
public class UnsupportedBaseException extends NumberFormatException {
  private static final long serialVersionUID = 1L;
  
  public UnsupportedBaseException() {
    super();
  }
  
  public UnsupportedBaseException(String s) {
    super(s);
  }
}
