package com.silvertree.ideplugin.views;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.silvertree.ideplugin.common.DeviceTree;
import com.silvertree.ideplugin.common.DeviceTreeRoot;
import com.silvertree.ideplugin.common.Token;

public class DeviceTreeContentProvider implements ITreeContentProvider {

	private DeviceTreeRoot invisibleRoot;
	private String _content;
	
	public DeviceTreeContentProvider(String content) {
		_content = content;
	}

	public Object[] getElements(Object parent) {
		if (invisibleRoot==null && _content != null) {
			try {
				Token tok = new Token(_content, 0, _content.length(), 0, Token.TokenType.TREE);
				invisibleRoot = DeviceTreeRoot.create(tok);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return getChildren(invisibleRoot);
	}
	public Object getParent(Object child) {
		if (child instanceof DeviceTree) {
			return ((DeviceTree)child).getParent();
		}
		return null;
	}
	public Object [] getChildren(Object parent) {
		if (parent instanceof DeviceTree) {
			return ((DeviceTree)parent).getChildren(true);
		}
		return new Object[0];
	}
	public boolean hasChildren(Object parent) {
		if (parent instanceof DeviceTree)
			return ((DeviceTree)parent).hasChildren();
		return false;
	}

}
