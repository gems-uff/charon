package br.ufrj.cos.lens.odyssey.tools.charon.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.netbeans.api.mdr.MDRepository;

import statemachines.PseudoState;
import statemachines.Transition;

/**
 * This class represents a decision.
 *
 * @author Leo Murta
 * @version 1.0, 22/12/2001
 */
public class CharonDecision extends CharonElement {
	/**
	 * Collection of the selected options (answers) of this decision
	 */
	Collection<String> selectedOptions;
	
	/**
	 * Constructs the decision
	 */
	public CharonDecision(String id, String context) {
		super(id, context);
		selectedOptions = new ArrayList<String>();
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
	public Collection<String> getOptions(MDRepository repository) {
		Collection<String> options = new ArrayList<String>();

		PseudoState decision = getSpemDecision(repository);
		Iterator i = decision.getOutgoing().iterator();
		while (i.hasNext()) {
			Transition transition = (Transition) i.next();
			options.add(transition.getName());
		}
		
		return options;
	}
	
	/**
	 * Selects an option (answers) for this decision
	 */
	public void addSelectedOption(String option) {
		selectedOptions.add(option);
	}
	
	/**
	 * Provides all selections made by the user to this decision
	 */
	public Collection<String> getSelectedOptions() {
		return Collections.unmodifiableCollection(selectedOptions);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Decision " + getId();
	}
}