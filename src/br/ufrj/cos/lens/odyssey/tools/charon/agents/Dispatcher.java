package br.ufrj.cos.lens.odyssey.tools.charon.agents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for dispatching proactive execution of
 * agents in separate threads
 *
 * @author Leo Murta
 * @version 1.0, 11/12/2001
 */
public class Dispatcher implements ActionListener {

	/**
	 * Target agent
	 */
	private Agent targetAgent = null;

	/**
	 * Constructs the dispatcher
	 */
	public Dispatcher(Agent targetAgent) {
		this.targetAgent = targetAgent;
	}

	/**
	 * Dispatch the target agent execution
	 */
	public void actionPerformed(ActionEvent event) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				targetAgent.proactiveExecution();
			}
		});
		thread.start();
	}
}