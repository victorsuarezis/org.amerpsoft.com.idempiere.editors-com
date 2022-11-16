/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package com.kpiactive.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for C_Suburb
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="C_Suburb")
public class X_C_Suburb extends PO implements I_C_Suburb, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20221116L;

    /** Standard Constructor */
    public X_C_Suburb (Properties ctx, int C_Suburb_ID, String trxName)
    {
      super (ctx, C_Suburb_ID, trxName);
      /** if (C_Suburb_ID == 0)
        {
			setC_Suburb_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_C_Suburb (Properties ctx, int C_Suburb_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, C_Suburb_ID, trxName, virtualColumns);
      /** if (C_Suburb_ID == 0)
        {
			setC_Suburb_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_C_Suburb (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 6 - System - Client 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_C_Suburb[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set C_Municipality.
		@param C_Municipality_ID C_Municipality
	*/
	public void setC_Municipality_ID (int C_Municipality_ID)
	{
		if (C_Municipality_ID < 1)
			set_Value (COLUMNNAME_C_Municipality_ID, null);
		else
			set_Value (COLUMNNAME_C_Municipality_ID, Integer.valueOf(C_Municipality_ID));
	}

	/** Get C_Municipality.
		@return C_Municipality	  */
	public int getC_Municipality_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Municipality_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Suburb.
		@param C_Suburb_ID Suburb
	*/
	public void setC_Suburb_ID (int C_Suburb_ID)
	{
		if (C_Suburb_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_Suburb_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_Suburb_ID, Integer.valueOf(C_Suburb_ID));
	}

	/** Get Suburb.
		@return Suburb	  */
	public int getC_Suburb_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Suburb_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set C_Suburb_UU.
		@param C_Suburb_UU C_Suburb_UU
	*/
	public void setC_Suburb_UU (String C_Suburb_UU)
	{
		set_Value (COLUMNNAME_C_Suburb_UU, C_Suburb_UU);
	}

	/** Get C_Suburb_UU.
		@return C_Suburb_UU	  */
	public String getC_Suburb_UU()
	{
		return (String)get_Value(COLUMNNAME_C_Suburb_UU);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set ZIP.
		@param Postal Postal code
	*/
	public void setPostal (String Postal)
	{
		set_Value (COLUMNNAME_Postal, Postal);
	}

	/** Get ZIP.
		@return Postal code
	  */
	public String getPostal()
	{
		return (String)get_Value(COLUMNNAME_Postal);
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}