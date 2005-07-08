package br.ufrj.cos.lens.odyssey.tools.charon.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.netbeans.api.mdr.MDRepository;

import statemachines.PseudoState;
import statemachines.StateVertex;
import statemachines.Transition;

/**
 * This class represents a decision.
 *
 * @author Leo Murta
 * @version 1.0, 22/12/2001
 */
public class CharonDecision extends CharonElement {
	/**
	 * Collection of the ID of the selections (decisions made)
	 */
	Collection<String> selectionIds;

	/**
	 * Constructs the decision
	 */
	public CharonDecision(String id, String context) {
		super(id, context);
		selectionIds = new ArrayList<String>();
	}

	/**
	 * Adds a selection to the decision. A selection is an option chosen by the
	 * user to "answer" this decision.
	 */
	public void addSpemSelection(StateVertex selection) {
		selectionIds.add(selection.refMofId());
	}
	
	/**
	 * Provides the SPEM Decision that is mapped to this Charon Decision
	 */
	public PseudoState getSpemDecision(MDRepository repository) {
		return (PseudoState) repository.getByMofId(getId());
	}

	/**
	 * Provides the SPEM Elements that are options to "answer" this decision
	 */
	public Collection<StateVertex> getSpemOptions(MDRepository repository) {
		Collection<StateVertex> result = new ArrayList<StateVertex>();

		PseudoState decision = getSpemDecision(repository);
		Iterator i = decision.getOutgoing().iterator();
		while (i.hasNext()) {
			Transition transition = (Transition) i.next();
			result.add(transition.getTarget());
		}

		return result;
	}
	
	/**
	 * Provides all selections made by the user to this decision
	 */
	public Collection<String> getSelectionIds() {
		return Collections.unmodifiableCollection(selectionIds);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Decision " + getId();
	}
}