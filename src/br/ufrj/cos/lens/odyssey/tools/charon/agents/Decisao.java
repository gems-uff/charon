package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.List;

/**
 * Classe que representa uma decisão que pode ser tomada (respondida)
 *
 * @author Leo Murta
 * @version 1.0, 22/12/2001
 */
public class Decisao {
	/**
	 * Id da decisão
	 */
	private long id;

	/**
	 * Contexto da decisão
	 */
	private String contexto = null;

	/**
	 * Respostas possíveis da decisão
	 */
	private List respostas = null;

	/**
	 * Resposta escolhida na decisão
	 */
	private String resposta = null;

	/**
	 * Constroi a decisão
	 *
	 * @param respostas Lista com as Strings das respostas possíveis
	 */
	public Decisao(long id, String contexto, List respostas) {
		this.id = id;
		this.contexto = contexto;
		this.respostas = respostas;
	}

	/**
	 * Fornece o identificador da decisão
	 */
	public long getId() {
		return id;
	}

	/**
	 * Fornece o contexto da decisão
	 */
	public String getContexto() {
		return contexto;
	}

	/**
	 * Fornece as respostas possíveis
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
	 * Fornece a representação textual da decisão
	 */
	public String toString() {
		return super.toString();
	}
}