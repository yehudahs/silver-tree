package com.silvertree.ideplugin.common;

import com.silvertree.ideplugin.common.Token.AttributeType;
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
		// this needs to be first
		parseAttributeType();
		
		setKeyAndVal();
		
		switch (getToken().getAttrType()) {
		case ADDRESS_CELLS_ATTRIBUTE:
			String addrCell = getValue().substring(getValue().indexOf("<")+1, getValue().indexOf(">")).trim();
			setValue(addrCell);
			break;
		case SIZE_CELLS_ATTRIBUTE:
			String sizeCells = getValue().substring(getValue().indexOf("<")+1, getValue().indexOf(">")).trim();
			setValue(sizeCells);
			break;
		case REG_ATTRIBUTE:
			String regs = getValue().substring(getValue().indexOf("<")+1, getValue().indexOf(">")).trim();
			setValue(regs);
			break;
		default:
			break;
		}
	}

	private void setKeyAndVal() {
		if (getToken().getAttrType() == AttributeType.SINGLE_LINE_ATTRIBUTE ||
				getToken().getAttrType() == AttributeType.NONE) {
			setKey(getToken().toString());
			setValue(null);
		}else {
			int equalIndex = getToken().toString().indexOf("=");
			setValue(getToken().toString().substring(equalIndex + 1, getToken().toString().length()));
			setKey(getToken().toString().substring(0, equalIndex));
		}
	}
	
	private void parseAttributeType() {
		Token tok = getToken();
		String tokStr = tok.toString();
		if (tokStr.indexOf("=") == -1) {
			tok.setAttrType(AttributeType.SINGLE_LINE_ATTRIBUTE);
		}else if (tokStr.startsWith("#address-cells")) {
			tok.setAttrType(AttributeType.ADDRESS_CELLS_ATTRIBUTE);
		}else if(tokStr.startsWith("#size-cells")) {
			tok.setAttrType(AttributeType.SIZE_CELLS_ATTRIBUTE);
		}else if(tokStr.startsWith("reg ") || tokStr.startsWith("reg:")) {
			tok.setAttrType(AttributeType.REG_ATTRIBUTE);
		}else {
			tok.setAttrType(AttributeType.NONE);
		}
	}
	
	public void setParent(DeviceTree parent) {
		super.setParent(parent);
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
