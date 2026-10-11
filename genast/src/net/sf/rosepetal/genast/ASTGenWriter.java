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

import java.io.*;
import java.util.*;
import javax.jmi.model.*;

/**
 *  base class for generator writers, it do not have coupling with MOF yet,
 *  but it do have coupling with java.
 *
 * @author    const
 * @author    mirt
 * @since     0.0.1
 */
public class ASTGenWriter {

  protected static CharArrayWriter charArray = null;
  /**
   *  number of iterator. this variable could be used for generating temporary
   *  variables.
   */
  protected static int iterNum;
  /** current indentation level */
  protected static int indentLevel;
  /** pointer to the generator object. */
  protected ASTGen generator;
  /* indetentation sting. */
  private String indentString = "  ";
  private String fileName;
  private String filePackage;

  /**
   *  Constructor for the ASTGenWriter object
   *
   * @param  generator        Generator for object
   * @param  pkg              package of java class
   * @param  file             file name to generate
   * @exception  IOException  exception thrown if there are problems
   * @since                   0.0.1
   */
  public ASTGenWriter(ASTGen generator, String pkg, String file) throws IOException {
    this.generator = generator;
    this.fileName = file;
    this.filePackage = pkg;
    charArray = new CharArrayWriter();
    indentLevel = 0;
    iterNum = 0;
    line("/*");
    line(" *  Rose Petal file API.");
    line(" *  Copyright (C) 2001-2002, Free Software Foundation.");
    line(" *  This library is free software; you can redistribute it and/or");
    line(" *  modify it under the terms of the GNU Library General Public");
    line(" *  License as published by the Free Software Foundation; either");
    line(" *  version 2 of the License, or (at your option) any later version.");
    line(" *  This library is distributed in the hope that it will be useful,");
    line(" *  but WITHOUT ANY WARRANTY; without even the implied warranty of");
    line(" *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU");
    line(" *  Library General Public License for more details.");
    line(" *");
    line(" *  You should have received a copy of the GNU Library General Public");
    line(" *  License along with this library; if not, write to the");
    line(" *  Free Software Foundation, Inc., 59 Temple Place - Suite 330,");
    line(" *  Boston, MA  02111-1307, USA. The text of license can be also found");
    line(" *  at http://www.gnu.org/copyleft/lgpl.html");
    line(" */");
  }


  /**
   *  Constructor for the CSGenWriter object
   *
   * @param  s  Description of Parameter
   */
  public ASTGenWriter() {
  }


  /**
   *  Gets the Generator attribute of the CSGenWriter object
   *
   * @return    the generator
   * @since     0.0.1
   */
  public ASTGen getGenerator() {
    return generator;
  }


  /**
   *  generate disclaimers and version information.
   *
   * @since    0.0.1
   */
  public void prolog() { }


  /**
   *  print empty line
   *
   * @since    0.0.1
   */
  public void line() {
    sline();
    println();
  }


  /**
   *  print line indented to current level.
   *
   * @param  s  line to print
   * @since     0.0.1
   */
  public void line(String s) {
    sline();
    println(s);
  }


  /**
   *  Start print indeneted line. Use print() to continue printig it and println()
   *  to finish printing.
   *
   * @since    0.0.1
   */
  public void sline() {
    for (int i = 0; i < indentLevel; i++) {
      print(indentString);
    }
  }


  /**
   *  Start print indeneted line with string. Use print() to continue printig
   *  it and println() to finish printing.
   *
   * @param  s  string to print.
   * @since     0.0.1
   */
  public void sline(String s) {
    sline();
    print(s);
  }


  /**
   *  increase indentation level.
   *
   * @since    0.0.1
   */
  public void indent() {
    indentLevel++;
  }


  /**
   *  Decrease indentation level.
   *
   * @since    0.0.1
   */
  public void unindent() {
    indentLevel--;
  }


  /**
   *  increase indentation level then print line with new level.
   *
   * @param  s  line to print
   * @since     0.0.1
   */
  public void iline(String s) {
    indent();
    line(s);
  }


  /**
   *  decrease indentation level then print line with new level.
   *
   * @param  s  line to print
   * @since     0.0.1
   */
  public void uline(String s) {
    unindent();
    line(s);
  }


  /**
   *  increase indentation level then print empty line with new level.
   *
   * @since    0.0.1
   */
  public void iline() {
    indent();
    line();
  }


  /**
   *  decrease indentation level then print empty line with new level.
   *
   * @since    0.0.1
   */
  public void uline() {
    unindent();
    line();
  }


  /**
   *  start java block.
   *
   * @since    0.0.1
   */
  public void sblock() {
    line("{");
    indent();
  }


  /**
   *  end java block.
   *
   * @since    0.0.1
   */
  public void eblock() {
    uline("}");
  }


  /**
   *  start for loop
   *
   * @param  arg  text printed in () of for operator
   * @since       0.0.1
   */
  public void sfor(String arg) {
    line("for (" + arg + ")");
    sblock();
  }


  /**
   *  end for loop
   *
   * @since    0.0.1
   */
  public void efor() {
    eblock();
  }


  /**
   *  start if operator
   *
   * @param  arg  condition
   * @since       0.0.1
   */
  public void sif(String arg) {
    line("if (" + arg + ")");
    sblock();
  }


  /**
   *  print else separator in if() {} else {} operator
   *
   * @since    0.0.1
   */
  public void pelse() {
    eblock();
    line("else");
    sblock();
  }


  /**
   *  Description of the Method
   *
   * @param  expr  Description of Parameter
   */
  public void pelseIf(String expr) {
    println("if(" + expr + ")");
    sblock();
  }


  /**
   *  end if operator
   *
   * @since    0.0.1
   */
  public void eif() {
    eblock();
  }


  /**
   *  strat while
   *
   * @param  arg  condition
   * @since       0.0.1
   */
  public void swhile(String arg) {
    line("while (" + arg + ")");
    sblock();
  }


  /**
   *  end while
   *
   * @since    0.0.1
   */
  public void ewhile() {
    eblock();
  }


  /**
   *  start do
   *
   * @since    0.0.1
   */
  public void sdo() {
    line("do");
    sblock();
  }


  /**
   *  end do
   *
   * @param  arg  condition
   * @since       0.0.1
   */
  public void edo(String arg) {
    eblock();
    line("while (" + arg + ")");
  }


  /**
   *  start foreach
   *
   * @param  col   collection to iterate
   * @param  type  type of element of collection
   * @param  obj   variable that is used for to keep current value
   * @since        0.0.1
   */
  public void sforeach(String col, String type, String obj) {
    String ivar = "iter" + (++iterNum);
    line("Iterator " + ivar + " = " + col + ".iterator();");
    swhile(ivar + ".hasNext()");
    line(type + " " + obj + " = (" + type + ")" + ivar + ".next();");
  }


  /**
   *  end foreach loop
   *
   * @since    0.0.1
   */
  public void eforeach() {
    ewhile();
  }


  /**
   *  create variable name (adds "_")
   *
   * @param  name  name to process
   * @return       return variable name
   * @since        0.0.1
   */
  public String vname(String name) {
    return "_" + name;
  }


  /**
   *  custom code generator
   *
   * @since    0.0.1
   */
  public void generateCustomCode() { }


  /**
   *  Description of the Method
   *
   * @param  s  Description of Parameter
   */
  public void print(String s) {
    charArray.write(s, 0, s.length());
  }


  /**  Description of the Method */
  public void print() { }


  /**
   *  Description of the Method
   *
   * @param  s  Description of Parameter
   */
  public void println(String s) {
    print(s + "\n");
  }


  /**  Description of the Method */
  public void println() {
    println("");
  }


  /**
   *  Closes this stream and perform checking of two buffers. Writes contents
   *  of this buffer into file if it is needed.
   */
  public void close() {
    if (isFileChanged()) {
      writeFile();
    }
    charArray.close();
  }


  /**
   *  start of mofier method. every method that contains modifications to the
   *  object should start writing with this method
   *
   * @since    0.0.1
   */
  protected void smodifier() {
    sblock();
    line("OperationStarted();");
    line("try");
    sblock();
  }


  /**
   *  closing section started by smodifier
   *
   * @since    0.0.1
   */
  protected void emodifier() {
    eblock();
    line("finally");
    sblock();
    line("OperationFinished();");
    eblock();
    eblock();
  }


  /**
   *  Checks contents of this buffer and file.
   *
   * @return    The FileChanged value
   */
  private boolean isFileChanged() {
    final File file = generator.getFileName(filePackage, fileName);

    if ((charArray.size() >= file.length()) && file.exists()) {
      return !isBuffersEquals(charArray.toCharArray(), getFileContents(file));
    }
    return true;
  }


  private boolean isBuffersEquals(char[] buf, char[] fileBuffer) {
    for (int i = 0; i < (int)fileBuffer.length; i++) {
      if (buf[i] != fileBuffer[i]) {
        return false;
      }
    }
    return true;
  }


  /**
   *  Returns file contents as array of chars.
   *
   * @param  file  file from which chars are read.
   * @return       The FileContents value
   */
  private char[] getFileContents(File file) {
    try {
      char[] fileBuffer = new char[(int)file.length()];
      FileReader fileReader = new FileReader(file);

      int offset = 0;
      int length = (int)file.length();

      while (fileReader.ready()) {
        int num = fileReader.read(fileBuffer, offset, length);
        offset += num;
        length -= num;
      }

      return fileBuffer;
    } catch (java.lang.Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }


  /**  Writes contents of this buffer into file.  */
  private void writeFile() {
    try {
      final FileWriter fileWriter = new FileWriter(generator.getFileName(filePackage, fileName));
      charArray.writeTo(fileWriter);
      fileWriter.close();
    } catch (java.lang.Exception ex) {
      ex.printStackTrace();
    }
  }


  /**
   *  make first letter of string uppercased
   *
   * @param  a  string to process
   * @return    processed string
   * @since     0.0.1
   */
  protected static String uname(String a) {

    if (a.length() > 0) {
      return a.substring(0, 1).toUpperCase() + a.substring(1);
    } else {
      return a;
    }
  }

  public String getTypeName(Classifier c)
  {
    return generator.getTypeName(c);
  }
  public String getName2(ModelElement m)
  {
    return generator.getName2(m);

  }
  public String getName1(ModelElement m)
  {
    return generator.getName1(m);

  }
  public String getName3(ModelElement m)
  {
    return generator.getName3(m);
  }
  public String getName4(ModelElement m)
  {
    return generator.getName4(m);
  }

  public String vname(ModelElement m)
  {
    return "__"+getName2(m);
  }

  public String setter(javax.jmi.model.StructuralFeature f,String v)
  {
    return "set"+getName1(f)+"("+v+")";
  }
  public String adder(javax.jmi.model.StructuralFeature f,String v)
  {
    return "add"+getName1(f)+"("+v+")";
  }

  public String getter(javax.jmi.model.StructuralFeature f)
  {
    if("Boolean".equals(f.getType().getName()))
    {
      if(getName2(f).startsWith("is"))
      {
        return getName4(f)+"()";
      }
      else
      {
        return "is"+getName1(f)+"()";
      }
    }
    return "get"+getName1(f)+"()";
  }

  public static Object getOneValue(Collection col)
  {
    return ASTGen.getOneValue(col);
  }

}



