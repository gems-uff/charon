package br.ufrj.cos.lens.odyssey.tools.charon.entities;

import org.netbeans.api.mdr.MDRepository;

import statemachines.SimpleState;

/**
 * This class represents an activity.
 *
 * @author Leo Murta
 * @version 1.0, 22/12/2001
 */
public class CharonActivity extends CharonElement {

	/**
	 * Constructs the activity
	 */
	public CharonActivity(String id, String context) {
		super(id, context);
	}

	/**
	 * Provides the SPEM Activity that is mapped to this Charon Activity
	 */
	public SimpleState getSpemActivity(MDRepository repository) {
		return (SimpleState) repository.getByMofId(getId());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Activity " + getId();
	}
}