package br.ufrj.cos.lens.odyssey.tools.charon.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


/**
 * This class represents a generic Charon element.
 *
 * @author Leo Murta
 * @version 1.0, 22/12/2001
 */
public class CharonElement implements Serializable{
	/**
	 * Charon element ID
	 */
	private String id;

	/**
	 * Charon element context. The context of a Charon element is the list of process that were
	 * executed recursivelly to reach this element. This element is "part of", recursivelly,
	 * of all processes in the context.
	 */
	private String context = null;
	
	/**
	 * Collection of people that are executing the element
	 */
	private List<String> performers;

	/**
	 * Constructs the Charon element
	 */
	public CharonElement(String id, String context, List<String> performers) {
		// Removes the ' from the id
		this.id = id.substring(1, id.length() - 1);
		this.context = context;
		this.performers = performers;
	}

	/**
	 * Provides the id of the Charon element
	 */
	public String getId() {
		return id;
	}

	/**
	 * Provides the context of the Charon element
	 */
	public String getContext() {
		return context;
	}
	
	/**
	 * Provides the collection of people that are executing the activity
	 */
	public List<String> getPerformers() {
		return Collections.unmodifiableList(performers);
	}
	
	/**
	 * Provides the id of the root process instance. This returned ID is the same ID returned when a process is instantiated.
	 */
	public String getProcessInstanceId() {
		String result = id;

		// Extracts the id from the context (rightmost process defined in the context)
		int fromIndex = context.lastIndexOf("process");
		if (fromIndex != -1) {
			int beginIndex = context.indexOf("('", fromIndex) + 2;
			int endIndex = context.indexOf("')", fromIndex);
			
			if ((beginIndex != -1) && (endIndex != -1)) {
				result = context.substring(beginIndex, endIndex);
			}
		}
		
		return result;
	}
}