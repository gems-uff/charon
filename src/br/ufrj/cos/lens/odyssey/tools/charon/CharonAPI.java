package br.ufrj.cos.lens.odyssey.tools.charon;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.EnactmentAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.LoadingAgent;


/**
 * This class is responsible for providing access to the Charon process machine
 * @author Anderson Marinho
 * @version 1.0, 15/08/2009
 */
public class CharonAPI {
	
	public final static int ACTIVITY = 1;
	public final static int PROCESS = 2;
	public final static int SYNCHRONISM = 3;
	public final static int DECISION = 4;
	
	
	/**
	 * Knowledge base instance
	 */
	private KnowledgeBase knowledgeBase;
	
	/**
	 * Creates a Charon API instance
	 * 
	 */
	public CharonAPI(KnowledgeBase knowledgeBase) throws CharonException {
		this.knowledgeBase = knowledgeBase;
	}
	
	
	/*
	 * Prospective Provenance
	 */
	
	public String createExperiment(String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createExperiment(knowledgeBase, name);
	}
	
	public boolean setExperimentRootProcess(String experimentId, String processClassId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.setExperimentRootProcess(knowledgeBase, experimentId, processClassId);
	}

	public String registerSGWf(String name, String host) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.registerSGWf(knowledgeBase, name, host);
	}
	
	public boolean associateProcessToSWFMS(String processInstanceId, String SWFMSId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateProcessToSWFMS(knowledgeBase, processInstanceId, SWFMSId);
	}
	
	public String defineProcess(String type, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.defineProcess(knowledgeBase, type, name);
	}
	
	public String defineActivity(String type, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.defineActivity(knowledgeBase, type, name);
	}
	
	public String defineArtifact(String type, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.defineArtifact(knowledgeBase, type, name);
	}
	
	public boolean associateArtifactUsedByActivity(String activityInstanceId, String productId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateArtifactUsedByActivity(knowledgeBase, activityInstanceId, productId);
	}
	
	public boolean associateArtifactUsedByProcess(String processInstanceId, String productId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateArtifactUsedByProcess(knowledgeBase, processInstanceId, productId);
	}
	
	public boolean associateArtifactGeneratedByActivity(String activityInstanceId, String productId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateArtifactGeneratedByActivity(knowledgeBase, activityInstanceId, productId);
	}
	
	public boolean associateArtifactGeneratedByProcess(String processInstanceId, String productId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateArtifactGeneratedByProcess(knowledgeBase, processInstanceId, productId);
	}
	
	public String instantiateActivity(String classId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.instantiate(knowledgeBase, classId);
	}
	
	public String instantiateProcess(String classId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.instantiate(knowledgeBase, classId);
	}
	
	public String createSynchronism() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createSynchronism(knowledgeBase);
	}
	
	public String createDecision(String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createDecision(knowledgeBase, name);
	}
	
	public String createOption(String decisionId, String name, int toElementType, String toElementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createOption(knowledgeBase, decisionId, name, toElementType, toElementId);
	}	
	
	public boolean associateElementToProcessWorkflow(String processId, int elementType, String elementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateElementToProcessWorkflow(knowledgeBase, processId, elementType, elementId);
	}
	
	public boolean defineFlow(String processId, int originElementType, String originElementId, int destinationElementType, String destinationElementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.defineFlow(knowledgeBase, processId, originElementType, originElementId, destinationElementType, destinationElementId);
	}
	
	public String createParameter(String name, String type, String value) throws CharonException{
		LoadingAgent enactmentAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return enactmentAgent.createParameter(knowledgeBase, name, type, value);	
	}
	
	public boolean associateParameterToActivity(String parameterId, String activityInstanceId) throws CharonException{
		LoadingAgent enactmentAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return enactmentAgent.associateParameterToActivity(knowledgeBase, parameterId, activityInstanceId);	
	}
	
	public boolean associateParameterToProcess(String parameterId, String processInstanceId) throws CharonException{
		LoadingAgent enactmentAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return enactmentAgent.associateParameterToProcess(knowledgeBase, parameterId, processInstanceId);
	}
	
	/*
	 * Retrospective Provenance
	 */
	
	public String intializeExperimentExecution(String experimentId) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.intializeExperimentExecution(knowledgeBase, experimentId);
	}
	
	public boolean notifyActivityExecutionStartup(String activityInstanceId, String[] context) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.notifyActivityExecutionStartup(knowledgeBase, activityInstanceId, context);
	}
	
	public boolean notifyProcessExecutionStartup(String processInstanceId, String[] context) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.notifyProcessExecutionStartup(knowledgeBase, processInstanceId, context);
	}

	public boolean notifyActivityExecutionEnding(String activityInstanceId, String[] context) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.notifyActivityExecutionEnding(knowledgeBase, activityInstanceId, context);
	}
	
	public boolean notifyProcessExecutionEnding(String processInstanceId, String[] context) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.notifyProcessExecutionEnding(knowledgeBase, processInstanceId, context);
	}

	public boolean notifyDecisionPointEnding(String decisionPointId, String[] context) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.notifyDecisionPointEnding(knowledgeBase, decisionPointId, context);
	}

	public boolean setArtifactData(String artifactId, String[] context, byte[] data) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.setArtifactData(knowledgeBase, artifactId, context, data);
	}
	
	public boolean publishArtifactDataLocation(String artifactId, String[] context, String hostURL, String hostLocalPath) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.publishArtifactDataLocation(knowledgeBase, artifactId, context, hostURL, hostLocalPath);
	}
	
}