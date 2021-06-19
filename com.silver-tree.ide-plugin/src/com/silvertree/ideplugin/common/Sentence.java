package com.silvertree.ideplugin.common;

public class Sentence extends DeviceTreeObject {
	
	private String sentence;
	private String _value;
	DeviceTreeType _type;
	
	
	public Sentence(String text) {
		sentence = text;
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
		
		int isKeyValue = sentence.indexOf("=");
		if (isKeyValue == -1) {
			// we found an attribute sentence.
			_type = DeviceTreeType.SINGLE_LINE_ATTRIBUTE;
			setName(sentence);
			_value = null;
		}else {
			// we found a key value sentence.
			setName(sentence.substring(0, isKeyValue));
			_value = sentence.substring(isKeyValue, sentence.length());
			int isKeyString = sentence.indexOf("\"", isKeyValue);
			if (isKeyString == -1) {
				_type = DeviceTreeType.KEY_INT_ATTRIBUTE;
			}else {
				_type = DeviceTreeType.KEY_STRING_ATTRIBUTE;
			}
		}
	}
}
