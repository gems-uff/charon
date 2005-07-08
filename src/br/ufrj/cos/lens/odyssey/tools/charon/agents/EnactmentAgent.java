package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrj.cos.lens.odyssey.tools.charon.AgentManager;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonException;
import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * This class is responsible for enacting the process
 *
 * @author Leo Murta
 * @version 1.0, 14/12/2001
 */
public class EnactmentAgent extends Agent {
	/**
	 * Agent's rules
	 */
	private Collection<String> rules = null;

	/**
	 * Constructs the agent as a listener of the following through agent
	 */
	public EnactmentAgent() throws CharonException {
		super();
		AgentManager.getInstance().getAgent(FollowingThroughAgent.class).addDisconnectionListener(this);
	}

	/**
	 * Instantiates a given knowledge base.
	 * 
	 * @return key to the instantiated process
	 */
	public String instantiate(KnowledgeBase knowledgeBase, String processClassId) {
		connect(knowledgeBase);
		String processId = String.valueOf(System.currentTimeMillis());
		long currentTime = System.currentTimeMillis() / 1000;
		knowledgeBase.isSolvable("assertz(classeProcesso('" + processId + "', '" + processClassId + "')), inicia(processo('" + processId + "'), [], " + currentTime + ").");
		disconnect();
		return processId;
	}

	/**
	 * Run after the execution of the following through agent, searching for new
	 * elements in the process that may lead to demanding actions.
	 * @see br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent#disconnectionPerformed(br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase)
	 */
	@Override
	public void disconnectionPerformed(KnowledgeBase knowledgeBase) {
		connect(knowledgeBase);
		
		// Current time in seconds
		long currentTime = System.currentTimeMillis() / 1000;

		// Finish the already executed activities
		String finishActivities = "processoPrimitivo(IdC), classeProcesso(IdP, IdC), finaliza(processo(IdP), _, " + currentTime + ").";
		while (knowledgeBase.isSolvable(finishActivities)) {}
		
		// Finish the already executed decisions
		String finishDecisions = "finaliza(decisao(_), _, " + currentTime + ").";
		while (knowledgeBase.isSolvable(finishDecisions)) {}

		disconnect();
	}
	
	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent#getRules()
	 */
	@Override
	public Collection<String> getRules() {
		if (rules == null) {
			rules = new ArrayList<String>();

			rules.add("inicia([],_,_)");
			rules.add("(inicia([E|Es], P, T) :- inicia(E, P, T), !, inicia(Es, P, T))");
			rules.add("(inicia([_|Es], P, T) :- !, inicia(Es, P, T))");

			rules.add("(inicia(inicio(Id), P, T) :- !, finaliza(inicio(Id), P, T))");

			rules.add("(inicia(processo(IdP), P, T) :- classeProcesso(IdP, IdC), processoPrimitivo(IdC), !, not(executando(processo(IdP), P, _)), assertz(executando(processo(IdP), P, T)))");

			rules.add("(inicia(processo(IdP), P, T) :- classeProcesso(IdP, IdC), processoComposto(IdC), !, not(executando(processo(IdP), P, _)), assertz(executando(processo(IdP), P, T)), inicia(inicio(IdC), [processo(IdP)|P], T))");

			rules.add("(inicia(decisao(IdD), P, T) :- !, not(executando(decisao(IdD), P, _)), assertz(executando(decisao(IdD), P, T)))");

			rules.add("(inicia(sincronismo(IdS), P, T) :- not(executando(sincronismo(IdS), P, _)), !, assertz(executando(sincronismo(IdS), P, T)), finaliza(sincronismo(IdS), P, T))");

			rules.add("(inicia(sincronismo(IdS), P, T) :- !, finaliza(sincronismo(IdS), P, T))");

			rules.add("(inicia(termino(IdC), P, T) :- !, finaliza(termino(IdC), P, T))");

			rules.add("(finaliza(inicio(IdC), P, T) :- !, findall(E, fluxo(inicio(IdC), E), Es), inicia(Es, P, T))");

			rules.add("(finaliza(processo(IdP), P, T) :- classeProcesso(IdP, IdC), processoPrimitivo(IdC), !, executando(processo(IdP), P, Ti), finalizado(IdP, P, Tf, _), Tf > Ti, Tf =< T, retract(executando(processo(IdP), P, Ti)), assertz(executado(processo(IdP), P, Ti, Tf)), findall(E, fluxo(processo(IdP), E), Es), inicia(Es, P, Tf))");

			rules.add("(finaliza(processo(IdP), P, T) :- classeProcesso(IdP, IdC), processoComposto(IdC), !, executando(processo(IdP), P, Ti), retract(executando(processo(IdP), P, Ti)), assertz(executado(processo(IdP), P, Ti, T)), findall(E, fluxo(processo(IdP), E), Es), inicia(Es, P, T))");

			rules.add("(finaliza(decisao(IdD), P, T) :- !, executando(decisao(IdD), P, Ti), findall(E, (respondido(IdD, P, R, Tr, _), Tr > Ti, Tr =< T, resposta(IdD, R, E)), Es), Es \\= [], retract(executando(decisao(IdD), P, Ti)), assertz(executado(decisao(IdD), P, Ti, T)), inicia(Es, P, T))");

			rules.add("(finaliza(sincronismo(IdS), P, T) :- !, executando(sincronismo(IdS), P, Ti), findall(E1,(fluxo(E1, sincronismo(IdS)), E1 \\= decisao(_), executado(E1, P, _, Tf), Tf >= Ti, Tf =< T), E1s), findall(E2,(fluxo(E2, sincronismo(IdS)), E2 = decisao(IdD), respondido(IdD, P, R, Tr, _), resposta(IdD, R, sincronismo(IdS)), Tr >= Ti, Tr =< T), E2s), append(E1s, E2s, E3s), findall(E4,(fluxo(E4, sincronismo(IdS)), E4 \\= inicio(_), E4 \\= decisao(_)), E4s), findall(E5,(fluxo(E5, sincronismo(IdS)), E5 = decisao(_)), E5s), append(E4s, E5s, E6s), E3s = E6s, retract(executando(sincronismo(IdS), P, Ti)), assertz(executado(sincronismo(IdS), P, Ti, T)), findall(E7, fluxo(sincronismo(IdS), E7), E7s), inicia(E7s, P, T))");

			rules.add("(finaliza(termino(_), [processo(IdP)|P], T) :- !, finaliza(processo(IdP), P, T))");
		}

		return rules;
	}
}