package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Timer;

import br.ufrj.cos.lens.odyssey.tools.charon.BaseConhecimento;

/**
 * Classe que representa um agente. Nela são implementados os mecanismos básicos
 * para que se possa construir um agente inteligente.
 * @author Leo Murta
 * @version 1.0, 11/12/2001
 */
public abstract class Agente {
	/**
	 * Conjunto de agentes que devem ser avisados quando este agente fizer algo
	 * (propriedade reativa)
	 */
	private Set escutadores = new HashSet();

	/**
	 * Temporizador que faz com que o agente seja autonomo, procurando por seu
	 * objetivo a cada segundo.
	 */
	private Timer temporizador = null;

	/**
	 * Base de conhecimento em que o agente está conectado
	 */
	private BaseConhecimento base = null;

	/**
	 * Constroi o agente iniciando seu temporizador
	 *
	 * @param intervaloEventoProativo Define o tempo entre os eventos proativos
	 *                                (em milisegundos)
	 */
	public Agente(int intervaloEventoProativo) {
		temporizador = new Timer(intervaloEventoProativo, new Disparador(this));
		temporizador.start();
	}

	/**
	 * Adiciona um agente no conjunto de agentes interessados em ações deste agente
	 */
	public void addEscutador(Agente agente) {
		escutadores.add(agente);
	}

	/**
	 * Remove um agente do conjunto de agentes interessados em ações deste agente
	 */
	public void removeEscutador(Agente agente) {
		escutadores.remove(agente);
	}

	/**
	 * Fornece um iterador de escutadores
	 */
	public Iterator getEscutadores() {
		return escutadores.iterator();
	}
	/**
	 * Conecta o agente em uma base de conhecimento
	 *
	 * @param base Base de conhecimento que o agente se conectará
	 */
	protected void conecta(BaseConhecimento base) {
		base.conecta(this);
		this.base = base;
	}

	/**
	 * Desconecta o agente da base de conhecimento que ele está atualmente conectado
	 */
	protected void desconecta() {
		if (base == null)
			return;

		base.desconecta();
		this.base = null;
	}

	/**
	 * Para o temporizador de interação pró-ativa
	 */
	protected void setProativo(boolean liga) {
		if (liga) {
			if (!temporizador.isRunning())
				temporizador.start();
		} else {
			if (temporizador.isRunning())
				temporizador.stop();
		}
	}

	/**
	 * Fornece a base em que o agente está atualmente conectado
	 */
	protected BaseConhecimento getBase() {
		return base;
	}

	/**
	 * Fornece a lista de regras existentes no agente
	 */
	public abstract Iterator getRegras();

	/**
	 * Executa este agente após a notificação de que aconteceu algum evento
	 * Para que essa notificação aconteça, é necessário que o agente se cadastre
	 * como escutador no outro agente (notificação automática). Ou que alguma
	 * entidade externa lance a notificação através do Disparador.
	 *
	 * @param origem Origem do evento
	 * @param base Base que está relacionada com o evanto
	 */
	public abstract void executaReativo(Object origem, BaseConhecimento base);

	/**
	 * Executa este agente após a notificaçao vinda do temporizador, fazendo com
	 * que o agente seja autônomo.
	 */
	public abstract void executaProativo();
}
