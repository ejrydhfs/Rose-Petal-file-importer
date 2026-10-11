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

/**
 * This is a temporary generator that generates mof interfaces.
 * If performance of this class will became an issue, it
 * coudl be fixed by more heavy caching of values.
 *
 * This generator is very specific to petal ast. It will break
 * on anything that will not fit assumptions about model. And
 * there were really lot of assumptions.
 */
public class ASTGen
{
  // tag constants
  /** name of tagId for package prefix */
  public static final String PACKAGE_PREFIX_TAG =
    "org.omg.mof.idl_prefix";
  /** name of tagId for substitute name */
  public static final String SUBSTITUTE_NAME_TAG =
    "org.omg.mof.idl_substitute_name";
  /** name of outermost package */
  private final static String MOF_PACKAGE_CLASS
     = "ru.novosoft.mdf.mof.impl.model.MOFModelPackageImpl";
  /** mof model */
  final ModelPackage metamodel;
  /** output directory */
  final File outputDirectory;
  /** root mof package */
  final MofPackage rootPackage;


  /** a constructor */
  private ASTGen(ModelPackage m, File od)
  {
    metamodel = m;
    MofPackage a = null;
    for(Iterator i= ((MDFOutermostPackage)m).getRoots().iterator();i.hasNext();)
    {
      MofPackage c = (MofPackage)i.next();
      if(c.getName().equals("ast"))
      {
        a= c;
      }
    }
    if(a==null)
    {
      throw new IllegalStateException("could not find ast package");
    }
    rootPackage = a;
    outputDirectory = od;
  }

  /** @return values attached to the model element */
  public List getTagValues(ModelElement me, String tagId)
  {
    for(Iterator i = metamodel.getAttachesTo().getTag(me).iterator();i.hasNext();)
    {
      Tag t = (Tag)i.next();
      if(tagId.equals(t.getTagId()))
      {
        return t.getValues();
      }
    }
    return Collections.EMPTY_LIST;
  }

  /** @return value attached to the model element */
  public String getTagValue(ModelElement me, String tagId)
  {
    return (String)getOneValue(getTagValues(me,tagId));
  }

  /** @return null if there are no elements in collectio and
   *  element if it is a single element in collection and throws
   *  exception if there more then one element in collection */
  public static Object getOneValue(Collection c)
  {
    int sz = c.size();
    if(sz == 1)
    {
      return c.iterator().next();
    }
    else if(sz == 0)
    {
      return null;
    }
    else
    {
      throw new IllegalArgumentException("there are more then one value in collection: "+c);
    }
  }

  /** get name in id1 format, possibly subsitute */
  String getName(ModelElement me)
  {
    String rc = getTagValue(me,SUBSTITUTE_NAME_TAG);
    if(rc == null)
      rc = me.getName();
    return rc;
  };


  /** get name in id1 format, possibly subsitute */
  String getName1(ModelElement me)
  {
    return NameUtil.getId1(getName(me));
  };
  /** get name in id1 format, possibly subsitute */
  String getName2(ModelElement me)
  {
    return NameUtil.getId2(getName(me));
  };
  /** get name in id1 format, possibly subsitute */
  String getName3(ModelElement me)
  {
    return NameUtil.getId3(getName(me));
  };
  /** get name in id1 format, possibly subsitute */
  String getName4(ModelElement me)
  {
    return NameUtil.getId4(getName(me));
  };

  /**
   * get java package from MOF namespace object
   * currently namespace structure is ignored and
   * all is generated to package preifix of root
   * namespace.
   */
  public void getNamespace(Namespace me, StringBuffer bu)
  {
    String pfx = getTagValue(me, PACKAGE_PREFIX_TAG);
    if(pfx != null)
    {
      bu.append(pfx);
      bu.append('.');
    }
    else
    {
      if(me.getContainer() != null)
      {
        getNamespace(me.getContainer(),bu);
        return;
        // add adding name here if namespace gathering
        // will chagne.
      }
    }
    bu.append(me.getName());
  }

  /** get .NET namespace from MOF namespace object*/
  public String getNamespace(Namespace me)
  {
    StringBuffer bu = new StringBuffer();
    getNamespace(me,bu);
    return bu.toString();
  }

  /** get name for instance type. this method is intended
   *  for getting type of return values, args etc
   *
   *  */
  public String getInstanceTypeName(ModelElement me)
  {
    StringBuffer bu = new StringBuffer();
    getNamespace(me.getContainer(), bu);
    bu.append('.');
    if(me instanceof EnumerationType)
    {

      bu.append(getName1(me));
    }
    else
    {
      throw new IllegalArgumentException("Not supported type: "+me.getQualifiedName());
    }
    return bu.toString();
  }

  /** star generation process */
  private void start()
  {
    try
    {
      System.out.println("Generating classes ... ");
      for(Iterator i = metamodel.getMofClass().refAllOfClass().iterator();i.hasNext();)
      {
        MofClass o = (MofClass)i.next();
        System.out.println("generating class: "+o.getQualifiedName());
        new ASTGenClass(this, o);
      }
      System.out.println("Generating enumerations ... ");
      for(Iterator i = metamodel.getEnumerationType().refAllOfClass().iterator();i.hasNext();)
      {
        EnumerationType o = (EnumerationType)i.next();
        if("IgnoredValue".equals(o.getName()))
        {
          continue;
        }
        new ASTGenEnum(this, o);
        System.out.println("generating enum: "+o.getQualifiedName());
      }
      System.out.println("Generating loader ... ");
      new ASTGenLoader(this, rootPackage);
      //System.out.println("Generating loader ... ");
      //new ASTGenDebugWriter(this, metamodel);
      System.out.println("Generation completed");
    }
    catch(Exception ex)
    {
      System.out.println("Generation completed with exception "+ex);
      ex.printStackTrace(System.out);
    }
  }

  /** a entry point into the program */
  public static void main(String args[])
  {
    if(args.length != 2)
    {
      usage();
    }
    System.out.println("Launching MOF to AST Generator");
    System.out.println("  input model     = "+args[0]);
    System.out.println("  output directoy = "+args[1]);
    if(! new File(args[0]).exists() )
    {
      System.out.println("File "+args[0]+" does not exists");
      usage();
    }
    File outputDirectory = new File(args[1]);
    if(! outputDirectory.exists() )
    {
      System.out.println("Directory "+args[1]+" does not exists, trying to create it");
      outputDirectory.mkdirs();
      if(! outputDirectory.exists() )
      {
        System.out.println("Directory "+args[1]+" could not be created.");
        usage();
      }
    }
    if(! outputDirectory.isDirectory() )
    {
      System.out.println("File "+args[1]+" is not a directory.");
      usage();
    }
    System.out.println("Starting loading MOF 1.4 model");
    ModelPackage p;
    try
    {
      p = (ModelPackage)Class.forName(MOF_PACKAGE_CLASS).newInstance();
    }
    catch(Exception ex)
    {
      ex.printStackTrace(System.out);
      System.out.println("ERROR: unable to create create MOF 1.4 extent with class "+MOF_PACKAGE_CLASS);
      throw new RuntimeException("unable to create create MOF 1.4 extent with class "+MOF_PACKAGE_CLASS);
    }
    // ok. model is created, loading xmi
    try
    {
      MDFOutermostPackage op = (MDFOutermostPackage)p;
      InputStream is = new FileInputStream(args[0]);
      try
      {
        XMI11MOF13ReaderDOM converter = new XMI11MOF13ReaderDOM();
        converter.setModelNamespace("org.omg.mof/Model/1.3");
        converter.read(op, is);
      }
      finally
      {
        is.close();
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace(System.out);
      System.out.println("ERROR: unable to load model from file "+args[0]);
      throw new RuntimeException("unable to load model from file "+args[0]);
    }
    new ASTGen(p,outputDirectory).start();
  }

  /** print usage */
  private static void usage()
  {
    System.out.println("java ASTGen <modefilename> <outputdirectory>");
    System.out.println("  <modefilename>    - file that contains mof 1.4 model for which interfaces will be generated");
    System.out.println("  <outputdirectory> - directory for where generated files will be saved ");
    throw new RuntimeException("The program was invoked with invalid arguments");
  }

  /**
   *  construct full file name by output directory of generator, package and
   *  name of file. The method also creates directories to that path.
   *
   * @param  pkg   name of the package in java form
   * @param  file  name of file
   * @return       full path to file
   * @since        0.0.1
   */
  public File getFileName(String pkg, String file) {
    StringTokenizer stk = new StringTokenizer(pkg, ".", false);
    File rc = outputDirectory;
    while (stk.hasMoreTokens()) {
      String tk = stk.nextToken();
      rc = new File(rc, tk);
    }
    rc.mkdirs();
    return new File(rc, file);
  }

  /** get type name */
  public String getTypeName(Classifier cls)
  {
    if(cls instanceof PrimitiveType)
    {
      if("String".equals(cls.getName()))
      {
        return "String";
      }
      if("Integer".equals(cls.getName()))
      {
        return "int";
      }
      if("Boolean".equals(cls.getName()))
      {
        return "boolean";
      }
      if("Double".equals(cls.getName()))
      {
        return "double";
      }
    }
    return "P"+getName1(cls);
  }

}
