package br.ufrj.cos.lens.odyssey.tools.charon;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jmi.reflect.RefBaseObject;

import processstructure.ProcessPerformer;
import processstructure.WorkDefinition;
import spem.SpemPackage;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.BacktrackingAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.EnactmentAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.FollowingThroughAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.SimulationAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonActivity;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonDecision;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonElement;
import br.ufrj.cos.lens.odyssey.tools.charon.util.CharonUtil;

/**
 * This class is responsible for providing access to the Charon process machine
 * @author Leonardo Murta
 * @version 1.0, 08/08/2001
 */
public class Charon {

	/**
	 * File where charon knowledge base should be stored
	 */
	private static final String FILE_NAME = "charon.pl";
	
	/**
	 * Knowledge base instance
	 */
	private KnowledgeBase knowledgeBase;
	
	/**
	 * Charon's API
	 */
	private CharonAPI charonAPI;
	
	/**
	 * Creates a Charon instance
	 * 
	 * @param repository Place where Charon will recover/store its knowledge base
	 */
	public Charon(String repositoryDirectory) throws CharonException {
		File directory = new File(repositoryDirectory);
		if (!directory.exists() && !directory.mkdirs()) {
			throw new CharonException("Could not create directory " + repositoryDirectory);
		}
		
		File file = new File(directory, FILE_NAME);
		knowledgeBase = new KnowledgeBase(file);
		AgentManager.getInstance().init();
		
		charonAPI = new CharonAPI(knowledgeBase);
	}
	
	public Charon(String database, String user, String password) throws CharonException {
		
		knowledgeBase = new KnowledgeBase(database, user, password);
		AgentManager.getInstance().init();
		
		charonAPI = new CharonAPI(knowledgeBase);
	}
	
	/**
	 * Add a new process defined in "spemPackage" inside charon.
	 * 
	 * @param spemPackage Package containing all elements of the process.
	 * @param dirName Directory that will store the knowledge base constaining the process.
	 */
	public void addProcess(SpemPackage spemPackage) throws CharonException {
		// Simulates the process (throws an exception in case of failure)
		AgentManager.getInstance().getAgent(SimulationAgent.class).simulate(spemPackage);
		
		knowledgeBase.add(spemPackage);
	}
	
	/**
	 * Instantiate a process in a given context.
	 *
	 * @param context Context where the process will be instantiated.
	 * @param process The process to be instantiated.
	 * @return The ID of the instantiated process. This ID is not a MOFID!
	 */
	public String instantiateProcess(WorkDefinition workDefinition) throws CharonException {
		return AgentManager.getInstance().getAgent(EnactmentAgent.class).instantiate(knowledgeBase, workDefinition.refMofId());
	}
	
	/**
	 * Provides the pending activities in a given context for a given collection of roles 
	 */
	public Collection<CharonActivity> getPendingActivities(Collection<ProcessPerformer> processPerformers) throws CharonException {
		FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
		return followingThroughAgent.getPendingActivities(knowledgeBase, getIds(processPerformers));
	}
	
	/**
	 * Provides the pending decisions in a given context for a given collection of roles 
	 */
	public Collection<CharonDecision> getPendingDecisions(Collection<ProcessPerformer> processPerformers) throws CharonException {
		FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
		return followingThroughAgent.getPendingDecisions(knowledgeBase, getIds(processPerformers));
	}
	
	/**
	 * Defines the people that are performing a collection of activities or decisions
	 */
	public void setPerformers(List<String> performers, Collection<? extends CharonElement> elements) throws CharonException {
		FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
		followingThroughAgent.setPerformers(performers, knowledgeBase, elements);		
	}
	
	/**
	 * Finishes some pending activities in a given context on behalf of a given user
	 */
	public void finishActivities(String user, Collection<CharonActivity> activities) throws CharonException {
		FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
		followingThroughAgent.finishActivities(user, knowledgeBase, activities);
	}	
	
	/**
	 * Make some pending decisions in a given context on behalf of a given user
	 */
	public void makeDecisions(String user, Collection<CharonDecision> decisions) throws CharonException {
		FollowingThroughAgent followingThroughAgent = AgentManager.getInstance().getAgent(FollowingThroughAgent.class);
		followingThroughAgent.makeDecisions(user, knowledgeBase, decisions);
	}	

	/**
	 * Backtracks the process of a given context
	 */
	public void backtrack(int seconds) throws CharonException {
		BacktrackingAgent backtrackingAgent = AgentManager.getInstance().getAgent(BacktrackingAgent.class);
		backtrackingAgent.backtrack(knowledgeBase, seconds);
	}
	
	/**
	 * Saves the context of a knowledge base of a given context into a given file
	 * (useful for debug)
	 */
	public void save() throws CharonException {
		knowledgeBase.save();
	}
	
	/**
	 * Provides a collection of MOFIDs from a Collection of RefObjects
	 */
	private Collection<String> getIds(Collection<? extends RefBaseObject> refBaseObjects) {
		Collection<String> result = new ArrayList<String>();
		
		for (RefBaseObject refBaseObject : refBaseObjects) {
			result.add("'" + refBaseObject.refMofId() + "'");
		}
		
		return result;
	}
	
	public CharonAPI getCharonAPI(){
		return charonAPI;
	}
	
	public List<Map<String,Object>> query(String goal){
		return knowledgeBase.getAllSolutions(goal);
	}
	
	public static void main(String[] args) throws Exception{
//		Charon charon = new Charon("jdbc:mysql://localhost:3306/charon","charon","edPibTR1");
		
		
		Charon charon = new Charon("resource");
		CharonAPI charonAPI = new CharonAPI(charon.knowledgeBase);
		
		System.out.println(charonAPI.isValidDerivatedWorkflow());
		
//		int ACTIVITY = Integer.parseInt(CharonUtil.ACTIVITY);
//		
//		boolean ok = false;
//		
//		String experimentId = charonAPI.createExperiment("NACAD");
//		String swfmsId = charonAPI.registerSGWf("Vistrails", "192.168.0.1");
//		String processId = charonAPI.createProcess("local", "P1");
//		String activityId1 = charonAPI.createActivity("local", "A1");
//		String activityId2 = charonAPI.createActivity("local", "A2");
//		String activityId3 = charonAPI.createActivity("local", "A3");
//		
//		String portId = charonAPI.createInPort("portA", "int");
//		String artifactId = charonAPI.createArtifact();
//		
//		charonAPI.addActivityPort(activityId1, portId);
		
		
//		String experimentId = "1";
//		String processRootId = "2";
//		String process1Id = "4";
//		String process2Id = "9";
//		String activityId1 = "7";
//		String activityId2 = "12";
//		String activityInstanceId1 = "8";
//		String activityInstanceId2 = "13";
//		String processRootInstanceId = "3";
//		String process1InstanceId = "5";
//		String process2InstanceId = "10";
		
//		String activityInstanceId1 = charonAPI.instantiateActivity(activityId1);
//		
//		charonAPI.associateArtifactToActivityPort(activityInstanceId1, portId, artifactId);
//		
//		String activityInstanceId2 = charonAPI.instantiateActivity(activityId2);
//		String activityInstanceId3 = charonAPI.instantiateActivity(activityId3);
//		ok = charonAPI.associateElementToProcessWorkflow(processId, ACTIVITY, activityInstanceId1);
//		ok = charonAPI.associateElementToProcessWorkflow(processId, ACTIVITY, activityInstanceId2);
//		ok = charonAPI.associateElementToProcessWorkflow(processId, ACTIVITY, activityInstanceId3);
//		ok = charonAPI.defineFlow(processId, ACTIVITY, activityInstanceId1, ACTIVITY, activityInstanceId2);
//		ok = charonAPI.defineFlow(processId, ACTIVITY, activityInstanceId1, ACTIVITY, activityInstanceId3);
//		String processInstanceId = charonAPI.instantiateProcess(processId);
//		ok = charonAPI.associateProcessToSWFMS(processInstanceId, swfmsId);
//		ok = charonAPI.setExperimentRootProcess(experimentId, processInstanceId);
		
//		String experimentInstanceId = charonAPI.initializeExperimentExecution(experimentId);
//		ok = charonAPI.notifyActivityExecutionEnding(activityInstanceId1, new String[]{process1InstanceId, experimentInstanceId});
//		ok = charonAPI.notifyActivityExecutionEnding(activityInstanceId2, new String[]{process2InstanceId, experimentInstanceId});
//
//		charonAPI.createExperimentNewVersion(experimentId);
		
		
//		
//		
//		
//		
		//charon.save();
	}
}