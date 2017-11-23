package nc.unc.simulation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.AdamsBashforthIntegrator;
import org.apache.commons.math3.ode.nonstiff.AdamsMoultonIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;
import org.apache.commons.math3.ode.nonstiff.GillIntegrator;
import org.apache.commons.math3.ode.nonstiff.GraggBulirschStoerIntegrator;
import org.apache.commons.math3.ode.nonstiff.HighamHall54Integrator;
import org.apache.commons.math3.ode.nonstiff.LutherIntegrator;
import org.apache.commons.math3.ode.nonstiff.MidpointIntegrator;
import org.apache.commons.math3.ode.nonstiff.ThreeEighthesIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * This is the model implementation of ApacheODESolver.
 * 
 *
 * @author Flouvat
 */
public class ApacheODESolverNodeModel extends NodeModel {

	// the logger instance
	//private static final NodeLogger logger = NodeLogger.getLogger(ApacheODESolverNodeModel.class);

	// number of rows generated
	private int nbrows = 0;

	/**
	 * the settings key which is used to retrieve and store the settings (from
	 * the dialog or from a settings file) (package visibility to be usable from
	 * the dialog).
	 */

	static final String CFKEY_DIM = "DIM";

	static final String CFKEY_ODE = "ODE";

	static final String CFKEY_INIT_STATE = "INIT_STATE";

	static final String CFKEY_TYPE = "TYPE";

	static final String CFKEY_INTEGRATOR = "INTEGRATOR";

	static final String CFKEY_STEP = "STEP";

	static final String CFKEY_MIN_STEP = "MIN_STEP";

	static final String CFKEY_MAX_STEP = "MAX_STEP";

	static final String CFKEY_ABS_ERROR = "ABS_ERROR";

	static final String CFKEY_REL_ERROR = "REL_ERROR";

	static final String CFKEY_NSTEP = "NSTEP";

	static final String CFKEY_INIT_TIME = "INIT_TIME";

	static final String CFKEY_TARGET_TIME = "TARGET_TIME";

	/** initial default values. */

	static final int DEFAULT_DIM = 2;

	static final String DEFAULT_ODE = new String();

	static final double DEFAULT_INIT_STATE = 0;

	static final String DEFAULT_TYPE = new String();

	static final String DEFAULT_INTEGRATOR = new String();

	static final double DEFAULT_STEP = 1;

	static final double DEFAULT_MIN_STEP = 0;

	static final double DEFAULT_MAX_STEP = 1;

	static final double DEFAULT_ABS_ERROR = 0;

	static final double DEFAULT_REL_ERROR = 0;

	static final int DEFAULT_NSTEP = 1;

	static final double DEFAULT_INIT_TIME = 0;

	static final double DEFAULT_TARGET_TIME = 100;

	// example value: the models count variable filled from the dialog
	// and used in the models execution method. The default components of the
	// dialog work with "SettingsModels".

	private final SettingsModelInteger m_dim = new SettingsModelInteger(ApacheODESolverNodeModel.CFKEY_DIM,
			ApacheODESolverNodeModel.DEFAULT_DIM);

	private final ArrayList<SettingsModelString> m_ode = new ArrayList<SettingsModelString>();

	private final ArrayList<SettingsModelDouble> m_init_state = new ArrayList<SettingsModelDouble>();

	private final SettingsModelString m_type = new SettingsModelString(ApacheODESolverNodeModel.CFKEY_TYPE,
			ApacheODESolverNodeModel.DEFAULT_TYPE);

	private final SettingsModelString m_integrator = new SettingsModelString(ApacheODESolverNodeModel.CFKEY_INTEGRATOR,
			ApacheODESolverNodeModel.DEFAULT_INTEGRATOR);

	private final SettingsModelDouble m_step = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_STEP,
			ApacheODESolverNodeModel.DEFAULT_STEP);

	private final SettingsModelDouble m_min_step = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_MIN_STEP,
			ApacheODESolverNodeModel.DEFAULT_MIN_STEP);

	private final SettingsModelDouble m_max_step = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_MAX_STEP,
			ApacheODESolverNodeModel.DEFAULT_MAX_STEP);

	private final SettingsModelDouble m_abs_error = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_ABS_ERROR,
			ApacheODESolverNodeModel.DEFAULT_ABS_ERROR);

	private final SettingsModelDouble m_rel_error = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_REL_ERROR,
			ApacheODESolverNodeModel.DEFAULT_REL_ERROR);

	private final SettingsModelInteger m_nstep = new SettingsModelInteger(ApacheODESolverNodeModel.CFKEY_NSTEP,
			ApacheODESolverNodeModel.DEFAULT_NSTEP);

	private final SettingsModelDouble m_init_time = new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_INIT_TIME,
			ApacheODESolverNodeModel.DEFAULT_INIT_TIME);

	private final SettingsModelDouble m_target_time = new SettingsModelDouble(
			ApacheODESolverNodeModel.CFKEY_TARGET_TIME, ApacheODESolverNodeModel.DEFAULT_TARGET_TIME);

	/**
	 * Constructor for the node model.
	 */
	protected ApacheODESolverNodeModel() {

		// TODO 0 incoming port and one outgoing port is assumed
		super(0, 1);

		for (int i = 0; i < m_dim.getIntValue(); i++) {
			m_ode.add(new SettingsModelString(ApacheODESolverNodeModel.CFKEY_ODE + i,
					ApacheODESolverNodeModel.DEFAULT_ODE));
			m_init_state.add(new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_INIT_STATE + i,
					ApacheODESolverNodeModel.DEFAULT_INIT_STATE));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {

		// logger.info("dimension:"+m_dim.getIntValue()+" nbODE:"+m_ode.size());

		// the data table spec of the single output table,
		// the table will have one column for the time t and
		// one column for each variable
		DataColumnSpec[] allColSpecs = new DataColumnSpec[m_dim.getIntValue() + 1];
		allColSpecs[0] = new DataColumnSpecCreator("t", DoubleCell.TYPE).createSpec();
		for (int i = 1; i < m_dim.getIntValue() + 1; i++) {
			allColSpecs[i] = new DataColumnSpecCreator("y[" + (i - 1) + "]", DoubleCell.TYPE).createSpec();

		}
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

		// the execution context will provide us with storage capacity, in this
		// case a data container to which we will add rows sequentially
		// Note, this container can also handle arbitrary big data tables, it
		// will buffer to disc if necessary.
		BufferedDataContainer container = exec.createDataContainer(outputSpec);

		// Initializing the selected integrator
		FirstOrderIntegrator integrator = new EulerIntegrator(m_step.getDoubleValue());

		switch (m_integrator.getStringValue()) {
		case "Euler":
			integrator = new EulerIntegrator(m_step.getDoubleValue());
			break;
		case "Midpoint":
			integrator = new MidpointIntegrator(m_step.getDoubleValue());
			break;
		case "Classical Runge-Kutta":
			integrator = new MidpointIntegrator(m_step.getDoubleValue());
			break;
		case "Gill":
			integrator = new GillIntegrator(m_step.getDoubleValue());
			break;
		case "3/8":
			integrator = new ThreeEighthesIntegrator(m_step.getDoubleValue());
			break;
		case "Luther":
			integrator = new LutherIntegrator(m_step.getDoubleValue());
			break;
		case "Higham and Hall":
			integrator = new HighamHall54Integrator(m_min_step.getDoubleValue(), m_max_step.getDoubleValue(),
					m_abs_error.getDoubleValue(), m_rel_error.getDoubleValue());
			break;
		case "Dormand-Prince 5(4)":
			integrator = new DormandPrince54Integrator(m_min_step.getDoubleValue(), m_max_step.getDoubleValue(),
					m_abs_error.getDoubleValue(), m_rel_error.getDoubleValue());
			break;
		case "Dormand-Prince 8(5,3)":
			integrator = new DormandPrince853Integrator(m_min_step.getDoubleValue(), m_max_step.getDoubleValue(),
					m_abs_error.getDoubleValue(), m_rel_error.getDoubleValue());
			break;
		case "Gragg-Bulirsch-Stoer":
			integrator = new GraggBulirschStoerIntegrator(m_min_step.getDoubleValue(), m_max_step.getDoubleValue(),
					m_abs_error.getDoubleValue(), m_rel_error.getDoubleValue());
			break;
		case "Adams-Bashforth":
			integrator = new AdamsBashforthIntegrator(m_nstep.getIntValue(), m_min_step.getDoubleValue(),
					m_max_step.getDoubleValue(), m_abs_error.getDoubleValue(), m_rel_error.getDoubleValue());
			break;
		case "Adams-Moulton":
			integrator = new AdamsMoultonIntegrator(m_nstep.getIntValue(), m_min_step.getDoubleValue(),
					m_max_step.getDoubleValue(), m_abs_error.getDoubleValue(), m_rel_error.getDoubleValue());
			break;
		}

		// Initialization of the script engine that will execute input ODE written in JavaScript  
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");

		// Get input ODE written in JavaScript and generate the script
		StringBuffer javascript_eq = new StringBuffer();
		for( int i=0; i< m_dim.getIntValue(); i++ )		
			javascript_eq.append("var yDot"+i+"="+m_ode.get(i).getStringValue()+";");
		
		//javascript_eq.append("var yDot0 = ((2.0 / 3.0) * y0) - ((4.0 / 3.0) * y0 * y1);");
		//javascript_eq.append("var yDot1 = (-(1.0) * y1) + ((1.0) * y0 * y1);");
		
		// Compile the script
		CompiledScript cs = ((Compilable) engine).compile(javascript_eq.toString());
		Bindings scriptParams = engine.createBindings();

		// Initialization of the equations
		FirstOrderDifferentialEquations equations = new FirstOrderDifferentialEquations() {

			@Override
			public int getDimension() {
				// returns the dimension
				return m_dim.getIntValue();
			}

			@Override
			public void computeDerivatives(double t, double[] y, double[] yDot)
					throws MaxCountExceededException, DimensionMismatchException {

				try {
					// Replacement of the string y0 by the contetn of variable y[0]
					scriptParams.put("y0", y[0]);
					scriptParams.put("y1", y[1]);
					
					// script execution
					cs.eval(scriptParams);
					
					// Load results from the script
					yDot[0] = (double) scriptParams.get("yDot0");
					yDot[1] = (double) scriptParams.get("yDot1");

					// engine.eval(javascript_eq0.toString());
				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		nbrows = 0;

		// Initialization of a step handler to have a continuous output (i.e.
		// intermediate results)
		StepHandler stepHandler = new StepHandler() {
			public void init(double t0, double[] y0, double t) {
			}

			public void handleStep(StepInterpolator interpolator, boolean isLast) {
				double t = interpolator.getCurrentTime();
				double[] y = interpolator.getInterpolatedState();
				// System.err.println(t + " " + y[0] + " " + y[1]);

				RowKey key = new RowKey("Row " + nbrows);
				// the cells of the current row, the types of the cells must
				// match
				// the column spec (see above)
				DataCell[] cells = new DataCell[m_dim.getIntValue() + 1];
				cells[0] = new DoubleCell(t);
				for (int i = 0; i < m_dim.getIntValue(); i++) {
					cells[i + 1] = new DoubleCell(y[i]);

				}

				DataRow row = new DefaultRow(key, cells);
				container.addRowToTable(row);

				nbrows++;

				// check if the execution monitor was canceled
				try {
					exec.checkCanceled();
				} catch (CanceledExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				exec.setProgress(t / (m_target_time.getDoubleValue() - m_init_time.getDoubleValue()),
						"Processing time " + t);
			}
		};

		integrator.addStepHandler(stepHandler);

		// get the initial states
		double[] y = new double[m_dim.getIntValue()];
		for (int i = 0; i < m_dim.getIntValue(); i++)
			y[i] = m_init_state.get(i).getDoubleValue();

		// Solve the equations
		integrator.integrate(equations, m_init_time.getDoubleValue(), y, m_target_time.getDoubleValue(), y);

		// once we are done, we close the container and return its table
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {

		// TODO: check if user settings are available, fit to the incoming
		// table structure, and the incoming types are feasible for the node
		// to execute. If the node can execute in its current state return
		// the spec of its output data table(s) (if you can, otherwise an array
		// with null elements), or throw an exception with a useful user message

		return new DataTableSpec[] { null };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {

		// TODO save user settings to the config object.
		m_dim.saveSettingsTo(settings);

		for (int i = 0; i < m_dim.getIntValue(); i++) {
			m_ode.get(i).saveSettingsTo(settings);
			m_init_state.get(i).saveSettingsTo(settings);
		}

		m_type.saveSettingsTo(settings);
		m_integrator.saveSettingsTo(settings);
		m_step.saveSettingsTo(settings);
		m_min_step.saveSettingsTo(settings);
		m_max_step.saveSettingsTo(settings);
		m_abs_error.saveSettingsTo(settings);
		m_rel_error.saveSettingsTo(settings);
		m_nstep.saveSettingsTo(settings);
		m_init_time.saveSettingsTo(settings);
		m_target_time.saveSettingsTo(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.

		m_dim.loadSettingsFrom(settings);

		if (m_dim.getIntValue() > m_ode.size()) {
			for (int i = m_ode.size(); i < m_dim.getIntValue(); i++) {
				m_ode.add(new SettingsModelString(ApacheODESolverNodeModel.CFKEY_ODE + i,
						ApacheODESolverNodeModel.DEFAULT_ODE));
				m_init_state.add(new SettingsModelDouble(ApacheODESolverNodeModel.CFKEY_INIT_STATE + i,
						ApacheODESolverNodeModel.DEFAULT_INIT_STATE));
			}
		}

		for (int i = 0; i < m_dim.getIntValue(); i++) {
			m_ode.get(i).loadSettingsFrom(settings);
			m_init_state.get(i).loadSettingsFrom(settings);
		}

		m_type.loadSettingsFrom(settings);
		m_integrator.loadSettingsFrom(settings);
		m_step.loadSettingsFrom(settings);
		m_min_step.loadSettingsFrom(settings);
		m_max_step.loadSettingsFrom(settings);
		m_abs_error.loadSettingsFrom(settings);
		m_rel_error.loadSettingsFrom(settings);
		m_nstep.loadSettingsFrom(settings);
		m_init_time.loadSettingsFrom(settings);
		m_target_time.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO check if the settings could be applied to our model
		// e.g. if the count is in a certain range (which is ensured by the
		// SettingsModel).
		// Do not actually set any values of any member variables.

		m_dim.validateSettings(settings);

		for (int i = 0; i < m_dim.getIntValue(); i++) {
			m_ode.get(i).validateSettings(settings);
			m_init_state.get(i).validateSettings(settings);
		}

		m_type.validateSettings(settings);
		m_integrator.validateSettings(settings);
		m_step.validateSettings(settings);
		m_min_step.validateSettings(settings);
		m_max_step.validateSettings(settings);
		m_abs_error.validateSettings(settings);
		m_rel_error.validateSettings(settings);
		m_nstep.validateSettings(settings);
		m_init_time.validateSettings(settings);
		m_target_time.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		// TODO load internal data.
		// Everything handed to output ports is loaded automatically (data
		// returned by the execute method, models loaded in loadModelContent,
		// and user settings set through loadSettingsFrom - is all taken care
		// of). Load here only the other internals that need to be restored
		// (e.g. data used by the views).

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		// TODO save internal models.
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

	}

}
