package com.silvertree.ideplugin.common;

public class DeviceTreeRoot extends DeviceTree {

	public DeviceTreeRoot(String text) throws Exception {
		super(text);
		setName("root");
		if (text != null) {
			setFromOffset(0);
			setToOffset(text.length());
		}
	}

}
