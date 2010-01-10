package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.HashMap;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;
import br.ufrj.cos.lens.odyssey.tools.charon.util.CharonUtil;
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
				"assertz_flow('"+CharonUtil.SYNCHRONISM+"', '"+processClassId+"', '"+CharonUtil.FINAL+"', '"+processClassId+"').");
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
	
	public String addActivityPort(KnowledgeBase knowledgeBase, String activityId, String portId){
		connect(knowledgeBase);
		String artifactId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_artifact('"+artifactId+"').");
		disconnect();
		if(isSolvable){
			return artifactId;
		}
		else
			return null;
	}
	
	public String definePort(KnowledgeBase knowledgeBase, String activityId, String portType, String portName, String portDataType){
		connect(knowledgeBase);
		String artifactId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_artifact('"+artifactId+"').");
		disconnect();
		if(isSolvable){
			return artifactId;
		}
		else
			return null;
	}
	
	public String deletePort(KnowledgeBase knowledgeBase, String portTId){
		connect(knowledgeBase);
		String artifactId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_artifact('"+artifactId+"').");
		disconnect();
		if(isSolvable){
			return artifactId;
		}
		else
			return null;
	}

	
	public String defineArtifact(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		String artifactId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("assertz_artifact('"+artifactId+"').");
		disconnect();
		if(isSolvable){
			return artifactId;
		}
		else
			return null;
	}
	
	public boolean associateArtifactToActivityOutPortGeneratedByActivity(KnowledgeBase knowledgeBase, String activityInstanceId, String artifactId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_flow('"+CharonUtil.ACTIVITY+"', '"+activityInstanceId+"', '"+CharonUtil.ARTIFACT+"', '"+artifactId+"'), assertz_activity_artifactName('"+activityInstanceId+"', '"+artifactId+"', '"+artifactName+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactGeneratedByProcess(KnowledgeBase knowledgeBase, String processInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_flow('"+CharonUtil.PROCESS+"', '"+processInstanceId+"', '"+CharonUtil.ARTIFACT+"', '"+artifactId+"'), assertz_process_artifactName('"+processInstanceId+"', '"+artifactId+"', '"+artifactName+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactUsedByActivity(KnowledgeBase knowledgeBase, String activityInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_flow('"+CharonUtil.ARTIFACT+"', '"+artifactId+"', '"+CharonUtil.ACTIVITY+"', '"+activityInstanceId+"'), assertz_activity_artifactName('"+activityInstanceId+"', '"+artifactId+"', '"+artifactName+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactUsedByProcess(KnowledgeBase knowledgeBase, String processInstanceId, String artifactId, String artifactName){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_flow('"+CharonUtil.ARTIFACT+"', '"+artifactId+"', '"+CharonUtil.PROCESS+"', '"+processInstanceId+"'), assertz_process_artifactName('"+processInstanceId+"', '"+artifactId+"', '"+artifactName+"').");
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
			//isSolvable = knowledgeBase.isSolvable("assertz(flow(initial('"+processId+"'), "+createElement(elementType, elementId)+")), assertz(flow("+createElement(elementType, elementId)+", synchronism('"+processId+"'))).");
			isSolvable = knowledgeBase.isSolvable("assertz_flow('"+CharonUtil.INITIAL+"', '"+processId+"', '"+elementType+"', '" + elementId+"'), assertz_flow('"+elementType+"', '" + elementId+ "', '"+CharonUtil.SYNCHRONISM+"', '" +processId+"').");
		}
		else if(elementType == 4){
			isSolvable = knowledgeBase.isSolvable("assertz_flow('"+CharonUtil.INITIAL+"', '"+processId+"', '"+elementType+"', '" + elementId+"').");
		}
		
		disconnect();
		return isSolvable;
	}
	
	public boolean defineFlow(KnowledgeBase knowledgeBase, String processId, int originElementType, String originElementId, int destinationElementType, String destinationElementId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz_flow('"+originElementType+"', '" + originElementId+"', '"+destinationElementType+"', '"+destinationElementId+"').");
		
		if(isSolvable){
			knowledgeBase.isSolvable("retract_flow('"+originElementType+"', '" + originElementId+"', '"+CharonUtil.SYNCHRONISM+"', '" +processId+"').");
			knowledgeBase.isSolvable("retract_flow('"+CharonUtil.INITIAL+"', '"+processId+"', '"+destinationElementType+"', '"+destinationElementId+"').");
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