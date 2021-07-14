package com.silvertree.ideplugin.views;

import java.util.Arrays;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.silvertree.ideplugin.common.DeviceTreeMemory;
import com.silvertree.ideplugin.common.Token;
import com.silvertree.ideplugin.common.address_regs.MemoryMapReg;

public class DeviceTreeMemoryProvider implements IStructuredContentProvider {

	private DeviceTreeMemory memoryProvider;
	private String _content;
	
	public DeviceTreeMemoryProvider(String content) {
		_content = content;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		if (memoryProvider==null && _content != null) {
			try {
				Token tok = new Token(_content, 0, _content.length(), 0, Token.TokenType.TREE);
				memoryProvider = new DeviceTreeMemory(tok);
				MemoryMapReg[] memRegs = memoryProvider.getMemoryMapRegRec();
				Arrays.sort(memRegs);
				return memRegs;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new Object[0];
	}

}
