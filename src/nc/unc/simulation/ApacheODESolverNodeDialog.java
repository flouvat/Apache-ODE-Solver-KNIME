package nc.unc.simulation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentButton;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "ApacheODESolver" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Flouvat
 */
public class ApacheODESolverNodeDialog extends DefaultNodeSettingsPane {

	// list of additional ODE dialogComponents
	ArrayList<DialogComponentString> list_dialog_ode = new ArrayList<DialogComponentString>();

	// list of additional ODE dialogComponents
	ArrayList<DialogComponentNumberEdit> list_dialog_init_state = new ArrayList<DialogComponentNumberEdit>();

	private final ArrayList<SettingsModelString> list_ode = new ArrayList<SettingsModelString>();

	private final ArrayList<SettingsModelDouble> list_init_state = new ArrayList<SettingsModelDouble>();

	// the dimension setting
	private final SettingsModelInteger settings_dim = new SettingsModelInteger(ApacheODESolverNodeModel.CFKEY_DIM,
			ApacheODESolverNodeModel.DEFAULT_DIM);
	
	private final DialogComponentLabel dialog_dim = new DialogComponentLabel("dimension: " + settings_dim.getIntValue());

	/**
	 * New pane for configuring ApacheODESolver node dialog. This is just a
	 * suggestion to demonstrate possible default dialog components.
	 */
	protected ApacheODESolverNodeDialog() {
		super();	
				
		// Integrators Tab
		// this tab is initialized first to enable dynamic insertion of equation
		// in the ODE tab (since insertions in knime's dialogComponent are
		// automatically done in the last tab)

		setDefaultTabTitle("Integrators");

		List<String> list_type = new ArrayList<String>();
		list_type.add("Fixed");
		list_type.add("Adaptative");

		SettingsModelString settings_list_type = new SettingsModelString(ApacheODESolverNodeModel.CFKEY_TYPE,
				ApacheODESolverNodeModel.DEFAULT_TYPE);

		addDialogComponent(new DialogComponentStringSelection(settings_list_type, "Type:", list_type));

		// list of available integrators
		List<String> list_integrator_fixed = new ArrayList<String>();
		list_integrator_fixed.add("Euler");
		list_integrator_fixed.add("Midpoint");
		list_integrator_fixed.add("Classical Runge-Kutta");
		list_integrator_fixed.add("Gill");
		list_integrator_fixed.add("3/8");
		list_integrator_fixed.add("Luther");

		List<String> list_integrator_adaptive = new ArrayList<String>();
		list_integrator_adaptive.add("Higham and Hall");
		list_integrator_adaptive.add("Dormand-Prince 5(4)");
		list_integrator_adaptive.add("Dormand-Prince 8(5,3)");
		list_integrator_adaptive.add("Gragg-Bulirsch-Stoer");
		list_integrator_adaptive.add("Adams-Bashforth");
		list_integrator_adaptive.add("Adams-Moulton");

		SettingsModelString settings_list_integrator = new SettingsModelString(ApacheODESolverNodeModel.CFKEY_INTEGRATOR,
				ApacheODESolverNodeModel.DEFAULT_INTEGRATOR);
		DialogComponentStringSelection dialog_list_integrator = new DialogComponentStringSelection(
				settings_list_integrator,
				"Integrator:", list_integrator_fixed);

		addDialogComponent(dialog_list_integrator);

		setHorizontalPlacement(false);

		createNewGroup("Intergator parameters:");

		// Parameter for fixed integrators
		SettingsModelDouble settings_step = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_STEP,
				ApacheODESolverNodeModel.DEFAULT_STEP);

		DialogComponentNumberEdit dialog_step = new DialogComponentNumberEdit(settings_step, "Step:");

		addDialogComponent(dialog_step);

		// Parameters for adaptive integrators
		setHorizontalPlacement(true);

		SettingsModelDouble settings_min_step = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_MIN_STEP,
				ApacheODESolverNodeModel.DEFAULT_MIN_STEP);

		DialogComponentNumberEdit dialog_min_step = new DialogComponentNumberEdit(settings_min_step, "Minimal Step:");

		settings_min_step.setEnabled(false);
		dialog_min_step.getComponentPanel().setVisible(false);

		addDialogComponent(dialog_min_step);

		SettingsModelDouble settings_max_step = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_MAX_STEP,
				ApacheODESolverNodeModel.DEFAULT_MAX_STEP);

		DialogComponentNumberEdit dialog_max_step = new DialogComponentNumberEdit(settings_max_step, "Maximal Step:");

		settings_max_step.setEnabled(false);
		dialog_max_step.getComponentPanel().setVisible(false);

		addDialogComponent(dialog_max_step);

		setHorizontalPlacement(false);

		SettingsModelDouble settings_abs_error = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_ABS_ERROR,
				ApacheODESolverNodeModel.DEFAULT_ABS_ERROR);

		DialogComponentNumberEdit dialog_abs_error = new DialogComponentNumberEdit(settings_abs_error,
				"Allowed absolute error:",10);

		settings_abs_error.setEnabled(false);
		dialog_abs_error.getComponentPanel().setVisible(false);

		addDialogComponent(dialog_abs_error);

		SettingsModelDouble settings_rel_error = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_REL_ERROR,
				ApacheODESolverNodeModel.DEFAULT_REL_ERROR);

		DialogComponentNumberEdit dialog_rel_error = new DialogComponentNumberEdit(settings_rel_error,
				"Allowed relative error:",10);

		settings_rel_error.setEnabled(false);
		dialog_rel_error.getComponentPanel().setVisible(false);

		addDialogComponent(dialog_rel_error);
				
		SettingsModelInteger settings_nstep = new SettingsModelInteger(ApacheODESolverNodeModel.CFKEY_NSTEP,
				ApacheODESolverNodeModel.DEFAULT_NSTEP);

		DialogComponentNumberEdit dialog_nstep = new DialogComponentNumberEdit(settings_nstep, "Number of steps:");

		settings_nstep.setEnabled(false);
		dialog_nstep.getComponentPanel().setVisible(false);

		addDialogComponent(dialog_nstep);
		
		// Parameters for integration
		
		setHorizontalPlacement(false);
		setHorizontalPlacement(true);

		createNewGroup("Intergation parameters:");
		
		SettingsModelDouble settings_init_time = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_INIT_TIME,
				ApacheODESolverNodeModel.DEFAULT_INIT_TIME);

		DialogComponentNumberEdit dialog_init_time = new DialogComponentNumberEdit(settings_init_time, "Initial time:");

		addDialogComponent(dialog_init_time);
		
		SettingsModelDouble settings_target_time = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_TARGET_TIME,
				ApacheODESolverNodeModel.DEFAULT_TARGET_TIME);

		DialogComponentNumberEdit dialog_target_time = new DialogComponentNumberEdit(settings_target_time, "Target time:");

		addDialogComponent(dialog_target_time);
		

		// ODE Tab

		createNewTabAt("Ordinary Differential Equations", 0);
		selectTab("Ordinary Differential Equations");
		setHorizontalPlacement(true);

		// button used to add an equation
		DialogComponentButton dialog_addODE = new DialogComponentButton("+");
		addDialogComponent(dialog_addODE);

		// display the current number of equations
		// SettingsModelInteger settings_dim = new
		// SettingsModelInteger(ApacheODESolverNodeModel.CFKEY_DIM,
		// ApacheODESolverNodeModel.DEFAULT_DIM);
		// settings_dim.setEnabled(true);
		// add a hidden dialog component with the dimension to record this
		// setting
		DialogComponentNumberEdit disabled_dialog_dim = new DialogComponentNumberEdit(settings_dim, "dimmension: ");
		addDialogComponent(disabled_dialog_dim);
		disabled_dialog_dim.getComponentPanel().setVisible(false);
		//DialogComponentLabel dialog_dim = new DialogComponentLabel("dimension: " + settings_dim.getIntValue());
		addDialogComponent(dialog_dim);

		// button used to remove an equation
		DialogComponentButton dialog_delODE = new DialogComponentButton("-");
		addDialogComponent(dialog_delODE);

		setHorizontalPlacement(false);
		setHorizontalPlacement(true);

		addDialogComponent(new DialogComponentString(
				new SettingsModelString(ApacheODESolverNodeModel.CFKEY_ODE + "0", ApacheODESolverNodeModel.DEFAULT_ODE),
				"dy0/dt =", true, 20));

		addDialogComponent(
				new DialogComponentNumberEdit(new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_INIT_STATE + "0",
						ApacheODESolverNodeModel.DEFAULT_INIT_STATE), "Initial state y0(t0):"));

		setHorizontalPlacement(false);
		setHorizontalPlacement(true);

		addDialogComponent(new DialogComponentString(
				new SettingsModelString(ApacheODESolverNodeModel.CFKEY_ODE + "1", ApacheODESolverNodeModel.DEFAULT_ODE),
				"dy1/dt =", true, 20));

		addDialogComponent(
				new DialogComponentNumberEdit(new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_INIT_STATE + "1",
						ApacheODESolverNodeModel.DEFAULT_INIT_STATE), "Initial state y1(t0):"));

		///////////////////
		// EVENTS HANDLING

		// Change the integrator list and parameters according to selected type
		settings_list_type.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				// if enabled is true, the parameter field should be enabled

				if (settings_list_type.getStringValue().equals("Fixed")) {
					dialog_list_integrator.replaceListItems(list_integrator_fixed, "Euler");

					// set visible/enable parameters of fixed integrators
					dialog_step.getComponentPanel().setVisible(true);
					settings_step.setEnabled(true);

					// hide/disable parameters of adaptive integrators
					dialog_min_step.getComponentPanel().setVisible(false);
					settings_min_step.setEnabled(false);
					dialog_max_step.getComponentPanel().setVisible(false);
					settings_max_step.setEnabled(false);
					dialog_abs_error.getComponentPanel().setVisible(false);
					settings_abs_error.setEnabled(false);
					dialog_rel_error.getComponentPanel().setVisible(false);
					settings_rel_error.setEnabled(false);
				} else {
					dialog_list_integrator.replaceListItems(list_integrator_adaptive, "Higham and Hal");

					// hide/disable parameters of fixed integrators
					dialog_step.getComponentPanel().setVisible(false);
					settings_step.setEnabled(false);

					// set visible/enable parameters of adaptive integrators
					dialog_min_step.getComponentPanel().setVisible(true);
					settings_min_step.setEnabled(true);
					dialog_max_step.getComponentPanel().setVisible(true);
					settings_max_step.setEnabled(true);
					dialog_abs_error.getComponentPanel().setVisible(true);
					settings_abs_error.setEnabled(true);
					dialog_rel_error.getComponentPanel().setVisible(true);
					settings_rel_error.setEnabled(true);							
					
				}

			}
		});
		
		// // set visible/enable the number of steps for Adams-Bashforth and Adams-Moulton integrators
		 settings_list_integrator.addChangeListener(new ChangeListener() {
					public void stateChanged(final ChangeEvent e) {
						
						if (settings_list_integrator.getStringValue().contains("Adams")) {
							
							// set visible/enable parameters
							dialog_nstep.getComponentPanel().setVisible(true);
							settings_nstep.setEnabled(true);
							
						} else {
							
							// hide/disable parameters of fixed integrators
							dialog_nstep.getComponentPanel().setVisible(false);
							settings_nstep.setEnabled(false);
							
						}
					}
				});

		// Add ODE input fields if pressed
		dialog_addODE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setHorizontalPlacement(false);
				setHorizontalPlacement(true);

				int dim = settings_dim.getIntValue();
				settings_dim.setIntValue(dim + 1);
				dialog_dim.setText("dimension: " + settings_dim.getIntValue());

				if (dim - 2 < list_dialog_ode.size()) // if input ode fields
														// have already been
														// generated but are not
														// visible
				{
					list_ode.get(dim - 2).setEnabled(true);
					list_dialog_ode.get(dim - 2).getComponentPanel().setVisible(true);
					list_dialog_ode.get(dim - 2).getComponentPanel().validate();
					list_dialog_ode.get(dim - 2).getComponentPanel().repaint();

					list_init_state.get(dim - 2).setEnabled(true);
					list_dialog_init_state.get(dim - 2).getComponentPanel().setVisible(true);
					list_dialog_init_state.get(dim - 2).getComponentPanel().validate();
					list_dialog_init_state.get(dim - 2).getComponentPanel().repaint();

				} else {

					SettingsModelString settings_ode = new SettingsModelString(ApacheODESolverNodeModel.CFKEY_ODE + dim,
							ApacheODESolverNodeModel.DEFAULT_ODE);

					DialogComponentString dialog_ode = new DialogComponentString(settings_ode, "dy" + dim + "/dt =",
							true, 20);

					list_ode.add(settings_ode);
					list_dialog_ode.add(dialog_ode);
					addDialogComponent(dialog_ode);

					SettingsModelDouble settings_init_state = new SettingsModelDouble(
							ApacheODESolverNodeModel.CFKEY_INIT_STATE + dim,
							ApacheODESolverNodeModel.DEFAULT_INIT_STATE);

					DialogComponentNumberEdit dialog_init_state = new DialogComponentNumberEdit(settings_init_state,
							"Initial state y" + dim + "(t0):");

					list_init_state.add(settings_init_state);
					list_dialog_init_state.add(dialog_init_state);
					addDialogComponent(dialog_init_state);
				}
			}
		});

		// remove ODE input fields if pressed
		dialog_delODE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int dim = settings_dim.getIntValue();

				if (dim > 2) { // hide and disable the last ODE input fields
					settings_dim.setIntValue(dim - 1);
					dialog_dim.setText("dimension: " + settings_dim.getIntValue());

					// -3 because the two first ode are not in the lists
					list_dialog_ode.get(dim - 3).getComponentPanel().setVisible(false);
					list_ode.get(dim - 3).setEnabled(false);

					list_dialog_init_state.get(dim - 3).getComponentPanel().setVisible(false);
					list_ode.get(dim - 3).setEnabled(false);
				}
			}
		});

	}

	public void loadAdditionalSettingsFrom(NodeSettingsRO settings, DataTableSpec[] specs) throws NotConfigurableException {
		super.loadAdditionalSettingsFrom(settings, specs);

		int dim =  settings_dim.getIntValue();
		
		// add the other (saved) input ode fields to the default settings interface		
		if( dim > 2 && dim - 2 > list_dialog_ode.size()){
			dialog_dim.setText("dimension: " +dim);
			
			for(int i=2; i<settings_dim.getIntValue();i++)
			{
				setHorizontalPlacement(false);
				setHorizontalPlacement(true);
				
				SettingsModelString settings_ode = new SettingsModelString(ApacheODESolverNodeModel.CFKEY_ODE + i,
						ApacheODESolverNodeModel.DEFAULT_ODE);
				settings_ode.setEnabled(true);				
				
				try {
					settings_ode.loadSettingsFrom(settings);
				} catch (InvalidSettingsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				DialogComponentString dialog_ode = new DialogComponentString(settings_ode, "dy[" + i + "]/dt =",
						true, 20);

				list_ode.add(settings_ode);
				list_dialog_ode.add(dialog_ode);
				addDialogComponent(dialog_ode);

				SettingsModelDouble settings_init_state = new SettingsModelDouble(
						ApacheODESolverNodeModel.CFKEY_INIT_STATE + i,
						ApacheODESolverNodeModel.DEFAULT_INIT_STATE);
				settings_init_state.setEnabled(true);
				
				try {
					settings_init_state.loadSettingsFrom(settings);
				} catch (InvalidSettingsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				DialogComponentNumberEdit dialog_init_state = new DialogComponentNumberEdit(settings_init_state,
						"Initial state y[" + i + "](t0):");

				list_init_state.add(settings_init_state);
				list_dialog_init_state.add(dialog_init_state);
				addDialogComponent(dialog_init_state);
			}
		}
		
		
	}
}
