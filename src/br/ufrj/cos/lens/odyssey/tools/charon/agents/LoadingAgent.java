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

	public List<Map<String, Object>> listImplications(KnowledgeBase knowledgeBase, String elementId, boolean selected){
		connect(knowledgeBase);
		
		knowledgeBase.getAllSolutions("retract(currentSelection(_,_,_)).");
		knowledgeBase.getAllSolutions("retract(currentDesselection(_,_,_)).");
		
		knowledgeBase.getAllSolutions("selectElement([['"+elementId+"', "+selected+", 'R0'], []]).");
		
		knowledgeBase.getAllSolutions("processResults(_).");
		
		List<Map<String, Object>> options = knowledgeBase.getAllSolutions("option(A).");
		
		disconnect();
		
		return options;
	}
	
}