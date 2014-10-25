package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrj.cos.lens.odyssey.tools.charon.AgentManager;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonException;
import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;
import br.ufrj.cos.lens.odyssey.tools.charon.util.IDGenerator;

/**
 * This class is responsible for enabling administrative operations in the knowledge base  
 *
 * @author Leo Murta
 * @version 1.0, 14/12/2001
 */
public class AdminAgent extends Agent {
	/**
	 * Agent's rules
	 */
	private Collection<String> rules = null;

	/**
	 * Constructs the agent as a listener of the following through agent
	 */
	public AdminAgent() throws CharonException {
		super();
		AgentManager.getInstance().getAgent(FollowingThroughAgent.class).addDisconnectionListener(this);
	}

	/**
	 * Cleans the content of the knowledge base.
	 * 
	 * @throws CharonException 
	 */
	public void cleanKnowledgeBase(KnowledgeBase knowledgeBase) throws CharonException {
		connect(knowledgeBase);
		knowledgeBase.clean();
		disconnect();
	}
	
}