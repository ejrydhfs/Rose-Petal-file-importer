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
package net.sf.rosepetal.ast;
import java.io.*;
/**
 * test for loading ast
 */

public class PetalASTLoaderTest
{

  public static void main(String args[])
  {
    PrintWriter err = new PrintWriter(new OutputStreamWriter(System.err));
    int i = 0;
    boolean debug = false;
    if("-debug".equals(args[0]))
    {
      debug = true;
      i++;
    }
    File f = new File(args[i]);
    System.out.println("Starting parsing "+f);
    long s = System.currentTimeMillis();
    PetalASTLoader p = new PetalASTLoader(f,err);
    PBase obj = p.parse();
    long e = System.currentTimeMillis();
    System.out.println("File parse for "+(e-s));
    if(debug)
    {
      /*DebugPetalWriter w = new DebugPetalWriter(System.out);
      w.write(d);
      w.flush();*/
    }
  }

}