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
package net.sf.rosepetal.dom.parser;



public final class PetalToken
{
  /** whitespaces */
  public static final int WS      = 1;
  /** eof, this is the last token in stream */
  public static final int EOF     = 2;
  /** integer */
  public static final int INTEGER = 3;
  /** boolean */
  public static final int BOOLEAN = 4;
  /** floating point number */
  public static final int FLOAT   = 5;
  /** identifier */
  public static final int NAME    = 6;
  /** string */
  public static final int STRING  = 7;
  /** string */
  public static final int CHAR    = 9;
  /** ref */
  public static final int REF     = 10;
  /** token "(" */
  public static final int OPEN    = 11;
  /** token ")" */
  public static final int CLOSE   = 12;
  /** comma "," */
  public static final int COMMA   = 13;
  /** start line */
  public final int startLine;
  /** start column */
  public final int startColumn;
  /** value of token */
  public final String value;
  /** kind of token */
  public final int kind;
  /** a constructor */
  public PetalToken(int k, String v, int sl, int sc)
  {
    startLine = sl;
    startColumn = sc;
    kind = k;
    value = v;
  }
  /** debug infor for token */
  public String toString()
  {
    StringBuffer bu = new StringBuffer();
    bu.append("PetalToken[");
    String k;
    switch(kind)
    {
      case WS       : k = "WS"     ; break;
      case EOF      : k = "EOF"    ; break;
      case INTEGER  : k = "INTEGER"; break;
      case BOOLEAN  : k = "BOOLEAN"; break;
      case FLOAT    : k = "FLOAT"  ; break;
      case NAME     : k = "NAME"   ; break;
      case STRING   : k = "STRING" ; break;
      case CHAR     : k = "CHAR"   ; break;
      case REF      : k = "REF"    ; break;
      case OPEN     : k = "OPEN"   ; break;
      case CLOSE    : k = "CLOSE"  ; break;
      case COMMA    : k = "COMMA"  ; break;
      default:
        k = "unknown("+kind+")";
    }
    bu.append(k);
    bu.append(",(");
    bu.append(startLine);
    bu.append(',');
    bu.append(startColumn);
    bu.append("),");
    bu.append(value);
    bu.append(']');
    return bu.toString();
  }
}

