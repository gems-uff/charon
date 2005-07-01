package br.ufrj.cos.lens.odyssey.tools.charon;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import processstructure.Activity;
import processstructure.WorkDefinition;
import spem.SpemPackage;
import statemachines.FinalState;
import statemachines.PseudoState;
import statemachines.SimpleState;
import statemachines.StateMachine;
import statemachines.StateVertex;
import statemachines.Transition;
import actions.CallAction;
import activitygraphs.ActivityGraph;
import datatypes.PseudoStateKindEnum;

/**
 * This class is responsible of mapping SPEM into prolog facts.
 * 
 * @author Leo Murta
 * @version 1.0, 30/06/2005
 */
public class Mapper implements Serializable {

	/**
	 * Singleton instance
	 */
	private static Mapper instance = null;

	/**
	 * Singleton constructor
	 */
	private Mapper() {}

	/**
	 * Provides the singleton instance
	 */
	public static synchronized Mapper getInstance() {
		if (instance  == null)
			instance = new Mapper();
		return instance;
	}
	
	/**
	 * Map a SPEM process into prolog facts.
	 * 
	 * @param spemPackage Package containing all SPEM processes.
	 * @param rootProcess Root Process.
	 */
	public Collection<String> map(SpemPackage spemPackage, WorkDefinition rootProcess) {
		Collection<String> facts = new HashSet<String>();
		
		// Run the map recursivelly
		map(spemPackage, facts);

		// Set the root process 
		facts.add("processoRaiz('" + rootProcess.refMofId() + "')");
		
		return facts;
	}
	
	/**
	 * Map all Work Definitions of a SPEM package into prolog facts. Goes recursivelly through the
	 * inner elements.
	 */
	private void map(SpemPackage spemPackage, Collection<String> facts) {
		// Process all composite processes
		Iterator i = spemPackage.getProcessStructure().getWorkDefinition().refAllOfClass().iterator();
		while (i.hasNext()) {
			map((WorkDefinition) i.next(), spemPackage, facts);
		}
		
		// Process all activities
		i = spemPackage.getProcessStructure().getActivity().refAllOfClass().iterator();
		while (i.hasNext()) {
			map((Activity) i.next(), facts);
		}		
	}
	
	/**
	 * Maps an Activity into prolog facts. Also maps the Process Roles related to the Activity.
	 */
	private void map(Activity activity, Collection<String> facts) {
		facts.add("processoPrimitivo('" + activity.refMofId() + "')");
		facts.add("papel('" + activity.refMofId() + "','" + activity.getPerformer().refMofId() + "')");
		
		// Assistent Roles
//		Iterator i = activity.getAssistant().iterator();
//		while (i.hasNext()) {
//			ProcessRole processRole = (ProcessRole) i.next();
//			facts.add("papel('" + activity.refMofId() + "','" + processRole.refMofId() + "')");
//		}
	}

	/**
	 * Maps a composite SPEM Process into prolog facts. Goes recursivelly through the
	 * inner transitions.
	 */
	private void map(WorkDefinition workDefinition, SpemPackage spemPackage, Collection<String> facts) {
		facts.add("processoComposto('" + workDefinition.refMofId() + "')");
		facts.add("papel('" + workDefinition.refMofId() + "','" + workDefinition.getPerformer().refMofId() + "')");

		// Map the inner transitions
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
	private void map(Transition transition, SpemPackage spemPackage, Collection<String> facts) {
		StateVertex source = transition.getSource();
		StateVertex target = transition.getTarget();

		facts.add("fluxo(" + map(source, spemPackage, facts) + "," + map(target, spemPackage, facts) + ")");
	}
	
	/**
	 * Maps a State Vertex into prolog facts.
	 */
	private String map(StateVertex stateVertex, SpemPackage spemPackage, Collection<String> facts) {
		String result = null;
		
		if (stateVertex.refIsInstanceOf(spemPackage.getStateMachines().getPseudoState().refMetaObject(), true)) {
			result = map((PseudoState)stateVertex, spemPackage, facts);
		} else if (stateVertex.refIsInstanceOf(spemPackage.getStateMachines().getSimpleState().refMetaObject(), true)) {
			result = map((SimpleState)stateVertex, facts);
		} else if (stateVertex.refIsInstanceOf(spemPackage.getStateMachines().getFinalState().refMetaObject(), true)) {
			result = map((FinalState)stateVertex);
		} else {
			throw new RuntimeException("Undetected type of StateVertex object: " + stateVertex.getName());
		}
		
		return result;
	}

	/**
	 * Maps a Pseudo State (initial, fork, join and junction) into prolog facts.
	 */
	private String map(PseudoState pseudoState, SpemPackage spemPackage, Collection<String> facts) {
		String result = null;
		
		StateMachine stateMachine = spemPackage.getStateMachines().getATopStateMachine().getStateMachine(pseudoState.getContainer());
		WorkDefinition workDefinition = (WorkDefinition) spemPackage.getStateMachines().getABehaviorContext().getContext(stateMachine);
		
		if (PseudoStateKindEnum.PK_INITIAL.equals(pseudoState.getKind())) {
			result = "inicio('" + workDefinition.refMofId() + "')";
		} else if (PseudoStateKindEnum.PK_FORK.equals(pseudoState.getKind())) {
			result = "sincronismo('" + pseudoState.refMofId() + "')";
		} else if (PseudoStateKindEnum.PK_JOIN.equals(pseudoState.getKind())) {
			result = "sincronismo('" + pseudoState.refMofId() + "')";
		} else if (PseudoStateKindEnum.PK_JUNCTION.equals(pseudoState.getKind())) {
			facts.add("papel('" + pseudoState.refMofId() + "','" + workDefinition.getPerformer().refMofId() + "')");
			result = "decisao('" + pseudoState.refMofId() + "')";
		} else {
			throw new RuntimeException("Undetected type of PseudoState object: " + pseudoState.getName());
		}

		return result;
	}
	
	/**
	 * Maps a Simple State into prolog facts.
	 */
	private String map(SimpleState simpleState, Collection<String> facts) {
		CallAction callAction = (CallAction)simpleState.getEntry();
		WorkDefinition workDefinition = (WorkDefinition)callAction.getOperation();
		
		facts.add("classeProcesso('" + simpleState.refMofId() + "','" + workDefinition.refMofId() + "')");
		
		return "processo('" + simpleState.refMofId() + "')";
	}

	/**
	 * Maps a Final State into prolog facts.
	 */
	private String map(FinalState finalState) {
		return "termino";
	}
}