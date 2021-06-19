package com.silvertree.ideplugin.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class DeviceTreeEditor extends TextEditor {

	public static final String ID = "com.silvertree.ideplugin.editors.DeviceTreeEditor"; //$NON-NLS-1$
	private ColorManager colorManager;

	public DeviceTreeEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
