/**
 * 
 */
package com.kpiactive.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;

/**
 *  @author <a href="mailto:victor.suarez.is@gmail.com">Ing. Victor Suarez</a>
 *
 */
public class MSuburb extends X_C_Suburb implements I_C_Suburb {

	private static final long serialVersionUID = -8980717791785580736L;
	
	/**	Suburb Cache				*/
	private static CCache<String,MSuburb> s_Suburbs = new CCache<String,MSuburb>("C_Suburb", 500);
	/**	Static Logger				*/
	private static CLogger		s_log = CLogger.getCLogger (MSuburb.class);

	/**
	 * @param ctx
	 * @param C_Suburb_ID
	 * @param trxName
	 */
	public MSuburb(Properties ctx, int C_Suburb_ID, String trxName) {
		super(ctx, C_Suburb_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ctx
	 * @param C_Suburb_ID
	 * @param trxName
	 * @param virtualColumns
	 */
	public MSuburb(Properties ctx, int C_Suburb_ID, String trxName, String... virtualColumns) {
		super(ctx, C_Suburb_ID, trxName, virtualColumns);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MSuburb(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 	Load Suburb (cached)
	 *	@param ctx context
	 */
	private static void loadAllSuburbs(Properties ctx) {
		String sql = "SELECT * FROM C_Suburb WHERE IsActive='Y' ORDER BY Name";
		try {
			Statement stmt = DB.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				MSuburb r = new MSuburb(ctx, rs, null);
				s_Suburbs.put(String.valueOf(r.getC_Suburb_ID()), r);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			s_log.log(Level.SEVERE, sql, e);
		}
	}	//	loadAllSuburbs
	
	/**
	 * Return Array of Suburb of C_Municipality_ID
	 * @param ctx
	 * @param C_Municipality_ID
	 * @return
	 */
	public static MSuburb[] getSuburb (Properties ctx, int C_Municipality_ID) {
		if (s_Suburbs == null || s_Suburbs.size() == 0)
			loadAllSuburbs(ctx);
		ArrayList<MSuburb> list = new ArrayList<MSuburb>();
		Iterator<MSuburb> it = s_Suburbs.values().iterator();
		while (it.hasNext()) {
			MSuburb sub = (MSuburb)it.next();
			if (sub.getC_Municipality_ID() == C_Municipality_ID)
				list.add(sub);
		}
		//  Sort it
		MSuburb[] retValue = new MSuburb[list.size()];
		list.toArray(retValue);
		Arrays.sort(retValue, new MSuburb(ctx, 0, null));
		return retValue;
	}	//	getSuburb
	
	/**
	 * Return Array of Suburb of C_Municipality_ID by SQL
	 * @param ctx
	 * @param C_Municipality_ID
	 * @return
	 */
	public static MSuburb[] getSQLSuburbs (Properties ctx, int C_Municipality_ID) {
		if(C_Municipality_ID <= 0)
			return new MSuburb[0];
		
		List<MSuburb> list = new ArrayList<>();
		
		String sql = "SELECT DISTINCT ON (Name) Name, * FROM C_Suburb "
				+ " WHERE IsActive='Y' AND C_Municipality_ID = " + C_Municipality_ID
				+ " ORDER BY Name ";
		try {
			Statement stmt = DB.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				MSuburb r = new MSuburb(ctx, rs, null);
				s_Suburbs.put(String.valueOf(r.getC_Suburb_ID()), r);
				list.add(r);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			s_log.log(Level.SEVERE, sql, e);
		}

		MSuburb[] retValue = new MSuburb[list.size()];
		list.toArray(retValue);
		return retValue;
	}	//	getSQLSuburubs
	
	/**
	 * 	Get Suburb (cached)
	 * 	@param ctx context
	 *	@param C_Suburb_ID
	 *	@return MSuburb
	 */
	public static MSuburb get (Properties ctx, int C_Suburb_ID) {
//		if (s_Suburbs == null || s_Suburbs.size() == 0)
//			loadAllSuburbs(ctx);
//		String key = String.valueOf(C_Suburb_ID);
//		MSuburb sub = (MSuburb)s_Suburbs.get(key);
//		if (sub != null)
//			return sub;
		if(C_Suburb_ID <=0)
			return null;
		MSuburb sub = new MSuburb(ctx, C_Suburb_ID, null);
		String key = String.valueOf(C_Suburb_ID);
		s_Suburbs.put(key, sub);
		return sub;
	}	//	get


}
