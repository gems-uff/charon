package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import processstructure.Activity;
import processstructure.WorkDefinition;
import spem.SpemPackage;
import statemachines.Transition;
import sun.security.krb5.internal.ktab.k;

import activitygraphs.ActivityGraph;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonException;
import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * Agente responsável por executar o processo
 *
 * @author Leo Murta
 * @version 1.0, 14/12/2001
 */
public class SimulationAgent extends Agent {
	
	/**
	 * Available state
	 */
	private static final int AVAILABLE = 1;

	/**
	 * Simulating state
	 */
	private static final int RUNNING = 2;

	/**
	 * Estado executado reativo, mas esperando pelo proativo
	 */
	private static final int ESPERANDO = 4;

	/**
	 * Estado em que a execução proativa decidiu por cancelar, mas está esperando
	 * a reativa.
	 */
	private static final int CANCELANDO = 8;

	/**
	 * Estado atual do agente
	 */
	private int state = AVAILABLE;

	/**
	 * Agent's rules
	 */
	private Collection<String> regras = null;
	
	/**
	 * Thread da simulação (null caso não exista simulação em andamento)
	 */
	Thread threadSimulacao = null;

	/**
	 * Número de repetições da simulação
	 */
	private static final int NUMERO_SIMULACOES = 100;
	
	/**
	 * Maximum recursion deep
	 */
	private static final int MAX_RECURSION = 100;

	/**
	 * Contador de recursões
	 */
	private long contadorRecursoes = 0;
	
	/**
	 * Counter of elements under simulation
	 */
	private Map<WorkDefinition, Integer> recursionCounters = null;
	
	/**
	 * Constructs the agent without proactive behavior
	 */
	public SimulationAgent() {
		super();
	}

//	/**
//	 * Simulates the root process recursivelly
//	 */
//	public synchronized void reactiveExecution(KnowledgeBase knowledgeBase) {
//		while (state != AVAILABLE) {
//			try {
//				wait();
//			} catch (Exception e) {
//				Logger.global.log(Level.WARNING, "Could not synchronize simulation agent", e);
//			}
//		}
//
//		state = RUNNING;
//
//		connect(knowledgeBase);
//		knowledgeBase.setState(KnowledgeBase.SIMULATING);
//
//		SpemPackage spemPackage = knowledgeBase.getSpemPackage();
//		Collection<String> errors = new ArrayList<String>();
//		recursionCounters = new HashMap<WorkDefinition, Integer>();
//		try {
//			// Simulates all composite processes
//			Iterator i = spemPackage.getProcessStructure().getWorkDefinition().refAllOfClass().iterator();
//			while (i.hasNext()) {
//				simulate((WorkDefinition) i.next(), spemPackage);
//			}
//
//			if (errors.isEmpty())
//				knowledgeBase.setState(KnowledgeBase.PENDING);
//			else
//				knowledgeBase.setState(KnowledgeBase.CORRUPTED);
//			
//			disconnect();
//
//			state = ESPERANDO;
//		} catch (CancelaSimulacaoException ex) {
//			knowledgeBase.setState(KnowledgeBase.CORRUPTED);
//			disconnect();
//			state = AVAILABLE;
//		} finally {
//			notify();
//		}
//	}

//	/**
//	 * Simulate a composite process
//	 */
//	private void simulate(WorkDefinition workDefinition, SpemPackage spemPackage) {
//		if (workDefinition.refIsInstanceOf(spemPackage.getProcessStructure().getWorkDefinition().refMetaObject(), false)) {
//			Integer recursionCounter = recursionCounters.get(workDefinition);
//			if (recursionCounter == null) {
//				recursionCounter = 0;
//				recursionCounters.put(workDefinition, recursionCounter);
//			}
//			
//			if (underSimulation.contains(workDefinition)) {
//				if (++contadorRecursoes >= MAX_RECURSION) {
//					throw new CharonException("Could not finish simulation: More than " + MAX_RECURSION + " recursive calls to " + workDefinition);
//				}
//
//				Iterator i = spemPackage.getStateMachines().getABehaviorContext().getBehavior(workDefinition).iterator();
//				while (i.hasNext()) {
//					ActivityGraph activityGraph = (ActivityGraph) i.next();
//					Iterator j = activityGraph.getTransitions().iterator();
//					while (j.hasNext()) {
//						map((Transition)j.next(), spemPackage, facts);
//					}
//				}
//				
//				simulacao = simulaWorkflow(workDefinition);
//			} else {
//				simulandos.add(workDefinition);
//
//				for (int i = 0; i < NUMERO_SIMULACOES; i++)
//					simulacao = ((simulacao * i) + simulaWorkflow(workDefinition)) / (i + 1);
//
//				simulandos.remove(workDefinition);
//				simulados.put(workDefinition, new Double(simulacao));
//			}
//		}
//	}

//	/**
//	 * simula um workflow
//	 */
//	private double simulaWorkflow(NoProcesso workflow) throws CancelaSimulacaoException {
//		ListaSimulacao simulandos = new ListaSimulacao();
//
//		// Map dos sincronismos indexando conjunto de elementos que já estão sincronizados
//		Map sincronizados = new HashMap();
//
//		// Percorre todas os nós do workflow do processo procurando pelos nós
//		// de início e inicia a simulação.
//		Enumeration nos = workflow.getDiagrama().getNodes();
//		while (nos.hasMoreElements()) {
//			NoPadrao no = (NoPadrao)nos.nextElement();
//			if (no.getSemantic().getIdentificador() == Categoria.idInicioProcesso) {
//				simulandos.put(0, null, no);
//			}
//		}
//
//		double tempo = simulandos.getTempo();
//
//		NoPadrao elemento = simulandos.pop();
//		long contadorRepeticoes = 0;
//		while (elemento != null) {
//			if ((++contadorRepeticoes % 1000) == 0) {
//				try {
//					if (GerenteProcesso.DEBUG)
//						System.out.println("Parou por excesso de repetições!");
//					wait();
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//				if (estado == CANCELANDO)
//					throw new CancelaSimulacaoException();
//			}
//
//			NoSemantico elementoSemantico = elemento.getSemantic();
//			Set proximos = getProximos(elemento, workflow);
//			switch (elementoSemantico.getIdentificador()) {
//				case Categoria.idInicioProcesso :
//					simulandos.put(tempo, elemento, proximos);
//					break;
//
//				case Categoria.idProcesso :
//					simulandos.put(tempo + simula((NoProcesso)elementoSemantico), elemento, proximos);
//					break;
//
//				case Categoria.idDecisao :
//					simulandos.put(tempo + ((NoDecisao)elementoSemantico).getTempoSimulacao(), elemento, getProximo(elemento, workflow));
//					break;
//
//				case Categoria.idSincronismo :
//					if (sincroniza(elemento, simulandos.getAnterior(), sincronizados, getAnteriores(elemento, workflow)))
//						simulandos.put(tempo, elemento, proximos);
//					break;
//
//				case Categoria.idFimProcesso :
//					return tempo;
//			}
//			tempo = simulandos.getTempo();
//			elemento = simulandos.pop();
//		}
//
//		erros.add("Workflow of the process '" + workflow + "' have no path from start node to finish node.");
//		return Double.POSITIVE_INFINITY;
//	}

//	/**
//	 * Coloca um elemento na lista de sincronismo
//	 *
//	 * @param sincronismo Emento responsável pelo sincronismo
//	 * @param elemento Elemento que acabou de entrar em sincronismo
//	 * @param sincronismos Map que contém todos os sincronismos atuais
//	 * @param todosElementos Conjunto de todos os elmentos necessários para o sincronismo
//	 */
//	private boolean sincroniza(NoPadrao sincronismo, NoPadrao elemento, Map sincronismos, Set todosElementos) {
//		Set sincronizados = (Set)sincronismos.get(sincronismo);
//		if (sincronizados == null) {
//			sincronizados = new HashSet();
//			sincronismos.put(sincronismo, sincronizados);
//		}
//		sincronizados.add(elemento);
//
//		return sincronizados.containsAll(todosElementos);
//	}

//	/**
//	 * Pega os próximos elementos de um determinado elemento do workflow
//	 */
//	private Set getProximos(NoPadrao elemento, NoProcesso workflow) {
//		Set elementos = new HashSet();
//
//		Enumeration fluxos = workflow.getDiagrama().getEdges();
//		while (fluxos.hasMoreElements()) {
//			ArestaPadrao fluxo = (ArestaPadrao)fluxos.nextElement();
//			NoPadrao origem = fluxo.getSource();
//
//			if (origem == elemento)
//				elementos.add(fluxo.getTarget());
//		}
//
//		return elementos;
//	}

//	/**
//	 * Escolha um proximo elemento de um determinado elemento do workflow
//	 * aleatoriamente
//	 */
//	private NoPadrao getProximo(NoPadrao elemento, NoProcesso workflow) {
//		NoPadrao elementoSorteado = null;
//		double numeroSorteado = Math.random() * 100;
//		double numeroAtual = 0;
//		double proximoNumero = 0;
//
//		Enumeration fluxos = workflow.getDiagrama().getEdges();
//		while (fluxos.hasMoreElements()) {
//			ArestaPadrao fluxo = (ArestaPadrao)fluxos.nextElement();
//			NoPadrao origem = fluxo.getSource();
//
//			if (origem == elemento) {
//				LigFluxo fluxoSemantico = (LigFluxo)fluxo.getSemantic();
//				proximoNumero += fluxoSemantico.getProbabilidadeSelecao();
//
//				if ((numeroSorteado >= numeroAtual) && (numeroSorteado < proximoNumero))
//					elementoSorteado = (NoPadrao)fluxo.getTarget();
//
//				numeroAtual = proximoNumero;
//			}
//		}
//
//		if (numeroAtual != 100)
//			erros.add("Workflow of the process '" + workflow + "' contains a decision '" + elemento.getSemantic() + "' with total probability not equal to 100%.");
//
//		return elementoSorteado;
//	}

//	/**
//	 * Pega os elementos anteriores de um determinado elemento do workflow
//	 */
//	private Set getAnteriores(NoPadrao elemento, NoProcesso workflow) {
//		Set elementos = new HashSet();
//
//		Enumeration fluxos = workflow.getDiagrama().getEdges();
//		while (fluxos.hasMoreElements()) {
//			ArestaPadrao fluxo = (ArestaPadrao)fluxos.nextElement();
//			NoPadrao destino = fluxo.getTarget();
//
//			if (destino == elemento)
//				elementos.add(fluxo.getSource());
//		}
//
//		return elementos;
//	}
}