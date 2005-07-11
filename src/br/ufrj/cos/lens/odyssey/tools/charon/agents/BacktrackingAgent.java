package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * Agente responsável por retroceder a execução do processo
 *
 * @author Leo Murta
 * @version 1.0, 23/12/2001
 */
public class BacktrackingAgent extends Agent {
	/**
	 * Lista de regras do agente
	 */
	private List<String> regras = null;

	public BacktrackingAgent() {
		super();
	}

	/**
	 * Exibe a jenela de retrocesso. Reação à seleção de opção de menu por
	 * parte do usuário.
	 * @param knowledgeBase Base que está relacionada com o evanto
	 */
	public void reactiveExecution(KnowledgeBase knowledgeBase) {
		switch (knowledgeBase.getState()) {
			case KnowledgeBase.SIMULATING :
				JOptionPane.showMessageDialog(null, "The process is being simulated. Please, wait a moment and try again.", "Process Backtracking Agent", JOptionPane.INFORMATION_MESSAGE);
				return;

			case KnowledgeBase.PENDING :
				JOptionPane.showMessageDialog(null, "The process is waiting for its initialization. Please, wait a moment and try again.", "Process Backtracking Agent", JOptionPane.INFORMATION_MESSAGE);
				return;

			case KnowledgeBase.CORRUPTED :
				JOptionPane.showMessageDialog(null, "The process has some errors. Please, correct them and reinstantiate it.", "Process Backtracking Agent", JOptionPane.INFORMATION_MESSAGE);
				return;
		}
		connect(knowledgeBase);

//		getBase().getProlog().getRespostaBooleana("desloca(" + janela.getTempo() + ")");

		disconnect();
	}

	/**
	 * Fornece a lista de regras existentes no agente
	 */
	public Collection<String> getRules() {
		if (regras == null) {
			regras = new ArrayList<String>();

			regras.add("(backtrack(T) :- executing(E, C, Ti), Ti > T, retract(executing(E, C, Ti)), !, backtrack(T))");
			regras.add("(backtrack(T) :- executed(E, C, Ti, Tf), Ti > T, retract(executed(E, C, Ti, Tf)), !, backtrack(T))");
			regras.add("(backtrack(T) :- finished(IdP, C, Tf, U), Tf > T, retract(finished(IdP, C, Tf, U)), !, backtrack(T))");
			regras.add("(backtrack(T) :- selected(IdD, C, R, Tr, U), Tr > T, retract(selected(IdD, C, R, Tr, U)), !, backtrack(T))");
			regras.add("(backtrack(T) :- executed(E, C, Ti, Tf), Ti =< T, Tf > T, retract(executed(E, C, Ti, Tf)), assertz(executing(E, C, Ti)), !, backtrack(T))");
		}

		return regras;
	}
}