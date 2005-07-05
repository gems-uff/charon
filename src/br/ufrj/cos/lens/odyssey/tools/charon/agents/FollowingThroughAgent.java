package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonFacade;

/**
 * Agente responsável por acompanhar a execução do processo fazendo interface com
 * o usuário
 *
 * @author Leo Murta
 * @version 1.0, 16/12/2001
 */
public class FollowingThroughAgent extends Agent {
	/**
	 * Lista de regras do agente
	 */
	private List<String> regras = null;

	/**
	 * Constroi o agente de acompanhamento
	 */
	public FollowingThroughAgent() {
		super(5000);
	}

	/**
	 * Exibe a jenela de acompanhamento. Reação à seleção de opção de menu por
	 * parte do usuário.
	 *
	 * @param origem Origem do evento
	 * @param base Base que está relacionada com o evanto
	 */
	public void executaReativo(Object origem, KnowledgeBase base) {
		switch (base.getState()) {
			case KnowledgeBase.BASE_FINALIZADA :
				JOptionPane.showMessageDialog(null, "The process has been finished.", "Process Accompaniment Agent", JOptionPane.INFORMATION_MESSAGE);
				return;

			case KnowledgeBase.BASE_SIMULANDO :
				JOptionPane.showMessageDialog(null, "The process is being simulated. Please, wait a moment and try again.", "Process Accompaniment Agent", JOptionPane.INFORMATION_MESSAGE);
				return;

			case KnowledgeBase.BASE_PENDENTE :
				JOptionPane.showMessageDialog(null, "The process is waiting for its initialization. Please, wait a moment and try again.", "Process Accompaniment Agent", JOptionPane.INFORMATION_MESSAGE);
				return;

			case KnowledgeBase.BASE_ERRO :
				JOptionPane.showMessageDialog(null, "The process has some errors. Please, correct them and reinstantiate it.", "Process Accompaniment Agent", JOptionPane.INFORMATION_MESSAGE);
				return;
		}

		setProativo(false);
		conecta(base);

		// Pega o login do usuário corrente
//		String usuario = GerenteUsuario.getInstancia().getUsuarioLogado().getLogin();
		String usuario = "test"; // TODO: Remover
		
		Iterator respostas = null;

		// Constroi a lista de processos pendentes
		List processos = new ArrayList();
//		respostas = base.getProlog().getRespostas("processoPendente('" + usuario + "', IdP, C)");
		while (respostas.hasNext()) {
			Map resposta = (Map)respostas.next();

			try {
				long id = Long.parseLong((String)resposta.get("IdP"));
				String contexto = resposta.get("C").toString();
				Processo processo = new Processo(id, contexto);
				processos.add(processo);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// Constroi a lista de decisões pendentes
		List decisoes = new ArrayList();
//		respostas = base.getProlog().getRespostas("decisaoPendente('" + usuario + "', IdD, C, Rs)");
		while (respostas.hasNext()) {
			Map resposta = (Map)respostas.next();

			try {
				long id = Long.parseLong((String)resposta.get("IdD"));
				String contexto = resposta.get("C").toString();
				List respostasPossiveis = (List)resposta.get("Rs");
				Decisao decisao = new Decisao(id, contexto, respostasPossiveis);
				decisoes.add(decisao);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

//		JanelaAcompanhamento janela = new JanelaAcompanhamento(this, processos, decisoes, GerenteProcesso.getInstancia().getProfile(usuario).getOpcaoAparecimento());

		// Pega o tempo atual
		long tempo = System.currentTimeMillis() / 1000;

		List<String> prolog = new ArrayList<String>();

//		Iterator processos2 = janela.getProcessosFinalizados();
//		while (processos2.hasNext()) {
//			Processo processo = (Processo)processos2.next();
//			prolog.add("finalizado(" + processo.getId() + ", " + processo.getContexto() + ", " + tempo + ", '" + usuario + "')");
//		}

//		Iterator decisoes2 = janela.getDecisoesTomadas();
//		while (decisoes2.hasNext()) {
//			Decisao decisao = (Decisao)decisoes2.next();
//			prolog.add("respondido(" + decisao.getId() + ", " + decisao.getContexto() + ", " + decisao.getResposta() + ", " + tempo + ", '" + usuario + "')");
//		}

		getBase().getInferenceMachine().addClauses(prolog);

//		GerenteProcesso.getInstancia().getProfile(usuario).setOpcaoAparecimento(janela.getOpcaoAparecimento());

		setProativo(true);
		desconecta();
	}

	/**
	 * Verifica se deve exibir a janela de acompanhamento segundo as opções do
	 * usuário
	 */
	public void executaProativo() {

		// TODO: Definir comportamento proativo 
		//		// Pega o login do usuário corrente
////		String usuario = GerenteUsuario.getInstancia().getUsuarioLogado().getLogin();
//		String usuario = "test";
//		
//		KnowledgeBase base = CharonFacade.getInstance().getBaseUsuario();
//
//		if ((base != null) && (CharonFacade.getInstance().getProfile(usuario).mostraAgora())) {
//			setProativo(false);
//			new Disparador(this, this, CharonFacade.getInstance().getBaseUsuario());
//		}
	}

	/**
	 * Fornece a lista de regras existentes no agente
	 */
	public Collection<String> getRegras() {
		if (regras == null) {
			regras = new ArrayList<String>();

			regras.add("(processoPendente(U, IdP, C) :- " + "findall(PU, usuario(U,PU), PUs), " + "!, " + "executando(processo(IdP), C, _), " + "classeProcesso(IdP, IdC), " + "processoPrimitivo(IdC), " + "findall(PP, papel(IdC, PP), PPs), " + "intersecao(PUs, PPs))");

			regras.add("(decisaoPendente(U, IdD, C, Rs) :- " + "findall(PU, usuario(U,PU), PUs), " + "!, " + "executando(decisao(IdD), C, _), " + "findall(PD, papel(IdD, PD), PDs), " + "intersecao(PUs, PDs), " + "findall(R, resposta(IdD, R, _), Rs))");

			regras.add("(intersecao([X|_], L2) :- " + "member(X, L2), " + "!)");
			regras.add("(intersecao([_|L1], L2) :- " + "intersecao(L1, L2), " + "!)");

		}

		return regras;
	}
}