package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonActivity;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonDecision;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonElement;

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

		for (Map<String,Object> solution : knowledgeBase.getAllSolutions("pendingActivities(" + roles + ", IdP, C, Performers).")) {
			String activityID = (String) solution.get("IdP");
			String context = solution.get("C").toString();
			List performers = (List) solution.get("Performers");
			result.add(new CharonActivity(activityID, context, performers));
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

		for (Map<String,Object> solution : knowledgeBase.getAllSolutions("pendingDecisions(" + roles + ", IdD, C, Performers).")) {
			String decisionID = (String) solution.get("IdD");
			String context = solution.get("C").toString();
			List performers = (List) solution.get("Performers");
			result.add(new CharonDecision(decisionID, context, performers));
		}
		
		disconnect();
		return result;
	}	

	/**
	 * Defines the people that are performing a collection of activities or decisions
	 */
	public void setPerformers(List<String> performers, KnowledgeBase knowledgeBase, Collection<? extends CharonElement> elements) {
		connect(knowledgeBase);
		
		for (CharonElement element : elements) {
			knowledgeBase.isSolvable("setPerformers(" + performers + ", '" + element.getId() + "', " + element.getContext() + ").");
		}
		
		disconnect();
	}
	
	/**
	 * Finish some activities in the name of a specific user
	 */
	public void finishActivities(String user, KnowledgeBase knowledgeBase, Collection<CharonActivity> activities) {
		connect(knowledgeBase);
		long currentTime = System.currentTimeMillis() / 1000;
		
		Collection<String> clauses = new ArrayList<String>();
		for (CharonActivity activity : activities) {
			clauses.add("finished('" + activity.getId() + "', " + activity.getContext() + ", " + currentTime + ", '" + user + "')");
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
			for (String selectedOption : decision.getSelectedOptions()) {
				clauses.add("selected('" + decision.getId() + "', " + decision.getContext() + ", '" + selectedOption + "', " + currentTime + ", '" + user + "')");
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
			
			rules.add("(setPerformers(NewPerformers, IdA, C) :- executing(activity(IdA), C, Ti, OldPerformers), !, retract(executing(activity(IdA), C, Ti, OldPerformers)), assertz(executing(activity(IdA), C, Ti, NewPerformers)))");
			rules.add("(setPerformers(NewPerformers, IdD, C) :- !, executing(decision(IdD), C, Ti, OldPerformers), !, retract(executing(decision(IdD), C, Ti, OldPerformers)), assertz(executing(decision(IdD), C, Ti, NewPerformers)))");

			rules.add("(pendingActivities(PUs, IdP, C, Performers) :- !, executing(activity(IdP), C, _, Performers), type(IdP, IdC), findall(PP, role(IdC, PP), PPs), intersection(PUs, PPs))");

			rules.add("(pendingDecisions(PUs, IdD, C, Performers) :- !, executing(decision(IdD), C, _, Performers), findall(PD, role(IdD, PD), PDs), intersection(PUs, PDs))");

			rules.add("(intersection([X|_], L2) :- " + "member(X, L2), " + "!)");
			rules.add("(intersection([_|L1], L2) :- " + "intersection(L1, L2), " + "!)");

		}

		return rules;
	}
}