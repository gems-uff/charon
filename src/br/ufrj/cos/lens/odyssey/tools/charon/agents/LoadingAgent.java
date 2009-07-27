package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.HashMap;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * This class is responsible of loading single information about the workflow into knowledge base.
 * 
 * @author Anderson Marinho
 * @version 1.0, 09/07/2009
 */
public class LoadingAgent extends Agent {

	
	/**
	 * Agent's rules
	 */
	private HashMap<String, String> typesById;
	
	private HashMap<String, String> classIdsByInstance;
	
	/**
	 * Singleton constructor
	 */
	public LoadingAgent() {
		super();
		typesById = new HashMap<String, String>();
		classIdsByInstance = new HashMap<String, String>();
	}
	
	public String createProcess(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		String processClassId = String.valueOf(System.currentTimeMillis());
		String processId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(process('"+processClassId+"'), type('"+processId+"', '"+processClassId+"'), initial('"+processClassId+"'), final('"+processClassId+"')).");
		disconnect();
		if(isSolvable){
			classIdsByInstance.put(processId, processClassId);
			return processId;
		}
		else
			return null;
	}
	
	public String createActivity(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		String activityClassId = String.valueOf(System.currentTimeMillis());
		String activityId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(activity('"+activityClassId+"'), type('"+activityId+"', '"+activityClassId+"')).");
		disconnect();
		if(isSolvable){
			classIdsByInstance.put(activityId, activityClassId);
			return activityId;
		}
		else
			return null;
	}
		
	public String createInitialState(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		String initialStateId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(initial('"+initialStateId+"')).");
		if(isSolvable)
			return initialStateId;
		else
			return null;
	}
	
	public String createFinalState(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		String finalStateId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(final('"+finalStateId+"')).");
		disconnect();
		if(isSolvable)
			return finalStateId;
		else
			return null;
	}
	
	public String createSynchronism(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		String synchronismId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(synchronism('"+synchronismId+"')).");
		disconnect();
		if(isSolvable)
			return synchronismId;
		else
			return null;
	}
	
	public String createDecision(KnowledgeBase knowledgeBase, String name){
		connect(knowledgeBase);
		String decisionId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(decision('"+decisionId+"', '"+name+"')).");
		disconnect();
		if(isSolvable)
			return decisionId;
		else
			return null;
	}
	
	public String createOption(KnowledgeBase knowledgeBase, String decisionId, String name, int toElementType, String toElementId){
		connect(knowledgeBase);
		
		boolean isSolvable = knowledgeBase.isSolvable("assertz(option('"+decisionId+"', '"+name+"', "+createElement(toElementType, toElementId)+")).");
		
		disconnect();		
		if(isSolvable)
			return decisionId;
		else
			return null;
	}
	
	public String defineFlow(KnowledgeBase knowledgeBase, int originElementType, String originElementId, int destinationElementType, String destinationElementId){
		connect(knowledgeBase);
		String decisionId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition("+createElement(originElementType, originElementId)+", "+createElement(destinationElementType, destinationElementId)+")).");
		disconnect();
		if(isSolvable)
			return decisionId;
		else
			return null;
	}
	
	public String createElement(int type, String id){
		
		switch (type) {
		case 1:
			return "activity('"+id+"')";
		case 2:
			return "process('"+id+"')";
		case 3:
			return "synchronism('"+id+"')";
		case 4:
			return "decision('"+id+"')";
		default:
			return "";
		}
		
	}
	
}