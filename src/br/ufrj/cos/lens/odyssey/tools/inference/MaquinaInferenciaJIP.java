package br.ufrj.cos.lens.odyssey.tools.inference;

import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import JIP.engine.JIPEngine2;
import JIP.engine.JIPList;
import JIP.engine.JIPQuery;
import JIP.engine.JIPTerm;
import JIP.engine.JIPVariable;

/**
 * Wrapper para maquina de inferência JIP (Java Internet Prolog).
 *
 * @author Leo Murta
 * @version 1.0, 18/12/2001
 */
public class MaquinaInferenciaJIP {
	/**
	 * Máquina de inferêcia JIP
	 */
	private JIPEngine2 jip = null;

	/**
	 * Consulta atualmente em execução
	 */
	private JIPQuery consulta = null;

	/**
	 * Constroi a máquina de inferência
	 */
	public MaquinaInferenciaJIP() {
		PrintStream out = System.out;
		System.setOut(new PrintStream(new ByteArrayOutputStream()));
		jip = new JIPEngine2(new JanelaFake(), "./");
		System.setOut(out);
	}

	/**
	 * Adiciona uma sequencia de clausulas à base de conhecimento
	 *
	 * @param clausulas Seqüência de clausulas que devem ser inseridas. Cada
	 *                  clausula é do tipo String e não contém ponto no final.
	 *                  Se a clausula for uma regra, deve estar entre parênteses.
	 */
	public synchronized void addClausulas(Iterator clausulas) {
		List asserts = new ArrayList();
		while (clausulas.hasNext())
			asserts.add("assertz(" + (String)clausulas.next() + ")");

		getRespostaBooleana(asserts.iterator());
	}

	/**
	 * Remove uma sequência de fatos da base de conhecimento
	 *
	 * @param clausulas Seqüência de fatos que devem ser removidos. Cada
	 *                  fato é do tipo String e não contém ponto no final.
	 */
	public synchronized void removeFatos(Iterator clausulas) {
		List retracts = new ArrayList();
		while (clausulas.hasNext())
			retracts.add("retract(" + (String)clausulas.next() + ")");

		getRespostaBooleana(retracts.iterator());
	}

	/**
	 * Para uma determinada lista de perguntas feita a base atual, fornece uma
	 * resposta booleana.
	 *
	 * @param perguntas Perguntas que serão concatenadas por virgula e
	 *                  questionadas à máquina de inferência. As perguntas
	 *                  não devem ser terminadas com ponto.
	 */
	public synchronized boolean getRespostaBooleana(Iterator perguntas) {
		StringBuffer pergunta = new StringBuffer();
		while (perguntas.hasNext())
			pergunta.append((String)perguntas.next() + ",");

		if (pergunta.length() != 0) {
			pergunta.deleteCharAt(pergunta.length() - 1);
			return getRespostaBooleana(pergunta.toString());
		} else
			return false;
	}

	/**
	 * Para uma determinada pergunta feita a base atual, fornece uma resposta
	 * booleana.
	 *
	 * @param pergunta Pergunta que deve ser feita à máquina de inferência. A
	 *                 pergunta não deve treminar com ponto.
	 */
	public synchronized boolean getRespostaBooleana(String pergunta) {
		criaConsulta(pergunta);
		return (getProximaResposta() != null);
	}

	/**
	 * Para uma determinada pergunta feita a base atual, fornece um Map que representa
	 * a primeira resposta, contendo o nome de cada variável instanciada indexando
	 * o seu valor (o nome da variável é String e
	 * o valor da variável pode ser String ou List de Strings, no caso de lista).
	 *
	 * @param pergunta Pergunta que deve ser feita à máquina de inferência. A
	 *                 pergunta não deve treminar com ponto.
	 * @return Map com Strings dos nomes das variáveis indexando os seus valores.
	 *         Caso a resposta seja NO, retorna null. Caso a resposta seja YES,
	 *         mas sem casamento de variáveis, retorna a Map vazia.
	 */
	public synchronized Map getResposta(String pergunta) {
		criaConsulta(pergunta);
		return getProximaResposta();
	}

	/**
	 * Para uma determinada pergunta feita a base atual, fornece uma seqüência que
	 * contém vários Maps. Cada Map representa uma resposta contendo o nome de
	 * cada variável instanciada indexando o seu valor (o nome da variável é String e
	 * o valor da variável pode ser String ou List de Strings, no caso de lista).
	 *
	 * @param pergunta Pergunta que deve ser feita à máquina de inferência. A
	 *                 pergunta não deve treminar com ponto.
	 * @return Seqüência com Maps com Strings dos nomes das variáveis indexando
	 *         os seus valores. Caso a resposta seja NO, retorna a seqüência vazia.
	 *         Caso a resposta seja YES, mas sem casamento de variáveis, retorna a
	 *         seqüência com uma Map vazia.
	 */
	public synchronized Iterator getRespostas(String pergunta) {
		List respostas = new ArrayList();

		criaConsulta(pergunta);
		Map resposta = getProximaResposta();
		while (resposta != null) {
			respostas.add(resposta);
			resposta = getProximaResposta();
		}

		return respostas.iterator();
	}

	/**
	 * Fornece um Map que representa a próxima resposta, contendo o nome de cada
	 * variável instanciada indexando o seu valor (o nome da variável é String e
	 * o valor da variável pode ser String ou List de Strings, no caso de lista).
	 *
	 * @return Map com Strings dos nomes das variáveis indexando os seus valores.
	 *         Caso a resposta seja NO, retorna null. Caso a resposta seja YES,
	 *         mas sem casamento de variáveis, retorna a Map vazia.
	 */
	public synchronized Map getProximaResposta() {
		if (consulta == null)
			return null;

		Map resposta = null;
		JIPTerm termo = consulta.nextSolution();
		if (termo != null) {
			resposta = new HashMap();
			JIPVariable[] variaveis = termo.getJIPVariables();
			for (int i = 0; i < variaveis.length; i++) {
				String nome = variaveis[i].getName();
				JIPTerm valor = variaveis[i].getValue();
				if ((valor != null) && (valor.isList())) {
					JIPList lista = valor.getList();
					List retorno = new ArrayList();
					while (lista != null) {
						JIPTerm cabeca = lista.getHead();
						if (cabeca != null)
							retorno.add(cabeca.toString());
						lista = lista.getTail();
					}
					resposta.put(nome, retorno);
				} else
					resposta.put(nome, (valor != null) ? valor.toString() : "");
			}
		} else
			consulta = null;

		return resposta;
	}

	/**
	 * Cria uma consulta para uma pergunta prolog
	 */
	private synchronized void criaConsulta(String pergunta) {
		try {
			consulta = jip.openQuery(JIPTerm.parseQuery(pergunta + "."));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

/**
 * Classe janela responsável por monitorar e destruir qualquer janela que seja
 * modal a ela (usado para não ser necessário dar OK na janela do JIP).
 */
class JanelaFake extends JFrame implements Runnable {
	/**
	 * Constroi a janela
	 */
	public JanelaFake() {
		Thread monitor = new Thread(this);
		monitor.setPriority(Thread.MIN_PRIORITY);
		monitor.start();
	}

	/**
	 * Monitora a lista de janelas modais, destruindo-as.
	 */
	public void run() {
		Window[] janelas;
		do janelas = getOwnedWindows();
		while (janelas.length == 0);

		janelas[0].dispose();
		this.dispose();
	}
}