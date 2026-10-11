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
import java.io.*;
/**
 * document that represent single petal file
 */
public class PetalDocument
{
  /** header for document */
  PetalObject header;
  /** body of document */
  PetalObject body;
  /** parent document */
  PetalDocument parent;
  /** file from which document were loaded */
  File file;
  /** initalization stat for document */
  boolean isInited = false;

  /** a constructor */
  public PetalDocument()
  {
  }

  public void init(File f, PetalObject h, PetalObject b)
  {
    init(f,null,h,b);

  }

  public void init(File f, PetalDocument p, PetalObject h, PetalObject b)
  {
    if(isInited)
      throw new IllegalStateException("document is aready initialized");
    file = f;
    parent = p;
    header = h;
    body = b;
  }


  public PetalObject getHeader()
  {
    return header;
  }
  public PetalObject getBody()
  {
    return body;
  }
  public File getFile()
  {
    return file;
  }
  public PetalDocument getParent()
  {
    return parent;
  }
}