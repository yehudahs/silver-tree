package com.silvertree.ideplugin.common;

import java.util.ArrayList;

import com.silvertree.ideplugin.common.address_regs.MemoryMapReg;

public class DeviceTreeMemory {
	private DeviceTreeRoot _root;

	public DeviceTreeMemory(Token tok) {
		try {
			_root = DeviceTreeRoot.create(tok);

			setMemoryTree();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMemoryTree() throws Exception {
		DeviceTree[] trees = _root.getAllSubTreesRec(false);
		DeviceTree memTree = null;
		for (DeviceTree tree : trees) {
			if (isMemoryTree(tree)) {
				memTree = tree;
				break;
			}
		}
		if (memTree == null) {
			System.out.println("could not find memory node:\n" + _root.getToken().toString());
			throw new Exception("could not find memory node");
		}

	}

	public MemoryMapReg[] getMemoryMapRegRec() {
		ArrayList<MemoryMapReg> memRegs = new ArrayList<MemoryMapReg>();
		for (DeviceTreeObject child : _root.getAllSubTreesRec(false)) {
			if (child instanceof DeviceTree && ((DeviceTree) child).hasMemoryMapReg()
					&& !isMemoryTree((DeviceTree) child)) {
				memRegs.addAll(((DeviceTree) child).getMemoryMapReg());
			}
		}
		return (MemoryMapReg[]) memRegs.toArray(new MemoryMapReg[memRegs.size()]);
	}

	private boolean isMemoryTree(DeviceTree tree) {
		if (tree.getKey().contentEquals("memory") || tree.getKey().startsWith("memory@")) {
			return true;
		}
		return false;
	}

}
