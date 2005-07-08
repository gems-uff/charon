package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonActivity;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonDecision;

/**
 * This agent is responsible for following through the process
 *
 * @author Leo Murta
 * @version 1.0, 16/12/2001
 */
public class FollowingThroughAgent extends Agent {
	/**
	 * Agent's rules
	 */
	private Collection<String> rules = null;

	/**
	 * Constructs the agent
	 */
	public FollowingThroughAgent() {
		// TODO: May code a proactive behavior notifying an external component to send e-mail describing pending processes and decisions.
		super();
	}

	/**
	 * Provides the pending activities for a given collection of roles 
	 */
	public Collection<CharonActivity> getPendingActivities(KnowledgeBase knowledgeBase, Collection<String> roles) {
		Collection<CharonActivity> result = new ArrayList<CharonActivity>();		
		connect(knowledgeBase);

		for (Map<String,Object> solution : knowledgeBase.getAllSolutions("processoPendente('" + roles + "', IdP, C).")) {
			String activityID = (String)solution.get("IdP");
			String context = solution.get("C").toString();
			result.add(new CharonActivity(activityID, context));
		}
		
		disconnect();		
		return result;
	}
	
	/**
	 * Provides the pending decisions for a given collection of roles 
	 */
	public Collection<CharonDecision> getPendingDecisions(KnowledgeBase knowledgeBase, Collection<String> roles) {
		Collection<CharonDecision> result = new ArrayList<CharonDecision>();		
		connect(knowledgeBase);

		for (Map<String,Object> solution : knowledgeBase.getAllSolutions("decisaoPendente('" + roles + "', IdD, C).")) {
			String decisionID = (String)solution.get("IdD");
			String context = solution.get("C").toString();
			result.add(new CharonDecision(decisionID, context));
		}
		
		disconnect();
		return result;
	}	

	/**
	 * Finish some activities in the name of a specific user
	 */
	public void finishActivities(String user, KnowledgeBase knowledgeBase, Collection<CharonActivity> activities) {
		connect(knowledgeBase);
		long currentTime = System.currentTimeMillis() / 1000;
		
		Collection<String> clauses = new ArrayList<String>();
		for (CharonActivity activity : activities) {
			clauses.add("finalizado('" + activity.getId() + "', " + activity.getContext() + ", " + currentTime + ", '" + user + "')");
		}
		knowledgeBase.addClauses(clauses);
		
		disconnect();
	}

	/**
	 * Take some decisions in the name of a specific user
	 */
	public void makeDecisions(String user, KnowledgeBase knowledgeBase, Collection<CharonDecision> decisions) {
		connect(knowledgeBase);

		long currentTime = System.currentTimeMillis() / 1000;
		
		Collection<String> clauses = new ArrayList<String>();
		for (CharonDecision decision : decisions) {
			for (String selectionId : decision.getSelectionIds()) {
				clauses.add("respondido('" + decision.getId() + "', " + decision.getContext() + ", '" + selectionId + "', " + currentTime + ", '" + user + "')");
			}
		}		
		knowledgeBase.addClauses(clauses);
		
		disconnect();
	}
	
	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent#getRules()
	 */
	@Override
	public Collection<String> getRules() {
		if (rules == null) {
			rules = new ArrayList<String>();

			rules.add("(processoPendente(PU, IdP, C) :- !, executando(processo(IdP), C, _), classeProcesso(IdP, IdC), processoPrimitivo(IdC), findall(PP, papel(IdC, PP), PPs), intersecao(PUs, PPs))");

			rules.add("(decisaoPendente(PU, IdD, C) :- !, executando(decisao(IdD), C, _), findall(PD, papel(IdD, PD), PDs), intersecao(PUs, PDs))");

			rules.add("(intersecao([X|_], L2) :- " + "member(X, L2), " + "!)");
			rules.add("(intersecao([_|L1], L2) :- " + "intersecao(L1, L2), " + "!)");

		}

		return rules;
	}
}