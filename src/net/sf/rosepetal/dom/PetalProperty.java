/*
 *  Rose Petal file API.
 *  Copyright (C) 2001-2002, Free Software Foundation.
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 *  Boston, MA  02111-1307, USA. The text of license can be also found
 *  at http://www.gnu.org/copyleft/lgpl.html
 */
package net.sf.rosepetal.dom;
import java.util.*;

/** property of petal object */
public final class PetalProperty extends PetalValue
{
  /** name of property */
  final private String name;
  /** value of property, it either standart wrapper type or subclass of PetalValue */
  final private Object value;
  /**
   * a constructor
   * @param n name of property
   * @param v value of property
   */
  public PetalProperty(int sl, int sc, String n, Object v)
  {
    super(sl,sc);
    name = n;
    value = v;
  }
  /** get name of property */
  public String getName()
  {
    return name;
  }
  /** get value of property */
  public Object getValue()
  {
    return value;
  }
}
