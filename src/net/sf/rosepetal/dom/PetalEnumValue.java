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
/** class for enumeration values in petal file */
public final class PetalEnumValue extends PetalValue
{
  /** enumeration kind */
  final private String enumKind;
  /** enumeration value */
  final private int value;
  /** a constructor */
  public PetalEnumValue(int sl, int sc, String ek, int v)
  {
    super(sl,sc);
    enumKind = ek;
    value = v;
  }
  /** get enumeration kind */
  public String getEnumKind()
  {
    return enumKind;
  }
  /** get enumeration value */
  public int getValue()
  {
    return value;
  }
  /** get debug info */
  public String toString()
  {
    return "("+enumKind+", "+value+")";
  }
}
