package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public boolean insertVariant(KnowledgeBase knowledgeBase, String variantId, String variationPointId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(variant('"+variantId+"', '"+variationPointId+"')).");
		disconnect();
		return isSolvable;
	}
	
	public boolean insertMandatory(KnowledgeBase knowledgeBase, String mandatorytId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(mandatory('"+mandatorytId+"')).");
		disconnect();
		return isSolvable;
	}
	
	public boolean insertOptional(KnowledgeBase knowledgeBase, String optionalId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("assertz(optional('"+optionalId+"')).");
		disconnect();
		return isSolvable;
	}
	
	public boolean insertVariationPoint(KnowledgeBase knowledgeBase, String variationPointId, boolean mandatory){
		connect(knowledgeBase);
		boolean isSolvable;
		if(mandatory)
			isSolvable = knowledgeBase.isSolvable("assertz(variationPoint('"+variationPointId+"')), assertz(mandatory('"+variationPointId+"')).");
		else
			isSolvable = knowledgeBase.isSolvable("assertz(variationPoint('"+variationPointId+"')).");
		
		disconnect();
		return isSolvable;
	}
	
	
	
	public boolean initializeDerivation(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("initializeDerivation(_).");
		
		disconnect();
		return isSolvable;
	}
	
	public boolean isValidDerivedWorkfloww(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("isValidDerivedWorkflow(_).");
		
		disconnect();
		return isSolvable;
	}
	
	
	
	public boolean isElementSelected(KnowledgeBase knowledgeBase, String elementId){
		connect(knowledgeBase);
		
		boolean isSolvable = knowledgeBase.isSolvable("abstractWorkflow('"+elementId+"').");
		
		disconnect();
		return isSolvable;
	}
	
	public boolean selectElement(KnowledgeBase knowledgeBase, String elementId){
		connect(knowledgeBase);
		knowledgeBase.getAllSolutions("selectElement('"+elementId+"').");
		
		boolean isSolvable = knowledgeBase.isSolvable("conflict(_).");
		
		disconnect();
		return !isSolvable;
	}
	
	public boolean unselectElement(KnowledgeBase knowledgeBase, String elementId){
		connect(knowledgeBase);
		knowledgeBase.getAllSolutions("unselectElement('"+elementId+"').");
		
		boolean isSolvable = knowledgeBase.isSolvable("conflict(_).");
		
		disconnect();
		return !isSolvable;
	}
	
	public Set<String> getInferredSelection(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		List<Map<String, Object>> list = knowledgeBase.getAllSolutions("currentSelection(_).");
		
		disconnect();
		return null;
	}
	
	public Set<String> getInferredDeselection(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		List<Map<String, Object>> list = knowledgeBase.getAllSolutions("currentDeselection(_).");
		
		disconnect();
		return null;
	}
	
	
	public void beginSelection(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		knowledgeBase.isSolvable("initSelection(_).");
		
		disconnect();
	}
	
	public void commitSelection(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		knowledgeBase.isSolvable("commitSelection(_).");
		
		disconnect();
	}
	
	public void rollbackSelection(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
		knowledgeBase.isSolvable("rollbackSelection(_).");
		
		disconnect();
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
	
	public boolean setExperimentRootProcess(KnowledgeBase knowledgeBase, String experimentId, String processId){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable("set_experimentRootProcess('"+experimentId+"', '"+processId+"').");
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
	
	public boolean isValidDerivedWorkflow(KnowledgeBase knowledgeBase){
		connect(knowledgeBase);
//		boolean isSolvable = knowledgeBase.isSolvable("isValidDerivedWorkflow(_).");
//		boolean isSolvable = knowledgeBase.isSolvable("findall(ActivityId, abstractWorkflow(ActivityId), L1), findall(ActivityId2, currentSelection(ActivityId2), L2), append(L1, L2, ['B2'|L]).");
//		boolean isSolvable = knowledgeBase.isSolvable("findall(ActivityId2, currentSelection(ActivityId2), []).");
//		boolean isSolvable = knowledgeBase.isSolvable("variant('B1', VP), findall(Var, variant(Var, VP), ['B1', 'B2']).");
//		boolean isSolvable = knowledgeBase.isSolvable("assertz(currentDesselection('B2')), selectVariantt('B1').");
//		boolean isSolvable = knowledgeBase.isSolvable("assertz(currentSelection('B1')), assertz(currentSelection('D1')), assertz(currentDesselection('B')).");
//		knowledgeBase.getAllSolutions("selectElement('B1').");
//		boolean isSolvable = knowledgeBase.isSolvable("currentSelection('D1').");
		
//		knowledgeBase.getAllSolutions("selectElement('B1').");
		
//				boolean isSolvable = knowledgeBase.isSolvable("selectElement('B1').");
//				boolean isSolvable = knowledgeBase.isSolvable("currentSelection('E1').");
//		        isSolvable = knowledgeBase.isSolvable("commit(_).");
//		        isSolvable = knowledgeBase.isSolvable("abstractWorkflow('D1').");
//				isSolvable = knowledgeBase.isSolvable("currentSelection('B1').");
//				List<Map<String, Object>> solutions = knowledgeBase.getAllSolutions("validationRule(RuleId).");
//		        knowledgeBase.getAllSolutions("selectElement('B1', []).");
//		        
//		        List<Map<String, Object>> solutions = knowledgeBase.getAllSolutions("currentSelection(A, B).");
//		        
//				boolean isSolvable = knowledgeBase.isSolvable("append(['A'], 'B', ['A', 'B']).");
				
				
//				List<Map<String, Object>> solutions = knowledgeBase.getAllSolutions("rulee(A, B, C).");
				
//				boolean isSolvable = knowledgeBase.isSolvable("rulee('A', 'B', 'C').");
				
//				boolean isSolvable = knowledgeBase.isSolvable("tableBody(A,B,and(A,B)).");
				
				
				boolean isSolvable = knowledgeBase.isSolvable("bool(A).");
				
		
				
		
//		        boolean isSolvable = knowledgeBase.isSolvable("currentSelection('F1', ['E1', 'B1']).");
//		        boolean isSolvable = knowledgeBase.isSolvable("currentSelection('D1', ['B1']).");
//		        boolean isSolvable = knowledgeBase.isSolvable("currentSelection('E1', ['B1']).");
		
//		        knowledgeBase.getAllSolutions("rule('X').");
//		        boolean isSolvable = knowledgeBase.isSolvable("selected('D1').");
		        
//		        boolean isSolvable = knowledgeBase.isSolvable("isThereAnyOtherVariantSelected('E', ['E1', 'E2'], ['E1', 'E2', 'A', 'E']).");
//		        boolean isSolvable = knowledgeBase.isSolvable("isVariantSelected('E', ['E1'], ['E1', 'E2', 'A', 'E']).");
//		        boolean isSolvable = knowledgeBase.isSolvable("checkVariationPoints(['E'], ['E1', 'E2', 'A', 'E']).");
		        
//		        boolean isSolvable = knowledgeBase.isSolvable("findall(RuleId, validationRule(RuleId), []).");
//		boolean isSolvable = knowledgeBase.isSolvable("append(['B1'], [], ['B1']).");
//		boolean isSolvable = knowledgeBase.isSolvable("mandatory('A').");
//		boolean isSolvable = knowledgeBase.isSolvable("initializeDerivation(_).");
		disconnect();
		return isSolvable;
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
	
	public String instantiateActivity(KnowledgeBase knowledgeBase, String activityClassId, String name){
		connect(knowledgeBase);
		String activityInstanceId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("create_activityInstance('"+activityInstanceId+"', '"+activityClassId+"', '"+name+"').");
		disconnect();
		if(isSolvable){
			classIdsByInstance.put(activityInstanceId, activityClassId);
			return activityInstanceId;
		}
		else
			return null;
	}
	
	public String instantiateProcess(KnowledgeBase knowledgeBase, String processClassId, String name){
		connect(knowledgeBase);
		String processInstanceId = IDGenerator.generateID();
		boolean isSolvable = knowledgeBase.isSolvable("create_processInstance('"+processInstanceId+"', '"+processClassId+"', '"+name+"').");
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
		 * TODO: Algoritmo de deletar fluxo n�o funciona bem no caso quando se cria um processo sem adicionar nenhum elemento
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
		
		
		//TODO: verificar quando o elemento n�o est� conectado em ningu�m "a frente ou atras" no workflow
		if(false){
			knowledgeBase.isSolvable("create_experiment_flow('"+originElementType+"', '" + originElementId+"', '"+CharonUtil.SYNCHRONISM+"', experiment('"+experimentId+"')).");
			knowledgeBase.isSolvable("create_experiment_flow('"+CharonUtil.INITIAL+"', experiment('"+experimentId+"'), '"+destinationElementType+"', '"+destinationElementId+"').");
		}
		disconnect();
		return isSolvable;
	}	
	
}