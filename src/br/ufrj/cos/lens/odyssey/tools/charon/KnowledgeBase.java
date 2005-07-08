package br.ufrj.cos.lens.odyssey.tools.charon;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import processstructure.WorkDefinition;
import spem.SpemPackage;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.MappingAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.SimulationAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.Dispatcher;
import br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine;

/**
 * Classe respossável por representar a base de conhecimento de um processo.
 *
 * @author Leonardo Murta
 * @version 1.0, 22/11/2001
 */
public class KnowledgeBase implements Serializable {
	/**
	 * Knowledge base being simulated. No other agent can execute.
	 * @TODO Is it possible to remove this (and other) states?
	 */
	public static final int SIMULATING = 0;

	/**
	 * Knowledge base ready to be enacted. The process has been simulated without errors.
	 */
	public static final int PENDING = 1;

	/**
	 * Knowledge base with errors. The process cannot be executed.
	 */
	public static final int CORRUPTED = 2;

	/**
	 * Knowledge base being enacted. The process is being executed.
	 */
	public static final int ENACTING = 3;

	/**
	 * Knowledge base in the finished state. The process has already been executed.
	 */
	public static final int FINISHED = 4;

	/**
	 * Knowledge base state
	 */
	private int state;

	/**
	 * Predicados utilizadas no mapeamento
	 * São reconstruidos sempre que o processo evoluir
	 * Os seguintes elementos nÃo são predicados (são funtores):
	 * "inicio/1", "processo/1", "decisao/1", "sincronismo/1", "termino/1"
	 */
	private static final String[] predicadosMapeamento = { "processoPrimitivo/1", "processoComposto/1", "processoRaiz/1", "papel/2", "classeProcesso/2", "fluxo/2", "simulado/2" };

	/**
	 * Predicados utilizadas na execução
	 * Nunca são reconstruidas (são permanentes)
	 */
	private static final String[] predicadosExecucao = { "executando/3", "executado/4", "respondido/5", "finalizado/4" };

	/**
	 * Predicados pertencentes a agentes
	 * São reconstruidas a cada junção do agente à base
	 * (não são armazenadas)
	 */
	private static final String[] predicadosAgentes = { "inicia/3", "finaliza/3", "desloca/1", "debug/1" };

	/**
	 * Inference machine that phisically holds the knowledge base
	 */
	private transient InferenceMachine inferenceMachine = null;

	/**
	 * Agent connected to the knowledge base
	 */
	private transient Agent agent = null;
	
	/**
	 * Clauses used to define the process
	 */
	private Collection<String> processClauses = null;

	/**
	 * Construtor sem parâmetro para reflection
	 */
	public KnowledgeBase() {}

	/**
	 * Constructs a new knowledge base
	 *
	 * @param spemPackage Package containing all elements of the process.
	 */
	public KnowledgeBase(SpemPackage spemPackage) throws CharonException {
		inferenceMachine = new InferenceMachine();
		processClauses = AgentManager.getInstance().getAgent(MappingAgent.class).map(spemPackage);
		inferenceMachine.addClauses(processClauses);
	}
	
	/** 
	 * Updates the knowledge base to reflect changes made into the process contained in
	 * the SPEM Package used to create this knowledge base.
	 * IMPORTANT: The current implementation uses MOFID to identify processes. It will be
	 * impossible to evolve the process if the new SpemPackage use different MOFIDs.
	 * 
	 * @param spemPackage Package containing all elements of the process.
	 */
	public synchronized void update(SpemPackage spemPackage) throws CharonException {
		inferenceMachine.removeClauses(processClauses);
		processClauses = AgentManager.getInstance().getAgent(MappingAgent.class).map(spemPackage);
		inferenceMachine.addClauses(processClauses);
	}

//	/**
//	 * Fornece a instância da máquina de inferência. Todas as requisições devem
//	 * ser feitas por esse método.
//	 */
//	public synchronized InferenceMachine getInferenceMachine() {
//		return inferenceMachine;
//	}

	/**
	 * Atribui um novo estado para a base
	 */
	public synchronized void setState(int estado) {
		this.state = estado;
	}

	/**
	 * Pega o estado atual da base
	 */
	public synchronized int getState() {
		return state;
	}

	/**
	 * Connects an agent to this knowledge base
	 */
	public synchronized void connect(Agent agent) {
		while (this.agent != null)
			try {
				// Waits the other agent to finish its work
				wait();
			} catch (InterruptedException e) {
				Logger.global.log(Level.WARNING, "Could not synchronize the connections of agents to the knowledge base", e);
				return;
			}

		this.agent = agent;
		
		// Adds the agent's rules into the knowledge base
		inferenceMachine.addClauses(agent.getRules());
	}

	/**
	 * Disconnect the current connected agent from this knowledge base
	 */
	public synchronized void disconnect() {
		if (agent != null) {
			// Removes the agent's rules from the knowledge base
			inferenceMachine.removeClauses(agent.getRules());
			agent = null;
		}

		// Lets other agents work
		notify();
	}

	/**
	 * Fornece o nome do processo raiz
	 *
	 * @return Nome do processo raiz
	 */
	public synchronized String getProcessoRaiz() {
		Map resposta = inferenceMachine.getSolution("processoRaiz(P),nome(P,N)");
		if (resposta != null) {
			String nomeProcesso = (String)resposta.get("N");
			return nomeProcesso.substring(1, (nomeProcesso.length() - 1));
		} else
			return "";
	}

	/**
	 * Fornece os papeis existentes na base de conhecimento
	 *
	 * @return Papeis existentes na base
	 */
	public synchronized List getPapeis() {
		List papeis = new ArrayList();
		Set encontrados = new HashSet();
		Iterator respostas = inferenceMachine.getAllSolutions("papel(_,P)").iterator();
		while (respostas.hasNext()) {
			Map resposta = (Map)respostas.next();
			String papel = (String)resposta.get("P");
			if (!encontrados.contains(papel)) {
				papeis.add(papel.substring(1, (papel.length() - 1)));
				encontrados.add(papel);
			}
		}

		return papeis;
	}

	/**
	 * Fornece uma lista com as cores que devem ser utilizadas para pintar um
	 * elemento em função das suas execuções anteriores
	 *
	 * @param pathObjetos Path dos processos que foram selecionados no diagramador
	 *                    na ordem da seleção.
	 */
	public synchronized List getCoresElemento(String elemento, long idSimulado, List pathObjetos) {
		List cores = new ArrayList();

		// Obtem o tempo de simulação
		Map resposta = inferenceMachine.getSolution("simulado(" + idSimulado + ", T)");
		double tempoSimulacao;
		if (resposta != null) {
			String tempo = (String)resposta.get("T");
			try {
				tempoSimulacao = Double.parseDouble(tempo.substring(1, (tempo.length() - 1)));
			} catch (Exception ex) {
				tempoSimulacao = Double.POSITIVE_INFINITY;
			}
		} else {
			tempoSimulacao = Double.POSITIVE_INFINITY;
		}

		// Transforma o tempo de simulação de horas para segundos
		tempoSimulacao *= 3600;

		// Transforma o path para prolog (invertendo a ordem)
		List path = new ArrayList();
//		for (int i = 0; i < pathObjetos.size(); i++)
//			path.add(0, "processo(" + mapeador.getMapeamento(pathObjetos.get(i)) + ")");
		path.add("processo(raiz)");

		// Monta a lista de cores em função do tempo de simulação e dos tempos de execuçao
		Iterator respostas = inferenceMachine.getAllSolutions("executado(" + elemento + ", P, Ti, Tf), P = " + path + ", T is (Tf - Ti)").iterator();
		while (respostas.hasNext()) {
			resposta = (Map)respostas.next();
			long tempoExecucao;
			String tempo = (String)resposta.get("T");
			try {
				tempoExecucao = Long.parseLong(tempo);
			} catch (Exception ex) {
				tempoExecucao = Long.MAX_VALUE;
			}

			if (tempoSimulacao < tempoExecucao)
				cores.add(Color.red);
			else
				cores.add(Color.green);
		}

		// Verifica se o processo está em execução, adicionando a cor amarela
		// Obtem o tempo de simulação
		if (inferenceMachine.isSolvable("executando(" + elemento + ", P, _), P = " + path))
			cores.add(Color.yellow);

		// Se nenhuma cor foi inserida é porque o elemento nunca entrou em execução
		// então ele ficará em cinza
		if (cores.isEmpty())
			cores.add(Color.gray);

		return cores;
	}

	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#getAllSolutions(java.lang.String)
	 */
	public List<Map<String, Object>> getAllSolutions(String goal) {
		return inferenceMachine.getAllSolutions(goal);
	}

	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#isSolvable(java.lang.String)
	 */
	public boolean isSolvable(String goal) {
		return inferenceMachine.isSolvable(goal);
	}

	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#addClauses(java.util.Collection)
	 */
	public void addClauses(Collection<String> clauses) {
		inferenceMachine.addClauses(clauses);
	}

	/**
	 * Saves the content of the knowledge base
	 * (useful for debug)
	 */
	public void save(String fileName) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);
			fileWriter.write(inferenceMachine.getContent());
			fileWriter.close();
		} catch (IOException e) {
			Logger.global.log(Level.WARNING, "Could not save the knowledge base", e);
		}
	}
}