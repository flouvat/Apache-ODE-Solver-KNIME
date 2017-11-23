package nc.unc.simulation;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ApacheODESolver" Node.
 * 
 *
 * @author Flouvat
 */
public class ApacheODESolverNodeFactory 
        extends NodeFactory<ApacheODESolverNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ApacheODESolverNodeModel createNodeModel() {
        return new ApacheODESolverNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<ApacheODESolverNodeModel> createNodeView(final int viewIndex,
            final ApacheODESolverNodeModel nodeModel) {
        return new ApacheODESolverNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new ApacheODESolverNodeDialog();
    }

}

