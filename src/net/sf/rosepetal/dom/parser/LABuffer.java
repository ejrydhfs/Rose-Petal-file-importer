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
import java.io.*;

/** look ahead byte buffer, note that bytes are needed here */
public abstract class LABuffer
{
  /** input stream, buffered stream is recomended */
  final InputStream is;
  /** buffer */
  final byte buffer[];
  /** buffer position */
  int consumed;
  /** buffer avaialable */
  int available;
  /** size of buffer */
  final int size;
  /** eof flag */
  boolean eofReached = false;

  /** constructor for buffer */
  LABuffer(InputStream input)
  {
    this(4096,input);
  }

  /** constructor for buffer */
  LABuffer(int sz, InputStream input)
  {
    is = input;
    size = sz;
    buffer = new byte[size];
  }

  /** look up byte at position, byte is returned at 0..255 range,
   *  -1 means position past end of file  */
  public final int la(int pos)
  {
    //System.out.println("LA: a="+available+", c="+consumed+", pos="+pos);
    if(pos < 0)
    {
      throw new IllegalArgumentException("pos should be positive: "+pos);
    }
    if(pos >= size)
    {
      throw new IllegalArgumentException("pos should be less then size: "+pos);
    }
    // refill buffer if needed and possible
    if(pos >= available - consumed && !eofReached)
    {
      refill(pos);
    }
    // finally return value
    if(pos >= available - consumed)
    {
      // return -1 if past end of file
      return -1;
    }
    else
    {
      return buffer[pos+consumed];
    }
  }

  /** refill buffer making at least size bytes avaiable or
   * specifying eof.
   * */
  private final void refill(int pos)
  {
    //System.out.println("refilling ("+pos+")" );
    try
    {
      // shift consumed bytes to fill as much space for filling as possible
      if(consumed != 0)
      {
        System.arraycopy(buffer,consumed,buffer,0,available-consumed);
        available = available - consumed;
      }
      while(available <= pos)
      {
        final int n = is.read(buffer, available, size - available);
        //System.out.println("read "+n+" bytes");
        if(n == -1)
        {
          eofReached = true;
          break;
        }
        else if(n == 0)
        {
          throw new IllegalStateException("stream should read at least some bytes");
        }
        else
        {
          available += n;
        }
      }
    }
    catch(IOException ex)
    {
      throw new PetalParserException("IO problem",ex);
    }
  }

  /** consume n bytes in buffer */
  public final void consume(int n)
  {
    if(n < 0)
    {
      throw new IllegalArgumentException("cannot consume negative amount: "+n);
    }
    if(n > available - consumed)
    {
      throw new IllegalArgumentException("want to consume too much: "+n);
    }
    consumed += n;
    if(consumed == available)
    {
      consumed = 0;
      available = 0;
    }
  }

  /** look ahead next byte */
  public int la()
  {
    return la(0);
  }

  /** consume next byte */
  public void consume()
  {
    consume(1);
  }

}

