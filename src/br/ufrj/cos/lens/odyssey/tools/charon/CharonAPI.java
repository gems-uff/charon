package br.ufrj.cos.lens.odyssey.tools.charon;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.LoadingAgent;


/**
 * This class is responsible for providing access to the Charon process machine
 * @author Leonardo Murta
 * @version 1.0, 08/08/2001
 */
public class CharonAPI {
	
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
	
	public String createProcess() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createProcess(knowledgeBase);
	}
	
	public String createActivity() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createActivity(knowledgeBase);
	}
		
	public String createInitialState() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createInitialState(knowledgeBase);
	}
	
	public String createFinalState() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createFinalState(knowledgeBase);
	}
	
	public String createSynchronism() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createSynchronism(knowledgeBase);
	}
	
	public String createDecision(String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createDecision(knowledgeBase, name);
	}
	
	//TODO: consertar...
//	public String createOption(String name) throws CharonException{
//		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
//		return loadingAgent.createOption(knowledgeBase, name);
//	}
	
}