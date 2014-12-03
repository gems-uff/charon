package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.LoadingAgent;


/**
 * This class is responsible for providing access to the Charon process machine
 * @author Anderson Marinho
 * @version 1.0, 15/08/2009
 */
public class CharonAPI {
	
	public final static int ACTIVITY = 1;
	public final static int PROCESS = 2;
	public final static int SYNCHRONISM = 3;
	public final static int DECISION = 4;
	
	
	/**
	 * Knowledge base instance
	 */
	private KnowledgeBase knowledgeBase;
	
	/**
	 * Creates a Charon API instance
	 * 
	 */
	public CharonAPI(KnowledgeBase knowledgeBase) throws CharonException {
		this.knowledgeBase = knowledgeBase;
	}

	
	
	
	public boolean insertVariant(String variantId, String variationPointId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.insertVariant(knowledgeBase, variantId, variationPointId);
	}
	
	public boolean insertMandatory(String mandatoryId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.insertMandatory(knowledgeBase, mandatoryId);
	}
	
	public boolean insertOptional(String optionalId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.insertOptional(knowledgeBase, optionalId);
	}

	public boolean insertVariationPoint(String variationPointId, boolean mandatory) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.insertVariationPoint(knowledgeBase, variationPointId, mandatory);
	}
	
	
	
	
	public boolean initializeDerivation() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.initializeDerivation(knowledgeBase);
	}
	
	public boolean isValidDerivedWorkflow() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.isValidDerivedWorkflow(knowledgeBase);
	}
	
	public boolean selectElement(String elementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.selectElement(knowledgeBase, elementId);
	}
	
	public boolean unselectElement(String elementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.unselectElement(knowledgeBase, elementId);
	}
	
	public boolean isElementSelected(String elementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.isElementSelected(knowledgeBase, elementId);
	}
	
	public Set<String> getInferredSelection() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.getInferredSelection(knowledgeBase);
	}
	
	public Set<String> getInferredDeselection() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.getInferredDeselection(knowledgeBase);
	}
	
	
	public List<Map<String, Object>> listValidConfigurations(String query, String elementId, boolean selected) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.listValidConfigurations(knowledgeBase, query, elementId, selected);
	}
	
	public boolean isValidPreliminaryDerivedWorkflow(String query, String elementId, boolean selected) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.isValidPreliminaryDerivedWorkflow(knowledgeBase, query, elementId, selected);
	} 
	
	public boolean insertRules(String rules) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.insertRules(knowledgeBase, rules);
	}
	
	public void beginSelection() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		loadingAgent.beginSelection(knowledgeBase);
	}
	
	public void commitSelection() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		loadingAgent.commitSelection(knowledgeBase);
	}
	
	public void rollbackSelection() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		loadingAgent.rollbackSelection(knowledgeBase);
	}

	
}