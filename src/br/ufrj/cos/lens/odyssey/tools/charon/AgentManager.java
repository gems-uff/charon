package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.HashMap;
import java.util.Map;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent;

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
