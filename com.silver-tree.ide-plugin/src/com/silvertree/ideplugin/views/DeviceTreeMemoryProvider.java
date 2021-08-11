package com.silvertree.ideplugin.views;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import silver.leaves.regs.MemoryMapReg;
import silver.leaves.tree.DeviceTreeMemory;
import silver.leaves.tree.Token;

public class DeviceTreeMemoryProvider implements IStructuredContentProvider {
	
	public class Address {
		String _address;
		String _name;
		
		public Address(String address, String name) {
			_address = address;
			_name = name;
		}

		public String get_address() {
			return _address;
		}

		public String get_name() {
			return _name;
		}

	}

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
				Address[] mem_ranges = generateMemoryMap();
				return mem_ranges;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new Object[0];
	}
	
	private Address[] generateMemoryMap() {
		MemoryMapReg[] device_tree_mem_map = memoryProvider.getDeviceTreeMemoryMapRegRec();
		Arrays.sort(device_tree_mem_map);
		ArrayList<Address> mem_ranges = new ArrayList<Address>();
		for (MemoryMapReg mem_range: device_tree_mem_map) {
			mem_ranges.add(new Address(mem_range.get_address(), mem_range.get_name()));
			mem_ranges.add(new Address(mem_range.get_end_address(), mem_range.get_name()));
		}
		return (Address[]) mem_ranges.toArray(new Address[mem_ranges.size()]);
	}

}
