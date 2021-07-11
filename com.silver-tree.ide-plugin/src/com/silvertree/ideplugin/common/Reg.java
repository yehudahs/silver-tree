package com.silvertree.ideplugin.common;

public class Reg {
	
	private String _address;
	private String _length;

	public Reg(String[] allRegs, int addr_cells_from, int addr_cells_to, int size_cells_from,
			int size_cells_to) {
		
		_address = "";
		while(addr_cells_from <= addr_cells_to) {
			_address += allRegs[addr_cells_from].replace("0x", "");
			addr_cells_from++;
		}
		_address = "0x" + _address;
		
		_length = "";
		while (size_cells_from <= size_cells_to) {
			_length += allRegs[size_cells_from].replace("0x", "");
			size_cells_from++;
		}
		_length = "0x" + _length;
	}

	public String get_address() {
		return _address;
	}

	public String get_length() {
		return _length;
	}
}
