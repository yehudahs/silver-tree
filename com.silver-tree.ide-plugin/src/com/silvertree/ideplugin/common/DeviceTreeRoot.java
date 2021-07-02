package com.silvertree.ideplugin.common;

public class DeviceTreeRoot extends DeviceTree {

	public DeviceTreeRoot(Token tok) throws Exception {
		super(tok);
	}
	
	@Override
	public String dump(int ident) {
        var dumpString = "";
		for (DeviceTreeObject ob : children) {
			var child = ob.dump(ident);
			if (child != null)
				dumpString += child;
		}
		return dumpString;
    }
	
	public boolean hasParent() {
		return false;
	}
}
