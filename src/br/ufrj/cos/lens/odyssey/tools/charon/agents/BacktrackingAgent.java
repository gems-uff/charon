package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * Agente respons�vel por retroceder a execu��o do processo
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
	 * Exibe a jenela de retrocesso. Rea��o � sele��o de op��o de menu por
	 * parte do usu�rio.
	 * @param knowledgeBase Base que est� relacionada com o evanto
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

		if ((!knowledgeBase.isSolvable("executando(processo(raiz), [], _).")) && (!knowledgeBase.isSolvable("executado(processo(raiz), [], _, _).")))
			knowledgeBase.setState(KnowledgeBase.PENDING);

		disconnect();
	}

	/**
	 * Fornece a lista de regras existentes no agente
	 */
	public Collection<String> getRules() {
		if (regras == null) {
			regras = new ArrayList<String>();

			regras.add("(desloca(T) :- " + "executando(E, C, Ti), " + "Ti > T, " + "retract(executando(E, C, Ti)), " + "!, " + "desloca(T))");
			regras.add("(desloca(T) :- " + "executado(E, C, Ti, Tf), " + "Ti > T, " + "retract(executado(E, C, Ti, Tf)), " + "!, " + "desloca(T))");
			regras.add("(desloca(T) :- " + "finalizado(IdP, C, Tf, U), " + "Tf > T, " + "retract(finalizado(IdP, C, Tf, U)), " + "!, " + "desloca(T))");
			regras.add("(desloca(T) :- " + "respondido(IdD, C, R, Tr, U), " + "Tr > T, " + "retract(respondido(IdD, C, R, Tr, U)), " + "!, " + "desloca(T))");
			regras.add("(desloca(T) :- " + "executado(E, C, Ti, Tf), " + "Ti =< T, " + "Tf > T, " + "retract(executado(E, C, Ti, Tf)), " + "assertz(executando(E, C, Ti)), " + "!, " + "desloca(T))");
		}

		return regras;
	}
}