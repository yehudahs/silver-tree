package com.silvertree.ideplugin.common;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;

abstract class DeviceTreeObject implements IAdaptable{
	
	private String _name;
	private int _fromTextOffset;
	private int _toTextOffset;
	private DeviceTreeObject parent = null;
	private ArrayList<Integer> _regs;
	private int _addressCells = -1;
	private int _sizeCells = -1;
	
	enum DeviceTreeType{
		NONE,
		TREE,
		SINGLE_LINE_ATTRIBUTE,
		KEY_STRING_ATTRIBUTE,
		KEY_INT_ATTRIBUTE,
		INCLUDE,
		
	}
	DeviceTreeType _type;
	
	public abstract void parse() throws Exception;
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = null;
		if (name != null)
			_name = name.trim();
	}
	
	public int getFromOffset() {
		return _fromTextOffset;
	}
	
	public void setFromOffset(int fromOffset) {
		_fromTextOffset = fromOffset;
	}
	
	public int getToOffset() {
		return _toTextOffset;
	}
	
	public void setToOffset(int toOffset) {
		_toTextOffset = toOffset;
	}
	
	DeviceTreeType getType() {
		return _type;
	}
	
	void setType(DeviceTreeType type) {
		_type = type;
	}
	
	public void setParent(DeviceTreeObject parent) {
		this.parent = parent;
	}
	
	public DeviceTreeObject getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return getName();
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

}
