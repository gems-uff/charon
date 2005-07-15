package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jmi.reflect.RefBaseObject;

import processstructure.WorkDefinition;
import spem.SpemPackage;
import statemachines.CompositeState;
import statemachines.PseudoState;
import statemachines.StateVertex;
import statemachines.Transition;
import actions.CallAction;
import activitygraphs.ActionState;
import activitygraphs.ActivityGraph;
import activitygraphs.ObjectFlowState;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonException;
import datatypes.PseudoStateKindEnum;


/**
 * This agent is responsible to simulate the process
 *
 * @author Leo Murta
 * @version 1.0, 14/12/2001
 */
public class SimulationAgent extends Agent {

	/**
	 * Number of simulations
	 */
	private final static int SIMULATIONS = 1000;
	
	/**
	 * Maximum deep of calls to work definitions
	 */
	private final static int MAXIMUM_DEEP = 1000;
	
	/**
	 * Maximum simulation steps (transitions followed) in work definitions
	 */
	private final static int MAXIMUM_STEPS = 1000;
	
	/**
	 * Current deep of calls to work definitions
	 */
	private int deep;
	
	/**
	 * Current set of simulated work definitions
	 */
	private Set<WorkDefinition> simulated;
	
	/**
	 * Constructs the simulation agent
	 */
	public SimulationAgent() {
		super();
	}
	
	/**
	 * Simulates all Work Definitions of a SPEM package. Goes recursivelly through the
	 * inner elements.
	 */
	public synchronized void simulate(SpemPackage spemPackage) throws CharonException {
		deep = 0;
		simulated = new HashSet<WorkDefinition>();
		Iterator i = spemPackage.getProcessStructure().getWorkDefinition().refAllOfClass().iterator();
		while (i.hasNext()) {
			WorkDefinition workDefinition = (WorkDefinition) i.next();
			for (int j = 0; j < SIMULATIONS; j++) {
				simulate(workDefinition, spemPackage);
			}
			simulated.add(workDefinition);
		}
	}

	/**
	 * Simulates a work definition (composite process)
	 */
	@SuppressWarnings("unchecked")
	private void simulate(WorkDefinition workDefinition, SpemPackage spemPackage) throws CharonException {
		if (!belongs(workDefinition, simulated)) {
			List<Transition> simulationQueue = new ArrayList<Transition>();
			Map<PseudoState, Collection<Transition>> synchronisms = new HashMap<PseudoState, Collection<Transition>>();
	
			if (++deep > MAXIMUM_DEEP) {
				throw new CharonException("Possible infinite recursion at work definition " + workDefinition.getName());
			}
			
			PseudoState initialState = getInitialState(workDefinition, spemPackage);
			simulationQueue.addAll(initialState.getOutgoing());
			if (simulationQueue.isEmpty()) {
				throw new CharonException("No transition departing from the initial state of work definition " + workDefinition.getName());
			}
	
			int steps = 1;
			while (!simulate(simulationQueue, synchronisms, workDefinition, spemPackage)) {
				if (simulationQueue.isEmpty()) {
					throw new CharonException("Not possible to reach the end of work definition " + workDefinition.getName());
				}
				if (++steps > MAXIMUM_STEPS) {
					throw new CharonException("Possible infinite loop at work definition " + workDefinition.getName());
				}
			}
			deep--;
		}
	}
	
	/**
	 * Performs a simulation step on the simulation queue
	 */
	@SuppressWarnings("unchecked")
	public boolean simulate(List<Transition> simulationQueue, Map<PseudoState, Collection<Transition>> synchronisms, WorkDefinition workDefinition, SpemPackage spemPackage) throws CharonException {
		Transition transition = simulationQueue.remove(0);
		StateVertex stateVertex = transition.getTarget();

		if (stateVertex.refIsInstanceOf(spemPackage.getStateMachines().getPseudoState().refMetaObject(), false)) {
			simulationQueue.addAll(simulate((PseudoState)stateVertex, transition, synchronisms, workDefinition, spemPackage));
		} else if (stateVertex.refIsInstanceOf(spemPackage.getActivityGraphs().getActionState().refMetaObject(), false)) {
			simulationQueue.addAll(simulate((ActionState)stateVertex, spemPackage));
		} else if (stateVertex.refIsInstanceOf(spemPackage.getActivityGraphs().getObjectFlowState().refMetaObject(), false)) {
			simulationQueue.addAll(simulate((ObjectFlowState)stateVertex));
		} else if (stateVertex.refIsInstanceOf(spemPackage.getStateMachines().getFinalState().refMetaObject(), false)) {
			return true;
		} else {
			throw new CharonException("Undetected type of StateVertex object: " + stateVertex.getClass().getName());
		}

		return false;
	}

	/**
	 * Simulates a pseudo state (initial, synchronism and decision)
	 */
	@SuppressWarnings("unchecked")
	private Collection simulate(PseudoState pseudoState, Transition transition, Map<PseudoState, Collection<Transition>> synchronisms, WorkDefinition workDefinition, SpemPackage spemPackage) throws CharonException {
		if (PseudoStateKindEnum.PK_INITIAL.equals(pseudoState.getKind())) {
			throw new CharonException("Invalid transition to a initial state in work definition " + workDefinition.getName());
		} else if (PseudoStateKindEnum.PK_FORK.equals(pseudoState.getKind())){
			return pseudoState.getOutgoing();
		} else if (PseudoStateKindEnum.PK_JOIN.equals(pseudoState.getKind())) {
			Collection<Transition> synchronism = synchronisms.get(pseudoState);
			if (synchronism == null) {
				synchronism = new HashSet<Transition>();
				synchronisms.put(pseudoState, synchronism);
			}
			synchronism.add(transition);
			if (contains(synchronism, pseudoState.getIncoming())) {
				synchronisms.remove(pseudoState);
				return pseudoState.getOutgoing();
			} else {
				return Collections.emptyList();
			}
		} else if (PseudoStateKindEnum.PK_JUNCTION.equals(pseudoState.getKind())) {
			List options = new ArrayList(pseudoState.getOutgoing());
			if (options.isEmpty()) {
				throw new CharonException("No transition departing from decision in work definition " + workDefinition.getName());
			}
			return Collections.singleton(options.get((int)Math.floor(options.size() * Math.random())));
		} else {
			throw new CharonException("Undetected type of PseudoState object " + pseudoState.getName());
		}
	}

	/**
	 * Simulates a work definition (activity and process)
	 */
	private Collection simulate(ActionState actionState, SpemPackage spemPackage) throws CharonException {
		CallAction callAction = (CallAction)actionState.getEntry();
		WorkDefinition workDefinition = (WorkDefinition)callAction.getOperation();

		if (workDefinition.refIsInstanceOf(spemPackage.getProcessStructure().getWorkDefinition().refMetaObject(), false)) {
			simulate(workDefinition, spemPackage);
		} else if (!workDefinition.refIsInstanceOf(spemPackage.getProcessStructure().getActivity().refMetaObject(), false)) {
			throw new CharonException("Undetected type of ActionState object: " + actionState.getClass().getName());
		}
		
		return actionState.getOutgoing();
	}
	
	/**
	 * Simulates an object flow state
	 */
	private Collection simulate(ObjectFlowState objectFlowState) {
		return objectFlowState.getOutgoing();
	}

	/**
	 * Provides the initial state of a work definition
	 */
	private PseudoState getInitialState(WorkDefinition workDefinition, SpemPackage spemPackage) throws CharonException {
		Collection<PseudoState> result = new ArrayList<PseudoState>();
		
		Collection activityGraphs = spemPackage.getStateMachines().getABehaviorContext().getBehavior(workDefinition);
		
		if (activityGraphs.isEmpty()) {
			throw new CharonException("No activity graph defined for work definition " + workDefinition.getName());
		}
		
		if (activityGraphs.size() > 1) {
			throw new CharonException("More than one activity graph defined for work definition " + workDefinition.getName());
		}
		
		ActivityGraph activityGraph = (ActivityGraph) activityGraphs.iterator().next();
		CompositeState topState = (CompositeState) activityGraph.getTop();
		Iterator j = topState.getSubvertex().iterator();
		while (j.hasNext()) {
			StateVertex stateVertex = (StateVertex) j.next();
			if (stateVertex.refIsInstanceOf(spemPackage.getStateMachines().getPseudoState().refMetaObject(), false)) {
				PseudoState pseudoState = (PseudoState) stateVertex;
				if (PseudoStateKindEnum.PK_INITIAL.equals(pseudoState.getKind())) {
					result.add(pseudoState);
				}
			}
		}
		
		if (result.isEmpty()) {
			throw new CharonException("No initial state defined for work definition " + workDefinition.getName());
		}
		
		if (result.size() > 1) {
			throw new CharonException("More than one initial state defined for work definition " + workDefinition.getName());
		}
		
		return result.iterator().next();
	}
	
	/**
	 * True if c1 contains c2
	 */
	private boolean contains(Collection c1, Collection c2) {
		Iterator i = c2.iterator();
		while (i.hasNext()) {
			RefBaseObject o2 = (RefBaseObject) i.next();
			if (!belongs(o2, c1)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * True if o belongs to c
	 */
	private boolean belongs(RefBaseObject o, Collection c) {
		Iterator i = c.iterator();
		while (i.hasNext()) {
			RefBaseObject o2 = (RefBaseObject) i.next();
			if (o.refMofId().equals(o2.refMofId())) {
				return true;
			}
		}
		return false;
	}
}