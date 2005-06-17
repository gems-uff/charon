package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Classe que representa um papel que pode ser associado a v�rios usu�rios
 *
 * @author Leo Murta
 * @version 1.0, 22/11/2001
 */
public class Papel {
	/**
	 * Nome do papel
	 */
	private String nome = null;

	/**
	 * Lista de usu�rios associados
	 */
	private Collection<String> usuarios = null;

	/**
	 * Constroi o papel
	 *
	 * @param nome Nome do papel
	 */
	public Papel(String nome) {
		this.nome = nome;
		usuarios = new ArrayList<String>();
	}

	/**
	 * Fornece a lista de usu�rios
	 *
	 * @return Lista de Usu�rios
	 */
	public Collection<String> getUsuarios() {
		return Collections.unmodifiableCollection(usuarios);
	}

	/**
	 * Fornece a representa��o prolog do papel
	 *
	 * @return Predicados da forma "usuario(login, nome_papel)"
	 */
	public List getProlog() {
		List<String> prolog = new ArrayList<String>();

		for (String login : usuarios) {
			prolog.add("usuario('" + login + "','" + nome + "')");
		}

		return prolog;
	}

	/**
	 * Fornece a representa��o textual do papel
	 *
	 * @return Nome do papel
	 */
	public String toString() {
		return nome;
	}
}