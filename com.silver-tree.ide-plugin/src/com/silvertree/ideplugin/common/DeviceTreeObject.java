package com.silvertree.ideplugin.common;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;

public abstract class DeviceTreeObject implements IAdaptable{
	
	private static int deviceTreeCount = 0;
	private int _deviceTreeId;
	private String _key;
	private String _value;
	private DeviceTreeObject parent = null;
	private ArrayList<Integer> _regs;
	private int _addressCells = -1;
	private int _sizeCells = -1;
	
	private Token _token;
	
	public abstract void parse() throws Exception;
	public abstract String dump(int ident);
	public abstract boolean isVisible();
	
	public DeviceTreeObject() {
		_deviceTreeId = deviceTreeCount;
		deviceTreeCount++;
	}
	
	public int getID() {
		return _deviceTreeId;
	}
	
	public String getKey() {
		return _key;
	}
	
	public void setKey(String name) {
		_key = null;
		if (name != null)
			_key = name.trim();
	}
	

	public void setParent(DeviceTreeObject parent) {
		this.parent = parent;
	}
	
	public DeviceTreeObject getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return true;
	}
	
	@Override
	public String toString() {
		String res = getKey();
		if (getValue() != null) {
			res += ":" + getValue();
		}
		return res;
	}
	
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the _addressCells
	 */
	public int getAddressCells() {
		return _addressCells;
	}

	/**
	 * @param _addressCells the _addressCells to set
	 */
	public void setAddressCells(int _addressCells) {
		this._addressCells = _addressCells;
	}

	public Boolean isAddressCellsExists() {
		return _addressCells != -1;
	}
	
	/**
	 * @return the _sizeCells
	 */
	public int getSizeCells() {
		return _sizeCells;
	}

	/**
	 * @param _sizeCells the _sizeCells to set
	 */
	public void setSizeCells(int _sizeCells) {
		this._sizeCells = _sizeCells;
	}
	
	public Boolean isSizeCellsExists() {
		return _sizeCells != -1;
	}

	/**
	 * @return the _regs
	 */
	public ArrayList<Integer> getRegs() {
		return _regs;
	}

	/**
	 * @param _regs the _regs to set
	 */
	public void setRegs(ArrayList<Integer> _regs) {
		this._regs = _regs;
	}

	/**
	 * @return the _token
	 */
	public Token getToken() {
		return _token;
	}

	/**
	 * @param _token the _token to set
	 */
	public void setToken(Token _token) {
		this._token = _token;
	}

	/**
	 * @return the _value
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * @param _value the _value to set
	 */
	public void setValue(String _value) {
		this._value = _value;
	}
	
	protected String identString(int ident) {
		var identString = "";
		for(int i = 0; i < ident; i++) {
			identString += '\t';
		}
		return identString;
	}
	
	public int getOffsetInEditor() {
		int offsetInEditor = getToken().getFromOffset();
		if (hasParent()) {
			offsetInEditor += getParent().getOffsetInEditor();
		}
		return offsetInEditor;
	}

}
