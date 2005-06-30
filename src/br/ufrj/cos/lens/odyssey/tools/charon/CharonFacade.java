package br.ufrj.cos.lens.odyssey.tools.charon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import processstructure.WorkDefinition;
import spem.SpemPackage;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.Agente;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.AgenteAcompanhamento;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.AgenteExecucao;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.AgenteRetrocesso;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.AgenteSimulacao;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.Disparador;

/**
 * Gerente singleton respons�vel por controlar o acesso a m�quina de processos
 * @author Leonardo Murta
 * @version 1.0, 08/08/2001
 */
public class CharonFacade implements Serializable {
	/**
	 * Identificador do Agente de Simula��o
	 */
	public static final int AGENTE_SIMULACAO = 0;

	/**
	 * Identificador do Agente de Execu��o
	 */
	public static final int AGENTE_EXECUCAO = 1;

	/**
	 * Identificador do Agente de Acompanhamento
	 */
	public static final int AGENTE_ACOMPANHAMENTO = 2;

	/**
	 * Identificador do Agente de Retrocesso
	 */
	public static final int AGENTE_RETROCESSO = 3;

	/**
	 * N�mero total de agentes cadastrados
	 */
	private static final int NUMERO_AGENTES = 4;

	/**
	 * Array de agentes
	 */
	private transient Agente[] agentes = null;

	/**
	 * Indica se a m�quina de processo est� em modo DEBUG
	 */
	public static final boolean DEBUG = false;

	/**
	 * Indica se o gerente de processo est� em modo visualizacao
	 */
	private boolean modoVisualizacao = false;

	/**
	 * Base de conhecimento que o usu�rio est� atualmente trabalhando
	 */
	private BaseConhecimento baseUsuario = null;

	/**
	 * Instancia singleton do gerente de processo
	 */
	private static CharonFacade instancia = null;

	/**
	 * Bases de conhecimento existentes indexadas pelos contextos em que elas
	 * foram criadas
	 */
	private Map<Object,BaseConhecimento> basesConhecimento = null;

	/**
	 * Profiles dos usu�rios (indexados pelo login do usu�rio)
	 */
	Map profiles = new HashMap();

	/**
	 * Constroi o gerente singleton
	 */
	public CharonFacade() {
		basesConhecimento = new HashMap<Object,BaseConhecimento>();
	}

	/**
	 * Fornece a inst�ncia �nica do gerente de processo
	 */
	public static CharonFacade getInstancia() {
		if (instancia == null)
			instancia = new CharonFacade();
		return instancia;
	}

	/**
	 * Atribui uma inst�ncia para o gerente (utilizado pelo mecanismo de armazenamento)
	 */
	public static void setInstancia(CharonFacade novaInstancia) {
		instancia = novaInstancia;

		// Chama os agentes para que seja efetuada a sua constru��o e inicializa��o
		// do mecanismo pr�-ativo.
		instancia.getAgente(AGENTE_SIMULACAO);
		instancia.getAgente(AGENTE_EXECUCAO);
		instancia.getAgente(AGENTE_ACOMPANHAMENTO);
		instancia.getAgente(AGENTE_RETROCESSO);
	}

	/**
	 * Modifica o modo de execu��o do gerente
	 */
	public void setModoVisualizacao(boolean modoVisualizacao) {
		this.modoVisualizacao = modoVisualizacao;
	}

	/**
	 * Verifica se o gerente est� em modo de visualizacao
	 */
	public boolean isModoVisualizacao() {
		return modoVisualizacao;
	}

	/**
	 * Fornece a base do usu�rio
	 */
	public BaseConhecimento getBaseUsuario() {
		return baseUsuario;
	}

	/**
	 * Atribui uma nova base de usu�rio
	 */
	public void setBaseUsuario(BaseConhecimento baseUsuario) {
		this.baseUsuario = baseUsuario;
//		String usuario = GerenteUsuario.getInstancia().getUsuarioLogado().getLogin();
//		Profile profile = getProfile(usuario);
//		profile.setNovoLogin();
	}

	/**
	 * Fornece o profile de um determinado usu�rio
	 */
	public Profile getProfile(String usuario) {
		Profile profile = (Profile)profiles.get(usuario);
		if (profile == null) {
			profile = new Profile();
			profiles.put(usuario, profile);
		}
		return profile;
	}

	/**
	 * Fornece o agente de simula��o
	 */
	public Agente getAgente(int tipo) {
		if ((tipo < 0) || (tipo >= NUMERO_AGENTES))
			return null;

		if (agentes == null)
			agentes = new Agente[NUMERO_AGENTES];

		Agente agente = agentes[tipo];
		if (agente == null) {
			switch (tipo) {
				case AGENTE_SIMULACAO :
					agente = new AgenteSimulacao();
					break;
				case AGENTE_EXECUCAO :
					agente = new AgenteExecucao();
					break;
				case AGENTE_ACOMPANHAMENTO :
					agente = new AgenteAcompanhamento();
					break;
				case AGENTE_RETROCESSO :
					agente = new AgenteRetrocesso();
					break;
			}
			agentes[tipo] = agente;
		}

		return agente;
	}

	/**
	 * Fornece as bases de conhecimento que est�o em um determinado estado
	 */
	public Iterator getBasesEstado(int estado) {
		Iterator basesExistentes = basesConhecimento.values().iterator();
		Set basesSelecionadas = new HashSet();

		while (basesExistentes.hasNext()) {
			BaseConhecimento base = (BaseConhecimento)basesExistentes.next();
			if (estado == base.getEstado())
				basesSelecionadas.add(base);
		}

		return basesSelecionadas.iterator();
	}
	
	/**
	 * Instancia um processo em um dado contexto.
	 *
	 * @param context Context em que o processo ser� instanciado. Esse objeto ser� usado
	 * futuramente para referenciar o processo.
	 * @param spemPackage pacote que contem todos os elementos do processo que est� sendo
	 * instanciado.
	 * @param rootProcess Processo que deve ser utilizado como raiz.
	 */
	public void instanciaProcesso(Object context, SpemPackage spemPackage, WorkDefinition rootProcess) {
		BaseConhecimento base = basesConhecimento.get(context);
		if (base == null) {
			base = new BaseConhecimento(spemPackage, rootProcess);
			basesConhecimento.put(context, base);
		} else {
			base.atualizaBase();
		}
	}

	/**
	 * Retrocede um processo dentro de um contexto.
	 *
	 * @param contexto Contexto em que o processo ser� retrocedido
	 */
	public void retrocedeProcesso(BaseConhecimento base) {
		if (base != null) {
			Agente agenteRetrocesso = CharonFacade.getInstancia().getAgente(CharonFacade.AGENTE_RETROCESSO);
			new Disparador(this, agenteRetrocesso, base);
		}
	}

	/**
	 * Acompanha a execu��o de um processo
	 */
	public void acompanhaProcesso(BaseConhecimento base) {
		if (base != null) {
			Agente agenteAcompanhamento = CharonFacade.getInstancia().getAgente(CharonFacade.AGENTE_ACOMPANHAMENTO);
			new Disparador(this, agenteAcompanhamento, base);
		}
	}

	/**
	 * Fornece a base de conhecimento de um determinado contexto
	 *
	 * @return Base de conhecimento do contexto ou null se ela n�o existir
	 */
	public BaseConhecimento getBaseConhecimento(Object context) {
		return (BaseConhecimento)basesConhecimento.get(context);
	}
}