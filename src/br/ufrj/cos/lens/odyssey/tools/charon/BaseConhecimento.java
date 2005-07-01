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

import br.ufrj.cos.lens.odyssey.tools.charon.agents.Agente;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.Disparador;
import br.ufrj.cos.lens.odyssey.tools.inference.MaquinaInferenciaJIP;

/**
 * Classe respossável por representar a base de conhecimento de um processo.
 *
 * @author Leonardo Murta
 * @version 1.0, 22/11/2001
 */
public class BaseConhecimento implements Serializable {
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
	 * São reconstruidos sempre que o processo evoluir
	 * Os seguintes elementos nÃo são predicados (são funtores):
	 * "inicio/1", "processo/1", "decisao/1", "sincronismo/1", "termino/0"
	 */
	private static final String[] predicadosMapeamento = { "processoPrimitivo/1", "processoComposto/1", "processoRaiz/1", "nome/2", "roteiro/2", "ferramenta/2", "papel/2", "artefatoEntrada/2", "artefatoSaida/2", "classeProcesso/2", "pergunta/2", "resposta/3", "fluxo/2", "usuario/2", "simulado/2" };

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
	 * Lista de fatos de mapeamento
	 */
	private Set<String> fatosMapeamento = new HashSet<String>();

	/**
	 * Lista de fatos de execucao
	 */
	private Set<String> fatosExecucao = new HashSet<String>();

	/**
	 * Máquina de inferência atrelada ao ambiente
	 */
	private transient MaquinaInferenciaJIP prolog = null;

	/**
	 * Agente que está conectado à base
	 */
	private transient Agente agente = null;

	/**
	 * Construtor sem parâmetro para reflection
	 */
	public BaseConhecimento() {}

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
	public BaseConhecimento(SpemPackage spemPackage, WorkDefinition rootProcess) {
		this.spemPackage = spemPackage;
		this.rootProcess = rootProcess;
		getProlog().addClausulas(Mapper.getInstance().map(spemPackage, rootProcess));
//		Agente agenteSimulacao = CharonFacade.getInstancia().getAgente(CharonFacade.AGENTE_SIMULACAO);
//		new Disparador(this, agenteSimulacao, this);
	}

	/**
	 * Fornece a instância da máquina de inferência. Todas as requisições devem
	 * ser feitas por esse método.
	 */
	public synchronized MaquinaInferenciaJIP getProlog() {
		if (prolog == null)
			inicializaMaquinaInferencia();
		return prolog;
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
	public synchronized int getEstado() {
		return estado;
	}

	/**
	 * Constroi uma instância da máquina de inferência configurando uma base de
	 * clausulas externa
	 */
	private synchronized void inicializaMaquinaInferencia() {
		// Guarda os fatos antigos para serem reinseridos na nova máquina
		Collection<String> fatosMapeamentoAntigos = new ArrayList<String>(this.fatosMapeamento);
		this.fatosMapeamento.clear();
		Collection<String> fatosExecucaoAntigos = new ArrayList<String>(this.fatosExecucao);
		this.fatosExecucao.clear();

		// Cria a nova máquina de inferência
		prolog = new MaquinaInferenciaJIP();

		// Constroi as clausulas de configuração das bases externas
		Collection<String> clausulas = new ArrayList<String>();
		for (int i = 0; i < predicadosMapeamento.length; i++)
			clausulas.add("extern(" + predicadosMapeamento[i] + ", 'br.ufrj.cos.lens.odyssey.tools.charon.BaseClausulas', '" + BaseClausulas.MAPEAMENTO + "')");
		for (int i = 0; i < predicadosExecucao.length; i++)
			clausulas.add("extern(" + predicadosExecucao[i] + ", 'br.ufrj.cos.lens.odyssey.tools.charon.BaseClausulas', '" + BaseClausulas.EXECUCAO + "')");
		for (int i = 0; i < predicadosAgentes.length; i++)
			clausulas.add("extern(" + predicadosAgentes[i] + ", 'br.ufrj.cos.lens.odyssey.tools.charon.BaseClausulas', '" + BaseClausulas.AGENTES + "')");

		// Cria as bases externas com esta base de conhecimento com responsável
		BaseClausulas.setBaseConhecimentoResponsavel(this);
		getProlog().getRespostaBooleana(clausulas.iterator());

		// Reinsere os fatos antigos
		getProlog().addClausulas(fatosMapeamentoAntigos);
		getProlog().addClausulas(fatosExecucaoAntigos);
	}

	/**
	 * Regera a base de conhecimento para refletir um novo mapeamento do processo
	 */
	public synchronized void atualizaBase() {
		// Esvazia o mapeamento antigo
		fatosMapeamento.clear();

		// recria o mapeamento e insere na base
		inicializaMaquinaInferencia();
		getProlog().addClausulas(Mapper.getInstance().map(spemPackage, rootProcess));

		// ressimula o processo mapeado
		Agente agenteSimulacao = CharonFacade.getInstancia().getAgente(CharonFacade.AGENTE_SIMULACAO);
		new Disparador(this, agenteSimulacao, this);
	}

	/**
	 * Conecta um agente à base
	 */
	public synchronized void conecta(Agente agente) {
		while (this.agente != null)
			try {
				wait();
			} catch (Exception ex) {
				System.out.println(ex);
			}

		this.agente = agente;

		getProlog().addClausulas(agente.getRegras());
	}

	/**
	 * Desconecta o agente atualmente conectado na base
	 */
	public synchronized void desconecta() {
		if (this.agente != null) {
			BaseClausulas.removeClausulasAgentes(this);

			// Notifica os escutadores do agente atual que ele está sendo removido
			Iterator iterator = agente.getEscutadores();
			while (iterator.hasNext()) {
				Agente agenteAlvo = (Agente)iterator.next();
				new Disparador(agente, agenteAlvo, this);
			}

			this.agente = null;
		}

		// Libera outras conexões
		notify();
	}

	/**
	 * Fornece o nome do processo raiz
	 *
	 * @return Nome do processo raiz
	 */
	public synchronized String getProcessoRaiz() {
		Map resposta = getProlog().getResposta("processoRaiz(P),nome(P,N)");
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
		Iterator respostas = getProlog().getRespostas("papel(_,P)");
		while (respostas.hasNext()) {
			Map resposta = (Map)respostas.next();
			String papel = (String)resposta.get("P");
			if (!encontrados.contains(papel)) {
				papeis.add(new Papel(papel.substring(1, (papel.length() - 1))));
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
		Map resposta = getProlog().getResposta("simulado(" + idSimulado + ", T)");
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
		Iterator respostas = getProlog().getRespostas("executado(" + elemento + ", P, Ti, Tf), P = " + path + ", T is (Tf - Ti)");
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
		if (getProlog().getRespostaBooleana("executando(" + elemento + ", P, _), P = " + path))
			cores.add(Color.yellow);

		// Se nenhuma cor foi inserida é porque o elemento nunca entrou em execução
		// então ele ficará em cinza
		if (cores.isEmpty())
			cores.add(Color.gray);

		return cores;
	}

	// Métodos para a manutenção das listas de clausulas
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
}