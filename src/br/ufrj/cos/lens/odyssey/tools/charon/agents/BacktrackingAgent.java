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
		super(0);
		setProativo(false);
	}

	/**
	 * Exibe a jenela de retrocesso. Reação à seleção de opção de menu por
	 * parte do usuário.
	 *
	 * @param origem Origem do evento
	 * @param base Base que está relacionada com o evanto
	 */
	public void executaReativo(Object origem, KnowledgeBase base) {
		switch (base.getState()) {
			case KnowledgeBase.BASE_SIMULANDO :
				JOptionPane.showMessageDialog(null, "The process is being simulated. Please, wait a moment and try again.", "Process Backtracking Agent", JOptionPane.INFORMATION_MESSAGE);
				return;

			case KnowledgeBase.BASE_PENDENTE :
				JOptionPane.showMessageDialog(null, "The process is waiting for its initialization. Please, wait a moment and try again.", "Process Backtracking Agent", JOptionPane.INFORMATION_MESSAGE);
				return;

			case KnowledgeBase.BASE_ERRO :
				JOptionPane.showMessageDialog(null, "The process has some errors. Please, correct them and reinstantiate it.", "Process Backtracking Agent", JOptionPane.INFORMATION_MESSAGE);
				return;
		}
		conecta(base);

//		getBase().getProlog().getRespostaBooleana("desloca(" + janela.getTempo() + ")");

		if ((!getBase().getProlog().getRespostaBooleana("executando(processo(raiz), [], _)")) && (!getBase().getProlog().getRespostaBooleana("executado(processo(raiz), [], _, _)")))
			getBase().setEstado(KnowledgeBase.BASE_PENDENTE);

		desconecta();
	}

	/**
	 * Não faz nada
	 */
	public synchronized void executaProativo() {}

	/**
	 * Fornece a lista de regras existentes no agente
	 */
	public Collection<String> getRegras() {
		if (regras == null) {
			regras = new ArrayList<String>();

			regras.add("(desloca(T) :- " + "executando(E, C, Ti), " + "Ti > T, " + "retract(executando(E, C, Ti)), " + "!, " + "desloca(T))");
			regras.add("(desloca(T) :- " + "executado(E, C, Ti, Tf), " + "Ti > T, " + "retract(executado(E, C, Ti, Tf)), " + "!, " + "desloca(T))");
			regras.add("(desloca(T) :- " + "finalizado(IdP, C, Tf, U), " + "Tf > T, " + "retract(finalizado(IdP, C, Tf, U)), " + "!, " + "desloca(T))");
			regras.add("(desloca(T) :- " + "respondido(IdD, C, R, Tr, U), " + "Tr > T, " + "retract(respondido(IdD, C, R, Tr, U)), " + "!, " + "desloca(T))");
			regras.add("(desloca(T) :- " + "executado(E, C, Ti, Tf), " + "Ti <= T, " + "Tf > T, " + "retract(executado(E, C, Ti, Tf)), " + "assertz(executando(E, C, Ti)), " + "!, " + "desloca(T))");
		}

		return regras;
	}
}