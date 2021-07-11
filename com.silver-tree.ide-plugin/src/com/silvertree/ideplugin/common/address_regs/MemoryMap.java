package com.silvertree.ideplugin.common.address_regs;

public class MemoryMap extends Reg{

	private String _length;
	
	public MemoryMap(String[] allRegs, int addr_cells_from, int addr_cells_to, int size_cells_from,
			int size_cells_to) {
		
		super("");
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
	
	public String get_length() {
		return _length;
	}
}
