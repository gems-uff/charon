package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import processstructure.WorkDefinition;
import spem.SpemPackage;
import statemachines.FinalState;
import statemachines.PseudoState;
import statemachines.StateMachine;
import statemachines.StateVertex;
import statemachines.Transition;
import actions.CallAction;
import activitygraphs.ActionState;
import activitygraphs.ActivityGraph;
import activitygraphs.ObjectFlowState;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonException;
import datatypes.PseudoStateKindEnum;

/**
 * This class is responsible of mapping SPEM into prolog facts.
 * 
 * @author Leo Murta
 * @version 1.0, 30/06/2005
 */
public class MappingAgent extends Agent {

	/**
	 * Singleton constructor
	 */
	public MappingAgent() {
		super();
	}

	/**
	 * Map all Work Definitions of a SPEM package into prolog facts. Goes recursivelly through the
	 * inner elements.
	 * 
	 * @param spemPackage Package containing all SPEM processes.
	 * @param rootProcess Root Process.
	 */
	public Collection<String> map(SpemPackage spemPackage) throws CharonException {
		Collection<String> facts = new HashSet<String>();
		
		// Process all composite processes
		Iterator i = spemPackage.getProcessStructure().getWorkDefinition().refAllOfType().iterator();
		while (i.hasNext()) {
			map((WorkDefinition) i.next(), spemPackage, facts);
		}
		
		return facts;
	}
	
	/**
	 * Maps a WorkDefinition (or Activity) into prolog facts. Goes recursivelly through the
	 * inner transitions.
	 */
	private void map(WorkDefinition workDefinition, SpemPackage spemPackage, Collection<String> facts) throws CharonException {
		facts.add("role('" + workDefinition.refMofId() + "','" + workDefinition.getPerformer().refMofId() + "')");

		// Map the inner transitions (for processes)
		Iterator i = spemPackage.getStateMachines().getABehaviorContext().getBehavior(workDefinition).iterator();
		while (i.hasNext()) {
			ActivityGraph activityGraph = (ActivityGraph) i.next();
			Iterator j = activityGraph.getTransitions().iterator();
			while (j.hasNext()) {
				map((Transition)j.next(), spemPackage, facts);
			}
		}
	}
	
	/**
	 * Maps a Transition into prolog facts. Goes through the source and target vertexes.
	 */
	private void map(Transition transition, SpemPackage spemPackage, Collection<String> facts) throws CharonException {
		StateVertex source = transition.getSource();
		StateVertex target = transition.getTarget();

		facts.add("transition(" + map(source, spemPackage, facts) + "," + map(target, spemPackage, facts) + ")");
	}
	
	/**
	 * Maps a State Vertex into prolog facts.
	 */
	private String map(StateVertex stateVertex, SpemPackage spemPackage, Collection<String> facts) throws CharonException {
		String result = null;
		
		if (stateVertex.refIsInstanceOf(spemPackage.getStateMachines().getPseudoState().refMetaObject(), false)) {
			result = map((PseudoState)stateVertex, spemPackage, facts);
		} else if (stateVertex.refIsInstanceOf(spemPackage.getActivityGraphs().getActionState().refMetaObject(), false)) {
			result = map((ActionState)stateVertex, spemPackage, facts);
		} else if (stateVertex.refIsInstanceOf(spemPackage.getActivityGraphs().getObjectFlowState().refMetaObject(), false)) {
			result = map((ObjectFlowState)stateVertex);
		} else if (stateVertex.refIsInstanceOf(spemPackage.getStateMachines().getFinalState().refMetaObject(), false)) {
			result = map((FinalState)stateVertex, spemPackage);
		} else {
			throw new CharonException("Undetected type of StateVertex object: " + stateVertex.getClass().getName());
		}
		
		return result;
	}

	/**
	 * Maps a Pseudo State (initial, fork, join and junction) into prolog facts.
	 */
	private String map(PseudoState pseudoState, SpemPackage spemPackage, Collection<String> facts) throws CharonException {
		String result = null;
		
		StateMachine stateMachine = spemPackage.getStateMachines().getATopStateMachine().getStateMachine(pseudoState.getContainer());
		WorkDefinition workDefinition = (WorkDefinition) spemPackage.getStateMachines().getABehaviorContext().getContext(stateMachine);
		
		if (PseudoStateKindEnum.PK_INITIAL.equals(pseudoState.getKind())) {
			result = "initial('" + workDefinition.refMofId() + "')";
		} else if (PseudoStateKindEnum.PK_FORK.equals(pseudoState.getKind()) || PseudoStateKindEnum.PK_JOIN.equals(pseudoState.getKind())) {
			result = "synchronism('" + pseudoState.refMofId() + "')";
		} else if (PseudoStateKindEnum.PK_JUNCTION.equals(pseudoState.getKind())) {
			facts.add("role('" + pseudoState.refMofId() + "','" + workDefinition.getPerformer().refMofId() + "')");
			
			Iterator i = pseudoState.getOutgoing().iterator();
			while (i.hasNext()) {
				Transition transition = (Transition) i.next();
				StateVertex stateVertex = transition.getTarget();
				facts.add("option('" + pseudoState.refMofId() + "','" + transition.getName() + "'," + map(stateVertex, spemPackage, facts) + ")");
			}
			
			result = "decision('" + pseudoState.refMofId() + "')";
		} else {
			throw new CharonException("Undetected type of PseudoState object " + pseudoState.getName());
		}

		return result;
	}
	
	/**
	 * Maps an Action State into prolog facts.
	 */
	private String map(ActionState actionState, SpemPackage spemPackage, Collection<String> facts) throws CharonException {
		CallAction callAction = (CallAction)actionState.getEntry();
		WorkDefinition workDefinition = (WorkDefinition)callAction.getOperation();
		
		facts.add("type('" + actionState.refMofId() + "','" + workDefinition.refMofId() + "')");
		
		if (workDefinition.refIsInstanceOf(spemPackage.getProcessStructure().getActivity().refMetaObject(), false))
			return "activity('" + actionState.refMofId() + "')";
		else if (workDefinition.refIsInstanceOf(spemPackage.getProcessStructure().getWorkDefinition().refMetaObject(), false))
			return "process('" + actionState.refMofId() + "')";
		else
			throw new CharonException("Undetected type of ActionState object: " + actionState.getClass().getName());
	}
	
	/**
	 * Maps a Object Flow State into prolog facts.
	 */
	private String map(ObjectFlowState objectFlowState) {
		return "product('" + objectFlowState.refMofId() + "')";
	}


	/**
	 * Maps a Final State into prolog facts.
	 */
	private String map(FinalState finalState, SpemPackage spemPackage) {
		StateMachine stateMachine = spemPackage.getStateMachines().getATopStateMachine().getStateMachine(finalState.getContainer());
		WorkDefinition workDefinition = (WorkDefinition) spemPackage.getStateMachines().getABehaviorContext().getContext(stateMachine);

		return "final('" + workDefinition.refMofId() + "')";
	}
}