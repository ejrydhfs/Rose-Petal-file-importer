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
package net.sf.rosepetal.genast;


import ru.novosoft.mdf.ext.*;
import ru.novosoft.mdf.impl.*;
import javax.jmi.model.*;
import java.util.*;
import java.io.*;

/** this is a temporary generator that generates mof interfaces */
public class ASTGenDebugWriter extends ASTGenWriter
{
  MofPackage modelElement;
  ASTGenDebugWriter(ASTGen g, MofPackage e) throws IOException
  {
    super(g, g.getNamespace(e.getContainer()), g.getName1(e)+"DebugWriter.java");
    modelElement = e;
    try
    {
      sline("package ");
      println(g.getNamespace(e));
      line("public class "+g.getName1(e)+"DebugWriter");
      sblock();
      {
      }
      eblock();
    }
    finally
    {
      close();
    }
  }
}
