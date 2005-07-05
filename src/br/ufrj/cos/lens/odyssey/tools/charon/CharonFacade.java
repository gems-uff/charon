package br.ufrj.cos.lens.odyssey.tools.charon;

import processstructure.WorkDefinition;
import spem.SpemPackage;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.BacktrackingAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.Dispatcher;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.FollowingThroughAgent;

/**
 * This class is responsible for providing access to the Charon process machine
 * @author Leonardo Murta
 * @version 1.0, 08/08/2001
 */
public class CharonFacade {

	/**
	 * Singleton instance
	 */
	private static CharonFacade instance = null;

	/**
	 * Constructs the singleton instance
	 */
	private CharonFacade() {}

	/**
	 * Provides the singleton instance
	 */
	public synchronized static CharonFacade getInstance() {
		if (instance == null)
			instance = new CharonFacade();
		return instance;
	}
	
	/**
	 * Instantiate a process in a given context.
	 *
	 * @param context Context where the process will be instantiated. This context can be used to
	 * refer to the process in the future.
	 * @param spemPackage Package containing all elements of the process.
	 * @param rootProcess The root process.
	 */
	public void instantiateProcess(Object context, SpemPackage spemPackage, WorkDefinition rootProcess) throws CharonException {
		if (KnowledgeBaseManager.getInstance().hasKnowledgeBase(context)) {
			KnowledgeBaseManager.getInstance().updateKnowledgeBase(context);
		} else {
			KnowledgeBaseManager.getInstance().addKnowledgeBase(context, spemPackage, rootProcess);
		}
	}

	/**
	 * Backtracks the process of a given context
	 */
	public void backtrack(Object context) throws CharonException {
		try {
			KnowledgeBase knowledgeBase = KnowledgeBaseManager.getInstance().getKnowledgeBase(context);
			BacktrackingAgent backtrackingAgent = AgentManager.getInstance().getAgent(BacktrackingAgent.class);
			new Dispatcher(this, backtrackingAgent, knowledgeBase);
		} catch (Exception e) {
			throw new CharonException("Could not backtrack process of context " + context, e);
		}
	}

	/**
	 * Follows through the process of a given context
	 */
	public void followThrough(Object context) throws CharonException {
		try {
			KnowledgeBase knowledgeBase = KnowledgeBaseManager.getInstance().getKnowledgeBase(context);
			FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
			new Dispatcher(this, followingThroughAgent, knowledgeBase);
		} catch (Exception e) {
			throw new CharonException("Could not backtrack process of context " + context, e);
		}
	}
}