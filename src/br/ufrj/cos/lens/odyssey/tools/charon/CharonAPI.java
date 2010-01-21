package br.ufrj.cos.lens.odyssey.tools.charon;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.AdminAgent;
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
	
	public boolean updateExperimentName(String experimentId, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateExperimentName(knowledgeBase, experimentId, name);
	}
	
	public boolean setExperimentRootProcess(String experimentId, String processClassId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.setExperimentRootProcess(knowledgeBase, experimentId, processClassId);
	}

	public String registerSGWf(String name, String host) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.registerSGWf(knowledgeBase, name, host);
	}
	
	public boolean updateSGWf(String name, String SWFMSId, String host) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateSGWf(knowledgeBase, name, SWFMSId, host);
	}
	
	public boolean updateSGWfName(String SWFMSId, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateSGWfName(knowledgeBase, SWFMSId, name);
	}
	
	public boolean updateSGWfHost(String SWFMSId, String host) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateSGWfHost(knowledgeBase, SWFMSId, host);
	}
	
	public boolean associateProcessToSWFMS(String processInstanceId, String SWFMSId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateProcessToSWFMS(knowledgeBase, processInstanceId, SWFMSId);
	}
	
	public String createProcess(String type, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createProcess(knowledgeBase, type, name);
	}
	
	public boolean updateProcess(String processClassId, String type, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateProcess(knowledgeBase, processClassId, type, name);
	}
	
	public boolean updateProcessName(String processClassId, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateProcessName(knowledgeBase, processClassId, name);
	}
	
	public boolean updateProcessType(String processClassId, String type) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateProcessType(knowledgeBase, processClassId, type);
	}
	
	public String createActivity(String type, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createActivity(knowledgeBase, type, name);
	}
	
	public boolean updateActivity(String activityClassId, String type, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateActivity(knowledgeBase, activityClassId, type, name);
	}
	
	public boolean updateActivityName(String activityClassId, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateActivityName(knowledgeBase, activityClassId, name);
	}
	
	public boolean updateActivityType(String activityClassId, String type) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updateActivityType(knowledgeBase, activityClassId, type);
	}
	
	public String createPort(String portType, String portName, String portDataType) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createPort(knowledgeBase, portType, portName, portDataType);
	}
	
	public boolean updatePort(String portId, String portType, String portName, String portDataType) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.updatePort(knowledgeBase, portId, portType, portName, portDataType);
	}
	
	public boolean addActivityPort(String activityId, String portId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.addActivityPort(knowledgeBase, activityId, portId);
	}
	
	public boolean addProcessPort(String processId, String portId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.addProcessPort(knowledgeBase, processId, portId);
	}
	
//	public String deletePort(String porttId){
//		connect(knowledgeBase);
//		String artifactId = IDGenerator.generateID();
//		boolean isSolvable = knowledgeBase.isSolvable("assertz_artifact('"+artifactId+"').");
//		disconnect();
//		if(isSolvable){
//			return artifactId;
//		}
//		else
//			return null;
//	}
	
	public String createArtifact(KnowledgeBase knowledgeBase) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createArtifact(knowledgeBase);
	}
	
	public boolean associateArtifactToActivityPort(String activityInstanceId, String portId, String artifactId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateArtifactToActivityPort(knowledgeBase, activityInstanceId, portId, artifactId);
	}
	
	public boolean associateArtifactToProcessPort(String processInstanceId, String portId, String artifactId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateArtifactToProcessPort(knowledgeBase, processInstanceId, portId, artifactId);
	}
	
	public String instantiateActivity(String activityClassId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.instantiateActivity(knowledgeBase, activityClassId);
	}
	
	public String instantiateProcess(String processClassId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.instantiateProcess(knowledgeBase, processClassId);
	}
	
	public String createSynchronism(KnowledgeBase knowledgeBase) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createSynchronism(knowledgeBase);
	}
	
	public String createDecision(String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createDecision(knowledgeBase, name);
	}
	
	public String createDecisionOption(String decisionId, String name, int toElementType, String toElementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createDecisionOption(knowledgeBase, decisionId, name, toElementType, toElementId);
	}
	
	public boolean associateElementToProcessWorkflow(String processId, int elementType, String elementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateElementToProcessWorkflow(knowledgeBase, processId, elementType, elementId);
	}
	
	public boolean defineFlow(String processId, int originElementType, String originElementId, int destinationElementType, String destinationElementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.defineFlow(knowledgeBase, processId, originElementType, originElementId, destinationElementType, destinationElementId);
	}
	
//	public String createParameter(String name, String type, String value) throws CharonException{
//		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
//		return loadingAgent.createParameter(knowledgeBase, name, type, value);	
//	}
//	
//	public boolean associateParameterToActivity(String parameterId, String activityInstanceId) throws CharonException{
//		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
//		return loadingAgent.associateParameterToActivity(knowledgeBase, parameterId, activityInstanceId);	
//	}
//	
//	public boolean associateParameterToProcess(String parameterId, String processInstanceId) throws CharonException{
//		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
//		return loadingAgent.associateParameterToProcess(knowledgeBase, parameterId, processInstanceId);
//	}
	
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

	public boolean notifyDecisionPointEnding(String decisionPointId, String answer, String[] context) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.notifyDecisionPointEnding(knowledgeBase, decisionPointId, answer, context);
	}

	public boolean setArtifactValue(String artifactId, String[] context, String value) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.setArtifactValue(knowledgeBase, artifactId, context, value);
	}
	
	public boolean publishArtifactDataLocation(String artifactId, String[] context, String hostURL, String hostLocalPath) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.publishArtifactDataLocation(knowledgeBase, artifactId, context, hostURL, hostLocalPath);
	}
	
	
	/*
	 * Administrative operations
	 */
	
	public void cleanKnowledgeBase() throws CharonException{
		AdminAgent adminAgent = AgentManager.getInstance().getAgent(AdminAgent.class);
		adminAgent.cleanKnowledgeBase(knowledgeBase);
	}
	
}