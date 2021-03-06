package com.github.biorobaw.scs_models.multiscale_f2019.gui.swing;

import java.awt.Color;


import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.gui.Display;
import com.github.biorobaw.scs.gui.DrawPanel;
import com.github.biorobaw.scs.gui.displays.scs_swing.drawer.CycleDataDrawer;
import com.github.biorobaw.scs.gui.displays.scs_swing.drawer.FeederDrawer;
import com.github.biorobaw.scs.gui.displays.scs_swing.drawer.PathDrawer;
import com.github.biorobaw.scs.gui.displays.scs_swing.drawer.RobotDrawer;
import com.github.biorobaw.scs.gui.displays.scs_swing.drawer.RuntimesDrawer;
import com.github.biorobaw.scs.gui.displays.scs_swing.drawer.WallDrawer;
import com.github.biorobaw.scs.gui.utils.GuiUtils;
import com.github.biorobaw.scs_models.multiscale_f2019.gui.swing.drawers.PCDrawer;
import com.github.biorobaw.scs_models.multiscale_f2019.gui.swing.drawers.PolarDataDrawer;
import com.github.biorobaw.scs_models.multiscale_f2019.gui.swing.drawers.VDrawer;
import com.github.biorobaw.scs_models.multiscale_f2019.model.MultiscaleModel;

public class GUI {
	
	// =========== PARAMETERS =====================
	static final int   wall_thickness = 1;
	static final Color wall_color 	  = GuiUtils.getHSBAColor(0f, 0f, 0f, 1);
	static final Color path_color 	  = Color.RED;
	
	// ============ VARIABLES =====================
	
	// reference to the display and model
	Display d = Experiment.get().display;
	MultiscaleModel model;
	
	// reference to drawers
	public WallDrawer wallDrawer;
	public PathDrawer pathDrawer;
	public RobotDrawer rDrawer;
	public FeederDrawer fDrawer;

	int numScales;
	public PCDrawer[] pcDrawers;
	public VDrawer[] TDrawers;
	public VDrawer[] VDrawers;
	
	
	public PolarDataDrawer qDrawer;
	public PolarDataDrawer affDrawer;
	public PolarDataDrawer biasDrawer;
	public PolarDataDrawer probDrawer;
	
	public RuntimesDrawer runtimes;
	
	
	final static String panel_pc = "PC Activation ";
	final static String panel_traces = "Traces ";
	final static String panel_value = "PC Value ";
	final static String panel_current_q = "Current Q Value";
	final static String panel_affordances = "Affordances";
	final static String panel_bias = "Bias";
	final static String panel_actions = "Actions";
	final static String panel_runtimes = "Run times";
	
	
	
	public GUI(MultiscaleModel model) {
		numScales = model.pcs.length;
		this.model = model;
		createPanels();
		createDrawers();
		addDrawersToPanels();
		
	}
	
	private void createPanels() {
		// =========== CREATE PANELS =================
		// PC PANELS
		int size = 200;
		for (int i = 0; i < numScales; i++) {
			d.addPanel(new DrawPanel(size, size), panel_pc + i, 0, i, 1, 1);
		}
		
		// TRACE PANELS
		for (int i = 0; i < numScales; i++) {
			d.addPanel(new DrawPanel(size, size), panel_traces + i, 1, i, 1, 1);
		}
		
		// VALUE PANELS
		for (int i = 0; i < numScales; i++) {
			d.addPanel(new DrawPanel(size, size), panel_value + i, 2, i, 1, 1);
		}
		
		// ACTION SELECTION PANELS
		d.addPanel(new DrawPanel(size,size), panel_current_q, 0, 3, 1, 1);
		d.addPanel(new DrawPanel(size,size), panel_affordances, 1, 3, 1, 1);
		d.addPanel(new DrawPanel(size,size), panel_bias, 2, 3, 1, 1);
		d.addPanel(new DrawPanel(size,size), panel_actions, 0, 4, 1, 1);
		
		d.addPanel(new DrawPanel(size,size), panel_runtimes, 1, 4, 1, 1);
	}
	
	private void createDrawers() {
		// =========== CREATE DRAWERS ===============
		

		// Maze related drawers
		wallDrawer = new WallDrawer( wall_thickness);
		wallDrawer.setColor(wall_color);
		
		pathDrawer = new PathDrawer(model.getRobot().getRobotProxy());
		pathDrawer.setColor(path_color);

		rDrawer = new RobotDrawer(model.getRobot().getRobotProxy());
		
		fDrawer = new FeederDrawer(0.1f);
		
		
		// PC drawers
		pcDrawers = new PCDrawer[numScales];
		for (int i = 0; i < numScales; i++) {
			var pc_bin = model.pc_bins[i];
			var pcs = model.pcs[i];
			pcDrawers[i] = new PCDrawer(pcs.xs, pcs.ys, pcs.rs,
						 				() -> pc_bin.active_pcs.as,
						 				() -> pc_bin.active_pcs.ids);
		}
		
		// Trace drawers:
		TDrawers = new VDrawer[numScales];
		for (int i = 0; i < numScales; i++) {
			var t = model.vTraces[i];
			var pcs = model.pcs[i];
			TDrawers[i] = new VDrawer(pcs.xs, pcs.ys, t.traces[0]);
			TDrawers[i].setMinValue(0);
		}
		
		// V drawers
		VDrawers = new VDrawer[numScales];
		for (int i = 0; i < numScales; i++) {
			var pcs = model.pcs[i];
			VDrawers[i] = new VDrawer(pcs.xs, pcs.ys, model.vTable[i]);
			VDrawers[i].distanceOption = 1; // use pc radis to draw PCs
		}
		
		// RL and action selection drawers
		
		qDrawer = new PolarDataDrawer("Q softmax",model.numActions ,() -> model.softmax );
		affDrawer = new PolarDataDrawer("Affordances",model.numActions , ()->model.affordances.affordances);
		biasDrawer = new PolarDataDrawer("Bias",model.numActions, ()->model.motionBias.getBias());
		probDrawer = new PolarDataDrawer("Probs",model.numActions, ()->model.action_selection_probs);
		probDrawer.setGetArrowFunction(()->model.chosenAction);
		
		int numEpisodes = Integer.parseInt(Experiment.get().getGlobal("numEpisodes").toString());
		runtimes = new RuntimesDrawer(numEpisodes, 0, 800);
		runtimes.doLines = false;
	}
	
	public void addDrawersToPanels() {

		// ======== ADD DRAWERS TO PANELS ============
		
		// UNIVERSE PANEL
		d.addDrawer("universe", "pcs", pcDrawers[0] );
		d.addDrawer("universe", "value", VDrawers[0]);
		d.addDrawer("universe", "maze", wallDrawer );
		d.addDrawer("universe", "feeders", fDrawer);
		d.addDrawer("universe", "path", pathDrawer);
		d.addDrawer("universe", "robot", rDrawer);
		d.addDrawer("universe", "cycle info", new CycleDataDrawer());
		
		// RUNTIMES
		d.addDrawer(panel_runtimes, "runtimes", runtimes);
		
		// PC PANELS
		for (int i = 0; i < numScales; i++) {
			d.addDrawer(panel_pc + i, "PC layer " + i, pcDrawers[i]);
			d.addDrawer(panel_pc + i, "maze", wallDrawer);
			d.addDrawer(panel_pc + i, "robot other", rDrawer);
		}
		
		// TRACE PANELS:
		for (int i = 0; i < numScales; i++) {
			d.addDrawer(panel_traces + i, "T layer " + i, TDrawers[i]);
			d.addDrawer(panel_traces + i, "maze", wallDrawer);
			d.addDrawer(panel_traces + i, "robot other", rDrawer);
		}
		
		// VALUE PANELS:
		for (int i = 0; i < numScales; i++) {
			d.addDrawer(panel_value + i, "V layer " + i, VDrawers[i]);
			d.addDrawer(panel_value + i, "maze", wallDrawer);
			d.addDrawer(panel_value + i, "robot other", rDrawer);
		}
		
		
		// ACTION SELECTION PANELS:
		d.addDrawer(panel_current_q, "qDrawer", qDrawer);
		d.addDrawer(panel_affordances, "affDrawer", affDrawer);
		d.addDrawer(panel_bias, "biasDrawer", biasDrawer);
		d.addDrawer(panel_actions, "probDrawer", probDrawer);
		
		
		


	}
	
	
}
