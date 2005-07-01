package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.ufrj.cos.lens.odyssey.tools.charon.BaseConhecimento;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonFacade;

/**
 * Agente responsável por executar o processo
 *
 * @author Leo Murta
 * @version 1.0, 14/12/2001
 */
public class AgenteExecucao extends Agente {
	/**
	 * Lista de regras do agente
	 */
	private List<String> regras = null;

	/**
	 * Constroi o agente se cadastrando como escutador do agente de acompanhamento
	 */
	public AgenteExecucao() {
		super(5000);
		Agente agenteAcompanhamento = CharonFacade.getInstancia().getAgente(CharonFacade.AGENTE_ACOMPANHAMENTO);
		agenteAcompanhamento.addEscutador(this);
	}

	/**
	 * Responde ao evento de desconexão do agente de acompanhamento verificando se
	 * algo novo que entrou na base motiva a continuação da execução
	 */
	public void executaReativo(Object origem, BaseConhecimento base) {
		if ((base == null) || (base.getEstado() != BaseConhecimento.BASE_EXECUTANDO))
			return;

		conecta(base);

		// Tempo em segundos
		long tempo = (System.currentTimeMillis() / 1000);

		// Finaliza os processos primitivos finalizados pelo usuário
		String finalizaProcesso = "processoPrimitivo(IdC), classeProcesso(IdP, IdC), finaliza(processo(IdP), _, " + tempo + ")";
		while (base.getProlog().getRespostaBooleana(finalizaProcesso));

		// Finaliza as decisões respondidas pelo usuário
		String finalizaDecisao = "finaliza(decisao(_), _, " + tempo + ")";
		while (base.getProlog().getRespostaBooleana(finalizaDecisao));

		// Vereifica se terminou a execução, mudando o estado da base
		if (base.getProlog().getRespostaBooleana("executado(processo(raiz), [], _, _))"))
			base.setEstado(BaseConhecimento.BASE_FINALIZADA);

		desconecta();
	}

	/**
	 * Verifica se existe alguma base pendente esperando que se inicie a execução
	 */
	public void executaProativo() {
		setProativo(false);
		Iterator basesPendentes = CharonFacade.getInstancia().getBasesEstado(BaseConhecimento.BASE_PENDENTE);
		while (basesPendentes.hasNext()) {
			BaseConhecimento base = (BaseConhecimento)basesPendentes.next();
			conecta(base);

			long tempo = System.currentTimeMillis() / 1000;

			base.getProlog().getRespostaBooleana("processoRaiz(P), assertz(classeProcesso(raiz, P)), inicia(processo(raiz), [], " + tempo + ")");

			base.setEstado(BaseConhecimento.BASE_EXECUTANDO);
			desconecta();
		}
		setProativo(true);
	}

	/**
	 * Fornece a lista de regras existentes no agente
	 */
	public Collection<String> getRegras() {
		if (regras == null) {
			regras = new ArrayList<String>();

			regras.add("inicia([],_,_)");
			regras.add("(inicia([E|Es], P, T) :- " + "inicia(E, P, T), " + "!, " + "inicia(Es, P, T))");
			regras.add("(inicia([_|Es], P, T) :- " + "!, " + "inicia(Es, P, T))");

			regras.add("(inicia(inicio(Id), P, T) :- " + "!, " + "finaliza(inicio(Id), P, T))");

			regras.add("(inicia(processo(IdP), P, T) :- " + "classeProcesso(IdP, IdC), " + "processoPrimitivo(IdC), " + "!, " + "not(executando(processo(IdP), P, _)), " + "assertz(executando(processo(IdP), P, T)))");

			regras.add("(inicia(processo(IdP), P, T) :- " + "classeProcesso(IdP, IdC), " + "processoComposto(IdC), " + "!, " + "not(executando(processo(IdP), P, _)), " + "assertz(executando(processo(IdP), P, T)), " + "inicia(inicio(IdC), [processo(IdP)|P], T))");

			regras.add("(inicia(decisao(IdD), P, T) :- " + "!, " + "not(executando(decisao(IdD), P, _)), " + "assertz(executando(decisao(IdD), P, T)))");

			regras.add("(inicia(sincronismo(IdS), P, T) :- " + "not(executando(sincronismo(IdS), P, _)), " + "!, " + "assertz(executando(sincronismo(IdS), P, T)), " + "finaliza(sincronismo(IdS), P, T))");

			regras.add("(inicia(sincronismo(IdS), P, T) :- " + "!, " + "finaliza(sincronismo(IdS), P, T))");

			regras.add("(inicia(termino, P, T) :- " + "!, " + "finaliza(termino, P, T))");

			regras.add("(finaliza(inicio(IdC), P, T) :- " + "!, " + "findall(E, fluxo(inicio(IdC), E), Es), " + "inicia(Es, P, T))");

			regras.add("(finaliza(processo(IdP), P, T) :- " + "classeProcesso(IdP, IdC), " + "processoPrimitivo(IdC), " + "!, " + "executando(processo(IdP), P, Ti), " + "finalizado(IdP, P, Tf, _), " + "Tf > Ti, " + "Tf <= T, " + "retract(executando(processo(IdP), P, Ti)), " + "assertz(executado(processo(IdP), P, Ti, Tf)), " + "findall(E, fluxo(processo(IdP), E), Es), " + "inicia(Es, P, Tf))");

			regras.add("(finaliza(processo(IdP), P, T) :- " + "classeProcesso(IdP, IdC), " + "processoComposto(IdC), " + "!, " + "executando(processo(IdP), P, Ti), " + "retract(executando(processo(IdP), P, Ti)), " + "assertz(executado(processo(IdP), P, Ti, T)), " + "findall(E, fluxo(processo(IdP), E), Es), " + "inicia(Es, P, T))");

			regras.add("(finaliza(decisao(IdD), P, T) :- " + "!, " + "executando(decisao(IdD), P, Ti), " + "respondido(IdD, P, R, Tr, _), " + "Tr > Ti, " + "Tr <= T, " + "retract(executando(decisao(IdD), P, Ti)), " + "assertz(executado(decisao(IdD), P, Ti, Tr)), " + "findall(E, resposta(IdD, R, E), Es), " + "inicia(Es, P, Tr))");

			regras.add("(finaliza(sincronismo(IdS), P, T) :- " + "!, " + "executando(sincronismo(IdS), P, Ti), " + "findall(E1,(fluxo(E1, sincronismo(IdS)), E1 \\= decisao(_), executado(E1, P, _, Tf), Tf >= Ti, Tf <= T), E1s), " + "findall(E2,(fluxo(E2, sincronismo(IdS)), E2 = decisao(IdD), respondido(IdD, P, R, Tr, _), resposta(IdD, R, sincronismo(IdS)), Tr >= Ti, Tr <= T), E2s), " + "append(E1s, E2s, E3s), " + "findall(E4,(fluxo(E4, sincronismo(IdS)), E4 \\= inicio(_), E4 \\= decisao(_)), E4s), " + "findall(E5,(fluxo(E5, sincronismo(IdS)), E5 = decisao(_)), E5s), " + "append(E4s, E5s, E6s), " + "E3s = E6s, " + "retract(executando(sincronismo(IdS), P, Ti)), " + "assertz(executado(sincronismo(IdS), P, Ti, T)), " + "findall(E7, fluxo(sincronismo(IdS), E7), E7s), " + "inicia(E7s, P, T))");

			regras.add("(finaliza(termino, [processo(IdP)|P], T) :- " + "!, " + "finaliza(processo(IdP), P, T))");
		}

		return regras;
	}
}