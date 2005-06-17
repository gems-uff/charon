package br.ufrj.cos.lens.odyssey.tools.charon;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classe responsável por mapear processos modelados em fatos prolog
 * @author Leonardo Murta
 * @version 1.0, 18/11/2001
 */
public class Mapeador implements Serializable {

	/**
	 * Lista contendo o mapeamento de cada elemento de processo para fatos prolog.
	 * Útil para obter os fatos gerados.
	 */
	private List fatos = null;

	/**
	 * Map contendo o mapeamento de cada elemento de processo para o id prolog.
	 * Útil para fazer controle de recursão durante o mapeamento.
	 */
	private Map ids = null;

	/**
	 * Map contendo o mapeamento de cada id gerado para o elemento de processo
	 * propriamente dito. Útil para permitir a localização do objeto que representa
	 * o elemento de processo a partir do seu id no prolog.
	 */
	private Map elementos = null;

	/**
	 * Conjunto contendo todos os elementos que já foram mapeados. Útil para
	 * fazer o remapeamento.
	 */
	private transient Set mapeados = null;

	/**
	 * Contém o proximo id de processo livre
	 */
	private long proximoId = 0;

//	/**
//	 * Processo raiz do mapeamento
//	 */
//	private NoProcesso raiz = null;

	/**
	 * Construtor sem parâmetro para reflection
	 */
	public Mapeador() {}

//	/**
//	 * Mapeia um processo modelado e todas as suas referências em fatos prolog
//	 *
//	 * @param raiz Processo raiz do mapeamento
//	 */
//	public Mapeador(NoProcesso raiz) {
//		this.raiz = raiz;
//		ids = new HashMap();
//		elementos = new HashMap();
//		fatos = new ArrayList();
//		mapeados = new HashSet();
//
//		// Inicia o mapeamento
//		long id = mapeia(raiz);
//
//		// Atualiza os fatos com a informação do processo raiz
//		fatos.add("processoRaiz(" + id + ")");
//	}

	/**
	 * Fornece os predicados mapeados
	 *
	 * @return Lista de predicados mapeados
	 */
	public Iterator getFatosProlog() {
		return fatos.iterator();
	}

	/**
	 * Fornece o id de um elemento modelado
	 */
	public long getMapeamento(Object elemento) {
		return ((Long)ids.get(elemento)).longValue();
	}

	/**
	 * Fornece o elemento modelado de um id mapeado
	 */
	public Object getMapeamento(long id) {
		return elementos.get(new Long(id));
	}

//	/**
//	 * Fornece o processo raiz do mapeamento
//	 */
//	public NoProcesso getProcessoRaiz() {
//		return raiz;
//	}

//	/**
//	 * Reexecuta o mapeamento para refletir as evoluções do processo.
//	 * Necessário manter os IDs, pois toda a execução anterior faz referências
//	 * a eles.
//	 */
//	public void remapeia() {
//		fatos = new ArrayList();
//		mapeados = new HashSet();
//
//		// Inicia o mapeamento
//		long id = mapeia(raiz);
//
//		// Atualiza os fatos com a informação do processo raiz
//		fatos.add("processoRaiz(" + id + ")");
//	}

//	/**
//	 * Mapeia um processo em fatos prolog. Verifica qual o tipo do processo e
//	 * repassa para o mapeador apropriado.
//	 *
//	 * @param processo Processo a ser mapeado
//	 * @return Id criado para representar o processo no Prolog
//	 */
//	private long mapeia(NoProcesso processo) {
//		// Verifica se o processo já foi processado
//		long id = getId(processo);
//		if (!isMapeado(processo)) {
//			// Executa o mapeamento de acordo com o tipo do processo
//			if (processo.getTipo().equals(NoProcesso.PRIMITIVO))
//				mapeiaPrimitivo(processo, id);
//			else if (processo.getTipo().equals(NoProcesso.COMPOSTO))
//				mapeiaComposto(processo, id);
//
//			// Inclui fatos comuns
//			fatos.add("nome(" + id + ",'" + processo.getNome() + "')");
//		}
//
//		return id;
//	}

//	/**
//	 * Mapeia um processo primitivo em fatos prolog
//	 *
//	 * @param processo Processo a ser mapeado
//	 * @param id Id que será usado no fato prolog para mapear o processo
//	 * @return Fatos prolog resultantes do mapeamento
//	 */
//	private void mapeiaPrimitivo(NoProcesso processo, long id) {
//		fatos.add("processoPrimitivo(" + id + ")");
//
//		fatos.add("roteiro(" + id + ",'" + processo.getRoteiro() + "')");
//
//		fatos.add("simulado(" + id + ",'" + processo.getTempoSimulacao() + "')");
//
//		SerializableListModel ferramentas = processo.getFerramentas();
//		for (int i = 0; i < ferramentas.getSize(); i++) {
//			NoFerramenta ferramenta = (NoFerramenta)ferramentas.elementAt(i);
//			fatos.add("ferramenta(" + id + ",'" + ferramenta.getNome() + "')");
//		}
//
//		SerializableListModel papeis = processo.getPapeis();
//		for (int i = 0; i < papeis.getSize(); i++) {
//			NoPapel papel = (NoPapel)papeis.elementAt(i);
//			fatos.add("papel(" + id + ",'" + papel.getNome() + "')");
//		}
//
//		SerializableListModel artefatosEntrada = processo.getArtefatosEntrada();
//		for (int i = 0; i < artefatosEntrada.getSize(); i++) {
//			NoArtefato artefato = (NoArtefato)artefatosEntrada.elementAt(i);
//			fatos.add("artefatoEntrada(" + id + ",'" + artefato.getNome() + "')");
//		}
//
//		SerializableListModel artefatosSaida = processo.getArtefatosSaida();
//		for (int i = 0; i < artefatosSaida.getSize(); i++) {
//			NoArtefato artefato = (NoArtefato)artefatosSaida.elementAt(i);
//			fatos.add("artefatoSaida(" + id + ",'" + artefato.getNome() + "')");
//		}
//	}

//	/**
//	 * Mapeia um processo composto, recursivamente pelos seus sub-processos, em
//	 * fatos prolog
//	 *
//	 * @param processo Processo a ser mapeado
//	 * @param id Id que será usado no fato prolog para mapear o processo
//	 * @return Fatos prolog resultantes do mapeamento
//	 */
//	private void mapeiaComposto(NoProcesso processo, long id) {
//		fatos.add("processoComposto(" + id + ")");
//
//		// Percorre todas as arestas mapeando para prolog. Para fazer esse mapeamento
//		// é necessário mapear cada um dos elementos "origem" e "destino" recursivamente
//		Enumeration fluxos = processo.getDiagrama().getEdges();
//		while (fluxos.hasMoreElements()) {
//			ArestaPadrao fluxo = (ArestaPadrao)fluxos.nextElement();
//			NoPadrao origem = fluxo.getSource();
//			NoPadrao destino = fluxo.getTarget();
//
//			String prologOrigem = mapeia(origem, id);
//			String prologDestino = mapeia(destino, id);
//
//			fatos.add("fluxo(" + prologOrigem + "," + prologDestino + ")");
//		}
//	}

//	/**
//	 * Mapeia um elemento de desenho de diagrama em prolog
//	 *
//	 * @param no Elemento a ser mapeado
//	 * @param idWorkflow Identificador no prolog do processo dono do diagrama
//	 * @return Predicado que representa o elemento mapeado no prolog
//	 */
//	private String mapeia(NoPadrao no, long idWorkflow) {
//		String predicado = null;
//		long id;
//
//		NoSemantico noSemantico = no.getSemantic();
//		switch (noSemantico.getIdentificador()) {
//			case Categoria.idInicioProcesso :
//				predicado = "inicio(" + idWorkflow + ")";
//				break;
//
//			case Categoria.idProcesso :
//				// Verifica se o elemento já foi processado
//				id = getId(no);
//				if (!isMapeado(no)) {
//					NoProcesso processo = (NoProcesso)noSemantico;
//					long idClasse = mapeia(processo);
//
//					// Cria o mapeamento do processo em prolog na Map de fatos
//					fatos.add("classeProcesso(" + id + "," + idClasse + ")");
//				}
//
//				// Prepara o predicado de retorno
//				predicado = "processo(" + id + ")";
//				break;
//
//			case Categoria.idDecisao :
//				// Verifica se o elemento já foi processado
//				id = getId(no);
//				if (!isMapeado(no)) {
//					// Cria o mapeamento da decisão em prolog na Map de fatos
//					NoDecisao decisao = (NoDecisao)noSemantico;
//					fatos.add("pergunta(" + id + ",'" + decisao.getPergunta() + "')");
//					fatos.add("simulado(" + id + ",'" + decisao.getTempoSimulacao() + "')");
//
//					SerializableListModel papeis = decisao.getPapeis();
//					for (int i = 0; i < papeis.getSize(); i++) {
//						NoPapel papel = (NoPapel)papeis.elementAt(i);
//						fatos.add("papel(" + id + ",'" + papel.getNome() + "')");
//					}
//
//					// Procura todos os fluxos que saem dessa decisão
//					NoProcesso processo = (NoProcesso)elementos.get(new Long(idWorkflow));
//					Enumeration fluxos = processo.getDiagrama().getEdges();
//					while (fluxos.hasMoreElements()) {
//						ArestaPadrao fluxo = (ArestaPadrao)fluxos.nextElement();
//						if (fluxo.getSource() == no) {
//							String resposta = ((LigFluxo)fluxo.getSemantic()).getValor();
//							fatos.add("resposta(" + id + ",'" + resposta + "'," + mapeia(fluxo.getTarget(), idWorkflow) + ")");
//						}
//					}
//				}
//
//				// Prepara o predicado de retorno
//				predicado = "decisao(" + id + ")";
//				break;
//
//			case Categoria.idSincronismo :
//				// Pega o ID do elemento
//				id = getId(no);
//
//				// Prepara o predicado de retorno
//				predicado = "sincronismo(" + id + ")";
//				break;
//
//			case Categoria.idFimProcesso :
//				predicado = "termino";
//				break;
//		}
//
//		return predicado;
//	}

	/**
	 * Fornece um id para um elemento, fazendo a inserção nas tabelas necessárias
	 */
	private long getId(Object elemento) {
		Long id = (Long)ids.get(elemento);
		if (id == null) {
			// Cria o id do elemento e insere na Map de ids e na de elementos de processos
			id = new Long(proximoId++);
			elementos.put(id, elemento);
			ids.put(elemento, id);
		}

		return id.longValue();
	}

	/**
	 * Verifica se um elemento foi mapeado, e o adiciona na lista de mapeados
	 */
	private boolean isMapeado(Object elemento) {
		if (mapeados.contains(elemento))
			return true;
		else {
			mapeados.add(elemento);
			return false;
		}
	}
}