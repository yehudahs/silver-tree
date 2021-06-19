package com.silvertree.ideplugin.common;

public class DeviceTreeAttribute extends DeviceTreeObject {
	
	private String _attributeOrigText;
	private String _key;
	private String _value;
	DeviceTreeType _type;
	
	
	public DeviceTreeAttribute(String text) {
		_attributeOrigText = text;
		setName(null);
		_value = null;
		setFromOffset(-1);
		setToOffset(-1);
		setType(DeviceTreeType.NONE);
		parse();
	}
	
	public void parse() {
		//there are 2 kinds of Sentences:
		//1. key-value
		//2. includes
		
		int isKeyValue = _attributeOrigText.indexOf("=");
		if (isKeyValue == -1) {
			// we found an attribute sentence.
			_type = DeviceTreeType.SINGLE_LINE_ATTRIBUTE;
			setName(_attributeOrigText);
			_value = null;
		}else {
			// we found a key value sentence.
			_value = _attributeOrigText.substring(isKeyValue, _attributeOrigText.length());
			_key = _attributeOrigText.substring(0, isKeyValue);
			String attr = _key + ":" + _value;
			setName(attr);
			int isKeyString = _attributeOrigText.indexOf("\"", isKeyValue);
			if (isKeyString == -1) {
				_type = DeviceTreeType.KEY_INT_ATTRIBUTE;
			}else {
				_type = DeviceTreeType.KEY_STRING_ATTRIBUTE;
			}
			
			if (_key.contains("#address-cells")) {
				int addrCell = Integer.parseInt(_value.substring(_value.indexOf("<")+1, _value.indexOf(">")));
				setAddressCells(addrCell);
			}else if(_key.contains("#size-cells")) {
				int sizeCells = Integer.parseInt(_value.substring(_value.indexOf("<")+1, _value.indexOf(">")));
				setSizeCells(sizeCells);
			}
		}
	}
	
	public void setParent(DeviceTreeObject parent) {
		super.setParent(parent);
		if (isAddressCellsExists()) {
			getParent().setAddressCells(getAddressCells());
		}
		
		if (isSizeCellsExists()) {
			getParent().setSizeCells(getSizeCells());
		}
	}
}
