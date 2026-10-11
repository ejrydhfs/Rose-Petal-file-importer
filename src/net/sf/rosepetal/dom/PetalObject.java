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
import java.util.*;

/**
 * object in petal file.
 */
public final class PetalObject extends PetalValue
{
  /** parent object */
  final PetalObject parent;
  /** parent property */
  final String parentProperty;
  /** document for this object */
  final private PetalDocument document;
  /** meta type of object likely it will be an "object" */
  final private String metaType;
  /** likely it will be an object */
  final private String type;
  /** positional arguments of object as they are */
  final private ArrayList arguments = new ArrayList();
  /** properties of object in order as they are */
  final private ArrayList properties = new ArrayList();
  /** positional arguments of object as they are */
  final private List uarguments = Collections.unmodifiableList(arguments);
  /** properties of object in order as they are */
  final private List uproperties = Collections.unmodifiableList(properties);
  /** quid of object */
  private String quid;
  /** flag that indicated that quid was searched */
  private boolean quidIsValid;

  /**
   * a constructor
   * @param mt meta type
   * @param t type of object
   */
  public PetalObject(PetalDocument d, PetalObject po, String pp, int sl,int sc,String mt, String t)
  {
    super(sl,sc);
    metaType = mt;
    type = t;
    document = d;
    parent = po;
    parentProperty = pp;
  }
  /**
   * get unique id of object. value is cached.
   * @returns petal quid
   */
  public String getQuid()
  {
    if(!quidIsValid)
    {
      for(Iterator i=properties.iterator();i.hasNext();)
      {
        PetalProperty pp = (PetalProperty)i.next();
        if("quid".equals(pp.getName()))
          quid= (String) pp.getValue();
      }
    }
    quidIsValid =true;
    return quid;
  }

  /** get meta type of object */
  public String getMetaType()
  {
    return metaType;
  }
  /** get type of object */
  public String getType()
  {
    return type;
  }
  /** add argument of object */
  public void addArgument(Object value)
  {
    arguments.add(value);
  }
  /** add property of object */
  public void addProperty(PetalProperty p)
  {
    properties.add(p);
  }
  /** get properties */
  public List getProperties()
  {
    return uproperties;
  }
  /** get arguments */
  public List getArguments()
  {
    return uarguments;
  }
  /** get document */
  public PetalDocument getDocument()
  {
    return document;
  }
  /** get parent object */
  public PetalObject getParent()
  {
    return parent;
  }
}