package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.List;

/**
 * Classe que representa uma decis�o que pode ser tomada (respondida)
 *
 * @author Leo Murta
 * @version 1.0, 22/12/2001
 */
public class Decisao {
	/**
	 * Id da decis�o
	 */
	private long id;

	/**
	 * Contexto da decis�o
	 */
	private String contexto = null;

	/**
	 * Respostas poss�veis da decis�o
	 */
	private List respostas = null;

	/**
	 * Resposta escolhida na decis�o
	 */
	private String resposta = null;

	/**
	 * Constroi a decis�o
	 *
	 * @param respostas Lista com as Strings das respostas poss�veis
	 */
	public Decisao(long id, String contexto, List respostas) {
		this.id = id;
		this.contexto = contexto;
		this.respostas = respostas;
	}

	/**
	 * Fornece o identificador da decis�o
	 */
	public long getId() {
		return id;
	}

	/**
	 * Fornece o contexto da decis�o
	 */
	public String getContexto() {
		return contexto;
	}

	/**
	 * Fornece as respostas poss�veis
	 */
	public List getRespostas() {
		return respostas;
	}

	/**
	 * Fornece a resposta escolhida
	 */
	public String getResposta() {
		return resposta;
	}

	/**
	 * Atribui a resposta escolhida
	 */
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	/**
	 * Fornece a representa��o textual da decis�o
	 */
	public String toString() {
		return super.toString();
	}
}