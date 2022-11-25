/******************************************************************************
 * Copyright (C) 2015 Luis Amesty                                             *
 * Copyright (C) 2015 AMERP Consulting                                        *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 ******************************************************************************/
package org.amerp.amxeditor.editor;

import java.util.*;
import java.util.logging.Level;

import org.adempiere.util.Callback;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.*;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.*;
//
import org.amerp.amxeditor.model.*;
import org.compiere.model.*;
//
import org.compiere.util.*;
//import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Executions;

//import org.zkoss.zul.*;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.South;
import org.zkoss.zul.Vbox;

import com.kpiactive.model.MSuburb;

/**
 * @author luisamesty
 *
 */
public class WLocationExtDialog extends Window implements EventListener<Event>, ValueChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5368065537791919302L;
	
	private static final String LABEL_STYLE = "white-space: nowrap;";
	/** Logger          */
	private static CLogger log = CLogger.getCLogger(WLocationExtDialog.class);
	private Label lblAddress1;
	private Label lblAddress2;
	private Label lblAddress3;
	private Label lblAddress4;
	private Label lblCity;
	private Label lblZip;
	private Label lblRegion;
	private Label lblPostal;
	private Label lblPostalAdd;
	private Label lblCountry;
	// ADDED FIELDS
	private Label lblMunicipality;
	private Label lblParish;
	private Label lblSuburb;
	

	private Textbox txtAddress1;
	private Textbox txtAddress2;
	private Textbox txtAddress3;
	private Textbox txtAddress4;
	private WAutoCompleterCity txtCity;
	private WTableDirEditor fCity;
	private Textbox txtPostal;
	private Textbox txtPostalAdd;
	private Listbox lstRegion;
	private Listbox lstCountry;
	// ADDED FIELDS
	private Listbox lstMunicipality;
	private Listbox lstParish;
	private Listbox lstSuburb;
	
	private ConfirmPanel confirmPanel;
	private Grid mainPanel;

	private boolean     m_change = false;
	private MLocationExt   m_location;
	private int         m_origCountry_ID;
	private int         m_origRegion_ID;
	private int         m_origMunicipality_ID;
	private int         m_selectedRegion_ID;
	private int         m_selectedMunicipality_ID;
	private int         s_oldCountry_ID = 0;
	private int         s_oldRegion_ID = 0;
	private int         s_oldMunicipality_ID = 0;
//	private int         s_oldParish_ID = 0;
	
	private int m_WindowNo = 0;

	private boolean isCityMandatory = false;
	private boolean isRegionMandatory = false;
	private boolean isAddress1Mandatory = false;
	private boolean isAddress2Mandatory = false;
	private boolean isAddress3Mandatory = false;
	private boolean isAddress4Mandatory = false;
	private boolean isPostalMandatory = false;
	private boolean isPostalAddMandatory = false;
	//
	private boolean isMunicipalityMandatory = false;
	private boolean isParishMandatory = false;
	private boolean isSuburbMandatory = false;

	private boolean inCountryAction;
	private boolean inOKAction;

	private Button toLink;
	private Button toRoute;
	
	private Listbox lstAddressValidation;
	private Button btnOnline;
	private Textbox txtResult;
	private Checkbox cbxValid;
	private ArrayList<String> enabledCountryList = new ArrayList<String>();
	
	private GridField m_GridField = null;
	private boolean onSaveError = false;
	
	private boolean isAutomaticPostalCode = false;
	Row pnlCity = null;
	//END

	public WLocationExtDialog(String title, MLocationExt location)
	{
		this (title, location, null);
	}

	public WLocationExtDialog(String title, MLocationExt location, GridField gridField) {
		m_GridField  = gridField;
		m_location = location;
		if (m_location == null)
			m_location = new MLocationExt (Env.getCtx(), 0, null);
		//  Overwrite title 
		if (m_location.getC_Location_ID() == 0)
			setTitle("Ext-"+Msg.getMsg(Env.getCtx(), "LocationNew"));
		else
			setTitle("Ext-"+Msg.getMsg(Env.getCtx(), "LocationUpdate"));    
		//
		// Reset TAB_INFO context
		Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Region_ID", null);
		Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Country_ID", null);
		//
		try {
			initComponents();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error: " + e.getLocalizedMessage());
		}
		//      Current Country
		for (MCountryExt country: MCountryExt.getCountries(Env.getCtx()))
			lstCountry.appendItem(country.toString(), country);
		setCountry();
		init();
		
		lstCountry.addEventListener(Events.ON_SELECT,this);
		lstRegion.addEventListener(Events.ON_SELECT,this);
		lstMunicipality.addEventListener(Events.ON_SELECT,this);
		lstSuburb.addEventListener(Events.ON_SELECT,this);
		//lstParish.addEventListener(Events.ON_SELECT,this);
		m_origCountry_ID = m_location.getC_Country_ID();
		//  Current Region
		lstRegion.appendItem("", null);
		for (MRegion region : MRegion.getRegions(m_origCountry_ID))
		{
			lstRegion.appendItem(region.getName(),region);
		}
		if (m_location.getCountryExt().isHasRegion()) {
			if (m_location.getCountryExt().get_Translation(MCountryExt.COLUMNNAME_RegionName) != null
					&& m_location.getCountryExt().get_Translation(MCountryExt.COLUMNNAME_RegionName).trim().length() > 0)
				lblRegion.setValue(m_location.getCountryExt().get_Translation(MCountryExt.COLUMNNAME_RegionName));
			else
				lblRegion.setValue(Msg.getMsg(Env.getCtx(), "Region"));
		}
		setRegion();
		// Municipality
		m_origRegion_ID = m_location.getC_Region_ID();
		m_selectedRegion_ID = m_origRegion_ID;	// lstRegion.getSelectedCount();;
		// 
		lstMunicipality.appendItem("", null); 
		for (MMunicipality municipality : MMunicipality.getSQLMunicipalitys(Env.getCtx(), m_selectedRegion_ID))
		{
			lstMunicipality.appendItem(municipality.getName(),municipality);
		}
		setMunicipality();
		// Parish
		m_origMunicipality_ID =  m_location.getC_Municipality_ID();
		m_selectedMunicipality_ID = m_origMunicipality_ID;	// lstMunicipality.getSelectedCount();	
		//
		lstParish.appendItem("", null); 
		for (MParish parish : MParish.getSQLParishs(Env.getCtx(), m_selectedMunicipality_ID,m_selectedRegion_ID))
		{
			lstParish.appendItem(parish.getName(),parish);
		}
		setParish();
		
		// Suburb	
		lstSuburb.appendItem("", null); 
		for (MSuburb suburb : MSuburb.getSQLSuburbs(Env.getCtx(), m_selectedMunicipality_ID)) {
			lstSuburb.appendItem(suburb.getName(), suburb);
		}
		setSuburb();
		
		// initLocation First Time
		initLocation();
		//  
		if (!ThemeManager.isUseCSSForWindowSize()) 
		{
			ZKUpdateUtil.setWindowWidthX(this, 380);
			ZKUpdateUtil.setWindowHeightX(this, 420); // required fixed height for ZK to auto adjust the position based on available space
		}
		else
		{
			addCallback(AFTER_PAGE_ATTACHED, t -> {
				ZKUpdateUtil.setCSSHeight(this);
				ZKUpdateUtil.setCSSWidth(this);
			});
		}
		//
		ZKUpdateUtil.setWidth(this,"350px");
		ZKUpdateUtil.setHeight(this, "420px");
		this.setSclass("popup-dialog");
		this.setClosable(true);
		this.setBorder("normal");
		this.setShadow(true);
		this.setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
	}

	/**
	 * initComponents()
	 * Set Form's Components associated with their fields on c_location thru MLocationExt 
	 * @throws Exception 
	 */
	private void initComponents() throws Exception {
		lblAddress1     = new Label(Msg.getElement(Env.getCtx(), "Address1"));
		lblAddress1.setStyle(LABEL_STYLE);
		lblAddress2     = new Label(Msg.getElement(Env.getCtx(), "Address2"));
		lblAddress2.setStyle(LABEL_STYLE);
		lblAddress3     = new Label(Msg.getElement(Env.getCtx(), "Address3"));
		lblAddress3.setStyle(LABEL_STYLE);
		lblAddress4     = new Label(Msg.getElement(Env.getCtx(), "Address4"));
		lblAddress4.setStyle(LABEL_STYLE);
		lblCity         = new Label(Msg.getMsg(Env.getCtx(), "City"));
		lblCity.setStyle(LABEL_STYLE);
		lblZip          = new Label(Msg.getMsg(Env.getCtx(), "Postal"));
		lblZip.setStyle(LABEL_STYLE);
		lblRegion       = new Label(Msg.getMsg(Env.getCtx(), "Region"));
		lblRegion.setStyle(LABEL_STYLE);
		lblPostal       = new Label(Msg.getMsg(Env.getCtx(), "Postal"));
		lblPostal.setStyle(LABEL_STYLE);
		lblPostalAdd    = new Label(Msg.getMsg(Env.getCtx(), "PostalAdd"));
		lblPostalAdd.setStyle(LABEL_STYLE);
		lblCountry      = new Label(Msg.getElement(Env.getCtx(), "C_Country_ID"));
		lblCountry.setStyle(LABEL_STYLE);
		lblMunicipality = new Label(Msg.getElement(Env.getCtx(), "C_Municipality_ID"));
		lblMunicipality.setStyle(LABEL_STYLE);
		lblParish		= new Label(Msg.getElement(Env.getCtx(), "C_Parish_ID"));
		lblParish.setStyle(LABEL_STYLE);
		lblSuburb		= new Label(Msg.getElement(Env.getCtx(), MSuburb.COLUMNNAME_C_Suburb_ID));
		lblSuburb.setStyle(LABEL_STYLE);
		
		txtAddress1 = new Textbox();
		txtAddress1.setCols(20);
		txtAddress1.setMaxlength(MLocationExt.getFieldLength(MLocationExt.COLUMNNAME_Address1));
		txtAddress2 = new Textbox();
		txtAddress2.setCols(20);
		txtAddress2.setMaxlength(MLocationExt.getFieldLength(MLocationExt.COLUMNNAME_Address2));
		txtAddress3 = new Textbox();
		txtAddress3.setCols(20);
		txtAddress3.setMaxlength(MLocationExt.getFieldLength(MLocationExt.COLUMNNAME_Address3));
		txtAddress4 = new Textbox();
		txtAddress4.setCols(20);
		txtAddress4.setMaxlength(MLocationExt.getFieldLength(MLocationExt.COLUMNNAME_Address4));

		//autocomplete City
		MLookup lookupCity = MLookupFactory.get(Env.getCtx(), m_WindowNo, 0, DisplayType.TableDir, Env.getLanguage(Env.getCtx()),
				"C_City_ID",0,false,"C_City.isActive='Y' and C_City.C_Region_ID = @C_Region_ID@ ");
		fCity = new WTableDirEditor("C_City_ID", false,false,true, lookupCity);
		fCity.getComponent().setWidth("100%");
		
		txtCity = new WAutoCompleterCity(m_WindowNo);
		txtCity.setCols(20);
		txtCity.setMaxlength(MLocationExt.getFieldLength(MLocationExt.COLUMNNAME_City));
		txtCity.setAutodrop(true);
		txtCity.setAutocomplete(true);
		txtCity.addEventListener(Events.ON_CHANGING, this);
		//txtCity

		txtPostal = new Textbox();
		txtPostal.setCols(20);
		txtPostal.setMaxlength(MLocationExt.getFieldLength(MLocationExt.COLUMNNAME_Postal));
		txtPostalAdd = new Textbox();
		txtPostalAdd.setCols(20);
		txtPostalAdd.setMaxlength(MLocationExt.getFieldLength(MLocationExt.COLUMNNAME_Postal_Add));

		lstRegion    = new Listbox();
		lstRegion.setMold("select");
		//lstRegion.setWidth("154px");
		ZKUpdateUtil.setWidth(lstRegion, "154px");
		lstRegion.setRows(0);

		lstCountry  = new Listbox();
		lstCountry.setMold("select");
		//lstCountry.setWidth("154px");
		ZKUpdateUtil.setWidth(lstCountry, "154px");		
		lstCountry.setRows(0);
		
		lstMunicipality    = new Listbox();
		lstMunicipality.setMold("select");
		//lstMunicipality.setWidth("154px");
		ZKUpdateUtil.setWidth(lstMunicipality, "154px");	
		lstMunicipality.setRows(0);

		lstParish    = new Listbox();
		lstParish.setMold("select");
		//lstParish.setWidth("154px");
		ZKUpdateUtil.setWidth(lstParish, "154px");	
		lstParish.setRows(0);
		
		lstSuburb    = new Listbox();
		lstSuburb.setMold("select");
		ZKUpdateUtil.setWidth(lstSuburb, "154px");	
		lstSuburb.setRows(0);

		confirmPanel = new ConfirmPanel(true);
		confirmPanel.addActionListener(this);

		toLink = new Button(Msg.getMsg(Env.getCtx(), "Map"));
		LayoutUtils.addSclass("txt-btn", toLink);
		toLink.addEventListener(Events.ON_CLICK,this);
		toRoute = new Button(Msg.getMsg(Env.getCtx(), "Route"));
		LayoutUtils.addSclass("txt-btn", toRoute);
		toRoute.addEventListener(Events.ON_CLICK,this);
		
		btnOnline = new Button(Msg.getElement(Env.getCtx(), "ValidateAddress"));
		LayoutUtils.addSclass("txt-btn", btnOnline);
		btnOnline.addEventListener(Events.ON_CLICK,this);
		
		txtResult = new Textbox();
		txtResult.setCols(2);
		txtResult.setRows(3);
		//txtResult.setHeight("100%");
		//ZKUpdateUtil.setHeight(txtResult, "100%");
		txtResult.setReadonly(true);
		
		cbxValid = new Checkbox();
		cbxValid.setText(Msg.getElement(Env.getCtx(), "IsValid"));
		cbxValid.setDisabled(true);
		
		lstAddressValidation = new Listbox();
		lstAddressValidation.setMold("select");
		//lstAddressValidation.setWidth("154px");
		ZKUpdateUtil.setWidth(lstAddressValidation, "154px");	
		lstAddressValidation.setRows(0);		

		mainPanel = GridFactory.newGridLayout();
	}
	
	/**
	 * init()
	 *
	 */
	private void init()
	{
		Columns columns = new Columns();
		mainPanel.appendChild(columns);
		
		Column column = new Column();
		columns.appendChild(column);
		//column.setWidth("30%");
		ZKUpdateUtil.setWidth(column, "30%");	
		
		column = new Column();
		columns.appendChild(column);
		//column.setWidth("70%");
		ZKUpdateUtil.setWidth(column, "70%");	
		
		Row pnlAddress1 = new Row();
		pnlAddress1.appendChild(lblAddress1.rightAlign());
		pnlAddress1.appendChild(txtAddress1);
		//txtAddress1.setHflex("1");
		ZKUpdateUtil.setHflex(txtAddress1, "1");


		Row pnlAddress2 = new Row();
		pnlAddress2.appendChild(lblAddress2.rightAlign());
		pnlAddress2.appendChild(txtAddress2);
		//txtAddress2.setHflex("1");
		ZKUpdateUtil.setHflex(txtAddress2, "1");
		
		Row pnlAddress3 = new Row();
		pnlAddress3.appendChild(lblAddress3.rightAlign());
		pnlAddress3.appendChild(txtAddress3);
		//txtAddress3.setHflex("1");
		ZKUpdateUtil.setHflex(txtAddress3, "1");
		
		Row pnlAddress4 = new Row();
		pnlAddress4.appendChild(lblAddress4.rightAlign());
		pnlAddress4.appendChild(txtAddress4);
		//txtAddress4.setHflex("1");
		ZKUpdateUtil.setHflex(txtAddress4, "1");

		pnlCity     = new Row();
		pnlCity.appendChild(lblCity.rightAlign());
		if(isAutomaticPostalCode) {
			pnlCity.appendChild(fCity.getComponent());
			ZKUpdateUtil.setHflex(fCity.getComponent(), "1");
		} else {
			pnlCity.appendChild(txtCity);
			ZKUpdateUtil.setHflex(txtCity, "1");
		}
		Row pnlMunicipality     = new Row();
		pnlMunicipality.appendChild(lblMunicipality.rightAlign());
		pnlMunicipality.appendChild(lstMunicipality);
		//lstMunicipality.setHflex("1");
		ZKUpdateUtil.setHflex(lstMunicipality, "1");
		
		Row pnlParish     = new Row();
		pnlParish.appendChild(lblParish.rightAlign());
		pnlParish.appendChild(lstParish);
		//lstParish.setHflex("1");
		ZKUpdateUtil.setHflex(lstParish, "1");
		
		Row pnlSuburb     = new Row();
		pnlSuburb.appendChild(lblSuburb.rightAlign());
		pnlSuburb.appendChild(lstSuburb);
		ZKUpdateUtil.setHflex(lstSuburb, "1");
		
		Row pnlPostal   = new Row();
		pnlPostal.appendChild(lblPostal.rightAlign());
		pnlPostal.appendChild(txtPostal);
		//txtPostal.setHflex("1");
		ZKUpdateUtil.setHflex(txtPostal, "1");
		
		Row pnlPostalAdd = new Row();
		pnlPostalAdd.appendChild(lblPostalAdd.rightAlign());
		pnlPostalAdd.appendChild(txtPostalAdd);
		//txtPostalAdd.setHflex("1");
		ZKUpdateUtil.setHflex(txtPostalAdd, "1");

		Row pnlRegion    = new Row();
		pnlRegion.appendChild(lblRegion.rightAlign());
		pnlRegion.appendChild(lstRegion);
		//lstRegion.setHflex("1");
		ZKUpdateUtil.setHflex(lstRegion, "1");
		
		Row pnlCountry  = new Row();
		pnlCountry.appendChild(lblCountry.rightAlign());
		pnlCountry.appendChild(lstCountry);
		//lstCountry.setHflex("1");
		ZKUpdateUtil.setHflex(lstCountry, "1");
		
		Panel pnlLinks    = new Panel();
		pnlLinks.appendChild(toLink);
		if (MLocationExt.LOCATION_MAPS_URL_PREFIX == null)
			toLink.setVisible(false);
		pnlLinks.appendChild(toRoute);
		if (MLocationExt.LOCATION_MAPS_ROUTE_PREFIX == null || Env.getAD_Org_ID(Env.getCtx()) <= 0)
			toRoute.setVisible(false);
		//pnlLinks.setWidth("100%");
		ZKUpdateUtil.setWidth(pnlLinks, "100%");
		pnlLinks.setStyle("text-align:right");
		
		Borderlayout borderlayout = new Borderlayout();
		this.appendChild(borderlayout);
		//borderlayout.setHflex("1");
		//borderlayout.setVflex("1");
		ZKUpdateUtil.setHflex(borderlayout, "1");
		ZKUpdateUtil.setVflex(borderlayout, "1");
		
		Center centerPane = new Center();
		centerPane.setSclass("dialog-content");
		centerPane.setAutoscroll(true);
		borderlayout.appendChild(centerPane);
		
		Vbox vbox = new Vbox();
		centerPane.appendChild(vbox);
		vbox.appendChild(mainPanel);
		if (MLocationExt.LOCATION_MAPS_URL_PREFIX != null || MLocationExt.LOCATION_MAPS_ROUTE_PREFIX != null)
			vbox.appendChild(pnlLinks);
		
		String addressValidation = MSysConfig.getValue(MSysConfig.ADDRESS_VALIDATION, null, Env.getAD_Client_ID(Env.getCtx()));
		enabledCountryList.clear();
		if (addressValidation != null && addressValidation.trim().length() > 0)
		{
			StringTokenizer st = new StringTokenizer(addressValidation, ";");
			while (st.hasMoreTokens())
			{
				String token = st.nextToken().trim();
				enabledCountryList.add(token);
			}
		}
			
		if (enabledCountryList.size() > 0)
		{
			Grid grid = GridFactory.newGridLayout();
			vbox.appendChild(grid);
			
			columns = new Columns();
			grid.appendChild(columns);
			
			Rows rows = new Rows();
			grid.appendChild(rows);
			
			Row row = new Row();
			rows.appendChild(row);
			row.appendCellChild(lstAddressValidation, 2);
			//lstAddressValidation.setHflex("1");			
			ZKUpdateUtil.setHflex(lstAddressValidation, "1");			
			
			MAddressValidationExt[] validations = MAddressValidationExt.getAddressValidation(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()), null);
			for (MAddressValidationExt validation : validations)
			{
				ListItem li = lstAddressValidation.appendItem(validation.getName(), validation);
				if (m_location.getC_AddressValidation_ID() == validation.getC_AddressValidation_ID())
					lstAddressValidation.setSelectedItem(li);
			}
			
			if (lstAddressValidation.getSelectedIndex() == -1 && lstAddressValidation.getChildren().size() > 0)
				lstAddressValidation.setSelectedIndex(0);
						
			row = new Row();
			rows.appendChild(row);
			row.appendCellChild(txtResult, 2);
			//txtResult.setHflex("1");
			ZKUpdateUtil.setHflex(txtResult, "1");
			txtResult.setText(m_location.getResult());
			
			row = new Row();
			rows.appendChild(row);
			row.appendChild(cbxValid);
			cbxValid.setChecked(m_location.isValid());
			Cell cell = new Cell();
			cell.setColspan(1);
			cell.setRowspan(1);
			cell.appendChild(btnOnline);
			cell.setAlign("right");
			row.appendChild(cell);
			
			if (!enabledCountryList.isEmpty())
			{
				boolean isEnabled = false;
				if (m_location.getCountryExt() != null)
				{
					for (String enabledCountry : enabledCountryList)
					{
						if (enabledCountry.equals(m_location.getCountryExt().getCountryCode().trim()))
						{
							isEnabled = true;
							break;
						}
					}
				}
				btnOnline.setEnabled(isEnabled);
			}
		}
		
		//vbox.setVflex("1");
		//vbox.setHflex("1");
		ZKUpdateUtil.setVflex(vbox, "1");
		ZKUpdateUtil.setHflex(vbox, "1");
		
		South southPane = new South();
		southPane.setSclass("dialog-footer");
		borderlayout.appendChild(southPane);
		southPane.appendChild(confirmPanel);
		
		addEventListener("onSaveError", this);
		fCity.addValueChangeListener(this);
	}
	
	/**
	 * Dynamically add fields to the Location dialog box
	 * @param panel panel to add
	 *
	 */
	private void addComponents(Row row)
	{
		if(row == null)
			return;
		if (mainPanel.getRows() != null)
			mainPanel.getRows().appendChild(row);
		else
			mainPanel.newRows().appendChild(row);
	}
	
	/**
	* initLocation()
	* Init Location Form as Tokens indicate on Capture Sequence
	* Country Variables 
	*/
	private void initLocation()
	{
		if (mainPanel.getRows() != null)
			mainPanel.getRows().getChildren().clear();
		
		txtCity.getChildren().clear();
		txtCity.appendItem("", null);
		txtCity.setValue(null);
		fCity.getLookup().removeAllElements();
		fCity.setValue(null);

		MCountryExt country =  m_location.getCountryExt();
		if (log.isLoggable(Level.FINE)) log.fine(country.getName() + ", Region=" + country.isHasRegion() + " " + country.getCaptureSequence()
				+ ", C_Location_ID=" + m_location.getC_Location_ID());
//		log.warning("m_location.initLocation Init Values..City:"+m_location.getC_City_ID()+"-"+m_location.getCity()+
//						"..Country:"+m_location.getC_Country_ID()+"..Region:"+m_location.getC_Region_ID()+
//						"..Municipality:"+m_location.getC_Municipality_ID()+"..Parish:"+m_location.getC_Parish_ID());
		//  new Country
		if (m_location.getC_Country_ID() != s_oldCountry_ID)
		{
			lstRegion.getChildren().clear();
			if (country.isHasRegion()) {
				lstRegion.appendItem("", null);
				for (MRegionExt region : MRegionExt.getRegions(Env.getCtx(), country.getC_Country_ID()))
				{
					lstRegion.appendItem(region.getName(),region);
				}
				if (m_location.getCountryExt().get_Translation(MCountryExt.COLUMNNAME_RegionName) != null
						&& m_location.getCountryExt().get_Translation(MCountryExt.COLUMNNAME_RegionName).trim().length() > 0)
					lblRegion.setValue(m_location.getCountryExt().get_Translation(MCountryExt.COLUMNNAME_RegionName));
				else
					lblRegion.setValue(Msg.getMsg(Env.getCtx(), "Region"));
			}
			s_oldCountry_ID = m_location.getC_Country_ID();
			pnlCity = null;
			pnlCity = new Row();
			pnlCity.appendChild(lblCity.rightAlign());
			if(isAutomaticPostalCode) {
				pnlCity.appendChild(fCity.getComponent());
				ZKUpdateUtil.setHflex(fCity.getComponent(), "1");
			} else {
				pnlCity.appendChild(txtCity);
				ZKUpdateUtil.setHflex(txtCity, "1");
			}
		}
		//  new Region
		if (m_location.getC_Region_ID() > 0 && m_location.getC_Region().getC_Country_ID() == country.getC_Country_ID()) {
			setRegion();
		} else {
			lstRegion.setSelectedItem(null);
			m_location.setC_Region_ID(0);
		}

		if (country.isHasRegion() && m_location.getC_Region_ID() > 0)
		{
			Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Region_ID", String.valueOf(m_location.getC_Region_ID()));
			Env.setContext(Env.getCtx(), m_WindowNo, "C_Region_ID", String.valueOf(m_location.getC_Region_ID()));
		} else {
			Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Region_ID", "0");
			Env.setContext(Env.getCtx(), m_WindowNo, "C_Region_ID", "0");
		}
		// log.warning("....C_Country_ID"+String.valueOf(country.get_ID()));
		Env.setContext(Env.getCtx(), m_WindowNo, Env.TAB_INFO, "C_Country_ID", String.valueOf(country.get_ID()));
		
		// City Filllist
		fCity.actionRefresh();
		txtCity.fillList();
		
		// actualiza cuando cambia la region	
		if (m_location.getC_Region_ID() != s_oldRegion_ID) {	
			lstMunicipality.getChildren().clear();
			lstMunicipality.appendItem("", null);
			txtPostal.setValue(null);
			
			lstParish.getChildren().clear();
			lstParish.appendItem("", null);
			
			lstSuburb.getChildren().clear();
			lstSuburb.appendItem("", null);
			for (MMunicipality municipality : MMunicipality.getSQLMunicipalitys(Env.getCtx(), m_location.getC_Region_ID()))	{
				lstMunicipality.appendItem(municipality.getName(),municipality);			
			}
			//setMunicipality();
			//m_origMunicipality_ID =  m_location.getC_Municipality_ID();
//			log.warning("getC_Municipality_ID()"+m_location.getC_Municipality_ID());		
			if(m_location.getC_Municipality_ID() > 0){
				setMunicipality();
			} else {
				lstMunicipality.setSelectedItem(null);
				m_location.setC_Municipality_ID(0);
			}
			s_oldRegion_ID = m_location.getC_Region_ID();
		}

		//  new Municipality
		// actualiza cuando cambia el municipio
		if (m_location.getC_Municipality_ID() != s_oldMunicipality_ID) {
			// Clear Parish
//			setParish();
			// log.warning("  Municipality_ID="+m_location.getC_Municipality_ID()+" C_Region_ID="+m_location.getC_Region_ID());
			txtPostal.setValue(null);
			lstParish.getChildren().clear();
			lstParish.appendItem("", null); 
			if(country.isHasParish()) {
				for (MParish parish : MParish.getSQLParishs(Env.getCtx(),  m_location.getC_Municipality_ID(),m_location.getC_Region_ID())) {
					lstParish.appendItem(parish.getName(),parish);
				}
				setParish();
			}
			
			lstSuburb.getChildren().clear();
			lstSuburb.appendItem("", null);
			if(country.isHasSuburb()) {
				for (MSuburb suburb : MSuburb.getSQLSuburbs(Env.getCtx(), m_location.getC_Municipality_ID())) {
					lstSuburb.appendItem(suburb.getName(), suburb);
				}
				setSuburb();
			}
			
			s_oldMunicipality_ID = m_location.getC_Municipality_ID();
		}
		//  new Parish
		//log.warning("m_location.initLocation OJO..City:"+m_location.getC_City_ID()+"-"+m_location.getCity()+
		//				"..Country:"+m_location.getC_Country_ID()+"..Region:"+m_location.getC_Region_ID()+
		//				"..Municipality:"+m_location.getC_Municipality_ID()+"..Parish:"+m_location.getC_Parish_ID());
		//      sequence of City Postal Region - @P@ @C@ - @C@, @R@ @P@
		String ds = country.getCaptureSequence();
		if (ds == null || ds.length() == 0) {
			log.log(Level.SEVERE, "CaptureSequence empty - " + country);
			ds = "";    //  @C@,  @P@
		}
		isCityMandatory = false;
		isRegionMandatory = false;
		isAddress1Mandatory = false;
		isAddress2Mandatory = false;
		isAddress3Mandatory = false;
		isAddress4Mandatory = false;
		isPostalMandatory = false;
		isPostalAddMandatory = false;
		isMunicipalityMandatory = false;
		isParishMandatory = false;
		isSuburbMandatory = false;
		StringTokenizer st = new StringTokenizer(ds, "@", false);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s.startsWith("CO")) {
				//  Country Last
				addComponents((Row)lstCountry.getParent());
				// if (m_location.getCountryExt().isPostcodeLookup()) {
					// addLine(line++, lOnline, fOnline);
				// }
			} else if (s.startsWith("A1")) {
				addComponents((Row)txtAddress1.getParent());
				isAddress1Mandatory = s.endsWith("!");
			} else if (s.startsWith("A2")) {
				addComponents((Row)txtAddress2.getParent());
				isAddress2Mandatory = s.endsWith("!");
			} else if (s.startsWith("A3")) {
				addComponents((Row)txtAddress3.getParent());
				isAddress3Mandatory = s.endsWith("!");
			} else if (s.startsWith("A4")) {
				addComponents((Row)txtAddress4.getParent());
				isAddress4Mandatory = s.endsWith("!");
			} else if (s.startsWith("C")) {
				addComponents(pnlCity);
				isCityMandatory = s.endsWith("!");
			} else if (s.startsWith("MU") && m_location.getCountryExt().isHasRegion() && m_location.getCountryExt().isHasMunicipality()) {
				addComponents((Row)lstMunicipality.getParent());
				isMunicipalityMandatory = s.endsWith("!");
			}else if (s.startsWith("PA") && s.trim().equalsIgnoreCase("PA") && m_location.getCountryExt().isHasRegion() && m_location.getCountryExt().isHasParish()) {
				addComponents((Row)lstParish.getParent());
				isParishMandatory = s.endsWith("!");
			} else if (s.startsWith("P") && s.trim().equalsIgnoreCase("P")) {
				addComponents((Row)txtPostal.getParent());
				isPostalMandatory = s.endsWith("!");
			} else if (s.startsWith("A")) {
				addComponents((Row)txtPostalAdd.getParent());
				isPostalAddMandatory = s.endsWith("!");
			} else if (s.startsWith("R") && m_location.getCountryExt().isHasRegion()) {
				addComponents((Row)lstRegion.getParent());
				isRegionMandatory = s.endsWith("!");
			} else if (s.startsWith("SU") && s.trim().equalsIgnoreCase("SU") && m_location.getCountryExt().isHasSuburb()) {
				addComponents((Row)lstSuburb.getParent());
				isSuburbMandatory = s.endsWith("!");
			}
			// NEW FIELDS ELEMENTS
			if (m_location.getCountryExt().isHasRegion()) {
				

			}
		}

		//      Fill it
		if (m_location.getC_Location_ID() != 0) {
			txtAddress1.setText(m_location.getAddress1());
			txtAddress2.setText(m_location.getAddress2());
			txtAddress3.setText(m_location.getAddress3());
			txtAddress4.setText(m_location.getAddress4());
			if(isAutomaticPostalCode)
				fCity.setValue(m_location.getC_City_ID());
			else
				txtCity.setText(m_location.getCity());
			txtPostal.setText(m_location.getPostal());
			txtPostalAdd.setText(m_location.getPostal_Add());
			if (m_location.getCountryExt().isHasRegion())
			{
				if (m_location.getCountryExt().get_Translation(MCountryExt.COLUMNNAME_RegionName) != null
						&& m_location.getCountryExt().get_Translation(MCountryExt.COLUMNNAME_RegionName).trim().length() > 0)
					lblRegion.setValue(m_location.getCountryExt().get_Translation(MCountryExt.COLUMNNAME_RegionName));
				else
					lblRegion.setValue(Msg.getMsg(Env.getCtx(), "Region"));

				setRegion();                
			}
			setCountry();
		}
//log.warning("m_location.initLocation  END..City:"+m_location.getC_City_ID()+"-"+m_location.getCity()+
//				"..Country:"+m_location.getC_Country_ID()+"..Region:"+m_location.getC_Region_ID()+
//				"..Municipality:"+m_location.getC_Municipality_ID()+"..Parish:"+m_location.getC_Parish_ID());
	}
	
	/**
	 * setCountry
	 */
	private void setCountry() {
		List<?> listCountry = lstCountry.getChildren();
		Iterator<?> iter = listCountry.iterator();
		while (iter.hasNext())
		{
			ListItem listitem = (ListItem)iter.next();
			if (m_location.getCountryExt().equals(listitem.getValue()))
			{
				lstCountry.setSelectedItem(listitem);
				MCountry country = (MCountry) m_location.getC_Country();
				isAutomaticPostalCode = country.get_ValueAsBoolean("IsAutomaticPostalCode");
			}
		}
	}

	/**
	* setRegion
	*/
	private void setRegion() {
		if (m_location.getRegionExt() != null) 
		{
			List<?> listState = lstRegion.getChildren();
			Iterator<?> iter = listState.iterator();
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				if (m_location.getRegionExt().equals(listitem.getValue()))
				{
					lstRegion.setSelectedItem(listitem);
				}
			}
		}
		else
		{
			lstRegion.setSelectedItem(null);
		}        
	}
	
	/**
	 * setMunicipality
	 */
	private void setMunicipality() {
		if (m_location.getMunicipality() != null ) 
		{
			List<?> listMun = lstMunicipality.getChildren();
			Iterator<?> iter = listMun.iterator();
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				if (m_location.getMunicipality().equals(listitem.getValue()))
				{
					lstMunicipality.setSelectedItem(listitem);
				}
			}
		}
		else
		{
			lstMunicipality.setSelectedItem(null);
		}        
	}
	
	/**
	 *  setParish
	 */
	private void setParish() {
		if (m_location.getParish() != null) 
		{
			List<?> listParr = lstParish.getChildren();
			Iterator<?> iter = listParr.iterator();
			while (iter.hasNext())
			{
				ListItem listitem = (ListItem)iter.next();
				if (m_location.getParish().equals(listitem.getValue()))
				{
					lstParish.setSelectedItem(listitem);
				}
			}
		}
		else
		{
			lstParish.setSelectedItem(null);
		}        
	}
	
	/**
	 * Set Suburb
	 */
	private void setSuburb() {
		if (m_location.getSuburb() != null) {
			List<?> listSub = lstSuburb.getChildren();
			Iterator<?> iter = listSub.iterator();
			while (iter.hasNext()) {
				ListItem listitem = (ListItem)iter.next();
				if (m_location.getSuburb().equals(listitem.getValue())) {
					lstSuburb.setSelectedItem(listitem);
				}
			}
		} else {
			lstSuburb.setSelectedItem(null);
		}
	}
	
	/**
	 *  Get result
	 *  @return true, if changed
	 */
	public boolean isChanged() {
		return m_change;
	}   //  getChange
	
	/**
	 *  Get edited Value (MLocationExt)
	 *  @return location
	 */
	public MLocationExt getValue() {
		return m_location;
	}   

	public void onEvent(Event event) throws Exception
	{
		if (event.getTarget() == confirmPanel.getButton(ConfirmPanel.A_OK)) 
		{
			onSaveError = false;
			
			inOKAction = true;
			
			if (m_location.getCountryExt().isHasRegion() && lstRegion.getSelectedItem() == null) {
				if(isAutomaticPostalCode) {
					int C_City_ID = fCity.getValue()==null?0:(int)fCity.getValue();
					if (C_City_ID>0) {
						MCity city = new MCity(Env.getCtx(), C_City_ID, null);
						if (city.getC_Region_ID() > 0 && city.getC_Region_ID() != m_location.getC_Region_ID()) {
							m_location.setRegion(MRegionExt.get(Env.getCtx(), city.getC_Region_ID()));
						}
					}
				} else {
					if (txtCity.getC_Region_ID() > 0 && txtCity.getC_Region_ID() != m_location.getC_Region_ID()) {
						m_location.setRegion(MRegionExt.get(Env.getCtx(), txtCity.getC_Region_ID()));
					}
				}
				setRegion();
			}
			
			String msg = validate_OK();
			if (msg != null) {
				onSaveError = true;
				FDialog.error(0, this, "FillMandatory", Msg.parseTranslation(Env.getCtx(), msg), new Callback<Integer>() {					
					@Override
					public void onCallback(Integer result) {
						Events.echoEvent("onSaveError", WLocationExtDialog.this, null);
					}
				});
				inOKAction = false;
				return;
			}
			
			if (action_OK())
			{
				m_change = true;
				inOKAction = false;
				this.dispose();
			}
			else
			{
				onSaveError = true;
				FDialog.error(0, this, "CityNotFound", (String)null, new Callback<Integer>() {					
					@Override
					public void onCallback(Integer result) {
						Events.echoEvent("onSaveError", WLocationExtDialog.this, null);
					}
				});
			}
			inOKAction = false;
		}
		else if (event.getTarget() == confirmPanel.getButton(ConfirmPanel.A_CANCEL))
		{
			m_change = false;
			this.dispose();
		}
		else if (toLink.equals(event.getTarget()))
		{
			String urlString = MLocationExt.LOCATION_MAPS_URL_PREFIX + getFullAdress();
			String message = null;
			try {
				Executions.getCurrent().sendRedirect(urlString, "_blank");
			}
			catch (Exception e) {
				message = e.getMessage();
				FDialog.warn(0, this, "URLnotValid", message);
			}
		}
		else if (toRoute.equals(event.getTarget()))
		{
			int AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());
			if (AD_Org_ID != 0){
				MOrgInfo orgInfo = 	MOrgInfo.get(Env.getCtx(), AD_Org_ID,null);
				MLocationExt orgLocation = new MLocationExt(Env.getCtx(),orgInfo.getC_Location_ID(),null);

				String urlString = MLocationExt.LOCATION_MAPS_ROUTE_PREFIX +
						         MLocationExt.LOCATION_MAPS_SOURCE_ADDRESS + orgLocation.getMapsLocation() + //org
						         MLocationExt.LOCATION_MAPS_DESTINATION_ADDRESS + getFullAdress(); //partner
				String message = null;
				try {
					Executions.getCurrent().sendRedirect(urlString, "_blank");
				}
				catch (Exception e) {
					message = e.getMessage();
					FDialog.warn(0, this, "URLnotValid", message);
				}
			}
		}
		else if (btnOnline.equals(event.getTarget()))
		{
			btnOnline.setEnabled(false);
			
			onSaveError = false;
			
			inOKAction = true;
			
			if (m_location.getCountryExt().isHasRegion() && lstRegion.getSelectedItem() == null) {
				if(isAutomaticPostalCode) {
					int C_City_ID = fCity.getValue()==null?0:(int)fCity.getValue();
					if (C_City_ID>0) {
						MCity city = new MCity(Env.getCtx(), C_City_ID, null);
						if (city.getC_Region_ID() > 0 && city.getC_Region_ID() != m_location.getC_Region_ID()) {
							m_location.setRegion(MRegionExt.get(Env.getCtx(), city.getC_Region_ID()));
							setRegion();
						}
					}
				} else {
					if (txtCity.getC_Region_ID() > 0 && txtCity.getC_Region_ID() != m_location.getC_Region_ID()) {
						m_location.setRegion(MRegionExt.get(Env.getCtx(), txtCity.getC_Region_ID()));
						setRegion();
					}
				}
			}
			// VALIDATION MESSAGE USING FDialog Class
			String msg = validate_OK();
			if (msg != null) {
				onSaveError = true;
				FDialog.error(0, this, "FillMandatory", Msg.parseTranslation(Env.getCtx(), msg), new Callback<Integer>() {					
					@Override
					public void onCallback(Integer result) {
						Events.echoEvent("onSaveError", WLocationExtDialog.this, null);
					}
				});
				inOKAction = false;
				return;
			}
			int City_ID = 0;
			if(isAutomaticPostalCode)
				City_ID = fCity.getValue()==null?0:(int)fCity.getValue();
				
			MLocationExt m_location = new MLocationExt(Env.getCtx(), 0, null);
			m_location.setAddress1(txtAddress1.getValue());
			m_location.setAddress2(txtAddress2.getValue());
			m_location.setAddress3(txtAddress3.getValue());
			m_location.setAddress4(txtAddress4.getValue());
			if (City_ID > 0) {
				m_location.setC_City_ID(City_ID); 
				m_location.setCity(fCity.getDisplay());
			} else {
				m_location.setC_City_ID(txtCity.getC_City_ID()); 
				m_location.setCity(txtCity.getValue());
			}
			m_location.setPostal(txtPostal.getValue());
			//  Country/Region
			MCountryExt country = (MCountryExt) lstCountry.getSelectedItem().getValue();
			m_location.setCountry(country);
			if (country.isHasRegion() && lstRegion.getSelectedItem() != null)
			{
				MRegionExt r = (MRegionExt)lstRegion.getSelectedItem().getValue();
				m_location.setRegion(r);
			}
			else
			{
				m_location.setC_Region_ID(0);
			}			
				
			MAddressValidationExt validation = lstAddressValidation.getSelectedItem().getValue();
			if (validation == null && lstAddressValidation.getChildren().size() > 0)
				validation = lstAddressValidation.getItemAtIndex(0).getValue();			
			if (validation != null)
			{
				boolean ok = m_location.processOnline(validation.getC_AddressValidation_ID());
				
				txtResult.setText(m_location.getResult());
				cbxValid.setChecked(m_location.isValid());
				
				List<?> list = lstAddressValidation.getChildren();
				Iterator<?> iter = list.iterator();
				while (iter.hasNext())
				{
					ListItem listitem = (ListItem)iter.next();
					if (m_location.getC_AddressValidation().equals(listitem.getValue()))
					{
						lstAddressValidation.setSelectedItem(listitem);
						break;
					}
				}
				if (!ok)
				{
					onSaveError = true;
					FDialog.error(0, this, "Error", m_location.getErrorMessage(), new Callback<Integer>() {					
						@Override
						public void onCallback(Integer result) {
							Events.echoEvent("onSaveError", WLocationExtDialog.this, null);
						}
					});
				}
			}
			
			inOKAction = false;
			

			btnOnline.setEnabled(true);
		}
		//  Country Changed - display in new Format
		else if (lstCountry.equals(event.getTarget()))
		{
			inCountryAction = true;
			MCountryExt c = (MCountryExt)lstCountry.getSelectedItem().getValue();
			isAutomaticPostalCode = c.get_ValueAsBoolean("IsAutomaticPostalCode");
			m_location.setCountry(c);
			m_location.setC_City_ID(0);
			m_location.setCity(null);
			//  refresh
			initLocation();
			
			if (!enabledCountryList.isEmpty())
			{
				boolean isEnabled = false;
				if (c != null)
				{
					for (String enabledCountry : enabledCountryList)
					{
						if (enabledCountry.equals(c.getCountryCode().trim()))
						{
							isEnabled = true;
							break;
						}
					}
				}
				btnOnline.setEnabled(isEnabled);
			}
			
			inCountryAction = false;
			lstCountry.focus();
		}
		//  Region Changed 
		else if (lstRegion.equals(event.getTarget()))
		{
			if (inCountryAction || inOKAction)
				return;
			MRegionExt r = (MRegionExt) lstRegion.getSelectedItem().getValue();
			m_location.setC_Country_ID(r.getC_Country_ID());
			m_location.setRegion(r);
			m_location.setC_City_ID(0);
			m_location.setCity(null);
			//  refresh
			initLocation();
			lstRegion.focus();
		}
		//  Municipality Changed 
		else if (lstMunicipality.equals(event.getTarget()))
		{
			if (inCountryAction || inOKAction)
				return;
			MMunicipality m = (MMunicipality) lstMunicipality.getSelectedItem().getValue();
			m_location.setMunicipality(m);
			m_location.setC_Parish_ID(0);
			m_location.setParish(null);
			m_location.setC_Suburb_ID(0);
			m_location.setSuburb(null);
			//  refresh
			initLocation();
			lstMunicipality.focus();
		} else if(lstSuburb.equals(event.getTarget()) && isAutomaticPostalCode) {
			MSuburb sub = lstSuburb.getSelectedItem().getValue();
			if(sub != null) {
				String postal = DB.getSQLValueStringEx(null, "SELECT Postal FROM C_Suburb WHERE C_Suburb_ID = " + sub.getC_Suburb_ID());
				if(postal != null)
					txtPostal.setValue(postal);
			}
		}
		else if ("onSaveError".equals(event.getName())) {
			onSaveError = false;
			doPopup();
			focus();			
		}
	}

	// LCO - address 1, region and city required
	private String validate_OK() {
		int C_City_ID = fCity.getValue()==null?0:(int)fCity.getValue();
		String fields = "";
		if (isAddress1Mandatory && txtAddress1.getText().trim().length() == 0) {
			fields = fields + " " + "@Address1@, ";
		}
		if (isAddress2Mandatory && txtAddress2.getText().trim().length() == 0) {
			fields = fields + " " + "@Address2@, ";
		}
		if (isAddress3Mandatory && txtAddress3.getText().trim().length() == 0) {
			fields = fields + " " + "@Address3@, ";
		}
		if (isAddress4Mandatory && txtAddress4.getText().trim().length() == 0) {
			fields = fields + " " + "@Address4@, ";
		}
		if (!isAutomaticPostalCode && isCityMandatory && txtCity.getValue().trim().length() == 0) {
			fields = fields + " " + "@City@, ";
		}
		if (isAutomaticPostalCode && isCityMandatory && C_City_ID == 0) {
			fields = fields + " " + "@City@, ";
		}
		if (isParishMandatory && lstParish.getName().trim().length() == 0) {
			fields = fields + " " + "@Parish@, ";
		}
		if (isMunicipalityMandatory && lstMunicipality.getName().trim().length() == 0) {
			fields = fields + " " + "@Municipality@, ";
		}
		if (isRegionMandatory && lstRegion.getSelectedItem() == null) {
			fields = fields + " " + "@Region@, ";
		}
		if (isSuburbMandatory && lstSuburb.getName().trim().length() == 0) {
			fields = fields + " " + "@Suburb@, ";
		}
		if (isPostalMandatory && txtPostal.getText().trim().length() == 0) {
			fields = fields + " " + "@Postal@, ";
		}
		if (isPostalAddMandatory && txtPostalAdd.getText().trim().length() == 0) {
			fields = fields + " " + "@PostalAdd@, ";
		}
		
		if (fields.trim().length() > 0)
			return fields.substring(0, fields.length() -2);

		return null;
	}

	/**
	 *  OK - check for changes (save them) & Exit
	 */
	private boolean action_OK()
	{
		Trx trx = Trx.get(Trx.createTrxName("WLocationExtDialog"), true);
		if(isAutomaticPostalCode) {
			int C_City_ID = fCity.getValue()==null ? 0 : (int)fCity.getValue();
			if (C_City_ID > 0) {		
				m_location.setC_City_ID(C_City_ID); 
				m_location.setCity(fCity.getDisplay());
			}
		} else {
			m_location.setC_City_ID(txtCity.getC_City_ID()); 
			m_location.setCity(txtCity.getValue());
		}
		m_location.set_TrxName(trx.getTrxName());
		m_location.setAddress1(txtAddress1.getValue());
		m_location.setAddress2(txtAddress2.getValue());
		m_location.setAddress3(txtAddress3.getValue());
		m_location.setAddress4(txtAddress4.getValue());
		m_location.setPostal(txtPostal.getValue());
		m_location.setPostal_Add(txtPostalAdd.getValue());
		//
//log.warning("m_location.action_OK  Values..City:"+m_location.getC_City_ID()+"-"+m_location.getCity()+
//		"..Country:"+m_location.getC_Country_ID()+"..Region:"+m_location.getC_Region_ID()+
//		"..Municipality:"+m_location.getC_Municipality_ID()+"..Parish:"+m_location.getC_Parish_ID());
//log.warning(
//		"Combos.."+"  lstRegion:"+lstRegion.getItemCount()+"  lstMunicipality:"+
//		lstMunicipality.getItemCount()+"  lstParish:"+lstParish.getItemCount()+
//		"  lstRegion.getSelectedItem():"+lstRegion.getSelectedItem()+
//		"  lstParish.getSelectedItem():"+lstParish.getSelectedItem()+
//		"  lstMunicipality.getSelectedItem():"+lstMunicipality.getSelectedItem());

		//  Country/Region
		MCountryExt country = (MCountryExt)lstCountry.getSelectedItem().getValue();
		m_location.setCountry(country);
		if (country.isHasRegion() ) {
			//if (!lstRegion.getSelectedItem().equals(null)) {
			if (lstRegion.getSelectedItem() != null) {
				MRegionExt r = (MRegionExt)lstRegion.getSelectedItem().getValue();
//				MRegionExt rr = (MRegion)lstRegion.getSelectedItem().getValue();
//				// POWrapper Class
//				I_C_Region_Amerp r =  POWrapper.create(rr, I_C_Region_Amerp.class);

				m_location.setRegion(r); 
			} else {
				m_location.setC_Region_ID(0);
			}
		} else {
			m_location.setC_Region_ID(0);
		}
		// Municipality
		if (lstMunicipality.getSelectedItem() != null ) {
			MMunicipality m=(MMunicipality)lstMunicipality.getSelectedItem().getValue();
			m_location.setMunicipality(m);
		} else {
			m_location.setMunicipality(null);
		}
		// Parish
		if (lstParish.getSelectedItem() != null) {
			MParish p=(MParish)lstParish.getSelectedItem().getValue();
			m_location.setParish(p);
		} else {
			m_location.setParish(null);
		}
		// Suburb
		if (lstSuburb.getSelectedItem() != null) {
			MSuburb p = (MSuburb)lstSuburb.getSelectedItem().getValue();
			m_location.setSuburb(p);
		} else {
			m_location.setSuburb(null);
		}
		
		//Save changes 		
		boolean success = false;
		if (m_location.save()) {
            // IDEMPIERE-417 Force Update BPLocation.Name
        	if (m_GridField != null && m_GridField.getGridTab() != null
        			&& "C_BPartner_Location".equals(m_GridField.getGridTab().getTableName())
        			&& !m_GridField.getGridTab().getValueAsBoolean("IsPreserveCustomName"))
    		{
        		m_GridField.getGridTab().setValue("Name", ".");
				success = true;
    		} else {
    			//Update BP_Location name IDEMPIERE 417
    			int bplID = DB.getSQLValueEx(trx.getTrxName(), "SELECT C_BPartner_Location_ID FROM C_BPartner_Location WHERE C_Location_ID = " + m_location.getC_Location_ID());
    			if (bplID>0)
    			{
    				MBPartnerLocationExt bpl = new MBPartnerLocationExt(Env.getCtx(), bplID, trx.getTrxName());
//    				bpl.setName(bpl.getBPLocName(m_location));
    				if (bpl.save())
    					success = true;
    			} else {
					success = true;
    			}
    		}
		}
		if (success) {
			trx.commit();
		} else {
			trx.rollback();
		}
		trx.close();

		return success;
	}   //  actionOK

	public boolean isOnSaveError() {
		return onSaveError;
	}
	
	@Override
	public void dispose()
	{
		if (!m_change && m_location != null && !m_location.is_new())
		{
			m_location = new MLocationExt(m_location.getCtx(), m_location.get_ID(), null);
		}	
		super.dispose();
	}
	
	/** returns a string that contains all fields of current form */
	String getFullAdress()
	{
		MRegionExt region = null;
		MMunicipality municipality = null;
		MParish parish = null;
		MSuburb suburb = null;

		if (lstRegion.getSelectedItem()!=null)
			region = (MRegionExt)lstRegion.getSelectedItem().getValue();
		if (lstMunicipality.getSelectedItem() != null)
			municipality = (MMunicipality)lstMunicipality.getSelectedItem().getValue();
		if (lstParish.getSelectedItem() != null)
			parish = (MParish)lstParish.getSelectedItem().getValue();
		if(lstSuburb.getSelectedItem() != null)
			suburb = (MSuburb)lstSuburb.getSelectedItem().getValue();
		
		MCountryExt c = (MCountryExt)lstCountry.getSelectedItem().getValue();

		String address = "";
		address = address + (txtAddress1.getText() != null ? txtAddress1.getText() + ", " : "");
		address = address + (txtAddress2.getText() != null ? txtAddress2.getText() + ", " : "");
		if(!isAutomaticPostalCode)
			address = address + (txtCity.getText() != null ? txtCity.getText() + ", " : "");
		else {
			int C_City_ID = fCity.getValue()==null?0:(int)fCity.getValue();
			if (C_City_ID > 0)
				address = address + (C_City_ID > 0  ? fCity.getDisplay() + ", " : "");
		}
		if (parish != null)
			address = address + (parish.getName() != null ? parish.getName() + ", " : "");
		if (suburb != null)
			address = address + (suburb.getName() != null ? suburb.getName() + ", " : "");
		if (municipality != null)
			address = address + (municipality.getName() != null ? municipality.getName() + ", " : "");
		if (region != null)
			address = address + (region.getName() != null ? region.getName() + ", " : "");
		address = address + (c.getName() != null ? c.getName() : "");
		//return address.replace(" ", "+");
		return address;
	}
	
	@Override
	public void valueChange(ValueChangeEvent evt) {
		String postal = null;
		 if (evt.getSource().equals(fCity) && postal == null) {
			 if (evt.getNewValue()!=null) {
				 postal = DB.getSQLValueStringEx(null, "SELECT Postal FROM C_City WHERE C_City_ID="+evt.getNewValue());
			 }
		 }
		 if(postal != null)
			 txtPostal.setValue(postal);
	}	

}
