package br.ufrj.cos.lens.odyssey.tools.charon;

import java.util.HashMap;
import java.util.Map;

import spem.SpemPackage;

/**
 * This class is responsible to manage knowledge bases
 * @author murta
 */
public class KnowledgeBaseManager {

	/**
	 * All knowledge bases indexed by their context
	 */
	private Map<Object,KnowledgeBase> knowledgeBases = null;
	
	/**
	 * Singleton instance
	 */
	private static KnowledgeBaseManager instance = null;
	
	/**
	 * Constructs the singleton instance
	 */
	private KnowledgeBaseManager() {
		knowledgeBases = new HashMap<Object,KnowledgeBase>();
	}
	
	/**
	 * Provides the singleton instance
	 */
	public synchronized static KnowledgeBaseManager getInstance() {
		if (instance == null)
			instance = new KnowledgeBaseManager();
		return instance;
	}
	
	/**
	 * Loads the stored knowledge bases
	 */
	public void init() {
		// TODO: CODE!
	}
	
	/**
	 * Verifies if there is a knowledge base associated with a given context
	 */
	public boolean hasKnowledgeBase(Object context) {
		return knowledgeBases.containsKey(context);
	}
	
	/**
	 * Provides the knowledge base of a given context
	 */
	public KnowledgeBase getKnowledgeBase(Object context) throws CharonException {
		KnowledgeBase base = knowledgeBases.get(context);
		if (base != null)
			return base;
		else
			throw new CharonException("Could not find a process for context " + context);
	}
	
	/**
	 * Updates the knowledge base of a given context
	 */
	public void updateKnowledgeBase(Object context, SpemPackage spemPackage) throws CharonException {
		knowledgeBases.get(context).update(spemPackage);
	}
	
	/**
	 * Adds a knowledge base to a given context
	 */
	public void addKnowledgeBase(Object context, SpemPackage spemPackage) throws CharonException {
		knowledgeBases.put(context, new KnowledgeBase(spemPackage));
	}
}
