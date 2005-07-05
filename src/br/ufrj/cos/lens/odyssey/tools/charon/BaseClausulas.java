package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import JIP.engine.JIPClause;
import JIP.engine.JIPClausesDatabase;

public class BaseClausulas extends JIPClausesDatabase {
	
	/**
	 * Indica se a máquina de processo está em modo DEBUG
	 */
	public static final boolean DEBUG = false;
	
	/**
	 * Tipo de base utilizada para armazenar clausulas de mapeamento
	 */
	public static final String MAPEAMENTO = "Mapeamento";

	/**
	 * Tipo de base utilizada para armazenar clausulas de execução
	 */
	public static final String EXECUCAO = "Execucao";

	/**
	 * Tipo de base utilizada para armazenar clausulas de agentes
	 */
	public static final String AGENTES = "Agentes";

	/**
	 * Tipo da base de clausulas
	 */
	private String tipo = null;

	/**
	 * Lista com todas as bases de cláusulas existentes
	 */
	private static List basesClausulas = new ArrayList();

	/**
	 * Base de conhecimento que será utilizada para associas às bases de clausulas
	 * criadas.
	 * Gambiarra necessária para permitir que exista conexão entre a base de
	 * conhecimento e a base de clausulas.
	 */
	private static KnowledgeBase baseConhecimentoResponsavel = null;

	/**
	 * Base de conhecimento associada a base de cláusulas
	 */
	private KnowledgeBase baseConhecimento = null;

	/**
	 * Lista com as clausulas
	 */
	private List clausulas = null;

	/**
	 * Constroi uma base de clausulas
	 */
	public BaseClausulas() {
		clausulas = new ArrayList();
		baseConhecimento = baseConhecimentoResponsavel;
		basesClausulas.add(this);
	}

	/**
	 * Atribui uma base de conhecimento responsável. Essa base receberá o callback
	 * para o armazenamento dos fatos de mapeamento e execução
	 */
	public static void setBaseConhecimentoResponsavel(KnowledgeBase responsavel) {
		baseConhecimentoResponsavel = responsavel;
	}

	/**
	 * Remove as clausulas de agentes que estão armazenadas nas bases de clausulas
	 * vinculadas com uma determinada base de conhecimento
	 */
	public static void removeClausulasAgentes(KnowledgeBase base) {
		for (int i = 0; i < basesClausulas.size(); i++) {
			BaseClausulas baseClausulas = (BaseClausulas)basesClausulas.get(i);
			if ((baseClausulas.isResponsavel(base)) && (baseClausulas.isTipo(AGENTES)))
				baseClausulas.clear();
		}
	}

	/**
	 * Verifica se uma determinada base de conhecimento é responsável pela base de clausulas
	 */
	public boolean isResponsavel(KnowledgeBase base) {
		return (baseConhecimento == base);
	}

	/**
	 * Verifica se a base de clausulas é de um deteminado tipo
	 */
	public boolean isTipo(String tipo) {
		return this.tipo.equals(tipo);
	}

	/**
	 * Esvazia a base de clausulas
	 */
	public void clear() {
		// Mostra o que foi feito
		if (DEBUG)
			System.out.println("clear: " + clausulas);

		clausulas.clear();
	}

	/**
	 * Fornece um iterator com as clausulas da base. Necessário para a construção
	 * da enumeração de clausulas
	 */
	public Iterator getClausulas() {
		return clausulas.iterator();
	}

	/**
	 * Atribui o tipo da base de clausulas
	 */
	public void setAttributes(String atributos) {
		tipo = atributos;

		// Mostra o que foi feito
		if (DEBUG)
			System.out.println("setAttributes: " + atributos);
	}

	/**
	 * Adiciona uma clausula em uma determinada posição da base de clausulas
	 */
	public boolean addClauseAt(int posicao, JIPClause clausula) {
		clausulas.add(posicao, clausula);

		// Atualiza a base de conhecimento
		if (tipo.equals(MAPEAMENTO))
			baseConhecimento.getFatosMapeamento().add(clausula.toString());
		else if (tipo.equals(EXECUCAO))
			baseConhecimento.getFatosExecucao().add(clausula.toString());

		// Mostra o que foi feito
		if (DEBUG)
			System.out.println("addClauseAt (" + tipo + "): " + posicao + " -> " + clausula);

		return true;
	}

	/**
	 * Adiciona uma clausula na base de clausulas
	 */
	public boolean addClause(JIPClause clausula) {
		clausulas.add(clausula);

		// Atualiza a base de conhecimento
		if (tipo.equals(MAPEAMENTO))
			baseConhecimento.getFatosMapeamento().add(clausula.toString());
		else if (tipo.equals(EXECUCAO))
			baseConhecimento.getFatosExecucao().add(clausula.toString());

		// Mostra o que foi feito
		if (DEBUG)
			System.out.println("addClause (" + tipo + "): " + clausula);

		return true;
	}

	/**
	 * Remove uma clausula da base de clausulas
	 */
	public boolean removeClause(JIPClause clausula) {
		List clausulasRemover = new ArrayList();

		// verifica as clausulas que tem que ser removidas
		for (int i = 0; i < clausulas.size(); i++) {
			JIPClause clausulaArmazenada = (JIPClause)clausulas.get(i);
			if (clausulaArmazenada.toString().equals(clausula.toString()))
				clausulasRemover.add(clausulaArmazenada);
		}

		// remove
		clausulas.removeAll(clausulasRemover);

		// Atualiza a base de conhecimento
		if (tipo.equals(MAPEAMENTO))
			baseConhecimento.getFatosMapeamento().remove(clausula.toString());
		else if (tipo.equals(EXECUCAO))
			baseConhecimento.getFatosExecucao().remove(clausula.toString());

		// Mostra o que foi feito
		if (DEBUG)
			System.out.println("removeClause (" + tipo + "): " + clausula);

		return true;
	}

	/**
	 * Fornece as clausulas para a méquina de inferência
	 */
	public Enumeration clauses() {
		return new EnumeracaoClausulas(this);
	}
}