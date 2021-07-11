package com.silvertree.ideplugin.common;

import org.eclipse.core.runtime.IAdaptable;

public abstract class DeviceTreeObject implements IAdaptable{
	
	private static int deviceTreeCount = 0;
	private int _deviceTreeId;
	private String _key;
	private String _value;
	private DeviceTree parent = null;
	
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
	

	public void setParent(DeviceTree parent) {
		this.parent = parent;
	}
	
	public DeviceTree getParent() {
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
	
	public String getPath() {
		String path = getKey();
		try {
			if (hasParent()) {
				String parentPath = getParent().getPath();
				path = parentPath + "-" + path;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DeviceTreeObject) {
			if (((DeviceTreeObject) obj).getPath().contentEquals(getPath()))
				return true;
			return false;
		}else {
			return super.equals(obj);	
		}
		
	}
	
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}



}
