package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.swing.Timer;

import br.ufrj.cos.lens.odyssey.tools.charon.KnowledgeBase;

/**
 * This class represents a generic agent
 * @author Leo Murta
 * @version 1.0, 11/12/2001
 */
public abstract class Agent {
	/**
	 * Agents that should be notified when this agent finishes working
	 * (reactive property)
	 */
	private Collection<Agent> disconnectionListeners = new HashSet<Agent>();

	/**
	 * Timer for autonomous execution
	 * (proactive property)
	 */
	private Timer timer = null;

	/**
	 * Knowledge base where this agent is connected
	 */
	private KnowledgeBase knowledgeBase = null;

	/**
	 * Constructs a proactive (and reactive) agent
	 *
	 * @param delay Delay between proactive executions (milisecods)
	 */
	public Agent(int delay) {
		timer = new Timer(delay, new Dispatcher(this));
		timer.start();
	}
	
	/**
	 * Constructs a reactive agent
	 */
	public Agent() { }

	/**
	 * Adds a new listener of executions of this agent
	 */
	public void addDisconnectionListener(Agent agente) {
		disconnectionListeners.add(agente);
	}

	/**
	 * Adds a listener of executions of this agent
	 */
	public void removeDisconnectionListener(Agent agente) {
		disconnectionListeners.remove(agente);
	}

	/**
	 * Fires the execution of the listeners of this agent
	 */
	private void fireDisconnectionListeners() {
		for (Agent agent : disconnectionListeners) {
			agent.disconnectionPerformed(knowledgeBase);
		}
	}
	
	/**
	 * Connects the agent to a knowledge base
	 */
	protected void connect(KnowledgeBase knowledgeBase) {
		if (timer != null) {
			timer.stop();
		}			
		knowledgeBase.connect(this);
		this.knowledgeBase = knowledgeBase;
	}

	/**
	 * Disconnect the agent from the current knowledge base
	 */
	protected void disconnect() {
		if (knowledgeBase != null) {
			knowledgeBase.disconnect();
			fireDisconnectionListeners();
			this.knowledgeBase = null;
		}
		if (timer != null) {
			timer.start();
		}
	}
	
/*
 * 		******************************************************
 * 		The following methods should be overrided if necessary
 * 		******************************************************
 */

	/**
	 * Provides the agent's rules.
	 * OVERRIDE IT when the agent have specific prolog rules. These rules will be
	 * automatically inserted and removed into the knowledge base when the agent
	 * connects and disconnects to/from it.
	 */
	public Collection<String> getRules() {
		return Collections.emptyList();
	}

	/**
	 * Runs the agent in a reactive mode, as a response to an external event
	 * that occurs automatically when it is a listener of other agent.
	 * OVERRIDE IT when this agent listens other agents.
	 * @param knowledgeBase Knowledge base where the agent shoud run
	 */
	public void disconnectionPerformed(KnowledgeBase knowledgeBase) {
		Logger.global.warning("Agent " + getClass().getName() + " should implement disconnectionPerformed(KnowledgeBase) to handle disconnection listening events.");
	}

	/**
	 * Runs the agent in a proactive mode, as a response of the timer.
	 * OVERRIDE IT when this agent is a proactive agent
	 */
	public void proactiveExecution() {
		Logger.global.warning("Agent " + getClass().getName() + " should implement proactiveExecution() to act proactivelly.");
	}
}
