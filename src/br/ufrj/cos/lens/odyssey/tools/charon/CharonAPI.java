package br.ufrj.cos.lens.odyssey.tools.charon;

import br.ufrj.cos.lens.odyssey.tools.charon.agents.AdminAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.EnactmentAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.LoadingAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.util.CharonUtil;


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
	
	public String createExperimentNewVersion(String experimentId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createExperimentNewVersion(knowledgeBase, experimentId);
	}
	
	public boolean setExperimentName(String experimentId, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.setExperimentName(knowledgeBase, experimentId, name);
	}
		
	public boolean setExperimentRootProcess(String experimentId, String processInstanceId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.setExperimentRootProcess(knowledgeBase, experimentId, processInstanceId);
	}

	public String registerSGWf(String name, String host) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.registerSGWf(knowledgeBase, name, host);
	}
		
	public boolean associateProcessToSWFMS(String processInstanceId, String SWFMSId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateProcessToSWFMS(knowledgeBase, processInstanceId, SWFMSId);
	}
	
	public String createProcess(String type, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createProcess(knowledgeBase, type, name);
	}
	
	public boolean isValidDerivatedWorkflow() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.isValidDerivedWorkflow(knowledgeBase);
	}
		
	public String createActivity(String type, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createActivity(knowledgeBase, type, name);
	}
	
	public String createInPort(String portName, String portDataType) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createPort(knowledgeBase, CharonUtil.INPORT, portName, portDataType);
	}
	
	public String createOutPort(String portName, String portDataType) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createPort(knowledgeBase, CharonUtil.OUTPORT, portName, portDataType);
	}
	
	public boolean addActivityPort(String activityId, String portId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.addActivityPort(knowledgeBase, activityId, portId);
	}
	
	public boolean addProcessPort(String processId, String portId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.addProcessPort(knowledgeBase, processId, portId);
	}
	
	public String createArtifact() throws CharonException{
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
	
	public String instantiateActivity(String activityClassId, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.instantiateActivity(knowledgeBase, activityClassId, name);
	}
	
	public String instantiateProcess(String processClassId, String name) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.instantiateProcess(knowledgeBase, processClassId, name);
	}
	
	public String createSynchronism() throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createSynchronism(knowledgeBase);
	}
	
	public boolean createInvisibleSynchronism(String synchronismId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.createInvisibleSynchronism(knowledgeBase, synchronismId);
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
	
	public boolean associateElementToExperimentRootProcessWorkflow(String experimentId, int elementType, String elementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.associateElementToExperimentRootProcessWorkflow(knowledgeBase, experimentId, elementType, elementId);
	}
	
	public boolean removeElementToExperimentRootProcessWorkflow(String experimentId, int elementType, String elementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.removeElementToExperimentRootProcessWorkflow(knowledgeBase, experimentId, elementType, elementId);
	}
	
	public boolean defineFlow(String processId, int originElementType, String originElementId, int destinationElementType, String destinationElementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.defineFlow(knowledgeBase, processId, originElementType, originElementId, destinationElementType, destinationElementId);
	}
	
	public boolean defineExperimentRootProcessFlow(String experimentId, int originElementType, String originElementId, int destinationElementType, String destinationElementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.defineExperimentRootProcessFlow(knowledgeBase, experimentId, originElementType, originElementId, destinationElementType, destinationElementId);
	}
	
	public boolean removeExperimentRootProcessFlow(String experimentId, int originElementType, String originElementId, int destinationElementType, String destinationElementId) throws CharonException{
		LoadingAgent loadingAgent = AgentManager.getInstance().getAgent(LoadingAgent.class);
		return loadingAgent.removeExperimentRootProcessFlow(knowledgeBase, experimentId, originElementType, originElementId, destinationElementType, destinationElementId);
	}

	
	/*
	 * Retrospective Provenance
	 */
	
	public String initializeExperimentExecution(String experimentId) throws CharonException{
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
	
	public boolean publishArtifactValueLocation(String artifactId, String[] context, String hostURL, String hostLocalPath) throws CharonException{
		EnactmentAgent enactmentAgent = AgentManager.getInstance().getAgent(EnactmentAgent.class);
		return enactmentAgent.publishArtifactValueLocation(knowledgeBase, artifactId, context, hostURL, hostLocalPath);
	}
	
	
	/*
	 * Administrative operations
	 */
	
	public void cleanKnowledgeBase() throws CharonException{
		AdminAgent adminAgent = AgentManager.getInstance().getAgent(AdminAgent.class);
		adminAgent.cleanKnowledgeBase(knowledgeBase);
	}
	
}