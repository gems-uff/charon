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
		boolean isSolvable = knowledgeBase.isSolvable("assertz(experiment('"+experimentId+"')), assertz(experimentName('"+experimentId+"', '"+name+"')).");
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
		String SWFMSId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("assertz(SWfMS('"+SWFMSId+"')), assertz(SWfMSName('"+SWFMSId+"', '"+name+"')), assertz(SWfMSHost('"+SWFMSId+"', '"+host+"')).");
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
	
	public String defineProcess(KnowledgeBase knowledgeBase, String type, String name){
		connect(knowledgeBase);
		String processClassId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("assertz(process('"+processClassId+"')), assertz(processType('"+processClassId+"', '"+type+"')), assertz(processName('"+processClassId+"', '"+name+"'))," +
				"assertz(initial('"+processClassId+"')), assertz(final('"+processClassId+"')), assertz(synchronism('"+processClassId+"'))," +
				"assertz(transition(synchronism('"+processClassId+"'), final('"+processClassId+"'))).");
		disconnect();
		if(isSolvable){
			return processClassId;
		}
		else
			return null;
	}
	
	public String defineActivity(KnowledgeBase knowledgeBase, String type, String name){
		connect(knowledgeBase);
		String activityClassId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("assertz(activity('"+activityClassId+"')), assertz(activityType('"+activityClassId+"', '"+type+"')), assertz(activityName('"+activityClassId+"', '"+name+"')).");
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
		boolean isSolvable = knowledgeBase.isSolvable("assertz(product('"+productId+"')), assertz(productType('"+productId+"', '"+type+"')).");
		disconnect();
		if(isSolvable){
			return productId;
		}
		else
			return null;
	}
	
	public boolean associateArtifactGeneratedByActivity(KnowledgeBase knowledgeBase, String activityInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition(activity('"+activityInstanceId+"'), product('"+artifactId+"'))), assertz(productName('"+activityInstanceId+"', '"+artifactId+"', '"+artifactName+"')).");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactGeneratedByProcess(KnowledgeBase knowledgeBase, String processInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition(process('"+processInstanceId+"'), product('"+artifactId+"'))), assertz(productName('"+processInstanceId+"', '"+artifactId+"', '"+artifactName+"')).");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactUsedByActivity(KnowledgeBase knowledgeBase, String activityInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition(product('"+artifactId+"'), activity('"+activityInstanceId+"'))), assertz(productName('"+activityInstanceId+"', '"+artifactId+"', '"+artifactName+"')).");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactUsedByProcess(KnowledgeBase knowledgeBase, String processInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition(product('"+artifactId+"'), process('"+processInstanceId+"'))), assertz(productName('"+processInstanceId+"', '"+artifactId+"', '"+artifactName+"')).");
		disconnect();
		return isSolvable;
	}
	
	public String instantiate(KnowledgeBase knowledgeBase, String classId){
		connect(knowledgeBase);
		String instanceId = IDGenerator.generateID();;
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
		String synchronismId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("assertz(synchronism('"+synchronismId+"')).");
		disconnect();
		if(isSolvable)
			return synchronismId;
		else
			return null;
	}
	
	public String createDecision(KnowledgeBase knowledgeBase, String name){
		connect(knowledgeBase);
		String decisionId = IDGenerator.generateID();;
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
			isSolvable = knowledgeBase.isSolvable("assertz(transition(initial('"+processId+"'), "+createElement(elementType, elementId)+")), assertz(transition("+createElement(elementType, elementId)+", synchronism('"+processId+"'))).");
		}
		else if(elementType == 4){
			isSolvable = knowledgeBase.isSolvable("assertz(transition(initial('"+processId+"'), "+createElement(elementType, elementId)+")).");
		}
		
		disconnect();
		return isSolvable;
	}
	
	public boolean defineFlow(KnowledgeBase knowledgeBase, String processId, int originElementType, String originElementId, int destinationElementType, String destinationElementId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(transition("+createElement(originElementType, originElementId)+", "+createElement(destinationElementType, destinationElementId)+")).");
		
		if(isSolvable){
			knowledgeBase.isSolvable("retract(transition("+createElement(originElementType, originElementId)+", synchronism('"+processId+"'))).");
			knowledgeBase.isSolvable("retract(transition(initial('"+processId+"'), "+createElement(destinationElementType, destinationElementId)+")).");
		}
		disconnect();
		return isSolvable;
	}
	
	public String createParameter(KnowledgeBase knowledgeBase, String name, String type, String value){
		connect(knowledgeBase);
		String parameterId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("assertz(parameter('"+parameterId+"')), assertz(parameterName('"+parameterId+"', '"+name+"'))," +
				"assertz(parameterType('"+parameterId+"', '"+type+"')), assertz(parameterValue('"+parameterId+"', '"+value+"')).");
		disconnect();
		if(isSolvable){
			return parameterId;
		}
		else
			return null;	
	}
	
	public boolean associateParameterToActivity(KnowledgeBase knowledgeBase, String parameterId, String activityInstanceId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(activityParameter('"+activityInstanceId+"', '"+parameterId+"')).");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateParameterToProcess(KnowledgeBase knowledgeBase, String parameterId, String processInstanceId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(processParameter('"+processInstanceId+"', '"+parameterId+"')).");
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