/*
The contents of this file are subject to the Common Public Attribution License 
Version 1.0 (the "License"); you may not use this file except in compliance with 
the License. You may obtain a copy of the License at 
http://www.projity.com/license . The License is based on the Mozilla Public 
License Version 1.1 but Sections 14 and 15 have been added to cover use of 
software over a computer network and provide for limited attribution for the 
Original Developer. In addition, Exhibit A has been modified to be consistent 
with Exhibit B.

Software distributed under the License is distributed on an "AS IS" basis, 
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the 
specific language governing rights and limitations under the License. The 
Original Code is OpenProj. The Original Developer is the Initial Developer and 
is Projity, Inc. All portions of the code written by Projity are Copyright (c) 
2006, 2007. All Rights Reserved. Contributors Projity, Inc.

Alternatively, the contents of this file may be used under the terms of the 
Projity End-User License Agreeement (the Projity License), in which case the 
provisions of the Projity License are applicable instead of those above. If you 
wish to allow use of your version of this file only under the terms of the 
Projity License and not to allow others to use your version of this file under 
the CPAL, indicate your decision by deleting the provisions above and replace 
them with the notice and other provisions required by the Projity  License. If 
you do not delete the provisions above, a recipient may use your version of this 
file under either the CPAL or the Projity License.

[NOTE: The text of this license may differ slightly from the text of the notices 
in Exhibits A and B of the license at http://www.projity.com/license. You should 
use the latest text at http://www.projity.com/license for your modifications.
You may not remove this license text from the source files.]

Attribution Information: Attribution Copyright Notice: Copyright � 2006, 2007 
Projity, Inc. Attribution Phrase (not exceeding 10 words): Powered by OpenProj, 
an open source solution from Projity. Attribution URL: http://www.projity.com 
Graphic Image as provided in the Covered Code as file:  openproj_logo.png with 
alternatives listed on http://www.projity.com/logo

Display of Attribution Information is required in Larger Works which are defined 
in the CPAL as a work which combines Covered Code or portions thereof with code 
not governed by the terms of the CPAL. However, in addition to the other notice 
obligations, all copies of the Covered Code in Executable and Source Code form 
distributed must, as a form of attribution of the original author, include on 
each user interface screen the "OpenProj" logo visible to all users.  The 
OpenProj logo should be located horizontally aligned with the menu bar and left 
justified on the top left of the screen adjacent to the File menu.  The logo 
must be at least 100 x 25 pixels.  When users click on the "OpenProj" logo it 
must direct them back to http://www.projity.com.  
*/
package com.projity.pm.graphic.views;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.apache.commons.collections.Closure;

import com.projity.configuration.Dictionary;
import com.projity.graphic.configuration.BarStyles;
import com.projity.grouping.core.model.NodeModel;
import com.projity.help.HelpUtil;
import com.projity.menu.MenuActionConstants;
import com.projity.menu.MenuManager;
import com.projity.pm.graphic.frames.DocumentFrame;
import com.projity.pm.graphic.model.cache.NodeModelCache;
import com.projity.pm.graphic.model.cache.NodeModelCacheFactory;
import com.projity.pm.graphic.model.cache.ReferenceNodeModelCache;
import com.projity.pm.graphic.spreadsheet.SpreadSheet;
import com.projity.pm.graphic.xbs.Xbs;
import com.projity.pm.task.Project;
import com.projity.undo.UndoController;
import com.projity.workspace.WorkspaceSetting;

/**
 *
 */
public class TreeView extends JScrollPane implements BaseView {
	private static final long serialVersionUID = 2390048109591199408L;

	protected Xbs tree;
	protected NodeModel model;
	protected Project project;
	DocumentFrame documentFrame;
	String viewName = null;
	protected NodeModelCache cache;
	/**
	 * 
	 */
	public TreeView(DocumentFrame documentFrame, MenuManager manager) {
		super();
		this.documentFrame = documentFrame;
		this.project = documentFrame.getProject();
	}
	public void init(ReferenceNodeModelCache cache, NodeModel model,String viewName,Closure transformerClosure){
		tree=new Xbs(project,viewName);
		this.viewName = viewName;
		this.cache=NodeModelCacheFactory.getInstance().createAntiAssignmentFilteredCache((ReferenceNodeModelCache)cache,viewName,transformerClosure);
		tree.setCache(this.cache);
		tree.setBarStyles((BarStyles) Dictionary.get(BarStyles.category, viewName));
			
		
		JViewport viewport = createViewport();
		viewport.setView(tree);
		setViewport(viewport);
		cache.update(); //this is not required by certain views 
		HelpUtil.addDocHelp(this,viewName == MenuActionConstants.ACTION_RBS ? "RBS_Chart" : "WBS_Chart");
	//tree.insertCacheData();
	}
	public void cleanUp() {
		tree.cleanUp();
		tree = null;
		model = null;
		project = null;
		documentFrame = null;
	}

	
	public void zoomIn(){
		tree.zoomIn();
	}
	public void zoomOut(){
		tree.zoomOut();
	}
	public boolean canZoomIn() {
		return tree.canZoomIn();
	}
	public boolean canZoomOut() {
		return tree.canZoomOut();
	}
	public int getScale() {
		return tree.getZoom();
	}
	
	public UndoController getUndoController() {
		if (showsTasks())
			return project.getUndoController();
		else 
			return project.getResourcePool().getUndoController();
	}
	
	public SpreadSheet getSpreadSheet() {
		return null;
	}
	public boolean hasNormalMinWidth() {
		return true;
	}
	public String getViewName() {
		return viewName;
	}
	public boolean showsTasks() {
		return viewName == MenuActionConstants.ACTION_WBS;
	}
	public boolean showsResources() {
		return viewName == MenuActionConstants.ACTION_RBS;
	}
	public void onActivate(boolean activate) {
	}
	public boolean isPrintable() {
		return true;
	}
	public void restoreWorkspace(WorkspaceSetting w, int context) {
		Workspace ws = (Workspace) w;
		tree.restoreWorkspace(ws.network, context);
	}
	public WorkspaceSetting createWorkspace(int context) {
		Workspace ws = new Workspace();
		ws.network = tree.createWorkspace(context);
		return ws;
	}

	public static class Workspace implements WorkspaceSetting { 
		private static final long serialVersionUID = 7828075902711289247L;
		WorkspaceSetting network;

		public WorkspaceSetting getNetwork() {
			return network;
		}

		public void setNetwork(WorkspaceSetting network) {
			this.network = network;
		}
	}

	public boolean canScrollToTask() {
		// TODO Auto-generated method stub
		return false;
	}
	public void scrollToTask() {
		// TODO Auto-generated method stub
		
	}
	
	public NodeModelCache getCache(){
		return cache;
	}
	

}
