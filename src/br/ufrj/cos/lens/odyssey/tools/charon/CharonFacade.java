package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.ArrayList;
import java.util.Collection;

import javax.jmi.reflect.RefBaseObject;

import processstructure.ProcessPerformer;
import processstructure.WorkDefinition;
import spem.SpemPackage;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.BacktrackingAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.EnactmentAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.FollowingThroughAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonActivity;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonDecision;

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
	private CharonFacade() throws CharonException {
		// Inits the internal managers
		AgentManager.getInstance().init();
		KnowledgeBaseManager.getInstance().init();
	}

	/**
	 * Provides the singleton instance
	 */
	public synchronized static CharonFacade getInstance() throws CharonException {
		if (instance == null)
			instance = new CharonFacade();
		return instance;
	}
	
	/**
	 * Adds a process into a given context.
	 * 
	 * @param context Context where the process will be added. This context can be used to
	 * refer to the process in the future.
	 * @param spemPackage Package containing all elements of the process.
	 * @param rootProcess The root process.
	 */
	public void addProcess(Object context, SpemPackage spemPackage) throws CharonException {
		// TODO: Simulates the process (throw exception in case of failure)
		// AgentManager.getInstance().getAgent(SimulationAgent.class).simulate(spemPackage);
		
		if (KnowledgeBaseManager.getInstance().hasKnowledgeBase(context)) {
			KnowledgeBaseManager.getInstance().updateKnowledgeBase(context, spemPackage);
		} else {
			KnowledgeBaseManager.getInstance().addKnowledgeBase(context, spemPackage);
		}
	}
	
	/**
	 * Instantiate a process in a given context.
	 *
	 * @param context Context where the process will be instantiated.
	 * @param process The process to be instantiated.
	 * @return The ID of the instantiated instance. This ID is not a MOFID!
	 */
	public String instantiateProcess(Object context, WorkDefinition workDefinition) throws CharonException {
		KnowledgeBase knowledgeBase = KnowledgeBaseManager.getInstance().getKnowledgeBase(context);
		
		if (knowledgeBase != null) {
			return AgentManager.getInstance().getAgent(EnactmentAgent.class).instantiate(knowledgeBase, workDefinition.refMofId());
		} else {
			throw new CharonException("No process has been defined in the context " + context);
		}
	}
	
	/**
	 * Provides the pending activities in a given context for a given collection of roles 
	 */
	public Collection<CharonActivity> getPendingActivities(Object context, Collection<ProcessPerformer> processPerformers) throws CharonException {
		KnowledgeBase knowledgeBase = KnowledgeBaseManager.getInstance().getKnowledgeBase(context);
		FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
		return followingThroughAgent.getPendingActivities(knowledgeBase, getIds(processPerformers));
	}
	
	/**
	 * Provides the pending decisions in a given context for a given collection of roles 
	 */
	public Collection<CharonDecision> getPendingDecisions(Object context, Collection<ProcessPerformer> processPerformers) throws CharonException {
		KnowledgeBase knowledgeBase = KnowledgeBaseManager.getInstance().getKnowledgeBase(context);
		FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
		return followingThroughAgent.getPendingDecisions(knowledgeBase, getIds(processPerformers));
	}
	
	/**
	 * Finishes some pending activities in a given context on behalf of a given user
	 */
	public void finishActivities(Object context, String user, Collection<CharonActivity> activities) throws CharonException {
		KnowledgeBase knowledgeBase = KnowledgeBaseManager.getInstance().getKnowledgeBase(context);
		FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
		followingThroughAgent.finishActivities(user, knowledgeBase, activities);
	}	
	
	/**
	 * Make some pending decisions in a given context on behalf of a given user
	 */
	public void makeDecisions(Object context, String user, Collection<CharonDecision> decisions) throws CharonException {
		KnowledgeBase knowledgeBase = KnowledgeBaseManager.getInstance().getKnowledgeBase(context);
		FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
		followingThroughAgent.makeDecisions(user, knowledgeBase, decisions);
	}	

	/**
	 * Backtracks the process of a given context
	 */
	public void backtrack(Object context) throws CharonException {
		// TODO: Review this code and the Agent code.
		try {
			KnowledgeBase knowledgeBase = KnowledgeBaseManager.getInstance().getKnowledgeBase(context);
			BacktrackingAgent backtrackingAgent = AgentManager.getInstance().getAgent(BacktrackingAgent.class);
			backtrackingAgent.reactiveExecution(knowledgeBase);
		} catch (Exception e) {
			throw new CharonException("Could not backtrack process of context " + context, e);
		}
	}
	
	/**
	 * Saves the context of a knowledge base of a given context into a given file
	 * (useful for debug)
	 */
	public void save(Object context, String fileName) throws CharonException {
		try {
			KnowledgeBase knowledgeBase = KnowledgeBaseManager.getInstance().getKnowledgeBase(context);
			knowledgeBase.save(fileName);
		} catch (Exception e) {
			throw new CharonException("Could not backtrack process of context " + context, e);
		}
	}
	
	/**
	 * Provides a collection of MOFIDs from a Collection of RefObjects
	 */
	private Collection<String> getIds(Collection<? extends RefBaseObject> refBaseObjects) {
		Collection<String> result = new ArrayList<String>();
		
		for (RefBaseObject refBaseObject : refBaseObjects) {
			result.add(refBaseObject.refMofId());
		}
		
		return result;
	}
}