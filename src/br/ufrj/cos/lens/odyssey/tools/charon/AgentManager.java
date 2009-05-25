package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.HashMap;
import java.util.Map;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.BacktrackingAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.EnactmentAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.MappingAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.SimulationAgent;

/**
 * This class is responsible for providing the available agents
 * @author murta
 */
public class AgentManager {
	
	/**
	 * Registered agents
	 */
	private Map<Class,Agent> agents = null;
	
	/**
	 * Singleton instance
	 */
	private static AgentManager instance = null;

	/**
	 * Constructs the singleton instance
	 */
	private AgentManager() {
		agents = new HashMap<Class,Agent>();
	}

	/**
	 * Provides the singleton instance
	 */
	public synchronized static AgentManager getInstance() {
		if (instance == null)
			instance = new AgentManager();
		return instance;
	}
	
	/**
	 * Loads all agents
	 * This code should be called before Charon starts working. It is responsible for
	 * allowing agents to register as listeners of other agents.
	 */
	public void init() throws CharonException {
		getAgent(MappingAgent.class);
		getAgent(SimulationAgent.class);
		getAgent(BacktrackingAgent.class);
		getAgent(EnactmentAgent.class);
	}
	
	/**
	 * Provides an agent of a given type
	 */
	public <T extends Agent> T getAgent(Class<T> type) throws CharonException {
		try {
			if (!agents.containsKey(type)) {
				agents.put(type, type.newInstance());
			}
			return type.cast(agents.get(type));
		} catch (Exception e) {
			throw new CharonException("Could not load agent " + type.getName(), e);
		}
	}
}
