package br.ufrj.cos.lens.odyssey.tools.charon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
 * This class represents a knowledge base of a process.
 *
 * @author Leonardo Murta
 * @version 1.0, 22/11/2001
 */
public class KnowledgeBase {
	
	/**
	 * Inference machine that phisically holds the knowledge base
	 */
	private InferenceMachine inferenceMachine = null;

	/**
	 * Agent connected to the knowledge base
	 */
	private Agent agent = null;
	
	/**
	 * Repository where the knowledge base should be stored
	 */
	private File repository;

	/**
	 * Constructs a new knowledge base loading existing facts from "repository"
	 * 
	 * @param repository File that store existing prolog facts.
	 */
	public KnowledgeBase(File repository) throws CharonException {
		this.repository = repository;

		if (repository.exists()) {
			try {
				inferenceMachine = new InferenceMachine(repository);
			} catch (Exception e) {
				throw new CharonException("Could not load existing knowledge base from " + repository, e);
			}
		} else {
			inferenceMachine = new InferenceMachine();
		}
	}

	/**
	 * Add a process into the knowledge base
	 *
	 * @param spemPackage Package containing all elements of the process.
	 */
	public void add(SpemPackage spemPackage) throws CharonException {
		Collection<String> processClauses = AgentManager.getInstance().getAgent(MappingAgent.class).map(spemPackage);
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
	public void save() throws CharonException {
		try {
			FileWriter fileWriter = new FileWriter(repository);
			fileWriter.write(inferenceMachine.getContent());
			fileWriter.close();
		} catch (IOException e) {
			throw new CharonException("Could not save the knowledge base", e);
		}
	}
}