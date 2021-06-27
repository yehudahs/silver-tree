package com.silvertree.ideplugin.common;

public class DeviceTreeAttribute extends DeviceTreeObject {
	
	public DeviceTreeAttribute(Token tok) {
		setToken(tok);
		parse();
	}
	
	public void parse() {
		//there are 2 kinds of Sentences:
		//1. key-value
		//2. includes
		
		int isKeyValue = getToken().toString().indexOf("=");
		if (isKeyValue == -1) {
			// we found an attribute sentence.
			getToken().setAttrType(Token.AttributeType.SINGLE_LINE_ATTRIBUTE);
			setValue(null);
		}else {
			// we found a key value sentence.
			setValue(getToken().toString().substring(isKeyValue, getToken().toString().length()));
			setKey(getToken().toString().substring(0, isKeyValue));

			int isKeyString = getKey().indexOf("\"", isKeyValue);
			if (isKeyString == -1) {
				getToken().setAttrType(Token.AttributeType.KEY_INT_ATTRIBUTE);
			}else {
				getToken().setAttrType(Token.AttributeType.KEY_STRING_ATTRIBUTE);
			}
			
			if (getKey().contains("#address-cells")) {
				int addrCell = Integer.parseInt(getValue().substring(getValue().indexOf("<")+1, getValue().indexOf(">")));
				setAddressCells(addrCell);
			}else if(getKey().contains("#size-cells")) {
				int sizeCells = Integer.parseInt(getValue().substring(getValue().indexOf("<")+1, getValue().indexOf(">")));
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

	@Override
	public String dump() {
		if (getToken().getType() == Token.TokenType.COMMENT)
			return null;
		
		var dumpString = getKey();
		if (getValue() != null)
			dumpString += " = " + getValue();
		
		return dumpString;
	}
}
