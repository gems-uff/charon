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
		knowledgeBase.isSolvable("assertz(type('" + processId + "', '" + processClassId + "')), start(process('" + processId + "'), [], " + currentTime + ").");
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
		String finishActivities = "finish(activity(_), _, " + currentTime + ").";
		while (knowledgeBase.isSolvable(finishActivities)) {}
		
		// Finish the already executed decisions
		String finishDecisions = "finish(decision(_), _, " + currentTime + ").";
		while (knowledgeBase.isSolvable(finishDecisions)) {}

		disconnect();
	}
	
	public String intializeExperimentExecution(KnowledgeBase knowledgeBase, String experimentId){
		connect(knowledgeBase);
		String processId = String.valueOf(System.currentTimeMillis());
		long currentTime = System.currentTimeMillis() / 1000;
		boolean isSolvable = knowledgeBase.isSolvable("start(experiment('"+experimentId+"'), '"+processId+"', '"+currentTime+"').");
		disconnect();
		if(isSolvable){
			return processId;
		}
		else
			return null;
	}
	
	public boolean notifyActivityExecutionStartup(KnowledgeBase knowledgeBase, String activityInstanceId, String[] context){
		connect(knowledgeBase);
		long currentTime = System.currentTimeMillis() / 1000;
		String contextList = createContextList(context);
		boolean isSolvable = knowledgeBase.isSolvable("start(activity('"+activityInstanceId+"'), "+contextList+", '"+currentTime+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean notifyProcessExecutionStartup(KnowledgeBase knowledgeBase, String processInstanceId, String[] context){
		connect(knowledgeBase);
		long currentTime = System.currentTimeMillis() / 1000;
		boolean isSolvable = knowledgeBase.isSolvable("start(process('"+processInstanceId+"'), "+context+", '"+currentTime+"')).");
		disconnect();
		return isSolvable;
	}

	public boolean notifyActivityExecutionEnding(KnowledgeBase knowledgeBase, String activityInstanceId, String[] context){
		connect(knowledgeBase);
		long currentTime = System.currentTimeMillis() / 1000;
		String contextList = createContextList(context);
		boolean isSolvable = knowledgeBase.isSolvable("finish(activity('"+activityInstanceId+"'), "+contextList+", '"+currentTime+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean notifyProcessExecutionEnding(KnowledgeBase knowledgeBase, String processInstanceId, String[] context){
		connect(knowledgeBase);
		long currentTime = System.currentTimeMillis() / 1000;
		String contextList = createContextList(context);
		boolean isSolvable = knowledgeBase.isSolvable("finish(process('"+processInstanceId+"'), "+contextList+", '"+currentTime+"').");
		disconnect();
		return isSolvable;
	}

	public boolean notifyDecisionPointEnding(KnowledgeBase knowledgeBase, String decisionPointId, String[] context){
		connect(knowledgeBase);
		long currentTime = System.currentTimeMillis() / 1000;
		String contextList = createContextList(context);
		boolean isSolvable = knowledgeBase.isSolvable("finish(decision('"+decisionPointId+"'), "+contextList+", '"+currentTime+"').");
		disconnect();
		return isSolvable;
	}

	/*
	 * TODO: Fazer esse método...
	 */
	public boolean setArtifactData(KnowledgeBase knowledgeBase, String artifactId, String[] context, byte[] data){
		
		/*
		 * Armazenar dados do artefato em banco de dados
		 */
		String databaseId = null;
		
		
		connect(knowledgeBase);
		String SWFMSId = String.valueOf(System.currentTimeMillis());
		String contextList = createContextList(context);
//		boolean isSolvable = knowledgeBase.isSolvable("assertz(artifactData('"+artifactId+"', '"+databaseId+"', '"+contextList+"')).");
		boolean isSolvable = knowledgeBase.isSolvable("assertz(artifactData('"+artifactId+"', '"+contextList+"', '"+new String(data)+"')).");
		disconnect();
		return isSolvable;
	}
	
	public boolean publishArtifactDataLocation(KnowledgeBase knowledgeBase, String artifactId, String[] context, String hostURL, String hostLocalPath){
		
		connect(knowledgeBase);
		String contextList = createContextList(context);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(artifactDataLocation('"+artifactId+"', '"+contextList+"', '"+hostURL+"', '"+hostLocalPath+"')).");
		disconnect();
		return isSolvable;

	}
		
	public String createContextList(String[] context){
		StringBuffer contextList = new StringBuffer("[");
		
		for (String element : context) {
			contextList.append(element+",");
		}
		
		if(contextList.length()>1)
			return contextList.substring(0, contextList.length())+"]";
		else
			return contextList.toString()+"]";
	}	
	
	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent#getRules()
	 */
	@Override
	public Collection<String> getRules() {
		if (rules == null) {
			rules = new ArrayList<String>();

			rules.add("start(experiment(experimentId), IdP, T) :- !, experimentRootProcess(experimentId, IdC), assertz(type(IdP, IdC)), start(process(IdP), [], T).");
			
			rules.add("start([],_,_)");
			rules.add("(start([E|Es], P, T) :- start(E, P, T), !, start(Es, P, T))");
			rules.add("(start([_|Es], P, T) :- !, start(Es, P, T))");

			rules.add("(start(initial(Id), P, T) :- !, finish(initial(Id), P, T))");

			rules.add("(start(activity(IdP), P, T) :- !, not(executing(activity(IdP), P, _, _)), assertz(executing(activity(IdP), P, T, [])))");

			rules.add("(start(process(IdP), P, T) :- !, not(executing(process(IdP), P, _)), assertz(executing(process(IdP), P, T)), type(IdP, IdC), start(initial(IdC), [process(IdP)|P], T))");

			rules.add("(start(decision(IdD), P, T) :- !, not(executing(decision(IdD), P, _, _)), assertz(executing(decision(IdD), P, T, [])))");

			rules.add("(start(synchronism(IdS), P, T) :- not(executing(synchronism(IdS), P, _)), !, assertz(executing(synchronism(IdS), P, T)), start(synchronism(IdS), P, T))");

			rules.add("(start(synchronism(IdS), P, T) :- !, executing(synchronism(IdS), P, Ti), findall(E1,(transition(E1, synchronism(IdS)), E1 \\= initial(_), E1 \\= decision(_), E1 \\= product(_), executed(E1, P, _, Tf), Tf >= Ti, Tf =< T), E1s), findall(E2,(transition(E2, synchronism(IdS)), E2 = decision(IdD), selected(IdD, P, R, Tr, _), option(IdD, R, synchronism(IdS)), Tr >= Ti, Tr =< T), E2s), append(E1s, E2s, E3s), findall(E4,(transition(E4, synchronism(IdS)), E4 = product(IdP), available(IdP, P, Tp), Tp >= Ti, Tp =< T), E4s), append(E3s, E4s, E5s), findall(E6,(transition(E6, synchronism(IdS)), E6 \\= initial(_), E6 \\= decision(_), E6 \\= product(_)), E6s), findall(E7,(transition(E7, synchronism(IdS)), E7 = decision(_)), E7s), append(E6s, E7s, E8s), findall(E9,(transition(E9, synchronism(IdS)), E9 = product(_)), E9s), append(E8s, E9s, E10s), E5s = E10s, finish(synchronism(IdS), P, T))");

			rules.add("(start(product(IdP), P, T) :- !, finish(product(IdP), P, T))");
			
			rules.add("(start(final(IdC), P, T) :- !, finish(final(IdC), P, T))");

			rules.add("(finish(initial(IdC), P, T) :- !, findall(E, transition(initial(IdC), E), Es), start(Es, P, T))");

			rules.add("(finish(activity(IdP), P, T) :- !, executing(activity(IdP), P, Ti, Performers), finished(IdP, P, Tf, _), Tf > Ti, Tf =< T, retract(executing(activity(IdP), P, Ti, Performers)), assertz(executed(activity(IdP), P, Ti, Tf, Performers)), findall(E, transition(activity(IdP), E), Es), start(Es, P, Tf))");

			rules.add("(finish(process(IdP), P, T) :- !, executing(process(IdP), P, Ti), retract(executing(process(IdP), P, Ti)), assertz(executed(process(IdP), P, Ti, T)), findall(E, transition(process(IdP), E), Es), start(Es, P, T))");

			rules.add("(finish(decision(IdD), P, T) :- !, executing(decision(IdD), P, Ti, Performers), findall(E, (selected(IdD, P, R, Tr, _), Tr > Ti, Tr =< T, option(IdD, R, E)), Es), Es \\= [], retract(executing(decision(IdD), P, Ti, Performers)), assertz(executed(decision(IdD), P, Ti, T, Performers)), start(Es, P, T))");

			rules.add("(finish(synchronism(IdS), P, T) :- !, retract(executing(synchronism(IdS), P, Ti)), assertz(executed(synchronism(IdS), P, Ti, T)), findall(E7, transition(synchronism(IdS), E7), E7s), start(E7s, P, T))");

			rules.add("(finish(product(IdP), P, T) :- !, assertz(available(IdP, P, T)), findall(E, transition(product(IdP), E), Es), start(Es, P, T))");
			
			rules.add("(finish(final(_), [process(IdP)|P], T) :- !, finish(process(IdP), P, T))");
		}

		return rules;
	}
}