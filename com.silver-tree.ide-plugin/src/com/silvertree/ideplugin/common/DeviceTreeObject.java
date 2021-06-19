package com.silvertree.ideplugin.common;

import org.eclipse.core.runtime.IAdaptable;

abstract class DeviceTreeObject implements IAdaptable{
	
	private String _name;
	private int _fromOffset;
	private int _toOffset;
	private DeviceTreeObject parent;
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
		return _fromOffset;
	}
	
	public void setFromOffset(int fromOffset) {
		_fromOffset = fromOffset;
	}
	
	public int getToOffset() {
		return _toOffset;
	}
	
	public void setToOffset(int toOffset) {
		_toOffset = toOffset;
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

}
