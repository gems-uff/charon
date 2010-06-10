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
		boolean isSolvable = knowledgeBase.isSolvable("create_experiment('"+experimentId+"', '"+name+"').");
		disconnect();
		if(isSolvable){
			return experimentId;
		}
		else
			return null;
	}
	
	public String createExperimentNewVersion(KnowledgeBase knowledgeBase, String experimentId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("create_experimentNewVersion('"+experimentId+"').");
		disconnect();
		if(isSolvable){
			return experimentId;
		}
		else
			return null;
	}
	
	public boolean setExperimentName(KnowledgeBase knowledgeBase, String experimentId, String name){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("set_experimentName('"+experimentId+"', '"+name+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean setExperimentRootProcess(KnowledgeBase knowledgeBase, String experimentId, String processInstanceId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("set_experimentRootProcess('"+experimentId+"', '"+processInstanceId+"').");
		disconnect();
		return isSolvable;
	}

	public String registerSGWf(KnowledgeBase knowledgeBase, String name, String host){
		connect(knowledgeBase);
		String SWFMSId = IDGenerator.generateID();;
		boolean isSolvable = knowledgeBase.isSolvable("create_SWFMS('"+SWFMSId+"', '"+name+"', '"+host+"').");
		disconnect();
		if(isSolvable){
			return SWFMSId;
		}
		else
			return null;
	}
	
	
	public boolean associateProcessToSWFMS(KnowledgeBase knowledgeBase, String processInstanceId, String SWFMSId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("set_SWFMSProcess('"+SWFMSId+"', '"+processInstanceId+"').");
		disconnect();
		return isSolvable;
	}
	
	public String createProcess(KnowledgeBase knowledgeBase, String type, String name){
		connect(knowledgeBase);
		String processClassId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("create_process('"+processClassId+"', '"+name+"', '"+type+"')," +
				"create_initial('"+processClassId+"'), create_final('"+processClassId+"'), create_invisible_synchronism('"+processClassId+"')," +
				"create_flow('"+CharonUtil.SYNCHRONISM+"', '"+processClassId+"', '"+CharonUtil.FINAL+"', '"+processClassId+"').");
		disconnect();
		if(isSolvable){
			return processClassId;
		}
		else
			return null;
	}
	
	
	public String createActivity(KnowledgeBase knowledgeBase, String type, String name){
		connect(knowledgeBase);
		String activityClassId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("create_activity('"+activityClassId+"', '"+name+"', '"+type+"').");
		disconnect();
		if(isSolvable){
			return activityClassId;
		}
		else
			return null;
	}
	
	
	public String createPort(KnowledgeBase knowledgeBase, String portType, String portName, String portDataType){
		connect(knowledgeBase);
		String portId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("create_port('"+portId+"', '"+portType+"', '"+portName+"', '"+portDataType+"').");
		disconnect();
		if(isSolvable){
			return portId;
		}
		else
			return null;
	}
	
	
	public boolean addActivityPort(KnowledgeBase knowledgeBase, String activityId, String portId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("add_activityPort('"+activityId+"', '"+portId+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean addProcessPort(KnowledgeBase knowledgeBase, String processId, String portId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("add_processPort('"+processId+"', '"+portId+"').");
		disconnect();
		return isSolvable;
	}
		
	public String createArtifact(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		String artifactId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("create_artifact('"+artifactId+"').");
		disconnect();
		if(isSolvable){
			return artifactId;
		}
		else
			return null;
	}
	
	public boolean associateArtifactToActivityPort(KnowledgeBase knowledgeBase, String activityInstanceId, String portId, String artifactId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("set_artifactActivityPort('"+activityInstanceId+"', '"+portId+"', '"+artifactId+"').");
		disconnect();
		return isSolvable;
	}
	
	public boolean associateArtifactToProcessPort(KnowledgeBase knowledgeBase, String processInstanceId, String portId, String artifactId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("set_artifactProcessPort('"+processInstanceId+"', '"+portId+"', '"+artifactId+"').");
		disconnect();
		return isSolvable;
	}
	
	public String instantiateActivity(KnowledgeBase knowledgeBase, String activityClassId){
		connect(knowledgeBase);
		String activityInstanceId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("create_activityInstance('"+activityInstanceId+"', '"+activityClassId+"').");
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
		boolean isSolvable = knowledgeBase.isSolvable("create_processInstance('"+processInstanceId+"', '"+processClassId+"').");
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
		boolean isSolvable = knowledgeBase.isSolvable("create_synchronism('"+synchronismId+"').");
		disconnect();
		if(isSolvable)
			return synchronismId;
		else
			return null;
	}
	
	public boolean createInvisibleSynchronism(KnowledgeBase knowledgeBase, String synchronismId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("create_invisible_synchronism('"+synchronismId+"').");
		disconnect();
		return isSolvable;
	}
	
	public String createDecision(KnowledgeBase knowledgeBase, String name){
		connect(knowledgeBase);
		String decisionId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("create_decision('"+decisionId+"', '"+name+"').");
		disconnect();
		if(isSolvable)
			return decisionId;
		else
			return null;
	}
	
	public String createDecisionOption(KnowledgeBase knowledgeBase, String decisionId, String name, int toElementType, String toElementId){
		connect(knowledgeBase);
		
		boolean isSolvable = knowledgeBase.isSolvable("add_decisionOption('"+decisionId+"', '"+name+"', '"+toElementType+"', '"+toElementId+"').");
		
		disconnect();		
		if(isSolvable)
			return decisionId;
		else
			return null;
	}	
	
	public boolean associateElementToProcessWorkflow(KnowledgeBase knowledgeBase, String processId, int elementType, String elementId){
		connect(knowledgeBase);
		
		boolean isSolvable = false;
		
		String type = elementType+"";
		
		if(type.equals(CharonUtil.PROCESS) || type.equals(CharonUtil.ACTIVITY) || type.equals(CharonUtil.SYNCHRONISM)){
			isSolvable = knowledgeBase.isSolvable("create_flow('"+CharonUtil.INITIAL+"', '"+processId+"', '"+elementType+"', '" + elementId+"'), create_flow('"+elementType+"', '" + elementId+ "', '"+CharonUtil.SYNCHRONISM+"', '" +processId+"').");
		}
		else if(type.equals(CharonUtil.DECISION)){
			isSolvable = knowledgeBase.isSolvable("create_flow('"+CharonUtil.INITIAL+"', '"+processId+"', '"+elementType+"', '" + elementId+"').");
		}
		
		disconnect();
		return isSolvable;
	}
	
	public boolean associateElementToExperimentRootProcessWorkflow(KnowledgeBase knowledgeBase, String experimentId, int elementType, String elementId){
		connect(knowledgeBase);
		
		boolean isSolvable = false;
		
		String type = elementType+"";
		
		if(type.equals(CharonUtil.PROCESS) || type.equals(CharonUtil.ACTIVITY) || type.equals(CharonUtil.SYNCHRONISM)){
			isSolvable = knowledgeBase.isSolvable("create_experiment_flow('"+CharonUtil.INITIAL+"', experiment('"+experimentId+"'), '"+elementType+"', '" + elementId+"'), create_experiment_flow('"+elementType+"', '" + elementId+ "', '"+CharonUtil.SYNCHRONISM+"', experiment('"+experimentId+"')).");
		}
		else if(type.equals(CharonUtil.DECISION)){
			isSolvable = knowledgeBase.isSolvable("create_experiment_flow('"+CharonUtil.INITIAL+"', experiment('"+experimentId+"'), '"+elementType+"', '" + elementId+"').");
		}
		
		disconnect();
		return isSolvable;
	}
	
	public boolean removeElementToExperimentRootProcessWorkflow(KnowledgeBase knowledgeBase, String experimentId, int elementType, String elementId){
		connect(knowledgeBase);
		
		boolean isSolvable = false;
		
		String type = elementType+"";
		
		if(type.equals(CharonUtil.PROCESS) || type.equals(CharonUtil.ACTIVITY) || type.equals(CharonUtil.SYNCHRONISM)){
			isSolvable = knowledgeBase.isSolvable("delete_experiment_flow('"+CharonUtil.INITIAL+"', experiment('"+experimentId+"'), '"+elementType+"', '" + elementId+"'), delete_experiment_flow('"+elementType+"', '" + elementId+ "', '"+CharonUtil.SYNCHRONISM+"', experiment('"+experimentId+"')).");
		}
		else if(type.equals(CharonUtil.DECISION)){
			isSolvable = knowledgeBase.isSolvable("delete_experiment_flow('"+CharonUtil.INITIAL+"', experiment('"+experimentId+"'), '"+elementType+"', '" + elementId+"').");
		}
		
		disconnect();
		return isSolvable;
	}
	
	public boolean defineFlow(KnowledgeBase knowledgeBase, String processId, int originElementType, String originElementId, int destinationElementType, String destinationElementId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("create_flow('"+originElementType+"', '" + originElementId+"', '"+destinationElementType+"', '"+destinationElementId+"').");
		
		/*
		 * TODO: Algoritmo de deletar fluxo não funciona bem no caso quando se cria um processo sem adicionar nenhum elemento
		 */
				
		
		if(isSolvable){
			knowledgeBase.isSolvable("delete_flow('"+originElementType+"', '" + originElementId+"', '"+CharonUtil.SYNCHRONISM+"', '" +processId+"').");
			knowledgeBase.isSolvable("delete_flow('"+CharonUtil.INITIAL+"', '"+processId+"', '"+destinationElementType+"', '"+destinationElementId+"').");
		}
		disconnect();
		return isSolvable;
	}
	
	public boolean defineExperimentRootProcessFlow(KnowledgeBase knowledgeBase, String experimentId, int originElementType, String originElementId, int destinationElementType, String destinationElementId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("create_flow('"+originElementType+"', '" + originElementId+"', '"+destinationElementType+"', '"+destinationElementId+"').");
		
		if(isSolvable){
			knowledgeBase.isSolvable("delete_experiment_flow('"+originElementType+"', '" + originElementId+"', '"+CharonUtil.SYNCHRONISM+"', experiment('"+experimentId+"')).");
			knowledgeBase.isSolvable("delete_experiment_flow('"+CharonUtil.INITIAL+"', experiment('"+experimentId+"'), '"+destinationElementType+"', '"+destinationElementId+"').");
		}
		disconnect();
		return isSolvable;
	}
	
	public boolean removeExperimentRootProcessFlow(KnowledgeBase knowledgeBase, String experimentId, int originElementType, String originElementId, int destinationElementType, String destinationElementId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("delete_flow('"+originElementType+"', '"+originElementId+"', '"+destinationElementType+"', '"+destinationElementId+"').");
		
		
		//TODO: verificar quando o elemento não está conectado em ninguém "a frente ou atras" no workflow
		if(false){
			knowledgeBase.isSolvable("create_experiment_flow('"+originElementType+"', '" + originElementId+"', '"+CharonUtil.SYNCHRONISM+"', experiment('"+experimentId+"')).");
			knowledgeBase.isSolvable("create_experiment_flow('"+CharonUtil.INITIAL+"', experiment('"+experimentId+"'), '"+destinationElementType+"', '"+destinationElementId+"').");
		}
		disconnect();
		return isSolvable;
	}	
	
}