package br.ufrj.cos.lens.odyssey.tools.charon.agents;


/**
 * Classe que representa um processo que pode ser finalizado
 *
 * @author Leo Murta
 * @version 1.0, 22/12/2001
 */
public class Processo {
	/**
	 * Id do processo
	 */
	private long id;

	/**
	 * Contexto do processo
	 */
	private String contexto = null;

	/**
	 * Constroi o processo
	 */
	public Processo(long id, String contexto) {
		this.id = id;
		this.contexto = contexto;
	}

	/**
	 * Fornece o identificador do processo
	 */
	public long getId() {
		return id;
	}

	/**
	 * Fornece o contexto do processo
	 */
	public String getContexto() {
		return contexto;
	}

	/**
	 * Fornece a representação textual do processo
	 */
	public String toString() {
		return super.toString();
	}
}