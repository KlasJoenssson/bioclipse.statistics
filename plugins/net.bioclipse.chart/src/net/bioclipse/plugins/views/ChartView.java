package net.bioclipse.plugins.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.chart.ChartUtils;
import net.bioclipse.model.ChartSelection;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;



/**
 * ChartView displays different charts generated with ChartUtils
 * @see ChartUtils
 */

public class ChartView extends ViewPart implements ISelectionListener, ISelectionProvider {
	private Action saveImageActionSVG,saveImageActionPNG,saveImageActionJPG;
	private Composite parent;
	private Label imageLabel;
	private List<ISelectionChangedListener> selectionListeners;
	private ChartSelection selection;
	private ChartView view;
	private static final Logger logger = Logger.getLogger(ChartView.class);

	/**
	 * The constructor.
	 */
	public ChartView() {
		super();
		selectionListeners = new ArrayList<ISelectionChangedListener>();
		view = this;
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		this.parent = parent;
		getSite().setSelectionProvider(this);
		getSite().getPage().addSelectionListener(this);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ChartView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(view.getParent());
		view.getParent().setMenu(menu);
		getSite().registerContextMenu(menuMgr, view);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		addActions(manager);
	}

	private void fillContextMenu(IMenuManager manager) {
		addActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		addActions(manager);
	}
	
	private void addActions( IContributionManager manager )
	{
		manager.add(saveImageActionSVG);
		manager.add(saveImageActionPNG);
		manager.add(saveImageActionJPG);
		manager.add(new Separator());
	}

	private void makeActions() {
		
		//Create action for saving charts in SVG format
		saveImageActionSVG = new Action() {
			public void run()
			{
				FileDialog dialog = 
					new FileDialog(view.getViewSite().getWorkbenchWindow().getShell(),
						SWT.SAVE);
				dialog.setFileName("Image.svg");
				String path = dialog.open();
				System.out.println(path);
				ChartUtils.saveImageSVG(path);
			}
		};
		saveImageActionSVG.setText("Export as SVG Image");
		saveImageActionSVG.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		saveImageActionSVG.setToolTipText("Export the chart as a SVG image");
		
		//Create action for saving charts in png format
		saveImageActionPNG = new Action(){
			public void run()
			{
				FileDialog dialog = 
					new FileDialog(view.getViewSite().getWorkbenchWindow().getShell(),
						SWT.SAVE);
				dialog.setFileName("Image.png");
				String path = dialog.open();
				System.out.println(path);
				try {
					ChartUtils.saveImagePNG(path);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Failed to save chart as png " + e);
				}
			}
		};
		saveImageActionPNG.setText("Export as PNG Image");
		saveImageActionPNG.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		saveImageActionPNG.setToolTipText("Export the chart as a PNG image");
		
		//Create action for saving charts in JPEG format
		saveImageActionJPG = new Action(){
			public void run()
			{
				FileDialog dialog = 
					new FileDialog(view.getViewSite().getWorkbenchWindow().getShell(),
						SWT.SAVE);
				dialog.setFileName("Image.jpg");
				String path = dialog.open();
				System.out.println(path);
				try {
					ChartUtils.saveImageJPG(path);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Failed to save chart as jpg " + e);
				}
			}
		};
		saveImageActionJPG.setText("Export as JPG Image");
		saveImageActionJPG.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		saveImageActionJPG.setToolTipText("Export the chart as a JPG image");
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		parent.setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
	{
	}

	public Composite getParent() {
		return parent;
	}

	public Label getImageLabel() {
		return imageLabel;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if(!selectionListeners.contains(listener))
		{
			selectionListeners.add(listener);
		}
	}

	public ISelection getSelection() {
		return selection;
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		if(selectionListeners.contains(listener))
			selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		
		this.getSite().getWorkbenchWindow().getWorkbench().getDisplay().asyncExec(new Runnable() {

			public void run() {
				view.getSite().getPage().activate(view);
			}
			
		});
		
		this.selection = (ChartSelection)selection;
		java.util.Iterator<ISelectionChangedListener> iter = selectionListeners.iterator();
		while( iter.hasNext() )
		{
			final ISelectionChangedListener listener = iter.next();
			final SelectionChangedEvent e = new SelectionChangedEvent(this, this.selection);
			//Does SWT stuff so this has to be called on SWT's thread
			this.getSite().getShell().getDisplay().asyncExec(new Runnable() {

				public void run() {
					listener.selectionChanged(e);
				}
				
			});
			
		}
	}
}