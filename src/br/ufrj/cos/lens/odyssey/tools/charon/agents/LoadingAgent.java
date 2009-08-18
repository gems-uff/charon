package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.HashMap;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * This class is responsible for loading single workflow information into knowledge base.
 * 
 * @author Anderson Marinho
 * @version 1.0, 09/07/2009
 */
public class LoadingAgent extends Agent {
	
	private HashMap<String, String> classIdsByInstance;
	
	/**
	 * Singleton constructor
	 */
	public LoadingAgent() {
		super();
		classIdsByInstance = new HashMap<String, String>();
	}

	
	public String createExperiment(KnowledgeBase knowledgeBase, String name){
		connect(knowledgeBase);
		String experimentId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(experiment('"+experimentId+"'), experimentName('"+experimentId+"', '"+name+"')).");
		disconnect();
		if(isSolvable){
			return experimentId;
		}
		else
			return null;
	}
	
	public boolean setExperimentRootProcess(KnowledgeBase knowledgeBase, String experimentId, String processClassId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(experimentRootProcess('"+experimentId+"', '"+processClassId+"')).");
		disconnect();
		return isSolvable;
	}

	public String registerSGWf(KnowledgeBase knowledgeBase, String name, String host){
		connect(knowledgeBase);
		String SWFMSId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(SWfMS('"+SWFMSId+"'), SWfMSName('"+SWFMSId+"', '"+name+"'), SWfMSHost('"+SWFMSId+"', '"+host+"')).");
		disconnect();
		if(isSolvable){
			return SWFMSId;
		}
		else
			return null;
	}
	
	public boolean associateProcessToSWFMS(KnowledgeBase knowledgeBase, String processInstanceId, String SWFMSId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(SWFMSProcesses('"+SWFMSId+"', '"+processInstanceId+"')).");
		disconnect();
		return isSolvable;
	}
	
	public String defineProcess(KnowledgeBase knowledgeBase, String name){
		connect(knowledgeBase);
		String processClassId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(process('"+processClassId+"'), processName('"+processClassId+"', '"+name+"')," +
				"initial('"+processClassId+"'), final('"+processClassId+"'), synchronism('"+processClassId+"')," +
				"transition(synchronism('"+processClassId+"'), final('"+processClassId+"'))).");
		disconnect();
		if(isSolvable){
			return processClassId;
		}
		else
			return null;
	}
	
	public String defineActivity(KnowledgeBase knowledgeBase, String name){
		connect(knowledgeBase);
		String activityClassId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(activity('"+activityClassId+"'), activityName('"+activityClassId+"', '"+name+"')).");
		disconnect();
		if(isSolvable){
			return activityClassId;
		}
		else
			return null;
	}
	
	public String defineProduct(KnowledgeBase knowledgeBase, String name, String type){
		connect(knowledgeBase);
		String productId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(product('"+productId+"'), productName('"+productId+"', '"+name+"'), productType('"+productId+"', '"+type+"')).");
		disconnect();
		if(isSolvable){
			return productId;
		}
		else
			return null;
	}
	
	public boolean associateProductUsedByActivity(KnowledgeBase knowledgeBase, String activityInstanceId, String productId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition(activity('"+activityInstanceId+"'), product('"+productId+"'))).");
		return isSolvable;
	}
	
	public boolean associateProductUsedByProcess(KnowledgeBase knowledgeBase, String processInstanceId, String productId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition(process('"+processInstanceId+"'), product('"+productId+"'))).");
		return isSolvable;
	}
	
	public boolean associateProductGeneratedByActivity(KnowledgeBase knowledgeBase, String activityInstanceId, String productId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition(product('"+productId+"'), activity('"+activityInstanceId+"'))).");
		return isSolvable;
	}
	
	public boolean associateProductGeneratedByProcess(KnowledgeBase knowledgeBase, String processInstanceId, String productId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition(product('"+productId+"'), process('"+processInstanceId+"'))).");
		return isSolvable;
	}
	
	public String instantiate(KnowledgeBase knowledgeBase, String classId){
		connect(knowledgeBase);
		String instanceId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(type('"+instanceId+"', '"+classId+"')).");
		disconnect();
		if(isSolvable){
			classIdsByInstance.put(instanceId, classId);
			return instanceId;
		}
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
	
	public boolean associateElementToProcessWorkflow(KnowledgeBase knowledgeBase, String processId, int elementType, String elementId){
		connect(knowledgeBase);
		
		boolean isSolvable = false;
		
		if(elementType == 1 || elementType == 2 || elementType == 3){
			isSolvable = knowledgeBase.isSolvable("assertz(transition(initial('"+processId+"'), "+createElement(elementType, elementId)+"), transition("+createElement(elementType, elementId)+"), synchronism('"+processId+"')).");
		}
		else if(elementType == 4){
			isSolvable = knowledgeBase.isSolvable("assertz(transition(initial('"+processId+"'), "+createElement(elementType, elementId)+").");
		}
		
		disconnect();
		return isSolvable;
	}
	
	public boolean defineFlow(KnowledgeBase knowledgeBase, int originElementType, String originElementId, int destinationElementType, String destinationElementId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition("+createElement(originElementType, originElementId)+", "+createElement(destinationElementType, destinationElementId)+")).");
		
		if(isSolvable){
			knowledgeBase.isSolvable("retract(transition("+createElement(originElementType, originElementId)+", synchronism("+")).");
			knowledgeBase.isSolvable("retract(transition(initial(), "+createElement(destinationElementType, destinationElementId)+")).");
		}
		disconnect();
		return isSolvable;
	}
	
	public String createParameter(KnowledgeBase knowledgeBase, String name, String type, String value){
		connect(knowledgeBase);
		String parameterId = String.valueOf(System.currentTimeMillis());
		boolean isSolvable = knowledgeBase.isSolvable("assertz(parameter('"+parameterId+"'), parameterName('"+parameterId+"', '"+name+"')," +
				"parameterType('"+parameterId+"', '"+type+"'), parameterValue('"+parameterId+"', '"+value+"')).");
		disconnect();
		if(isSolvable){
			return parameterId;
		}
		else
			return null;	
	}
	
	public boolean associateParameterToActivity(KnowledgeBase knowledgeBase, String parameterId, String activityInstanceId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(activityParameter('"+activityInstanceId+"', '"+parameterId+")).");
		disconnect();
		return isSolvable;	
	}
	
	public boolean associateParameterToProcess(KnowledgeBase knowledgeBase, String parameterId, String processInstanceId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(processParameter('"+processInstanceId+"', '"+parameterId+")).");
		disconnect();
		return isSolvable;	
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