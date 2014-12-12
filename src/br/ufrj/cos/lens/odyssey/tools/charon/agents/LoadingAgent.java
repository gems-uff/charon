package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
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
	
	public boolean isValidDerivedWorkflow(KnowledgeBase knowledgeBase){
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
	
	public boolean insertValidationRule(KnowledgeBase knowledgeBase, String rule){
		connect(knowledgeBase);
		boolean isSolvable = knowledgeBase.isSolvable(rule);
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
	
	public boolean isValidDerivedWorkflow2(KnowledgeBase knowledgeBase){
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
				
				
				List<Map<String, Object>> solutions = knowledgeBase.getAllSolutions("tableBody(A,B,C,D,and(imp(A, or(B,C)),imp(C, D))).");
				
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

	public List<Map<String, Object>> listValidConfigurations(
			KnowledgeBase knowledgeBase, String query, String elementId, boolean selected) {
		
		connect(knowledgeBase);
		
		if(selected)
			knowledgeBase.isSolvable("retract(boolean"+elementId+"(false)).");
		else
			knowledgeBase.isSolvable("retract(boolean"+elementId+"(true)).");
		
		List<Map<String, Object>> solutions = knowledgeBase.getAllSolutions(query);
		
//		knowledgeBase.getAllSolutions2(query);
				
		if(selected)
			knowledgeBase.isSolvable("assertz(boolean"+elementId+"(false)).");
		else
			knowledgeBase.isSolvable("assertz(boolean"+elementId+"(true)).");		
		
		disconnect();
		
		return solutions;
	}
	
	public List<Map<String, Object>> listValidConfigurations2(
			KnowledgeBase knowledgeBase, String query, String elementId, boolean selected) {
		
		connect(knowledgeBase);
		
		if(selected)
			knowledgeBase.isSolvable("retract(boolean"+elementId+"(false)).");
		else
			knowledgeBase.isSolvable("retract(boolean"+elementId+"(true)).");
		
//		List<Map<String, Object>> solutions = knowledgeBase.getAllSolutions(query);
		
		knowledgeBase.getAllSolutions2(query);
				
		if(selected)
			knowledgeBase.isSolvable("assertz(boolean"+elementId+"(false)).");
		else
			knowledgeBase.isSolvable("assertz(boolean"+elementId+"(true)).");		
		
		disconnect();
		
		return null;
	}
	
	public boolean isValidPreliminaryDerivedWorkflow(
			KnowledgeBase knowledgeBase, String query, String elementId, boolean selected) {
		
		connect(knowledgeBase);
		
		setCurrentState(knowledgeBase);
		
		if(selected){
			knowledgeBase.isSolvable("assertz(boolean"+elementId+"(true)).");
			knowledgeBase.isSolvable("retract(boolean"+elementId+"(false)).");
		}
		else{
			knowledgeBase.isSolvable("assertz(boolean"+elementId+"(false)).");
			knowledgeBase.isSolvable("retract(boolean"+elementId+"(true)).");
		}
		
		boolean isSolvable = knowledgeBase.isSolvable(query);
		
		cleanState(knowledgeBase);
		
		disconnect();
		
		return isSolvable;
	}
	
	private void setCurrentState(KnowledgeBase knowledgeBase){
		
		List<Map<String, Object>> solutions = knowledgeBase.getAllSolutions("variant(A, _).");
		
		for (Map<String, Object> map : solutions) {
			
			String id = (String) map.get("A");
			id = id.substring(1, id.length()-1);
			
			if(knowledgeBase.isSolvable("abstractWorkflow('"+id+"')."))
				knowledgeBase.isSolvable("retract(boolean"+id+"(false)).");
			else
				knowledgeBase.isSolvable("retract(boolean"+id+"(true)).");
			
		}
		
		solutions = knowledgeBase.getAllSolutions("optional(B).");
		
		for (Map<String, Object> map : solutions) {
			
			String id = (String) map.get("B");
			id = id.substring(1, id.length()-1);
			
			if(knowledgeBase.isSolvable("abstractWorkflow('"+id+"')."))
				knowledgeBase.isSolvable("retract(boolean"+id+"(false)).");
			else
				knowledgeBase.isSolvable("retract(boolean"+id+"(true)).");
			
		}
		
	}
	
	private void cleanState(KnowledgeBase knowledgeBase){
		
		List<Map<String, Object>> solutions1 = knowledgeBase.getAllSolutions("variant(A, _).");
		
		for (Map<String, Object> map : solutions1) {
			
			String id = (String) map.get("A");
			id = id.substring(1, id.length()-1);
			
			knowledgeBase.isSolvable("assertz(boolean"+id+"(false)).");
			knowledgeBase.isSolvable("assertz(boolean"+id+"(true)).");
			
		}
		
		List<Map<String, Object>> solutions2 = knowledgeBase.getAllSolutions("optional(B).");
		
		for (Map<String, Object> map : solutions2) {
			
			String id = (String) map.get("B");
			id = id.substring(1, id.length()-1);
			
			knowledgeBase.isSolvable("assertz(boolean"+id+"(false)).");
			knowledgeBase.isSolvable("assertz(boolean"+id+"(true)).");
			
		}
		
	}
	
	public boolean insertRules(KnowledgeBase knowledgeBase, String rules) {
		
		connect(knowledgeBase);
		
		boolean isSolvable = knowledgeBase.appendTheory(rules);
		
		disconnect();
		
		return isSolvable;
	}	
	
}