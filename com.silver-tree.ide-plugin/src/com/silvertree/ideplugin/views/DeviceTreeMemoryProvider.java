package com.silvertree.ideplugin.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.silvertree.ideplugin.common.DeviceTreeRoot;
import com.silvertree.ideplugin.common.Token;

public class DeviceTreeMemoryProvider implements IStructuredContentProvider {

	private DeviceTreeRoot invisibleRoot;
	private String _content;
	
	public DeviceTreeMemoryProvider(String content) {
		_content = content;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		if (invisibleRoot==null && _content != null) {
			try {
				Token tok = new Token(_content, 0, _content.length(), 0, Token.TokenType.TREE);
				invisibleRoot = DeviceTreeRoot.create(tok);
				return invisibleRoot.getAllSubTreesRec(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new Object[0];
	}

}
