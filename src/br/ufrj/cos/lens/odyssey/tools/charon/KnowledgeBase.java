package br.ufrj.cos.lens.odyssey.tools.charon;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import spem.SpemPackage;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.MappingAgent;
import br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine;

/**
 * Classe respossável por representar a base de conhecimento de um processo.
 *
 * @author Leonardo Murta
 * @version 1.0, 22/11/2001
 */
public class KnowledgeBase implements Serializable {

	/**
	 * Inference machine that phisically holds the knowledge base
	 */
	private transient InferenceMachine inferenceMachine = null;

	/**
	 * Agent connected to the knowledge base
	 */
	private transient Agent agent = null;
	
	/**
	 * Clauses used to define the process
	 */
	private Collection<String> processClauses = null;

	/**
	 * Constructs a new knowledge base
	 *
	 * @param spemPackage Package containing all elements of the process.
	 */
	public KnowledgeBase(SpemPackage spemPackage) throws CharonException {
		inferenceMachine = new InferenceMachine();
		processClauses = AgentManager.getInstance().getAgent(MappingAgent.class).map(spemPackage);
		inferenceMachine.addClauses(processClauses);
	}
	
	/** 
	 * Updates the knowledge base to reflect changes made into the process contained in
	 * the SPEM Package used to create this knowledge base.
	 * IMPORTANT: The current implementation uses MOFID to identify processes. It will be
	 * impossible to evolve the process if the new SpemPackage use different MOFIDs.
	 * 
	 * @param spemPackage Package containing all elements of the process.
	 */
	public synchronized void update(SpemPackage spemPackage) throws CharonException {
		inferenceMachine.removeClauses(processClauses);
		processClauses = AgentManager.getInstance().getAgent(MappingAgent.class).map(spemPackage);
		inferenceMachine.addClauses(processClauses);
	}

	/**
	 * Connects an agent to this knowledge base
	 */
	public synchronized void connect(Agent agent) {
		while (this.agent != null)
			try {
				// Waits the other agent to finish its work
				wait();
			} catch (InterruptedException e) {
				Logger.global.log(Level.WARNING, "Could not synchronize the connections of agents to the knowledge base", e);
				return;
			}

		this.agent = agent;
		
		// Adds the agent's rules into the knowledge base
		inferenceMachine.addClauses(agent.getRules());
	}

	/**
	 * Disconnect the current connected agent from this knowledge base
	 */
	public synchronized void disconnect() {
		if (agent != null) {
			// Removes the agent's rules from the knowledge base
			inferenceMachine.removeClauses(agent.getRules());
			agent = null;
		}

		// Lets other agents work
		notify();
	}

	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#getAllSolutions(java.lang.String)
	 */
	public List<Map<String, Object>> getAllSolutions(String goal) {
		return inferenceMachine.getAllSolutions(goal);
	}

	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#isSolvable(java.lang.String)
	 */
	public boolean isSolvable(String goal) {
		return inferenceMachine.isSolvable(goal);
	}

	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#addClauses(java.util.Collection)
	 */
	public void addClauses(Collection<String> clauses) {
		inferenceMachine.addClauses(clauses);
	}

	/**
	 * Saves the content of the knowledge base
	 * (useful for debug)
	 */
	public void save(String fileName) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);
			fileWriter.write(inferenceMachine.getContent());
			fileWriter.close();
		} catch (IOException e) {
			Logger.global.log(Level.WARNING, "Could not save the knowledge base", e);
		}
	}
}