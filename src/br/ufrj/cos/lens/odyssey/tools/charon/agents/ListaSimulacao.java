package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * Classe responsável por manter o controle da lista de elementos em simulação
 *
 * @author Leo Murta
 * @version 1.0, 15/12/2001
 */
public class ListaSimulacao {
	/**
	 * Map ordenado que associa tempos com uma lista de elementos
	 */
	private TreeMap simulandos = null;

	/**
	 * Elemento anterior
	 */
	private Object elementoAnterior = null;

	/**
	 * Constroi a lista de simulação
	 */
	public ListaSimulacao() {
		simulandos = new TreeMap();
	}

	/**
	 * Adiciona uma coleção de elementos na lista
	 */
	public void put(double tempo, Object elementoAnterior, Collection elementos) {
		Iterator iterator = elementos.iterator();
		while (iterator.hasNext()) {
			Object elemento = iterator.next();
			put(tempo, elementoAnterior, elemento);
		}
	}

	/**
	 * Adiciona um novo elemento na lista
	 */
	public void put(double tempo, Object elementoAnterior, Object elemento) {
		Double tempoWrapper = new Double(tempo);
		List elementos = (List)simulandos.get(tempoWrapper);

		if (elementos == null) {
			elementos = new ArrayList();
			simulandos.put(tempoWrapper, elementos);
		}

		Object[] estrutura = { elementoAnterior, elemento };
		elementos.add(estrutura);
	}

	/**
	 * Fornece o tempo atual da simulação (que é o do elemento com menor tempo)
	 */
	public double getTempo() {
		Double tempo;

		try {
			tempo = (Double)simulandos.firstKey();
		} catch (Exception ex) {
			return Double.POSITIVE_INFINITY;
		}

		return tempo.doubleValue();
	}

	/**
	 * Fornece o elemento anterior no workflow ao elmento que foi fornecido
	 * na última chamada ao método pop
	 */
	public Object getAnterior() {
		return elementoAnterior;
	}

	/**
	 * Fornece o próximo elemento da lista de simulação e remove esse elemento
	 */
	public Object pop() {
		Double tempo;

		try {
			tempo = (Double)simulandos.firstKey();
		} catch (Exception ex) {
			return null;
		}

		List elementos = (List)simulandos.get(tempo);
		Object[] estrutura = (Object[])elementos.remove(0);

		elementoAnterior = estrutura[0];

		if (elementos.isEmpty())
			simulandos.remove(tempo);

		return estrutura[1];
	}
}