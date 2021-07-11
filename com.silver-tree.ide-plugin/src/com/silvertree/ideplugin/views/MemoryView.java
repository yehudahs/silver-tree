package com.silvertree.ideplugin.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import com.silvertree.ideplugin.common.DeviceTree;
import com.silvertree.ideplugin.editors.DeviceTreeEditor;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class MemoryView extends ViewPart {
	@Inject IWorkbench workbench;
	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	private IWorkbenchPage page; 
	String currEditorName;
	 

//	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
//		@Override
//		public String getColumnText(Object obj, int index) {
//			return getText(obj);
//		}
//		@Override
//		public Image getColumnImage(Object obj, int index) {
//			return getImage(obj);
//		}
//		@Override
//		public Image getImage(Object obj) {
//			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
//		}
//	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		createColumns(viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        String editorContent = null;
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (workbenchPart != null) {
			page = workbenchPart.getSite().getPage();
			currEditorName = page.getActiveEditor().getEditorInput().getName();
			editorContent = getDeviceTreeEditorContent();
		}
//		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setContentProvider(new DeviceTreeMemoryProvider(editorContent));
//		viewer.setInput(new String[] { "One", "Two", "Three" });
		viewer.setInput(new DeviceTreeMemoryProvider(editorContent));
//		viewer.setLabelProvider(new ViewLabelProvider());
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		workbench.getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener2() {
			
			public void partActivated(IWorkbenchPartReference partRef) {
				if (partRef.getId().equals(DeviceTreeEditor.ID)) {
					
					// check if the editor is showing the same page, in that case just need to refresh.
					if (page != null) {
						String activatedEditorName = partRef.getPage().getActiveEditor().getEditorInput().getName();
						if (currEditorName.contentEquals(activatedEditorName)) {
							viewer.refresh();
							return;
						}
					}
					
					
					// either the page is opened in the first time (page==null)
					// or we opened a new file
					// in that case need to set a new content to the tree.
					page = partRef.getPage();
					currEditorName = page.getActiveEditor().getEditorInput().getName();
					String editorContent = getDeviceTreeEditorContent();
					viewer.setContentProvider(new DeviceTreeMemoryProvider(editorContent));
					return;			
				}
			}

		});
	}

	private void createColumns(TableViewer viewer) {
		String[] titles = { "Address", "Node Name" };
        int[] bounds = { 100, 100 };

        // First column is for the first name
        TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object treeNode) {
            	if (treeNode instanceof DeviceTree) {
            		return ((DeviceTree) treeNode).getKey();
            	}else {
            		return "";
            	}
            }
        });

        // Second column is for the last name
        col = createTableViewerColumn(titles[1], bounds[1], 1);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object treeNode) {
            	if (treeNode instanceof DeviceTree) {
            		return ((DeviceTree) treeNode).getKey();
            	}else {
            		return "";
            	}
            }
        });
		
	}
	
    private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
                SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;

    }
    
	public String getDeviceTreeEditorContent() {
		InputStream inputStream;
		try {
			if (page == null) {
				return null;
			}
			IFile file = (IFile) page.getActiveEditor().getEditorInput().getAdapter(IFile.class);
			if (file == null) {
				return null;
			}
			inputStream = file.getContents();
			StringBuilder textBuilder = new StringBuilder();
			Reader reader = new BufferedReader(new InputStreamReader (inputStream, Charset.forName(StandardCharsets.UTF_8.name())));
	    	int c = 0;
		    while ((c = reader.read()) != -1) {
		    	textBuilder.append((char) c);
		    }
		    String fileContent = textBuilder.toString();
		    return fileContent;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MemoryView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(workbench.getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Memory View",
			message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
