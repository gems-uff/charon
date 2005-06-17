package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.Iterator;

import JIP.engine.JIPClause;
import JIP.engine.JIPClausesEnumeration;

/**
 * Classe responsável por fornecer a enumeraçao das clausulas da base de
 * clausulas para a máquina de inferência
 */
public class EnumeracaoClausulas extends JIPClausesEnumeration {
	/**
	 * Clausulas existentes
	 */
	private Iterator elementos = null;

	/**
	 * Constroi a classe com base em uma base de clausulas
	 */
	public EnumeracaoClausulas(BaseClausulas baseClausulas) {
		super(baseClausulas);
		elementos = baseClausulas.getClausulas();
	}

	/**
	 * Fornece a próxima clausula da enumeração
	 */
	public JIPClause nextClause() {
		return (JIPClause)elementos.next();
	}

	/**
	 * Informa se existem mais clausulas
	 */
	public boolean hasMoreElements() {
		return elementos.hasNext();
	}
}