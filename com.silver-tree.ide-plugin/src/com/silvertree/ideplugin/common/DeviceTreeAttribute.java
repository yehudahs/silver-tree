package com.silvertree.ideplugin.common;

import com.silvertree.ideplugin.common.Token.TokenType;

public class DeviceTreeAttribute extends DeviceTreeObject {
	
	public DeviceTreeAttribute(Token tok) {
		super();
		setToken(tok);
		try {
			parse();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error parsing:\n" + tok);
		}
	}
	
	public void parse() {
		//there are 2 kinds of Sentences:
		//1. key-value
		//2. includes
		
		int isKeyValue = getToken().toString().indexOf("=");
		if (isKeyValue == -1) {
			// we found an attribute sentence.
			getToken().setAttrType(Token.AttributeType.SINGLE_LINE_ATTRIBUTE);
			setKey(getToken().toString());
			setValue(null);
		}else {
			// we found a key value sentence.
			setValue(getToken().toString().substring(isKeyValue + 1, getToken().toString().length()));
			setKey(getToken().toString().substring(0, isKeyValue));

			int isKeyString = getKey().indexOf("\"", isKeyValue);
			if (isKeyString == -1) {
				getToken().setAttrType(Token.AttributeType.KEY_INT_ATTRIBUTE);
			}else {
				getToken().setAttrType(Token.AttributeType.KEY_STRING_ATTRIBUTE);
			}
			
			if (getKey().contains("#address-cells")) {
				int addrCell = Integer.decode(getValue().substring(getValue().indexOf("<")+1, getValue().indexOf(">")).trim());
				setAddressCells(addrCell);
			}else if(getKey().contains("#size-cells")) {
				int sizeCells = Integer.decode(getValue().substring(getValue().indexOf("<")+1, getValue().indexOf(">")).trim());
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
	public String dump(int ident) {
		if (getToken().getType() == Token.TokenType.COMMENT)
			return null;
		
		var identString = identString(ident);
		var dumpString = identString + getKey();
		if (getValue() != null)
			dumpString += " = " + getValue();
		
		return dumpString + "\n";
	}

	@Override
	public boolean isVisible() {
		if(getToken().getType() == TokenType.COMMENT)
			return false;
		return true;
	}
}
