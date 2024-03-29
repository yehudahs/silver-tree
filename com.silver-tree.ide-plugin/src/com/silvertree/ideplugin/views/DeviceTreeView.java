package com.silvertree.ideplugin.views;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import silver.leaves.tree.*;
import com.silvertree.ideplugin.editors.DeviceTreeEditor;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import javax.inject.Inject;


/**
 * device tree view
 */

public class DeviceTreeView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.quilt.ideplugin.views.DeviceTreeView";

	@Inject IWorkbench workbench;
	
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
//	private Action action1;
//	private Action action2;
	private Action doubleClickAction;
	private IWorkbenchPage page; 
	String currEditorName;
	 
	class DeviceTreeLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof DeviceTree)
			   imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return workbench.getSharedImages().getImage(imageKey);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setUseHashlookup(true);
		drillDownAdapter = new DrillDownAdapter(viewer);
		String editorContent = null;
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (workbenchPart != null) {
			page = workbenchPart.getSite().getPage();
			currEditorName = page.getActiveEditor().getEditorInput().getName();
			editorContent = getDeviceTreeEditorContent();
		}
		viewer.setContentProvider(new DeviceTreeContentProvider(editorContent));
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new DeviceTreeLabelProvider());

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "com.quilt.ideplugin.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		createResourceListner();
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
					viewer.setContentProvider(new DeviceTreeContentProvider(editorContent));
					return;			
				}
			}

		});
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
	
	private void createResourceListner() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		   IResourceChangeListener listener = new IResourceChangeListener() {
		      public void resourceChanged(IResourceChangeEvent event) {
		    	  if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
					  String editorContent = getDeviceTreeEditorContent();
		    		  viewer.setContentProvider(new DeviceTreeContentProvider(editorContent));
		    	  }
		      }
		   };
		   workspace.addResourceChangeListener(listener, IResourceChangeEvent.PRE_CLOSE | 
				   IResourceChangeEvent.PRE_DELETE | 
				   IResourceChangeEvent.POST_CHANGE |
				   IResourceChangeEvent.PRE_REFRESH |
				   IResourceChangeEvent.PRE_DELETE);
		   
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				DeviceTreeView.this.fillContextMenu(manager);
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
//		manager.add(action1);
		manager.add(new Separator());
//		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
//		manager.add(action1);
//		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
//		action1 = new Action() {
//			public void run() {
//				showMessage("Action 1 executed");
//			}
//		};
//		action1.setText("Action 1");
//		action1.setToolTipText("Action 1 tooltip");
//		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
//		
//		action2 = new Action() {
//			public void run() {
//				showMessage("Action 2 executed");
//			}
//		};
//		action2.setText("Action 2");
//		action2.setToolTipText("Action 2 tooltip");
//		action2.setImageDescriptor(workbench.getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				navigateToLine((((DeviceTreeObject)obj).getOffsetInEditor()), ((DeviceTreeObject)obj).getToken().toString().length());
			}
		};
	}
	
	public void navigateToLine(int offset, int length){
		if (page != null) {
			IFile file = (IFile) page.getActiveEditor().getEditorInput().getAdapter(IFile.class);
			try {
				IEditorPart editor = (IEditorPart) IDE.openEditor(page, file);
				((AbstractTextEditor) editor).selectAndReveal(offset, length);
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			"Device Tree View",
			message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
