package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.AdminAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.EnactmentAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.LoadingAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.util.CharonUtil;


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
		return loadingAgent.initializeDerivation(knowledgeBase);
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
	
	public List<Map<String,Object>> listImplications(String elementId, boolean selected) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.listImplications(knowledgeBase, elementId, selected);
	}
	
}