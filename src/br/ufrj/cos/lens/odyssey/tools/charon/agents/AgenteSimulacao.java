package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * Agente responsável por executar o processo
 *
 * @author Leo Murta
 * @version 1.0, 14/12/2001
 */
public class AgenteSimulacao extends Agent {
	/**
	 * Estado livre
	 */
	private static final int LIVRE = 1;

	/**
	 * Estado executando reativo
	 */
	private static final int SIMULANDO = 2;

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
	private int estado = 1;

	/**
	 * Lista de regras do agente
	 */
	private List<String> regras = null;

	/**
	 * Thread da simulação (null caso não exista simulação em andamento)
	 */
	Thread threadSimulacao = null;

	/**
	 * Lista de erros encontrados na simulação
	 */
	Set erros = null;

	/**
	 * Número de repetições da simulação
	 */
	private static final int NUMERO_SIMULACOES = 100;

	/**
	 * Elementos que já foram simulados indexando o seu tempo de simulação
	 */
	private Map simulados = null;

	/**
	 * Processos compostos que estão sendo simulados
	 */
	private Set simulandos = null;

//	/**
//	 * Processo raiz da simulação
//	 */
//	private NoProcesso processoRaiz = null;

	/**
	 * Contador de recursões
	 */
	private long contadorRecursoes = 0;

	/**
	 * Constroi o agente de simulação com intervalo de execuções pró-ativas de
	 * 10 segundos
	 */
	public AgenteSimulacao() {
		super(5000);
	}

	/**
	 * Simula um processo recursivamente
	 *
	 * @param origem Não utilizado
	 * @param base Base que deverá receber as informações do processo simulado
	 */
	public synchronized void executaReativo(Object origem, KnowledgeBase base) {
		if (base == null)
			return;

		while (estado != LIVRE) {
			try {
				wait();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		estado = SIMULANDO;

		conecta(base);
		base.setEstado(KnowledgeBase.BASE_SIMULANDO);

//		// Pega o processo raiz
//		processoRaiz = base.getMapeador().getProcessoRaiz();

		simulados = new HashMap();
		simulandos = new HashSet();
		erros = new HashSet();

//		try {
////			simula(processoRaiz);
//
//			List prolog = new ArrayList();
//			Iterator processos = simulados.keySet().iterator();
//			while (processos.hasNext()) {
//				NoProcesso processo = (NoProcesso)processos.next();
//				long id = base.getMapeador().getMapeamento(processo);
//				double simulacao = ((Double)simulados.get(processo)).doubleValue();
//
//				prolog.add("simulado(" + id + ",'" + simulacao + "')");
//			}
//
//			base.getProlog().addClausulas(prolog.iterator());
//
//			if (erros.isEmpty())
//				base.setEstado(BaseConhecimento.BASE_PENDENTE);
//			else
//				base.setEstado(BaseConhecimento.BASE_ERRO);
//			desconecta();
//
//			estado = ESPERANDO;
//		} catch (CancelaSimulacaoException ex) {
//			base.setEstado(BaseConhecimento.BASE_ERRO);
//			desconecta();
//			estado = LIVRE;
//			notifyAll();
//		}
	}

	/**
	 * Verifica se a simulação não está em loop infinito
	 */
	public synchronized void executaProativo() {
		if (estado == LIVRE)
			return;

		setProativo(false);

		int tipoJanela;
//		if (estado == ESPERANDO)
//			tipoJanela = JanelaSimulacao.OK;
//		else
//			tipoJanela = JanelaSimulacao.TRY + JanelaSimulacao.CANCEL;
//
//		JanelaSimulacao janela = new JanelaSimulacao(this, processoRaiz, tipoJanela, erros);
//
//		janela.toBack();
//		janela.setVisible(true);

//		janela.setVisible(false);
//
//		switch (acao) {
//			case JanelaSimulacao.CANCEL :
//				estado = CANCELANDO;
//				break;
//
//			case JanelaSimulacao.OK :
//				estado = LIVRE;
//				break;
//
//			case JanelaSimulacao.TRY :
//				estado = SIMULANDO;
//				break;
//		}

		setProativo(true);
		notifyAll();
	}

//	/**
//	 * Simula um processo
//	 */
//	private double simula(NoProcesso processo) throws CancelaSimulacaoException {
//		double simulacao = 0;
//		Double simulacaoExistente = (Double)simulados.get(processo);
//
//		if (simulacaoExistente == null) {
//			if (processo.getTipo().equals(NoProcesso.PRIMITIVO))
//				simulacao = processo.getTempoSimulacao();
//			else if (processo.getTipo().equals(NoProcesso.COMPOSTO)) {
//				if (simulandos.contains(processo)) {
//					if ((++contadorRecursoes % 1000) == 0) {
//						try {
//							if (GerenteProcesso.DEBUG)
//								System.out.println("Parou por excesso de recursões!");
//							wait();
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//						if (estado == CANCELANDO)
//							throw new CancelaSimulacaoException();
//					}
//
//					simulacao = simulaWorkflow(processo);
//				} else {
//					simulandos.add(processo);
//
//					for (int i = 0; i < NUMERO_SIMULACOES; i++)
//						simulacao = ((simulacao * i) + simulaWorkflow(processo)) / (i + 1);
//
//					simulandos.remove(processo);
//					simulados.put(processo, new Double(simulacao));
//				}
//			}
//		} else
//			simulacao = simulacaoExistente.doubleValue();
//
//		return simulacao;
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

	/**
	 * Fornece a lista de regras existentes no agente
	 */
	public Collection<String> getRegras() {
		if (regras == null)
			regras = new ArrayList<String>();

		return regras;
	}
}