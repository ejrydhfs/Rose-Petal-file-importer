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
/**
 * This lexer is supporting only single byte encodings
 * This lexer version also a bit loosy as it is able to
 * parse incorrect input too, but it should not be a
 * problem for the task
 */
public final class PetalLexer extends LABuffer
{
  /** start line of token */
  int startLine;
  /** start column of token */
  int startColumn;
  /** current column */
  int line = 1;
  /** current line */
  int column = 1;
  /** buffer for token */
  StringBuffer tokenBuffer = new StringBuffer();
  /** eof token, if not null, eof reached. this value is modified only
   *  in nextToken() */
  PetalToken eofToken;

  /** a constructor */
  public PetalLexer(InputStream is)
  {
    super(is);
  }

  /** parse next token */
  public PetalToken nextToken()
  {
    if(eofToken != null)
    {
      return eofToken;
    }
    int ch = la(0);
    startLine = line;
    startColumn = column;
    switch(ch)
    {
      case -1:
        eofToken = parseEofToken();
        return eofToken;
      case ' ':
      case '\t':
      case '\r':
      case '\n':
        return parseWhiteSpace();
      case '@':
        return parseRef();
      case '+':
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case '.':
        return parseNumber();
      case '|':
      case '\"':
        return parseString();
      case '\'':
        return parseChar();
      case '(':
        return parseOneChar(ch, PetalToken.OPEN);
      case ')':
        return parseOneChar(ch, PetalToken.CLOSE);
      case ',':
        return parseOneChar(ch, PetalToken.COMMA);
      default:
        if(ch == '_' || 'A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z')
          return parseIdentifier();
        throw new PetalParserException("Unknown token start: "+ch+" = "+(char)ch);
    }
  }
  /** parse eof */
  private PetalToken parseEofToken()
  {
    return new PetalToken(PetalToken.EOF,"",line,column);
  }
  /** parse white space */
  private PetalToken parseWhiteSpace()
  {
    int ch = la();
    startToken();
    loop: while( true )
    {
      switch(ch)
      {
        case '\n':
        case '\r':
          processnl();
          break;
        case '\t':
          processtab();
          break;
        case ' ':
          process(ch);
          break;
        default:
          break loop;
      }
      ch = la();
    }
    return makeToken(PetalToken.WS);
  }
  /** parse reference */
  private PetalToken parseRef()
  {
    int ch = la();
    startToken();
    process(ch); // '@' symbol
    ch = la();
    while('0' <= ch && ch <= '9')
    {
      process(ch);
      ch = la();
    }
    return makeToken(PetalToken.REF);
  }
  /** parse number */
  private PetalToken parseNumber()
  {
    int ch = la();
    int k = PetalToken.INTEGER;
    startToken();
    if(ch == '+' || ch == '-')
    {
      process(ch);
      ch = la();
    }
    while('0' <= ch && ch <= '9')
    {
      process(ch);
      ch = la();
    }
    if(ch == '.')
    {
      k = PetalToken.FLOAT;
      process(ch);
      ch = la();
      while('0' <= ch && ch <= '9')
      {
        process(ch);
        ch = la();
      }
    }
    if( ch == 'E' || ch == 'e')
    {
      k = PetalToken.FLOAT;
      process(ch);
      ch = la();
      if(ch == '+' || ch == '-')
      {
        process(ch);
        ch = la();
      }
      while('0' <= ch && ch <= '9')
      {
        process(ch);
        ch = la();
      }
    }
    return makeToken(k);
  }
  /** parse string */
  private PetalToken parseString()
  {
    int ch = la();
    startToken();
    if(ch == '\"')
    {
      process(ch); // '\"' symbol
      ch = la();
      loop: while(true)
      {
        switch(ch)
        {
          case -1:
            break loop;
          case '\"':
            process(ch);
            break loop;
          case '\t':
            processtab();
            break;
          case '\n':
          case '\r':
            break loop;
          case '\\':
            processStringEscape();
            break;
          default:
            process(ch);
            break;
        }
        ch = la();
      }
    }
    else if(ch == '|')
    {
      // multiline string
      while(ch == '|')
      {
        process(ch);
        ch = la();
        loop: while(true)
        {
          switch(ch)
          {
            case -1:
              break loop;
            case '\n':
            case '\r':
              processnl();
              ch = la();
              break loop;
            case '\t':
              processtab();
              ch = la();
              break;
            default:
              process(ch);
              ch = la();
              break;
          }
        }
      }
    }
    else
    {
      throw new IllegalStateException("No string ahead: "+ch+" = "+(char)ch);
    }
    return makeToken(PetalToken.STRING);
  }
  /** parse character */
  private PetalToken parseChar()
  {
    int ch = la();
    startToken();
    process(ch); // '\'' symbol
    ch = la();
    loop: while(true)
    {
      switch(ch)
      {
        case -1:
          break loop;
        case '\'':
          process(ch);
          break loop;
        case '\t':
          processtab();
          break;
        case '\n':
        case '\r':
          break loop;
        case '\\':
          processStringEscape();
          break;
        default:
          process(ch);
          break;
      }
      ch = la();
    }
    return makeToken(PetalToken.CHAR);
  }
  /** parse one char token */
  private PetalToken parseOneChar(int ch, int tokenKind)
  {
    consume();
    column++;
    return new PetalToken(tokenKind, String.valueOf((char)ch) ,startLine, startColumn);
  }
  /** parse identifier */
  private PetalToken parseIdentifier()
  {
    // note that it could parse more then allowed, but it
    // ok due condition checked before calling this method
    int ch = la();
    startToken();
    while(ch == '_' || '0' <= ch && ch <= '9'
      || 'A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z')
    {
      process(ch);
      ch = la();
    }
    String v = tokenBuffer.toString();
    int k;
    if("TRUE".equals(v) || "FALSE".equals(v))
    {
      k = PetalToken.BOOLEAN;
    }
    else
    {
      k = PetalToken.NAME;
    }
    return new PetalToken(k, v , startLine, startColumn);

  }
  /** start parsing token */
  private void startToken()
  {
    tokenBuffer = new StringBuffer();
  }
  /** process character in token */
  private void process(int ch)
  {
    tokenBuffer.append((char)ch);
    consume();
    column++;
  }
  /** make token */
  private PetalToken makeToken(int k)
  {
    String v = tokenBuffer.toString();
    return new PetalToken(k, v , startLine, startColumn);
  }
  /** process new line */
  private void processnl()
  {
    int ch = la();
    if(ch == '\r')
    {
      process(ch);
      ch = la();
      if(ch == '\n')
      {
        process(ch);
      }
    }
    else if(ch == '\n')
    {
      process(ch);
      ch = la();
      if(ch == '\n')
      {
        process(ch);
      }
    }
    else
    {
      throw new IllegalStateException("No new line ahead: "+ch+" = "+(char)ch);
    }
    column = 1;
    line++;
  }
  /** process tab symbol */
  private void processtab()
  {
    int ch = la();
    consume();
    column = ((column)/8+1)*8+1;
    tokenBuffer.append((char)ch);
  }
  /** process escape in string or char */
  private void processStringEscape()
  {
    int ch = la();
    process(ch);
    ch = la();
    switch(ch)
    {
      case -1:
        break;
      case '\'':
      case '\"':
      case 'n':
      case 'r':
      case 't':
      case '\\':
        process(ch);
        break;
      default:
        if('0' <= ch && ch <= '7')
        {
          process(ch);
          ch = la();
          if('0' <= ch && ch <= '7')
          {
            process(ch);
            ch = la();
            if('0' <= ch && ch <= '7')
            {
              process(ch);
              ch = la();
            }
          }
        }
        break;
    }
  }
  /** test main function */
  public static void main(String args[])
  {

    boolean debug = false;
    if(args.length > 0)
    {
      if("-debug".equals(args[0]))
      {
        debug = true;
      }
    }
    long st = System.currentTimeMillis();
    PetalLexer l = new PetalLexer(System.in);
    PetalToken tk = null;
    do
    {
      tk = l.nextToken();
      if(debug)
        System.out.println(tk);
    }
    while(tk.kind != PetalToken.EOF);
    System.out.println("Time :"+(System.currentTimeMillis()-st));
  }
}
