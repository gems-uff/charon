package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * Classe respons�vel por disparar a execu��o reativa ou pr�-ativa em um
 * determinado agente
 *
 * @author Leo Murta
 * @version 1.0, 11/12/2001
 */
public class Dispatcher implements ActionListener, Runnable {
	/**
	 * Execu��o do tipo reativa
	 */
	private static final int REATIVO = 1;

	/**
	 * Execu��o do tipo pr�-ativa
	 */
	private static final int PROATIVO = 2;

	/**
	 * Tipo de execu�ao selecionada
	 */
	private int tipo;

	/**
	 * Agente de origem do evento, usado no caso reativo
	 */
	private Agent agenteFonte = null;

	/**
	 * Agente alvo do evento
	 */
	private Agent agenteAlvo = null;

	/**
	 * Base em que o agente origem se desconectou, usado no caso reativo
	 */
	private KnowledgeBase base = null;

	/**
	 * Constroi o disparador configurando o agente alvo para execu��o reativa
	 *
	 * @param origem Objeto de origem do evento
	 * @param agenteAlvo Agente destino do evento
	 * @param base Base que teve o agente origem desconectado
	 */
	public Dispatcher(Object origem, Agent agenteAlvo, KnowledgeBase base) {
		tipo = REATIVO;

		this.agenteAlvo = agenteAlvo;
		this.base = base;

		this.actionPerformed(null);
	}

	/**
	 * Constroi o disparador configurando o agente alvo para execu��o pr�-ativa
	 *
	 * @param agenteAlvo Agente destino do evento
	 */
	public Dispatcher(Agent agenteAlvo) {
		tipo = PROATIVO;

		this.agenteAlvo = agenteAlvo;
	}

	/**
	 * Indica que o disparador deve disparar a execu��o agente alvo
	 */
	public void actionPerformed(ActionEvent evento) {
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * Chama a execu��o reativa do agente alvo
	 */
	public void run() {
		if (agenteAlvo == null)
			return;

		if (tipo == REATIVO)
			agenteAlvo.executaReativo(agenteFonte, base);
		else if (tipo == PROATIVO)
			agenteAlvo.executaProativo();
	}
}