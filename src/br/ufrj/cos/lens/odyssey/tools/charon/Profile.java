package br.ufrj.cos.lens.odyssey.tools.charon;

import java.io.Serializable;

/**
 * Representa o profile de um usuário para a máquina de processos
 * @author Leonardo Murta
 * @version 1.0, 23/12/2001
 */
public class Profile implements Serializable {
	/**
	 * Opção de aparecimento do agente no proximo login
	 */
	public static final int MOSTRA_PROXIMO_LOGIN = 0;

	/**
	 * Opção de aparecimento do agente em um minuto
	 */
	public static final int MOSTRA_UM_MINUTO = 1;

	/**
	 * Opção de aparecimento do agente em dez minutos
	 */
	public static final int MOSTRA_DEZ_MINUTOS = 2;

	/**
	 * Opção de aparecimento do agente em meia hora
	 */
	public static final int MOSTRA_MEIA_HORA = 3;

	/**
	 * Opção de aparecimento do agente em uma hora
	 */
	public static final int MOSTRA_UMA_HORA = 4;

	/**
	 * Opção de aparecimento do agente em um dia
	 */
	public static final int MOSTRA_UM_DIA = 5;

	/**
	 * Opção de aparecimento do agente em uma semana
	 */
	public static final int MOSTRA_UMA_SEMANA = 6;

	/**
	 * Opção de aparecimento do agente em um mes
	 */
	public static final int MOSTRA_UM_MES = 7;

	/**
	 * Opção de aparecimento do agente desligada
	 */
	public static final int MOSTRA_NUNCA = 8;

	/**
	 * Opcao de aparecimento do agente de acompanhamento
	 */
	private int opcaoAparecimento = MOSTRA_PROXIMO_LOGIN;

	/**
	 * Último tempo de aparecimento em segundos
	 */
	private long ultimoTempoAparecimento = 0;

	/**
	 * Indica se é o primeiro login do usuário
	 */
	private boolean novoLogin = true;

	/**
	 * Constroi um novo profile vazio
	 */
	public Profile() {}

	/**
	 * Atribui uma nova opção de aparecimento
	 */
	public void setOpcaoAparecimento(int opcaoAparecimento) {
		long tempo = System.currentTimeMillis() / 1000;
		this.opcaoAparecimento = opcaoAparecimento;
		ultimoTempoAparecimento = tempo;
	}

	/**
	 * Fornece a opção de aparecimento
	 */
	public int getOpcaoAparecimento() {
		return opcaoAparecimento;
	}

	/**
	 * Avisa que ocorreu um novo login
	 */
	public void setNovoLogin() {
		novoLogin = true;
	}

	/**
	 * Verifica se o agente deve ser exibido agora
	 */
	public boolean mostraAgora() {
		long tempo = System.currentTimeMillis() / 1000;

		boolean mostra = false;
		switch (opcaoAparecimento) {
			case MOSTRA_PROXIMO_LOGIN :
				mostra = novoLogin;
				break;
			case MOSTRA_UM_MINUTO :
				mostra = (tempo >= (ultimoTempoAparecimento + 60));
				break;
			case MOSTRA_DEZ_MINUTOS :
				mostra = (tempo >= (ultimoTempoAparecimento + (10 * 60)));
				break;
			case MOSTRA_MEIA_HORA :
				mostra = (tempo >= (ultimoTempoAparecimento + (30 * 60)));
				break;
			case MOSTRA_UMA_HORA :
				mostra = (tempo >= (ultimoTempoAparecimento + (60 * 60)));
				break;
			case MOSTRA_UM_DIA :
				mostra = (tempo >= (ultimoTempoAparecimento + (24 * 60 * 60)));
				break;
			case MOSTRA_UMA_SEMANA :
				mostra = (tempo >= (ultimoTempoAparecimento + (7 * 24 * 60 * 60)));
				break;
			case MOSTRA_UM_MES :
				mostra = (tempo >= (ultimoTempoAparecimento + (30 * 24 * 60 * 60)));
				break;
			case MOSTRA_NUNCA :
				mostra = false;
		}

		if (mostra) {
			ultimoTempoAparecimento = tempo;
			novoLogin = false;
		}

		return mostra;
	}
}