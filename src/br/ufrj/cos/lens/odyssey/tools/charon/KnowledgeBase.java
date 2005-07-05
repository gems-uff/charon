package br.ufrj.cos.lens.odyssey.tools.charon;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import processstructure.WorkDefinition;
import spem.SpemPackage;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.AgenteSimulacao;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.Dispatcher;
import br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine;

/**
 * Classe resposs�vel por representar a base de conhecimento de um processo.
 *
 * @author Leonardo Murta
 * @version 1.0, 22/11/2001
 */
public class KnowledgeBase implements Serializable {
	/**
	 * Base de conhecimento sendo simulada
	 */
	public static final int BASE_SIMULANDO = 0;

	/**
	 * Base de conhecimento pendente de inicializacao
	 */
	public static final int BASE_PENDENTE = 1;

	/**
	 * Base de conhecimento com erro
	 */
	public static final int BASE_ERRO = 2;

	/**
	 * Base de conhecimento executando
	 */
	public static final int BASE_EXECUTANDO = 3;

	/**
	 * Base de conhecimento inicializada
	 */
	public static final int BASE_FINALIZADA = 4;

	/**
	 * Estado da base
	 */
	private int estado;

	/**
	 * Predicados utilizadas no mapeamento
	 * S�o reconstruidos sempre que o processo evoluir
	 * Os seguintes elementos n�o s�o predicados (s�o funtores):
	 * "inicio/1", "processo/1", "decisao/1", "sincronismo/1", "termino/0"
	 */
	private static final String[] predicadosMapeamento = { "processoPrimitivo/1", "processoComposto/1", "processoRaiz/1", "papel/2", "classeProcesso/2", "fluxo/2", "simulado/2" };

	/**
	 * Predicados utilizadas na execu��o
	 * Nunca s�o reconstruidas (s�o permanentes)
	 */
	private static final String[] predicadosExecucao = { "executando/3", "executado/4", "respondido/5", "finalizado/4" };

	/**
	 * Predicados pertencentes a agentes
	 * S�o reconstruidas a cada jun��o do agente � base
	 * (n�o s�o armazenadas)
	 */
	private static final String[] predicadosAgentes = { "inicia/3", "finaliza/3", "desloca/1", "debug/1" };

	/**
	 * Lista de fatos de mapeamento
	 */
	private Set<String> fatosMapeamento = new HashSet<String>();

	/**
	 * Lista de fatos de execucao
	 */
	private Set<String> fatosExecucao = new HashSet<String>();

	/**
	 * Inference machine that phisically holds the knowledge base
	 */
	private transient InferenceMachine inferenceMachine = null;

	/**
	 * Agente que est� conectado � base
	 */
	private transient Agent agente = null;

	/**
	 * Construtor sem par�metro para reflection
	 */
	public KnowledgeBase() {}

	/**
	 * Package with all SPEM elements
	 */
	private SpemPackage spemPackage = null;

	/**
	 * Root process
	 */
	private WorkDefinition rootProcess = null;
	
	/**
	 * Constroi a base de conhecimento do processo
	 *
	 * @param processo Processo raiz da base de conhecimento
	 */
	public KnowledgeBase(SpemPackage spemPackage, WorkDefinition rootProcess) {
		this.spemPackage = spemPackage;
		this.rootProcess = rootProcess;
		inferenceMachine = new InferenceMachine();
		
		System.out.println(Mapper.getInstance().map(spemPackage, rootProcess));
		getInferenceMachine().addClauses(Mapper.getInstance().map(spemPackage, rootProcess));
//		Agente agenteSimulacao = CharonFacade.getInstancia().getAgente(CharonFacade.AGENTE_SIMULACAO);
//		new Disparador(this, agenteSimulacao, this);
	}

	/**
	 * Fornece a inst�ncia da m�quina de infer�ncia. Todas as requisi��es devem
	 * ser feitas por esse m�todo.
	 */
	public synchronized InferenceMachine getInferenceMachine() {
		return inferenceMachine;
	}

	/**
	 * Atribui um novo estado para a base
	 */
	public synchronized void setEstado(int estado) {
		this.estado = estado;
	}

	/**
	 * Pega o estado atual da base
	 */
	public synchronized int getState() {
		return estado;
	}

//	/**
//	 * Constroi uma inst�ncia da m�quina de infer�ncia configurando uma base de
//	 * clausulas externa
//	 */
//	private synchronized void initializeInferenceMachine() {
//		// Guarda os fatos antigos para serem reinseridos na nova m�quina
//		Collection<String> fatosMapeamentoAntigos = new ArrayList<String>(this.fatosMapeamento);
//		this.fatosMapeamento.clear();
//		Collection<String> fatosExecucaoAntigos = new ArrayList<String>(this.fatosExecucao);
//		this.fatosExecucao.clear();
//
//		// Cria a nova m�quina de infer�ncia
//		prolog = new InferenceMachine();
//
//		// Constroi as clausulas de configura��o das bases externas
//		Collection<String> clausulas = new ArrayList<String>();
//		for (int i = 0; i < predicadosMapeamento.length; i++)
//			clausulas.add("extern(" + predicadosMapeamento[i] + ", 'br.ufrj.cos.lens.odyssey.tools.charon.BaseClausulas', '" + BaseClausulas.MAPEAMENTO + "')");
//		for (int i = 0; i < predicadosExecucao.length; i++)
//			clausulas.add("extern(" + predicadosExecucao[i] + ", 'br.ufrj.cos.lens.odyssey.tools.charon.BaseClausulas', '" + BaseClausulas.EXECUCAO + "')");
//		for (int i = 0; i < predicadosAgentes.length; i++)
//			clausulas.add("extern(" + predicadosAgentes[i] + ", 'br.ufrj.cos.lens.odyssey.tools.charon.BaseClausulas', '" + BaseClausulas.AGENTES + "')");
//
//		// Cria as bases externas com esta base de conhecimento com respons�vel
//		BaseClausulas.setBaseConhecimentoResponsavel(this);
//		getProlog().getBooleanAnswer(clausulas);
//
//		// Reinsere os fatos antigos
//		getProlog().addClauses(fatosMapeamentoAntigos);
//		getProlog().addClauses(fatosExecucaoAntigos);
//	}

	/**
	 * Regera a base de conhecimento para refletir um novo mapeamento do processo
	 */
	public synchronized void update() throws CharonException {
		// Esvazia o mapeamento antigo
		fatosMapeamento.clear();

		// recria o mapeamento e insere na base
//		initializeInferenceMachine();
		getInferenceMachine().addClauses(Mapper.getInstance().map(spemPackage, rootProcess));

		// ressimula o processo mapeado
		Agent agenteSimulacao = AgentManager.getInstance().getAgent(AgenteSimulacao.class);
		new Dispatcher(this, agenteSimulacao, this);
	}

	/**
	 * Conecta um agente � base
	 */
	public synchronized void conecta(Agent agente) {
		while (this.agente != null)
			try {
				wait();
			} catch (Exception ex) {
				System.out.println(ex);
			}

		this.agente = agente;

		getInferenceMachine().addClauses(agente.getRegras());
	}

	/**
	 * Desconecta o agente atualmente conectado na base
	 */
	public synchronized void desconecta() {
		if (this.agente != null) {
			removeClausulasAgentes();

			// Notifica os escutadores do agente atual que ele est� sendo removido
			Iterator iterator = agente.getEscutadores();
			while (iterator.hasNext()) {
				Agent agenteAlvo = (Agent)iterator.next();
				new Dispatcher(agente, agenteAlvo, this);
			}

			this.agente = null;
		}

		// Libera outras conex�es
		notify();
	}

	/**
	 * Fornece o nome do processo raiz
	 *
	 * @return Nome do processo raiz
	 */
	public synchronized String getProcessoRaiz() {
		Map resposta = getInferenceMachine().getSolution("processoRaiz(P),nome(P,N)");
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
		Iterator respostas = getInferenceMachine().getAllSolutions("papel(_,P)").iterator();
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
	 * elemento em fun��o das suas execu��es anteriores
	 *
	 * @param pathObjetos Path dos processos que foram selecionados no diagramador
	 *                    na ordem da sele��o.
	 */
	public synchronized List getCoresElemento(String elemento, long idSimulado, List pathObjetos) {
		List cores = new ArrayList();

		// Obtem o tempo de simula��o
		Map resposta = getInferenceMachine().getSolution("simulado(" + idSimulado + ", T)");
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

		// Transforma o tempo de simula��o de horas para segundos
		tempoSimulacao *= 3600;

		// Transforma o path para prolog (invertendo a ordem)
		List path = new ArrayList();
//		for (int i = 0; i < pathObjetos.size(); i++)
//			path.add(0, "processo(" + mapeador.getMapeamento(pathObjetos.get(i)) + ")");
		path.add("processo(raiz)");

		// Monta a lista de cores em fun��o do tempo de simula��o e dos tempos de execu�ao
		Iterator respostas = getInferenceMachine().getAllSolutions("executado(" + elemento + ", P, Ti, Tf), P = " + path + ", T is (Tf - Ti)").iterator();
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

		// Verifica se o processo est� em execu��o, adicionando a cor amarela
		// Obtem o tempo de simula��o
		if (getInferenceMachine().isSolvable("executando(" + elemento + ", P, _), P = " + path))
			cores.add(Color.yellow);

		// Se nenhuma cor foi inserida � porque o elemento nunca entrou em execu��o
		// ent�o ele ficar� em cinza
		if (cores.isEmpty())
			cores.add(Color.gray);

		return cores;
	}

	// M�todos para a manuten��o das listas de clausulas
	// Devem ser usados somente pela BaseClausulas

	/**
	 * Fornece a lista de fatos do tipo Mapeamento
	 */
	public Set getFatosMapeamento() {
		return fatosMapeamento;
	}

	/**
	 * Fornece a lista de fatos do tipo Execucao
	 */
	public Set getFatosExecucao() {
		return fatosExecucao;
	}
	
	/**
	 * Remove as clausulas de agentes que est�o armazenadas nas bases de clausulas
	 * vinculadas com uma determinada base de conhecimento
	 */
	public static void removeClausulasAgentes() {
//		for (int i = 0; i < basesClausulas.size(); i++) {
//			BaseClausulas baseClausulas = (BaseClausulas)basesClausulas.get(i);
//			if ((baseClausulas.isResponsavel(base)) && (baseClausulas.isTipo(AGENTES)))
//				baseClausulas.clear();
//		}
	}
}