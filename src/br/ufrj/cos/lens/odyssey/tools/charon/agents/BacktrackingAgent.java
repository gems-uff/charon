package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * This agent is responsible for backtracking the process in case of mistake
 *
 * @author Leo Murta
 * @version 1.0, 23/12/2001
 */
public class BacktrackingAgent extends Agent {
	
	/**
	 * Agent's rules
	 */
	private List<String> rules = null;

	/**
	 * Constructs the agent
	 */
	public BacktrackingAgent() {
		super();
	}

	/**
	 * Backtracks a given knowledge base.
	 */
	public void backtrack(KnowledgeBase knowledgeBase, int seconds) {
		connect(knowledgeBase);

		knowledgeBase.isSolvable("backtrack(" + seconds + ")");

		disconnect();
	}

	/**
	 * Provides the agent's rules
	 */
	public Collection<String> getRules() {
		if (rules == null) {
			rules = new ArrayList<String>();

			rules.add("(backtrack(T) :- executing(E, C, Ti), Ti > T, retract(executing(E, C, Ti)), !, backtrack(T))");
			rules.add("(backtrack(T) :- executed(E, C, Ti, Tf), Ti > T, retract(executed(E, C, Ti, Tf)), !, backtrack(T))");
			rules.add("(backtrack(T) :- finished(IdP, C, Tf, U), Tf > T, retract(finished(IdP, C, Tf, U)), !, backtrack(T))");
			rules.add("(backtrack(T) :- selected(IdD, C, R, Tr, U), Tr > T, retract(selected(IdD, C, R, Tr, U)), !, backtrack(T))");
			rules.add("(backtrack(T) :- executed(E, C, Ti, Tf), Ti =< T, Tf > T, retract(executed(E, C, Ti, Tf)), assertz(executing(E, C, Ti)), !, backtrack(T))");
		}

		return rules;
	}
}