package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.HashMap;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;
import br.ufrj.cos.lens.odyssey.tools.charon.util.IDGenerator;

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
		String experimentId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_experiment('"+experimentId+"', '"+name+"').");
		disconnect();
		if(isSolvable){
			return experimentId;
		}
		else
			return null;
	}
	
	public boolean setExperimentRootProcess(KnowledgeBase knowledgeBase, String experimentId, String processClassId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_experimentRootProcess('"+experimentId+"', '"+processClassId+"').");
		disconnect();
		return isSolvable;
	}

	public String registerSGWf(KnowledgeBase knowledgeBase, String name, String host){
		connect(knowledgeBase);
		String SWFMSId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("assertz_SWFMS('"+SWFMSId+"', '"+name+"', '"+host+"').");
		disconnect();
		if(isSolvable){
			return SWFMSId;
		}
		else
			return null;
	}
	
	public boolean associateProcessToSWFMS(KnowledgeBase knowledgeBase, String processInstanceId, String SWFMSId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_SWFMSProcess('"+SWFMSId+"', '"+processInstanceId+"').");
		disconnect();
		return isSolvable;
	}
	
	public String defineProcess(KnowledgeBase knowledgeBase, String type, String name){
		connect(knowledgeBase);
		String processClassId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("assertz_process('"+processClassId+"', '"+name+"', '"+type+"')," +
				"assertz_initial('"+processClassId+"'), assertz_final('"+processClassId+"'), assertz_synchronism('"+processClassId+"')," +
				"assertz_transition('3', '"+processClassId+"', '6', '"+processClassId+"').");
		disconnect();
		if(isSolvable){
			return processClassId;
		}
		else
			return null;
	}
	
	public String defineActivity(KnowledgeBase knowledgeBase, String type, String name){
		connect(knowledgeBase);
		String activityClassId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_activity('"+activityClassId+"', '"+type+"', '"+name+"').");
		disconnect();
		if(isSolvable){
			return activityClassId;
		}
		else
			return null;
	}
	
	public String defineArtifact(KnowledgeBase knowledgeBase, String type){
		connect(knowledgeBase);
		String productId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("assertz_product('"+productId+"', '"+type+"').");
		disconnect();
		if(isSolvable){
			return productId;
		}
		else
			return null;
	}
	
	public boolean associateArtifactGeneratedByActivity(KnowledgeBase knowledgeBase, String activityInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_transition('2', '"+activityInstanceId+"', '6', '"+artifactId+"'), assertz_activity_productName('"+activityInstanceId+"', '"+artifactId+"', '"+artifactName+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactGeneratedByProcess(KnowledgeBase knowledgeBase, String processInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_transition('2', '"+processInstanceId+"', '6', '"+artifactId+"'), assertz_process_productName('"+processInstanceId+"', '"+artifactId+"', '"+artifactName+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactUsedByActivity(KnowledgeBase knowledgeBase, String activityInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_transition('6', '"+artifactId+"', '1', '"+activityInstanceId+"'), assertz_activity_productName('"+activityInstanceId+"', '"+artifactId+"', '"+artifactName+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactUsedByProcess(KnowledgeBase knowledgeBase, String processInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_transition('6', '"+artifactId+"', '2', '"+processInstanceId+"'), assertz_process_productName('"+processInstanceId+"', '"+artifactId+"', '"+artifactName+"').");
		disconnect();
		return isSolvable;
	}
	
	public String instantiateActivity(KnowledgeBase knowledgeBase, String activityClassId){
		connect(knowledgeBase);
		String activityInstanceId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_activityInstance('"+activityInstanceId+"', '"+activityClassId+"').");
		disconnect();
		if(isSolvable){
			classIdsByInstance.put(activityInstanceId, activityClassId);
			return activityInstanceId;
		}
		else
			return null;
	}
	
	public String instantiateProcess(KnowledgeBase knowledgeBase, String processClassId){
		connect(knowledgeBase);
		String processInstanceId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_processInstance('"+processInstanceId+"', '"+processClassId+"').");
		disconnect();
		if(isSolvable){
			classIdsByInstance.put(processInstanceId, processClassId);
			return processInstanceId;
		}
		else
			return null;
	}
	
	public String createSynchronism(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		String synchronismId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_synchronism('"+synchronismId+"').");
		disconnect();
		if(isSolvable)
			return synchronismId;
		else
			return null;
	}
	
	public String createDecision(KnowledgeBase knowledgeBase, String name){
		connect(knowledgeBase);
		String decisionId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_decision('"+decisionId+"', '"+name+"').");
		disconnect();
		if(isSolvable)
			return decisionId;
		else
			return null;
	}
	
	public String createOption(KnowledgeBase knowledgeBase, String decisionId, String name, int toElementType, String toElementId){
		connect(knowledgeBase);
		
		boolean isSolvable = knowledgeBase.isSolvable("assertz_option('"+decisionId+"', '"+name+"', '"+toElementType+"', '"+toElementId+"').");
		
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
			//isSolvable = knowledgeBase.isSolvable("assertz(transition(initial('"+processId+"'), "+createElement(elementType, elementId)+")), assertz(transition("+createElement(elementType, elementId)+", synchronism('"+processId+"'))).");
			isSolvable = knowledgeBase.isSolvable("assertz_transition('5', '"+processId+"', '"+elementType+"', '" + elementId+"'), assertz(transition('"+elementType+"', '" + elementId+ "', '3', '" +processId+"').");
		}
		else if(elementType == 4){
			isSolvable = knowledgeBase.isSolvable("assertz_transition('5', '"+processId+"', '"+elementType+"', '" + elementId+"').");
		}
		
		disconnect();
		return isSolvable;
	}
	
	public boolean defineFlow(KnowledgeBase knowledgeBase, String processId, int originElementType, String originElementId, int destinationElementType, String destinationElementId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_transition('"+originElementType+"', '" + originElementId+"', '"+destinationElementType+"', '"+destinationElementId+"').");
		
		if(isSolvable){
			knowledgeBase.isSolvable("retract_transition('"+originElementType+"', '" + originElementId+"', '3', '" +processId+"').");
			knowledgeBase.isSolvable("retract_transition('5', '"+processId+"', '"+destinationElementType+"', '"+destinationElementId+"').");
		}
		disconnect();
		return isSolvable;
	}
	
	public String createParameter(KnowledgeBase knowledgeBase, String name, String type, String value){
		connect(knowledgeBase);
		String parameterId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("assertz_parameter('"+parameterId+"', '"+name+"', '"+type+"', '"+value+"').");
		disconnect();
		if(isSolvable){
			return parameterId;
		}
		else
			return null;	
	}
	
	public boolean associateParameterToActivity(KnowledgeBase knowledgeBase, String parameterId, String activityInstanceId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_activityParameter('"+activityInstanceId+"', '"+parameterId+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateParameterToProcess(KnowledgeBase knowledgeBase, String parameterId, String processInstanceId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_processParameter('"+processInstanceId+"', '"+parameterId+"').");
		disconnect();
		return isSolvable;	
	}
	
}