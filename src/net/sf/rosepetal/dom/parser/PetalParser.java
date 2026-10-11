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
import net.sf.rosepetal.dom.*;
import net.sf.rosepetal.dom.test.*;
import java.io.*;
import java.util.*;
/**
 * petal parser.
 */
public class PetalParser
{
  final private HashMap s2i = new HashMap();
  /** current file that is being parsed */
  final private File file;
  /** log for logging errors */
  final private PrintWriter log;
  /** lexer */
  final private PetalLexer lexer;
  /** header of document */
  private PetalObject header;
  /** body of document */
  private PetalObject body;
  /** current encoding (should be name passable to String(byte[],String)) */
  private String encoding;
  /** current token */
  private PetalToken token;
  /** current document for parser */
  private final PetalDocument doc;

  /** a cosntructor */
  public PetalParser(File f, PrintWriter l)
  {
    this(null,f,l);
  }


  /** a cosntructor */
  public PetalParser(PetalDocument p, File f, PrintWriter l)
  {
    try
    {
      log = l;
      file = f.getAbsoluteFile();
      doc = new PetalDocument();
      FileInputStream is = new FileInputStream(file.toString());
      try
      {
        lexer = new PetalLexer(is);
        if(!moveNext())
        {
          error("file is empty");
        }
        else
        {
          parseHeader();
          parseBody();
          doc.init(file,p,header,body);

        }
      }
      finally
      {
        is.close();
      }
    }
    catch(IOException ex)
    {
      throw new PetalParserException("IO problem: "+ex, ex);
    }
  }

  /** intern name string */
  private String name(String nm)
  {
    String rc = (String)s2i.get(nm);
    if(rc == null)
    {
      rc = nm.intern();
      s2i.put(rc,rc);
    }
    return rc;
  }


  /** get document that is result of parsing */
  public PetalDocument getDocument()
  {
    return doc;
  }
  /** parse header */
  private void parseHeader()
  {
    Object obj = parseValue(null, null);
    if(obj instanceof PetalObject)
    {
      PetalObject po = (PetalObject)obj;
      header = po;
      if(! "Petal".equals(po.getType()) )
      {
        error("object of type Petal is expected and found "+po.getType());
      }
      // look for charset
      for(Iterator i = po.getProperties().iterator();i.hasNext();)
      {
        PetalProperty pp = (PetalProperty)i.next();
        if("charSet".equals(pp.getName()))
        {
          Integer v = (Integer)pp.getValue();
          setEncoding(v);
          break;
        }
      }
    }
    else
    {
      error("header is expected to be object and is "+(obj != null ? obj.getClass().getName() : null));
    }
  }
  /** parse body */
  private void parseBody()
  {
    Object obj = parseValue(null,null);
    if(obj instanceof PetalObject)
    {
      PetalObject po = (PetalObject)obj;
      body = po;
    }
    else
    {
      error("body is expected to be object and is "+(obj != null ? obj.getClass().getName() : null));
    }

  }
  /** parse value */
  private Object parseValue(PetalObject po, String pp)
  {
    PetalToken tk = current();
    switch(tk.kind)
    {
      case PetalToken.INTEGER  :
        return parseInt();
      case PetalToken.BOOLEAN  :
        return parseBoolean();
      case PetalToken.FLOAT    :
        return parseFloat();
      case PetalToken.STRING   :
        return parseString();
      case PetalToken.CHAR     :
        return parseChar();
      case PetalToken.REF      :
        return parseRef();
      case PetalToken.OPEN     :
        return parseComosite(po,pp);
      default:
        error("token is not expected there"+tk);
        return null;
    }
  }

  /** report error */
  public void error(String msg)
  {
    if(log != null)
    {
      log.println("ERROR("+file+"): "+msg);
      new Exception().printStackTrace(log);
      log.flush();
    }
  }
  /** report error */
  public void error(String msg, Throwable ex)
  {
    if(log != null)
    {
      log.println("ERROR("+file+"): "+msg);
      log.print("Related Exception: ");
      ex.printStackTrace(log);
      new Exception().printStackTrace(log);
      log.flush();
    }
  }

  /** set encoding */
  public void setEncoding(Integer v)
  {
  }

  /** move to next token */
  private boolean moveNext()
  {
    if(token != null && token.kind == PetalToken.EOF)
      return false;
    do
    {
      token = lexer.nextToken();
    }
    while(token.kind == PetalToken.WS);
    return token.kind != PetalToken.EOF;
  }

  /** get current token */
  private PetalToken current()
  {
    return token;
  }
  /** parse interger value */
  private Object parseInt()
  {
    PetalToken tk = current();
    try
    {
      moveNext();
      return new Integer(tk.value);
    }
    catch(Exception ex)
    {
      error("could not int in "+tk);
      return null;
    }
  }
  /** parse boolean value */
  private Object parseBoolean()
  {
    PetalToken tk = current();
    try
    {
      moveNext();
      return new Boolean(tk.value);
    }
    catch(Exception ex)
    {
      error("could not parse boolean in "+tk);
      return null;
    }
  }
  /** parse float value */
  private Object parseFloat()
  {
    PetalToken tk = current();
    try
    {
      moveNext();
      return new Double(tk.value);
    }
    catch(Exception ex)
    {
      error("could not double in "+tk);
      return null;
    }
  }
  /** parse string */
  private Object parseString()
  {
    PetalToken tk = current();
    try
    {
      moveNext();
      if(tk.value.startsWith("\""))
      {
        if(!tk.value.endsWith("\"") || tk.value.length() < 2)
        {
          error("not terminated string "+tk);
          return convertString(tk.value.substring(1));
        }
        return convertString(tk.value.substring(1, tk.value.length()-1));
      }
      else if(tk.value.startsWith("|"))
      {
        return convertPetalMultiString2String(tk.value);
      }
      error("unable to parse string from token: "+tk);
      return null;
    }
    catch(Exception ex)
    {
      error("unable to parse string from token: "+tk, ex);
      return null;
    }
  }
  /** parse single character */
  private Object parseChar()
  {
    PetalToken tk = current();
    try
    {
      moveNext();
      if(tk.value.startsWith("\'"))
      {
        String s;
        if(!tk.value.endsWith("\'") || tk.value.length() < 3)
        {
          error("not terminated or invalid char "+tk);
          s = convertString(tk.value.substring(1));
        }
        else
        {
          s = convertString(tk.value.substring(1, tk.value.length()-1));
        }
        if(s.length() > 2)
        {
          error("too big char "+tk);
        }
        if(s.length() > 0)
          return new Character(s.charAt(0));
        else
          return null;
      }
      error("unable to parse char from token: "+tk);
      return null;
    }
    catch(Exception ex)
    {
      error("unable to parse char from token: "+tk, ex);
      return null;
    }
  }
  /** parse reference */
  private Object parseRef()
  {
    PetalToken tk = current();
    moveNext();
    return new PetalRefValue(tk.startLine, tk.startColumn, tk.value);
  }
  /** parse composite object */
  private Object parseComosite(PetalObject po, String pp)
  {
    if(!moveNext())
    {
      error("eof is not expected at "+current());
    }
    PetalToken tk = current();
    switch(tk.kind)
    {
      case PetalToken.INTEGER  :
        return parsePoint();
      case PetalToken.STRING   :
        return parseEnum();
      case PetalToken.NAME     :
        if("object".equals(tk.value))
          return parseObject(po,pp);
        if("list".equals(tk.value))
          return parseList(po,pp);
        if("value".equals(tk.value))
          return parseValueComposte();
    }
    error("unexpected composite object start: "+tk);
    skipContent(1);
    return null;
  }

  /** covert string */
  private String convertPetalMultiString2String(String value)
  {
    StringBuffer res= new StringBuffer(value.length());
    StringTokenizer st = new StringTokenizer(value,"\r\n", false);
    while(st.hasMoreTokens())
    {
      String t = st.nextToken();
      if (t.startsWith("|"))
      {
        res.append(t.substring(1));
        res.append('\n');
      }
    }
    return res.toString();
  }
  /** skip content counting "()" */
  private void skipContent(int intialNestingLevel)
  {
    PetalToken tk = current();
    int l = intialNestingLevel;
    while(l > 0)
    {
      if(tk.kind == PetalToken.OPEN)
      {
        l++;
      }
      if(tk.kind == PetalToken.CLOSE)
      {
        l--;
      }
      if(tk.kind == PetalToken.EOF)
      {
        error("unexpected eof while nesting level is "+l);
        l = 0;
      }
      moveNext();
      tk = current();
    }
  }

  /** convert multiline string */
  private static String convertString(String value)
  {
    final int  ln = value.length();
    int j=0;
    char ch;
    StringBuffer res= new StringBuffer(ln);
    while(j < ln)
    {
      ch = value.charAt(j);
      if (ch == '\\')
      {
        j++;
        if(j>=ln)
           throw new PetalParserException("Unterminated escape sequence: "+value);
        ch = value.charAt(j);
        switch(ch)
        {
          case 'n':  {res.append('\n'); j++; break;}
          case 't':  {res.append('\t'); j++; break;}
          case 'b':  {res.append('\b'); j++; break;}
          case 'r':  {res.append('\r'); j++; break;}
          case 'f':  {res.append('\f'); j++; break;}
          case '\\': { res.append('\\'); j++; break;}
          case '\"': { res.append('\"'); j++; break;}
          case '\'': { res.append('\''); j++; break;}
          case '0':case '1' : case '2' : case '3' :
          case '4':case '5' : case '6' : case '7':
            {
              int ival = 0;
              int i = 0;
              do
              {
                ival = ival * 8 + (ch - '0');
                j++;
                if(j<ln)
                  ch = value.charAt(j);
                i++;
              }
              while(j<ln && ch>='0' && ch <= '7' && i<3);
              res.append((char)ival);
              break;
            }
          default:
            throw new PetalParserException("Unknown escape sequence in the string: "+value);
        }
      }
      else
      {
        res.append(ch);
        j++;
      }
    }
    return res.toString();
  }

  /** parse point like "(1,3)" */
  private Object parsePoint()
  {
    PetalToken tk = current();
    PetalToken postk = tk;
    Integer x;
    Integer y;
    if(tk.kind == PetalToken.INTEGER)
    {
      x = (Integer)parseInt();
      if(x == null)
      {
        skipContent(1);
        return null;
      }
    }
    else
    {
      error("integer expected and found "+tk);
      skipContent(1);
      return null;
    }
    tk = current();
    if(tk.kind == PetalToken.COMMA)
    {
      if(!moveNext())
      {
        error("unexected eof: "+current());
        return null;
      }
      tk = current();
    }
    else
    {
      error("',' expected and found "+tk);
      skipContent(1);
      return null;
    }

    if(tk.kind == PetalToken.INTEGER)
    {
      y = (Integer)parseInt();
      if(y == null)
      {
        skipContent(1);
        return null;
      }
      tk = current();
    }
    else
    {
      error("integer expected and found "+tk);
      skipContent(1);
      return null;
    }
    if(tk.kind == PetalToken.CLOSE)
    {
      moveNext();
      return new PetalPointValue(postk.startLine, postk.startColumn,
        x.intValue(), y.intValue());
    }
    else
    {
      error("')' expected and found "+tk);
      skipContent(1);
      return null;
    }
  }
  /** parse enum like ("MyEnum" 3) */
  private Object parseEnum()
  {
    PetalToken tk = current();
    PetalToken postk = tk;
    String  nm;
    Integer val;
    if(tk.kind == PetalToken.STRING)
    {
      nm = (String)parseString();
      if(nm == null)
      {
        skipContent(1);
        return null;
      }
    }
    else
    {
      error("string expected and found "+tk);
      skipContent(1);
      return null;
    }
    tk = current();
    if(tk.kind == PetalToken.INTEGER)
    {
      val = (Integer)parseInt();
      if(val == null)
      {
        skipContent(1);
        return null;
      }
    }
    else
    {
      error("integer expected and found "+tk);
      skipContent(1);
      return null;
    }
    tk = current();
    if(tk.kind == PetalToken.CLOSE)
    {
      moveNext();
      return new PetalEnumValue(postk.startLine, postk.startColumn,
        nm, val.intValue());
    }
    else
    {
      error("')' expected and found "+tk);
      skipContent(1);
      return null;
    }
  }
  /** parse value text like (value Text "string")*/
  private Object parseValueComposte()
  {
    PetalToken tk = current();
    PetalToken postk = tk;
    String value;
    if(tk.kind == PetalToken.NAME && "value".equals(tk.value))
    {
      if(!moveNext())
      {
        error("unexected eof: "+current());
        return null;
      }
    }
    else
    {
      error("token 'value' is expected and found: "+tk);
      skipContent(1);
      return null;
    }
    tk = current();
    if(tk.kind == PetalToken.NAME && (
      "Text".equals(tk.value)) ||
      "cardinality".equals(tk.value))
    {
      if(!moveNext())
      {
        error("unexected eof: "+current());
        return null;
      }
    }
    else
    {
      error("token 'Text' or 'cardinality' is expected and found: "+tk);
      skipContent(1);
      return null;
    }
    tk = current();
    if(tk.kind == PetalToken.STRING)
    {
      value = (String)parseString();
      if(value == null)
      {
        skipContent(1);
        return null;
      }
    }
    else
    {
      error("string expected and found "+tk);
      skipContent(1);
      return null;
    }
    tk = current();
    if(tk.kind == PetalToken.CLOSE)
    {
      moveNext();
      return value;
    }
    else
    {
      error("')' expected and found "+tk);
      skipContent(1);
      return null;
    }
  }
  /** parse list like (list Type  "ar1" "dfafds" (object a))*/
  private Object parseList(PetalObject po, String pp)
  {
    PetalToken tk = current();
    PetalToken postk = tk;
    String t;
    if(tk.kind == PetalToken.NAME && "list".equals(tk.value))
    {
      if(!moveNext())
      {
        error("unexected eof: "+current());
        return null;
      }
    }
    else
    {
      error("token 'value' is expected and found: "+tk);
      skipContent(1);
      return null;
    }
    tk = current();
    if(tk.kind == PetalToken.NAME)
    {
      if(!moveNext())
      {
        error("unexected eof: "+current());
        return null;
      }
      t = name(tk.value);
    }
    else
    {
      t = null;
    }
    PetalListValue rc = new PetalListValue(postk.startLine, postk.startColumn,
      postk.value,t);
    tk = current();
    while(tk.kind != PetalToken.CLOSE && tk.kind != PetalToken.EOF)
    {
      Object v = parseValue(po,pp);
      if(v != null )
      {
        rc.addValue(v);
      }
      else
      {
        error("element is not parsed for list at "+postk);
      }
      tk = current();
    }
    if(tk.kind == PetalToken.CLOSE)
    {
      moveNext();
      return rc;
    }
    else
    {
      error("')' expected and found "+tk);
      skipContent(1);
      return null;
    }

  }


  /** parse object like (object Type "ar1" @2 prop1 "dfafds" prop2 "dfafda")*/
  private Object parseObject(PetalObject po, String pp)
  {
    // object consists from metatype, which is only object for now,
    // type, arguments and attributes
    PetalToken tk = current();
    PetalToken postk = tk;
    // parse type
    String mt;
    String t;
    if(tk.kind == PetalToken.NAME)
    {
      mt = name(tk.value);
      if(!moveNext())
      {
        error("unexected eof: "+current());
        return null;
      }
    }
    else
    {
      error("token 'value' is expected and found: "+tk);
      skipContent(1);
      return null;
    }
    tk = current();
    if(tk.kind == PetalToken.NAME)
    {
      if(!moveNext())
      {
        error("unexected eof: "+current());
        return null;
      }
      t = name(tk.value);
    }
    else
    {
      error("type token expected and found: "+tk);
      skipContent(1);
      return null;
    }
    PetalObject rc =  new PetalObject(doc, po, pp, postk.startLine, postk.startColumn,
      mt, t);
    tk = current();
    while(tk.kind != PetalToken.CLOSE && tk.kind != PetalToken.EOF && tk.kind != PetalToken.NAME)
    {
      Object v = parseValue(rc,null);
      if(v != null )
      {
        rc.addArgument(v);
      }
      else
      {
        error("element is not parsed for list at "+postk);
      }
      tk = current();
    }
    tk = current();
    while(tk.kind == PetalToken.NAME)
    {
      PetalToken pn = tk;
      String pnm = name(pn.value);
      if(!moveNext())
      {
        error("unexected eof: "+current());
        return null;
      }
      Object v = parseValue(rc, pnm);
      if(v != null )
      {
        rc.addProperty(new PetalProperty(pn.startLine, pn.startColumn, pnm, v));
      }
      else
      {
        error("value is not parsed for property "+pn);
      }
      tk = current();
    }
    if(tk.kind == PetalToken.CLOSE)
    {
      moveNext();
      return rc;
    }
    else
    {
      error("')' expected and found "+tk);
      skipContent(1);
      return null;
    }
  }

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
    PetalParser p = new PetalParser(f,err);
    long e = System.currentTimeMillis();
    System.out.println("File parse for "+(e-s));
    PetalDocument d = p.getDocument();
    if(debug)
    {
      DebugPetalWriter w = new DebugPetalWriter(System.out);
      w.write(d);
      w.flush();
    }
  }
}

