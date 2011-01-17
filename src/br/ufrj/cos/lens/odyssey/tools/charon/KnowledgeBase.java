package br.ufrj.cos.lens.odyssey.tools.charon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import spem.SpemPackage;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.MappingAgent;
import br.ufrj.cos.lens.odyssey.tools.charon.util.CharonUtil;
import br.ufrj.cos.lens.odyssey.tools.charon.util.IDGenerator;
import br.ufrj.cos.lens.odyssey.tools.charon.util.MysqlConnector;
import br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine;

/**
 * This class represents a knowledge base of a process.
 *
 * @author Leonardo Murta
 * @version 1.0, 22/11/2001
 */
public class KnowledgeBase {
	
	/**
	 * Inference machine that phisically holds the knowledge base
	 */
	private InferenceMachine inferenceMachine = null;

	/**
	 * Agent connected to the knowledge base
	 */
	private Agent agent = null;
	
	/**
	 * Repository where the knowledge base should be stored
	 */
	private File repository;
	
	private boolean database;
	
	private String databaseLocation;
	
	private String user;
	
	private String password;

	/**
	 * Constructs a new knowledge base loading existing facts from "repository"
	 * 
	 * @param repository File that store existing prolog facts.
	 */
	public KnowledgeBase(File repository) throws CharonException {
		this.repository = repository;

		if (repository.exists()) {
			try {
				inferenceMachine = new InferenceMachine(repository);
			} catch (Exception e) {
				throw new CharonException("Could not load existing knowledge base from " + repository, e);
			}
		} else {
			inferenceMachine = new InferenceMachine();
		}
		
		setCharonRules(false);
	}
	
	/**
	 * Constructs a new knowledge base loading existing facts from "repository"
	 * 
	 * @param repository File that store existing prolog facts.
	 */
	public KnowledgeBase(String database, String user, String password) throws CharonException {

		this.database = true;
		this.databaseLocation = database;
		this.user = user;
		this.password = password;
		
		inferenceMachine = new InferenceMachine();
		
		try {
			loadFromDataBase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setCharonRules(true);
		
	}
	
	private void setCharonRules(boolean IsUsingDatabase) {

		ArrayList<String> charonRules = new ArrayList<String>();
		
		charonRules.add("(createElement(ElementId, '"+CharonUtil.EXPERIMENT+"', experiment(ElementId)) :- !)");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.PROCESS+"', process(ElementId)) :- !)");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.ACTIVITY+"', activity(ElementId)) :- !)");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.SYNCHRONISM+"', synchronism(ElementId)) :- !)");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.DECISION+"', decision(ElementId)) :- !)");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.INITIAL+"', initial(ElementId)) :- !)");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.FINAL+"', final(ElementId)) :- !)");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.ARTIFACT+"', product(ElementId)) :- !)");
		
		charonRules.add("(nextVersionId(Element, VersionId) :- currentVersion(Element, PreviousVersionId), VersionId is PreviousVersionId + 1)");
		charonRules.add("(setCurrentVersion(Element, VersionId, PreviousVersionId) :- retract(currentVersion(Element, PreviousVersionId)), assertz(currentVersion(Element, VersionId)))");
		charonRules.add("(associateElementToExperimentLastVersion(ExperimentId, Element) :- currentVersion(experiment(ExperimentId), CurrentVersionId), assertz(experimentVersionDimension(ExperimentId, CurrentVersionId, swfms(SWFMS_Id))))");

		
		
		charonRules.add("(last(X,[X]))");
		charonRules.add("(last(X,[_|L]) :- last(X,L))");

		
		//Lista atividades de um workflow

		charonRules.add("(getProcessElements(final(_), []) :- !)");
		charonRules.add("(getProcessElements(E, [process(Name)|ElementList]) :- processFlow(E, process(Id)), !, processInstanceType(Id, Id2), processName(Id2, Name), getProcessElements(process(Id), ElementList))");
		charonRules.add("(getProcessElements(E, [activity(Name)|ElementList]) :- processFlow(E, activity(Id)), !, activityInstanceType(Id, Id2), activityName(Id2, Name), getProcessElements(activity(Id), ElementList))");
		charonRules.add("(getProcessElements(E, ElementList) :- processFlow(E, E2), getProcessElements(E2, ElementList))");
		charonRules.add("(containsProcess(E, ProcessInstanceId) :- processFlow(E, process(ProcessInstanceId)), !)");
		charonRules.add("(containsProcess(E, ProcessInstanceId) :- processFlow(E, E2), containsProcess(E2, ProcessInstanceId))");
		charonRules.add("(listActivitiesFromSWFMS(ExperimentId, SWFMSName, Activities) :- experimentRootProcess(ExperimentId, _, ExperimentRootProcessId), swfmsName(SWFMSId, SWFMSName), swfmsProcess(ProcessInstanceId, SWFMSId), containsProcess(initial(ExperimentRootProcessId), ProcessInstanceId), processInstanceType(ProcessInstanceId, ProcessClassId), getProcessElements(initial(ProcessClassId), Activities))");
		
		//Lista de portas de uma atividade
		charonRules.add("(getActivityPortName([], []))");
		charonRules.add("(getActivityPortName([[PortId, PortName]|PortIdNameList], [PortId|PortIdList]) :- portName(PortId, PortName), getActivityPortName(PortIdNameList, PortIdList))");
		charonRules.add("(activityPorts(ExperimentId, ExperimentInstanceId, ActivityName, ActivityPorts) :- experimentInstance(ExperimentInstanceId, _, ExperimentId), activityName(ActivityClassId, ActivityName), findall(PortId, activityPort(ActivityClassId, PortId), PortIdList), getActivityPortName(ActivityPorts, PortIdList))");
		
		//Tempo de duração de uma execução de um experimento
		charonRules.add("(experimentExecutionTime(ExperimentId, ExperimentInstanceId, ExecutionTime) :- experimentInstance(ExperimentInstanceId, _, ExperimentId), executed(experiment(ExperimentInstanceId), _, Ti, Tf, _),  class('java.lang.Long') <- parseLong(Ti) returns V1, class('java.lang.Long') <- parseLong(Tf) returns V2, ExecutionTime is V2 - V1)");
		
		//Tempo de execução de uma atividade do experimento
		charonRules.add("(activityExecutionTime(ExperimentId, ExperimentInstanceId, ActivityName, ExecutionTime) :- experimentInstance(ExperimentInstanceId, _, ExperimentId), activityName(ActivityClassId, ActivityName), activityInstanceType(ActivityInstanceId, ActivityClassId), executed(activity(ActivityInstanceId), P , Ti, Tf, _), last(ExperimentInstanceId, P), class('java.lang.Long') <- parseLong(Ti) returns V1, class('java.lang.Long') <- parseLong(Tf) returns V2, ExecutionTime is V2 - V1)");
		
		//Lista de artefatos consumidos por uma atividade
		charonRules.add("(consumedArtifactList(_, [], []))");
		charonRules.add("(consumedArtifactList(ActivityInstanceId, [PortId|PortIdList], [[ArtifactId,ArtifactValue]|ArtifactList]) :- portType(PortId, '"+CharonUtil.INPORT+"'), artifactActivityPort(ArtifactId, ActivityInstanceId, PortId), artifactValue(ArtifactId, ArtifactValue, _), consumedArtifactList(ActivityInstanceId, PortIdList, ArtifactList))");
		charonRules.add("(consumedArtifactList(ActivityInstanceId, [PortId|PortIdList], ArtifactList) :- portType(PortId, '"+CharonUtil.OUTPORT+"'), artifactActivityPort(ArtifactId, ActivityInstanceId, PortId), consumedArtifactList(ActivityInstanceId, PortIdList, ArtifactList))");
		charonRules.add("(artifactsConsumedByActivity(ExperimentId, ExperimentInstanceId, ActivityName, ArtifactIdList) :- experimentInstance(ExperimentInstanceId, _, ExperimentId), activityName(ActivityClassId, ActivityName), activityInstanceType(ActivityInstanceId, ActivityClassId), findall(PortId, activityPort(ActivityClassId, PortId), PortIdList), consumedArtifactList(ActivityInstanceId, PortIdList, ArtifactIdList))");
		
		//Lista de artefatos gerados por uma atividade
		charonRules.add("(generatedArtifactList(_, [], []))");
		charonRules.add("(generatedArtifactList(ActivityInstanceId, [PortId|PortIdList], [[ArtifactId,ArtifactValue]|ArtifactList]) :- portType(PortId, '"+CharonUtil.OUTPORT+"'), artifactActivityPort(ArtifactId, ActivityInstanceId, PortId), artifactValue(ArtifactId, ArtifactValue, _), generatedArtifactList(ActivityInstanceId, PortIdList, ArtifactList))");
		charonRules.add("(generatedArtifactList(ActivityInstanceId, [PortId|PortIdList], ArtifactList) :- portType(PortId, '"+CharonUtil.INPORT+"'), artifactActivityPort(ArtifactId, ActivityInstanceId, PortId), generatedArtifactList(ActivityInstanceId, PortIdList, ArtifactList))");
		charonRules.add("(artifactsGeneratedByActivity(ExperimentId, ExperimentInstanceId, ActivityName, ArtifactIdList) :- experimentInstance(ExperimentInstanceId, _, ExperimentId), activityName(ActivityClassId, ActivityName), activityInstanceType(ActivityInstanceId, ActivityClassId), findall(PortId, activityPort(ActivityClassId, PortId), PortIdList), generatedArtifactList(ActivityInstanceId, PortIdList, ArtifactIdList))");
		
		//Valor de um artefato
		charonRules.add("(artifactValue(ExperimentId, ExperimentInstanceId, ArtifactId, ArtifactValue) :- artifactValue(ArtifactId, ArtifactValue, _))");
		
		//Lista de artefatos ancestrais de um artefato
		charonRules.add("(existsPortOut(ArtifactId) :- artifactActivityPort(ArtifactId, _, PortIdOUT), portType(PortIdOUT, '"+CharonUtil.OUTPORT+"'))");
		charonRules.add("(artifactAncestors(ArtifactId, []) :- not(existsPortOut(ArtifactId)))");
		charonRules.add("(artifactAncestors(ArtifactId, [[ArtifactAncestorId,ArtifactValue]|ArtifactAncestors]) :- artifactActivityPort(ArtifactId, ActivityInstanceId, PortIdOUT), portType(PortIdOUT, '"+CharonUtil.OUTPORT+"'), artifactActivityPort(ArtifactAncestorId, ActivityInstanceId, PortIdIN), portType(PortIdIN, '"+CharonUtil.INPORT+"'), artifactAncestors(ArtifactAncestorId, ArtifactAncestors), artifactValue(ArtifactAncestorId, ArtifactValue, _))");
		
		
		
		if(IsUsingDatabase){
			
			//General rules
			
			charonRules.add("(concat(Str1, Str2, Str3) :- java_object('java.lang.StringBuffer', [Str1], Temp), Temp <- append(Str2), java_object('java.lang.String', [Temp], Str3))");
			charonRules.add("(listToString([], ''))");
			charonRules.add("(listToString([Element|[]], Element) :- !)");
			charonRules.add("(listToString([Element|Elements], StringList) :- listToString(Elements, StringList2), concat(Element, ', ', StringList3), concat(StringList3, StringList2, StringList))");
			charonRules.add("(createParamList2([], ' ('))");
			charonRules.add("(createParamList2([Param|Params], ParamList) :- createParamList2(Params, ParamList2), concat(ParamList2, Param, ParamList3), concat(ParamList3, ', ', ParamList))");
			charonRules.add("(createParamList([Param|Params], ParamList) :- createParamList2(Params, ParamList2), concat(ParamList2, Param, ParamList3), concat(ParamList3, ')', ParamList))");
			charonRules.add("(createValueList2([], ' VALUES('))");
			charonRules.add("(createValueList2([Value|Values], ValueList) :- createValueList2(Values, ValueList2), concat(ValueList2, '\"', ValueList3), concat(ValueList3, Value, ValueList4), concat(ValueList4, '\", ', ValueList))");
			charonRules.add("(createValueList([Value|Values], ValueList) :- createValueList2(Values, ValueList2), concat(ValueList2, '\"', ValueList3), concat(ValueList3, Value, ValueList4), concat(ValueList4, '\")', ValueList))");
			charonRules.add("(createInsertCommand(Table, Params, Values, Query) :- concat('INSERT INTO ', Table, Query_part1), createParamList(Params, ParamList), createValueList(Values, ValueList), concat(Query_part1, ParamList, Query_part2), concat(Query_part2, ValueList, Query))");
			charonRules.add("(createSetValues2([], [], ' SET '))");
			charonRules.add("(createSetValues2([Param|Params], [Value|Values], SetValues) :- createSetValues2(Params, Values, SetValues2), concat(SetValues2, Param, SetValues3), concat(SetValues3, '=\"', SetValues4), concat(SetValues4, Value, SetValues5), concat(SetValues5, '\", ', SetValues))");
			charonRules.add("(createSetValues([Param|Params], [Value|Values], SetValues) :- createSetValues2(Params, Values, SetValues2), concat(SetValues2, Param, SetValues3), concat(SetValues3, '=\"', SetValues4), concat(SetValues4, Value, SetValues5), concat(SetValues5, '\"', SetValues))");
			charonRules.add("(createWhereClauses2([], [], ' WHERE '))");
			charonRules.add("(createWhereClauses2([Param|Params], [Value|Values], WhereClauses) :- createWhereClauses2(Params, Values, WhereClauses2), concat(WhereClauses2, Param, WhereClauses3), concat(WhereClauses3, '=\"', WhereClauses4), concat(WhereClauses4, Value, WhereClauses5), concat(WhereClauses5, '\" AND ', WhereClauses))");
			charonRules.add("(createWhereClauses([Param|Params], [Value|Values], WhereClauses) :- createWhereClauses2(Params, Values, WhereClauses2), concat(WhereClauses2, Param, WhereClauses3), concat(WhereClauses3, '=\"', WhereClauses4), concat(WhereClauses4, Value, WhereClauses5), concat(WhereClauses5, '\"', WhereClauses))");
			charonRules.add("(createUpdateCommand(Table, SetParams, SetValues, WhereParams, WhereValues, Query) :- concat('UPDATE ', Table, Query_part1), createSetValues(SetParams, SetValues, SetValuesList), concat(Query_part1, SetValuesList, Query_part2), createWhereClauses(WhereParams, WhereValues, WhereClauses), concat(Query_part2, WhereClauses, Query))");
			charonRules.add("(createDeleteCommand(Table, WhereParams, WhereValues, Query) :- concat('DELETE FROM ', Table, Query_part1), createWhereClauses(WhereParams, WhereValues, WhereClauses), concat(Query_part1, WhereClauses, Query))");
			charonRules.add("(init_dbase(DBase, Username, Password, Connection) :- java_object('br.ufrj.cos.lens.odyssey.tools.charon.util.MysqlConnector', [DBase, Username, Password], Connection), Connection <- connect('1'))");
			charonRules.add("(exec(Connection, Query) :- Connection <- exec(Query))");
			charonRules.add("(println(Str) :- class('java.lang.System').out <- get(StdOut), StdOut <- println(Str))");
			charonRules.add("(println([]))");
			charonRules.add("(println([A|B]) :- println(A), println(B))");
			
			
            // EXPERIMENT
            charonRules.add("(create_experiment(ExperimentId, ExperimentName) :- assertz(experiment(ExperimentId)), VersionId is 1, assertz(version(experiment(ExperimentId), VersionId, 0)), assertz(currentVersion(experiment(ExperimentId), VersionId)), assertz(experimentName(ExperimentId, ExperimentName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('EXPERIMENT', ['id', 'name'], [ExperimentId, ExperimentName], Query1), exec(Conn, Query1), createInsertCommand('EXPERIMENT_VERSION', ['version', 'experiment'], [VersionId, ExperimentId], Query2), exec(Conn, Query2))");
            charonRules.add("(create_experimentNewVersion(ExperimentId) :- nextVersionId(experiment(ExperimentId), VersionId), currentVersion(experiment(ExperimentId), PreviousVersionId), assertz(version(experiment(ExperimentId), VersionId, PreviousVersionId)), setCurrentVersion(experiment(ExperimentId), VersionId, PreviousVersionId), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('EXPERIMENT_VERSION', ['version', 'previous_version', 'experiment'], [VersionId, PreviousVersionId, ExperimentId], Query), exec(Conn, Query))");
            charonRules.add("(set_experimentName(ExperimentId, ExperimentName) :- assertz(experimentName(ExperimentId, ExperimentName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createUpdateCommand('EXPERIMENT', ['name'], [ExperimentName], ['id'], [ExperimentId], Query), exec(Conn, Query))");
            charonRules.add("(set_experimentRootProcess(ExperimentId, ExperimentRootProcessId) :- currentVersion(experiment(ExperimentId), CurrentVersionId), assertz(experimentRootProcess(ExperimentId, CurrentVersionId, ExperimentRootProcessId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createUpdateCommand('EXPERIMENT_VERSION', ['root_process'], [ExperimentRootProcessId], ['version', 'experiment'], [CurrentVersionId, ExperimentId], Query), exec(Conn, Query))");
            charonRules.add("(create_experimentInstance(ExperimentInstanceId, ExperimentVersionId, ExperimentId) :- assertz(experimentInstance(ExperimentInstanceId, ExperimentVersionId, ExperimentId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('EXPERIMENT_INSTANCE', ['instance_id', 'version_id', 'experiment_id'], [ExperimentInstanceId, ExperimentVersionId, ExperimentId], Query), exec(Conn, Query))");
						
            // PROCESS
            charonRules.add("(create_process(ProcessClassId, ProcessClassName, ProcessClassType) :- assertz(process(ProcessClassId)), assertz(processType(ProcessClassId, ProcessClassType)), assertz(processName(ProcessClassId, ProcessClassName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PROCESS', ['id', 'name', 'type_'], [ProcessClassId, ProcessClassName, ProcessClassType], Query), exec(Conn, Query))");
            charonRules.add("(add_processPort(ProcessClassId, PortId) :- assertz(processPort(ProcessClassId, PortId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PROCESS_PORT', ['process', 'port'], [ProcessClassId, PortId], Query), exec(Conn, Query))");
            
            // ACTIVITY
			charonRules.add("(create_activity(ActivityClassId, ActivityClassName, ActivityClassType) :- assertz(activity(ActivityClassId)), assertz(activityType(ActivityClassId, ActivityClassType)), assertz(activityName(ActivityClassId, ActivityClassName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ACTIVITY', ['id', 'name', 'type_'], [ActivityClassId, ActivityClassName, ActivityClassType], Query), exec(Conn, Query))");
			charonRules.add("(add_activityPort(ActivityId, PortId) :- assertz(activityPort(ActivityId, PortId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ACTIVITY_PORT', ['activity', 'port'], [ActivityId, PortId], Query), exec(Conn, Query))");
			
			// SWFMS
			charonRules.add("(create_SWFMS(SWFMS_Id, SWFMS_Name, SWFMS_Host) :- assertz(swfms(SWFMS_Id)), assertz(swfmsName(SWFMS_Id, SWFMS_Name)), assertz(swfmsHost(SWFMS_Id, SWFMS_Host)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('SWFMS', ['id', 'name', 'host'], [SWFMS_Id, SWFMS_Name, SWFMS_Host], Query), exec(Conn, Query))");

			//Decision and Option
			charonRules.add("(create_decision(DecisionId, DecisionName) :- assertz(decision(DecisionId)), assertz(decisionName(DecisionId, DecisionName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('DECISION', ['id', 'name'], [DecisionId, DecisionName], Query), exec(Conn, Query))");
			charonRules.add("(add_decisionOption(OptionId, OptionName, ToElementType, ToElementId) :- createElement(ToElementId, ToElementType, ToElement), assertz(option(OptionId, OptionName, ToElement)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('OPTION', ['id', 'name'], [OptionId, OptionName], Query), exec(Conn, Query), createInsertCommand('FLOW', ['id_origin_element', 'type_origin_element', 'id_destination_element', 'type_destination_element'], [OptionId, '"+CharonUtil.OPTION+"', ToElementId, ToElementType], Query2), exec(Conn, Query2))");

			//Defining flow
			charonRules.add("(create_flow(OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), assertz(processFlow(OriginElement, DestinationElement)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('FLOW', ['id_origin_element', 'type_origin_element', 'id_destination_element', 'type_destination_element'], [OriginElementId, OriginElementType, DestinationElementId, DestinationElementType], Query), exec(Conn, Query))");
			charonRules.add("(delete_flow(OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), retract(processFlow(OriginElement, DestinationElement)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createDeleteCommand('FLOW', ['id_origin_element', 'type_origin_element', 'id_destination_element', 'type_destination_element'], [OriginElementId, OriginElementType, DestinationElementId, DestinationElementType], Query), exec(Conn, Query))");

			//Defining experiment flow
			charonRules.add("(create_experiment_flow(OriginElementType, experiment(ExperimentId), DestinationElementType, DestinationElementId) :- currentVersion(experiment(ExperimentId), CurrentVersionId), experimentRootProcess(ExperimentId, CurrentVersionId, ProcessClassId), create_flow(OriginElementType, ProcessClassId, DestinationElementType, DestinationElementId))");
			charonRules.add("(create_experiment_flow(OriginElementType, OriginElementId, DestinationElementType, experiment(ExperimentId)) :- currentVersion(experiment(ExperimentId), CurrentVersionId), experimentRootProcess(ExperimentId, CurrentVersionId, ProcessClassId), create_flow(OriginElementType, OriginElementId, DestinationElementType, ProcessClassId))");
			charonRules.add("(delete_experiment_flow(OriginElementType, experiment(ExperimentId), DestinationElementType, DestinationElementId) :- currentVersion(experiment(ExperimentId), CurrentVersionId), experimentRootProcess(ExperimentId, CurrentVersionId, ProcessClassId), delete_flow(OriginElementType, ProcessClassId, DestinationElementType, DestinationElementId))");
			charonRules.add("(delete_experiment_flow(OriginElementType, OriginElementId, DestinationElementType, experiment(ExperimentId)) :- currentVersion(experiment(ExperimentId), CurrentVersionId), experimentRootProcess(ExperimentId, CurrentVersionId, ProcessClassId), delete_flow(OriginElementType, OriginElementId, DestinationElementType, ProcessClassId))");
			
			//Synchronism
			charonRules.add("(create_synchronism(SynchronismId) :- assertz(synchronism(SynchronismId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('SYNCHRONISM', ['id', 'visible'], [SynchronismId, 1], Query), exec(Conn, Query))");
			charonRules.add("(create_invisible_synchronism(SynchronismId) :- assertz(synchronism(SynchronismId)), assertz(invisible(synchronism(SynchronismId))), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('SYNCHRONISM', ['id', 'visible'], [SynchronismId, 0], Query), exec(Conn, Query))");
			//charonRules.add("(create_invisible_synchronism(SynchronismId) :- assertz(synchronism(SynchronismId)), assertz(invisible(synchronism(SynchronismId))), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('SYNCHRONISM', ['id', 'visible'], [SynchronismId, '0'], Query), exec(Conn, Query))");
			
			//Initial
			charonRules.add("(create_initial(InitialId) :- assertz(initial(InitialId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('INITIAL', ['id'], [InitialId], Query), exec(Conn, Query))");
			
			//Final
			charonRules.add("(create_final(FinalId) :- assertz(final(FinalId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('FINAL', ['id'], [FinalId], Query), exec(Conn, Query))");
			
			//Artifact
			charonRules.add("(create_artifact(ArtifactId) :- assertz(artifact(ArtifactId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ARTIFACT', ['id'], [ArtifactId], Query), exec(Conn, Query))");

			//Port
			charonRules.add("(create_port(PortId, PortType, PortName, PortDataType) :- assertz(port(PortId)), assertz(portType(PortId, PortType)), assertz(portName(PortId, PortName)), assertz(portDataType(PortId, PortDataType)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PORT', ['id', 'type_', 'name', 'data_type'], [PortId, PortType, PortName, PortDataType], Query), exec(Conn, Query))");
			
			//Process Instance
			charonRules.add("(create_processInstance(ProcessInstanceId, ProcessClassId, ProcessInstanceName) :- assertz(processInstance(ProcessInstanceId)), assertz(processInstanceType(ProcessInstanceId, ProcessClassId)), assertz(processInstanceName(ProcessInstanceId, ProcessInstanceName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PROCESS_INSTANCE', ['instance_id', 'process_id', 'name'], [ProcessInstanceId, ProcessClassId, ProcessInstanceName], Query), exec(Conn, Query))");
			charonRules.add("(set_SWFMSProcess(SWFMSId, ProcessInstanceId) :- assertz(swfmsProcess(ProcessInstanceId, SWFMSId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createUpdateCommand('PROCESS_INSTANCE', ['swfms_id'], [SWFMSId], ['instance_id'], [ProcessInstanceId], Query), exec(Conn, Query))");
			charonRules.add("(set_artifactProcessPort(ProcessInstanceId, PortId, ArtifactId) :- assertz(artifactProcessPort(ArtifactId, ProcessInstanceId, PortId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ARTIFACT_PORT_PROCESS_INSTANCE', ['process_instance', 'artifact', 'port'], [ProcessInstanceId, ArtifactId, PortId], Query), exec(Conn, Query))");
			
			//Activity Instance
			charonRules.add("(create_activityInstance(ActivityInstanceId, ActivityClassId, ActivityInstanceName) :- assertz(activityInstance(ActivityInstanceId)), assertz(activityInstanceType(ActivityInstanceId, ActivityClassId)), assertz(activityInstanceName(ActivityInstanceId, ActivityInstanceName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ACTIVITY_INSTANCE', ['instance_id', 'activity_id', 'name'], [ActivityInstanceId, ActivityClassId, ActivityInstanceName], Query), exec(Conn, Query))");
			charonRules.add("(set_artifactActivityPort(ActivityInstanceId, PortId, ArtifactId) :- assertz(artifactActivityPort(ArtifactId, ActivityInstanceId, PortId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ARTIFACT_PORT_ACTIVITY_INSTANCE', ['activity_instance', 'artifact', 'port'], [ActivityInstanceId, ArtifactId, PortId], Query), exec(Conn, Query))");
			
			// Execution
			charonRules.add("(assertz_executing(ElementType, ElementId, P, T, Performers) :- listToString(P, PathList), listToString(Performers, PerformersList), createElement(ElementId, ElementType, Element), assertz(executing(Element, P, T, Performers)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('EXECUTION_STATUS', ['id_element', 'type_element', 'status', 'start_time', 'path', 'performers'], [ElementId, ElementType, '"+CharonUtil.EXECUTING_STATUS+"', T, PathList, PerformersList], Query), exec(Conn, Query))");
			charonRules.add("(assertz_executed(ElementType, ElementId, P, Ti, Tf, Performers) :- listToString(P, PathList), createElement(ElementId, ElementType, Element), assertz(executed(Element, P, Ti, Tf, Performers)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createUpdateCommand('EXECUTION_STATUS', ['status', 'end_time'], ['"+CharonUtil.EXECUTED_STATUS+"', Tf], ['id_element', 'type_element', 'path'], [ElementId, ElementType, PathList], Query), exec(Conn, Query))");
			charonRules.add("(assertz_option_selected(OptionId, A, P) :- listToString(P, PathList), assertz(option_selected(OptionId, A, P)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('OPTION_SELECTED', ['id_option', 'name', 'path'], [OptionId, A, PathList], Query), exec(Conn, Query))");
			
			charonRules.add("(assertz_artifactValue(ArtifactId, ArtifactValue, P) :- assertz(artifactValue(ArtifactId, ArtifactValue, P)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ARTIFACT_VALUE', ['artifact', 'value', 'path'], [ArtifactId, ArtifactValue, P], Query), exec(Conn, Query))");
			charonRules.add("(assertz_artifactValueLocation(ArtifactId, HostURL, HostLocalPath, P) :- assertz(artifactValueLocation(ArtifactId, HostURL, HostLocalPath, P)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ARTIFACT_VALUE_LOCATION', ['artifact', 'host_url', 'host_local_path', 'path'], [ArtifactId, HostURL, HostLocalPath, P], Query), exec(Conn, Query))");
			
		}
		else{
            
            // EXPERIMENT
            charonRules.add("(create_experiment(ExperimentId, ExperimentName) :- assertz(experiment(ExperimentId)), VersionId is 1, assertz(version(experiment(ExperimentId), VersionId, 0)), assertz(currentVersion(experiment(ExperimentId), VersionId)), assertz(experimentName(ExperimentId, ExperimentName)))");
            charonRules.add("(create_experimentNewVersion(ExperimentId) :- nextVersionId(experiment(ExperimentId), VersionId), currentVersion(experiment(ExperimentId), PreviousVersionId), assertz(version(experiment(ExperimentId), VersionId, PreviousVersionId)), setCurrentVersion(experiment(ExperimentId), VersionId, PreviousVersionId))");
            charonRules.add("(set_experimentName(ExperimentId, ExperimentName) :- assertz(experimentName(ExperimentId, ExperimentName)))");
            charonRules.add("(set_experimentRootProcess(ExperimentId, ExperimentRootProcessInstanceId) :- currentVersion(experiment(ExperimentId), CurrentVersionId), assertz(experimentRootProcess(ExperimentId, CurrentVersionId, ExperimentRootProcessInstanceId)))");
			charonRules.add("(create_experimentInstance(ExperimentInstanceId, ExperimentVersionId, ExperimentId) :- assertz(experimentInstance(ExperimentInstanceId, ExperimentVersionId, ExperimentId)))");
            
            // PROCESS
            charonRules.add("(create_process(ProcessClassId, ProcessClassName, ProcessClassType) :- assertz(process(ProcessClassId)), assertz(processName(ProcessClassId, ProcessClassName)), assertz(processType(ProcessClassId, ProcessClassType)))");
            charonRules.add("(add_processPort(ProcessClassId, PortId) :- assertz(processPort(ProcessClassId, PortId)))");
           
			// ACTIVITY
            charonRules.add("(create_activity(ActivityClassId, ActivityClassName, ActivityClassType) :- assertz(activity(ActivityClassId)), assertz(activityName(ActivityClassId, ActivityClassName)), assertz(activityType(ActivityClassId, ActivityClassType)))");
            charonRules.add("(add_activityPort(ActivityClassId, PortId) :- assertz(activityPort(ActivityClassId, PortId)))");
			
			// SWFMS
			charonRules.add("(create_SWFMS(SWFMS_Id, SWFMS_Name, SWFMS_Host) :- assertz(swfms(SWFMS_Id)), assertz(swfmsName(SWFMS_Id, SWFMS_Name)), assertz(swfmsHost(SWFMS_Id, SWFMS_Host)))");
			
			//Decision and Option
			charonRules.add("(create_decision(DecisionId, DecisionName) :- assertz(decision(DecisionId)), assertz(decisionName(DecisionId, DecisionName)))");
			charonRules.add("(add_decisionOption(OptionId, OptionName, ToElementType, ToElementId) :- createElement(ToElementId, ToElementType, ToElement), assertz(option(OptionId, OptionName, ToElement)))");
			
			//Defining flow
			charonRules.add("(create_flow(OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), assertz(processFlow(OriginElement, DestinationElement)))");
			charonRules.add("(delete_flow(OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), retract(processFlow(OriginElement, DestinationElement)))");
			
			//Defining experiment flow
			charonRules.add("(create_experiment_flow(OriginElementType, experiment(ExperimentId), DestinationElementType, DestinationElementId) :- currentVersion(experiment(ExperimentId), CurrentVersionId), experimentRootProcess(ExperimentId, CurrentVersionId, ProcessInstanceId), processInstanceType(ProcessInstanceId, ProcessClassId), create_flow(OriginElementType, ProcessClassId, DestinationElementType, DestinationElementId))");
			charonRules.add("(create_experiment_flow(OriginElementType, OriginElementId, DestinationElementType, experiment(ExperimentId)) :- currentVersion(experiment(ExperimentId), CurrentVersionId), experimentRootProcess(ExperimentId, CurrentVersionId, ProcessInstanceId), processInstanceType(ProcessInstanceId, ProcessClassId), create_flow(OriginElementType, OriginElementId, DestinationElementType, ProcessClassId))");
			charonRules.add("(delete_experiment_flow(OriginElementType, experiment(ExperimentId), DestinationElementType, DestinationElementId) :- currentVersion(experiment(ExperimentId), CurrentVersionId), experimentRootProcess(ExperimentId, CurrentVersionId, ProcessInstanceId), processInstanceType(ProcessInstanceId, ProcessClassId), retract_flow(OriginElementType, ProcessClassId, DestinationElementType, DestinationElementId))");
			charonRules.add("(delete_experiment_flow(OriginElementType, OriginElementId, DestinationElementType, experiment(ExperimentId)) :- currentVersion(experiment(ExperimentId), CurrentVersionId), experimentRootProcess(ExperimentId, CurrentVersionId, ProcessInstanceId), processInstanceType(ProcessInstanceId, ProcessClassId), retract_flow(OriginElementType, OriginElementId, DestinationElementType, ProcessClassId))");

			//Synchronism
			charonRules.add("(create_synchronism(SynchronismId) :- assertz(synchronism(SynchronismId)))");
			charonRules.add("(create_invisible_synchronism(SynchronismId) :- assertz(synchronism(SynchronismId)), assertz(invisible(synchronism(SynchronismId))))");
			
			//Initial
			charonRules.add("(create_initial(InitialId) :- assertz(initial(InitialId)))");
			
			//Final
			charonRules.add("(create_final(FinalId) :- assertz(final(FinalId)))");
			
			//Artifact
			charonRules.add("(create_artifact(ArtifactId) :- assertz(artifact(ArtifactId)))");
			
			//Port
			charonRules.add("(create_port(PortId, PortType, PortName, PortDataType) :- assertz(port(PortId)), assertz(portType(PortId, PortType)), assertz(portName(PortId, PortName)), assertz(portDataType(PortId, PortDataType)))");
			
			//Process Instance
			charonRules.add("(create_processInstance(ProcessInstanceId, ProcessClassId) :- assertz(processInstance(ProcessInstanceId)), assertz(processInstanceType(ProcessInstanceId, ProcessClassId)))");
			charonRules.add("(set_SWFMSProcess(SWFMSId, ProcessInstanceId) :- assertz(swfmsProcess(ProcessInstanceId, SWFMSId)))");
			charonRules.add("(set_artifactProcessPort(ProcessInstanceId, PortId, ArtifactId) :- assertz(artifactProcessPort(ArtifactId, ProcessInstanceId, PortId)))");
			
			//Activity Instance
			charonRules.add("(create_activityInstance(ActivityInstanceId, ActivityClassId, ActivityInstanceName) :- assertz(activityInstance(ActivityInstanceId)), assertz(activityInstanceType(ActivityInstanceId, ActivityClassId)), assertz(activityInstanceName(ActivityInstanceId, ActivityInstanceName)))");
			charonRules.add("(set_artifactActivityPort(ActivityInstanceId, PortId, ArtifactId) :- assertz(artifactActivityPort(ArtifactId, ActivityInstanceId, PortId)))");
			
			// Execution
			charonRules.add("(assertz_executing(ElementType, ElementId, P, T, Performers) :- createElement(ElementId, ElementType, Element), assertz(executing(Element, P, T, Performers)))");
			charonRules.add("(assertz_executed(ElementType, ElementId, P, Ti, Tf, Performers) :- createElement(ElementId, ElementType, Element), assertz(executed(Element, P, Ti, Tf, Performers)))");
			charonRules.add("(assertz_option_selected(OptionId, OptionName) :- assertz(option_selected(OptionId, OptionName)))");

			charonRules.add("(assertz_artifactValue(ArtifactId, ArtifactValue, P) :- assertz(artifactValue(ArtifactId, ArtifactValue, P)))");
			charonRules.add("(assertz_artifactValueLocation(ArtifactId, HostURL, HostLocalPath, P) :- assertz(artifactValueLocation(ArtifactId, HostURL, HostLocalPath, P)))");

		}
		

		
		this.addClauses(charonRules);

		
	}

	private void loadFromDataBase() throws Exception {
		// TODO Auto-generated method stub
		
		MysqlConnector session = new MysqlConnector(databaseLocation, user, password);
		session.connect();
		
		
		//Load id
		
		ResultSet result = session.query("SELECT * FROM ID_CONTROL");
		
		while(result.next()){
			String lastId = result.getString(1);
			IDGenerator.setLastGeneratedID(lastId);
		}

		
		//Load activities
		
		result = session.query("SELECT * FROM ACTIVITY");
		
		while(result.next()){
			String activityId = result.getString(1);
			String activityName = result.getString(2);
			String activityType = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(activity('"+activityId+"')), assertz(activityName('"+activityId+"', '"+activityName+"')), assertz(activityType('"+activityId+"', '"+activityType+"')).");
		}
		
		//Load processes
		
		result = session.query("SELECT * FROM PROCESS");
		
		while(result.next()){
			String processId = result.getString(1);
			String processName = result.getString(2);
			String processType = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(process('"+processId+"')), assertz(processName('"+processId+"', '"+processName+"')), assertz(processType('"+processId+"', '"+processType+"')).");
		}
		
		//Load synchronism points
		
		result = session.query("SELECT * FROM SYNCHRONISM");
		
		while(result.next()){
			String synchronismId = result.getString(1);
			boolean visible = result.getBoolean(2);
			
			inferenceMachine.isSolvable("assertz(synchronism('"+synchronismId+"')).");
			
			if(!visible){
				inferenceMachine.isSolvable("assertz(invisible(synchronism('"+synchronismId+"'))).");
			}
		}
		
		//Load initial points
		
		result = session.query("SELECT * FROM INITIAL");
		
		while(result.next()){
			String initialId = result.getString(1);
			
			inferenceMachine.isSolvable("assertz(initial('"+initialId+"')).");
		}
		
		//Load final points
		
		result = session.query("SELECT * FROM FINAL");
		
		while(result.next()){
			String finalId = result.getString(1);
			
			inferenceMachine.isSolvable("assertz(final('"+finalId+"')).");
		}
		
		//Load decision points
		
		result = session.query("SELECT * FROM DECISION");
		
		while(result.next()){
			String decisionId = result.getString(1);
			String decisionName = result.getString(2);
			
			inferenceMachine.isSolvable("assertz(decision('"+decisionId+"')), assertz(decisionName('"+decisionId+"', '"+decisionName+"')).");
		}
		
		//Load options
		
		result = session.query("SELECT * FROM OPTION_");
		
		while(result.next()){
			String optionId = result.getString(1);
			String optionName = result.getString(2);
			
			ResultSet result2 = session.query("SELECT id_destination_element FROM FLOW WHERE id_origin_element ='"+ optionId+"' AND id_origin_type ='"+CharonUtil.OPTION+"'");
			
			while(result2.next()){
				String originElementId = result2.getString(1);
				String toElement = "";
				inferenceMachine.isSolvable("assertz(option('"+optionId+"', '"+optionName+"', '"+CharonUtil.createElement(CharonUtil.OPTION, originElementId)+"')).");
			}
			
		}
		
		//Load selected options
		
		result = session.query("SELECT * FROM OPTION_SELECTED");
		
		while(result.next()){
			String id_option = result.getString(1);
			String name = result.getString(2);
			String path = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(option_selected('"+id_option+"', '"+name+"', '"+path+"')).");
		}
		
		//Load experiments
		
		result = session.query("SELECT * FROM EXPERIMENT");
		
		while(result.next()){
			String experimentId = result.getString(1);
			String experimentName = result.getString(2);
			
			inferenceMachine.isSolvable("assertz(experiment('"+experimentId+"')), assertz(experimentName('"+experimentId+"', '"+experimentName+"')).");
		}
		
		//Load experiment version
		
		result = session.query("SELECT * FROM EXPERIMENT_VERSION");
		
		while(result.next()){
			String experimentId = result.getString(1);
			String versionId = result.getString(2);
			String previousVersionId = result.getString(3);
			String root_process = result.getString(4);
			
			inferenceMachine.isSolvable("assertz(version(experiment('"+experimentId+"'), "+versionId+", "+previousVersionId+")).");
			if(root_process!=null)
				inferenceMachine.isSolvable("assertz(experimentRootProcess('"+experimentId+"', "+versionId+", '"+root_process+"')).");
			
		}
		
		result = session.query("SELECT exp1.experiment, exp1.version FROM EXPERIMENT_VERSION exp1 where not exists(select * from EXPERIMENT_VERSION exp2 where exp2.experiment = exp1.experiment AND exp2.previous_version = exp1.version)");
		
		if(result.next()){
			String experimentId = result.getString(1);
			String versionId = result.getString(2);
			
			inferenceMachine.isSolvable("assertz(currentVersion(experiment('"+experimentId+"'), "+versionId+")).");
		}
		
		//Load experiment instances
		
		result = session.query("SELECT * FROM EXPERIMENT_INSTANCE");
		
		while(result.next()){
			String experimentInstanceId = result.getString(1);
			String experimentVersionId = result.getString(2);
			String experimentId = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(experimentInstance('"+experimentInstanceId+"', "+experimentVersionId+", '"+experimentId+"')).");
		}
		
		//Load SWFMS
		
		result = session.query("SELECT * FROM SWFMS");
		
		while(result.next()){
			String swfmsId = result.getString(1);
			String swfmsName = result.getString(2);
			String swfmsHost = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(swfms('"+swfmsId+"')), assertz(swfmsName('"+swfmsId+"', '"+swfmsName+"')), assertz(swfmsHost('"+swfmsId+"', '"+swfmsHost+"')).");
		}
		
		//Load process instance
		
		result = session.query("SELECT * FROM PROCESS_INSTANCE");
		
		while(result.next()){
			String instanceId = result.getString(1);
			String processId = result.getString(2);
			String instanceName = result.getString(3);
			String swfmsId = result.getString(4);
			
			inferenceMachine.isSolvable("assertz(processInstance('"+instanceId+"')), assertz(processInstanceType('"+instanceId+"', '"+processId+"')), assertz(swfmsProcess('"+instanceId+"', '"+swfmsId+"')), assertz(processInstanceName('"+instanceId+"', '"+instanceName+"')).");
		}
		
		//Load activity instance
		
		result = session.query("SELECT * FROM ACTIVITY_INSTANCE");
		
		while(result.next()){
			String instanceId = result.getString(1);
			String activityId = result.getString(2);
			String instanceName = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(activityInstance('"+instanceId+"')), assertz(activityInstanceType('"+instanceId+"', '"+activityId+"')), assertz(activityInstanceName('"+instanceId+"', '"+instanceName+"')).");
		}
		
		//Load flows
		
		result = session.query("SELECT * FROM FLOW WHERE type_origin_element <> '"+CharonUtil.OPTION+"'");
		
		while(result.next()){
			String originElementId = result.getString(1);
			String originElementType = result.getString(2);
			String destinationElementId = result.getString(3);
			String destinationElementType = result.getString(4);
			
			inferenceMachine.isSolvable("assertz(processFlow("+CharonUtil.createElement(originElementType, originElementId)+", "+CharonUtil.createElement(destinationElementType, destinationElementId)+")).");
		}
		
		//Load artifacts
		
		result = session.query("SELECT * FROM ARTIFACT");
		
		while(result.next()){
			String artifactId = result.getString(1);
			
			inferenceMachine.isSolvable("assertz(artifact('"+artifactId+"')).");
		}
		
		//Load ports
		
		result = session.query("SELECT * FROM PORT");
		
		while(result.next()){
			String portId = result.getString(1);
			String portType = result.getString(2);
			String portName = result.getString(3);
			String portDataType = result.getString(4);
						
			inferenceMachine.isSolvable("assertz(port('"+portId+"')), assertz(portType('"+portId+"', '"+portType+"')), assertz(portName('"+portId+"', '"+portName+"')), assertz(portDataType('"+portId+"', '"+portDataType+"')).");
		}
		
		//Load activity_port
		
		result = session.query("SELECT * FROM ACTIVITY_PORT");
				
		while(result.next()){
			String activityId = result.getString(1);
			String portId = result.getString(2);
				
			inferenceMachine.isSolvable("assertz(activityPort('"+activityId+"', '"+portId+"')).");
		}
		
		//Load process_port
		
		result = session.query("SELECT * FROM PROCESS_PORT");
				
		while(result.next()){
			String processId = result.getString(1);
			String portId = result.getString(2);
				
			inferenceMachine.isSolvable("assertz(processPort('"+processId+"', '"+portId+"')).");
		}

		
		//Load  ARTIFACT_PORT_ACTIVITY_INSTANCE
		
		result = session.query("SELECT * FROM ARTIFACT_PORT_ACTIVITY_INSTANCE");
		
		while(result.next()){
			String activityInstanceId = result.getString(1);
			String artifactId = result.getString(2);
			String portId = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(artifactActivityPort('"+artifactId+"', '"+activityInstanceId+"', '"+portId+"')).");
		}
		
		//Load ARTIFACT_PORT_PROCESS_INSTANCE
		
		result = session.query("SELECT * FROM ARTIFACT_PORT_PROCESS_INSTANCE");
		
		while(result.next()){
			String processInstanceId = result.getString(1);
			String artifactId = result.getString(2);
			String portId = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(artifactProcessPort('"+artifactId+"', '"+processInstanceId+"', '"+portId+"')).");
		}
		
		//Load EXECUTION_STATUS
		
		result = session.query("SELECT * FROM EXECUTION_STATUS");
		
		while(result.next()){
			String elementId = result.getString(1);
			String elementType = result.getString(2);
			String status = result.getString(3);
			String startTime = result.getString(4);
			String endTime = result.getString(5);
			String path = transformList(result.getString(6));
			String performers = transformList(result.getString(7));
			
			
			if(status.equals(CharonUtil.EXECUTING_STATUS))
				inferenceMachine.isSolvable("assertz(executing("+CharonUtil.createElement(elementType, elementId)+", ["+path+"], '"+startTime+"', [])).");
			else
			if(status.equals(CharonUtil.EXECUTED_STATUS))
				inferenceMachine.isSolvable("assertz(executed("+CharonUtil.createElement(elementType, elementId)+", ["+path+"], '"+startTime+"', '"+endTime+"', [])).");
		}
		
		//Load EXECUTION_STATUS
		
		result = session.query("SELECT * FROM ARTIFACT_VALUE");
		
		while(result.next()){
			String artifactId = result.getString(1);
			String artifactValue = result.getString(2);
			String path = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(artifactValue('"+artifactId+"', '"+artifactValue+"', ["+path+"])).");
			
		}
		
		//Load EXECUTION_STATUS_LOCATION
		
		result = session.query("SELECT * FROM ARTIFACT_VALUE_LOCATION");
		
		while(result.next()){
			String artifactId = result.getString(1);
			String hostURL = result.getString(2);
			String hostLocalPath = result.getString(3);
			String path = result.getString(4);
			
			inferenceMachine.isSolvable("assertz(artifactValueLocation('"+artifactId+"', '"+hostURL+"', '"+hostLocalPath+"', ["+path+"])).");
			
		}
		
	}
	
	public String transformList(String list){
		
		String[] elements = list.split(",");
		StringBuffer transformedList = new StringBuffer("");

		if(elements.length>1){
			for(int i=0; i<elements.length; i++){
				String temp = elements[i].trim();
				transformedList.append("'"+temp+"',");
			}
		}

		
		if(transformedList.length()>1){
			return transformedList.substring(0, transformedList.length()-1);
		}
		else{
			String temp = list.trim();
			if(temp.length()>1)
				return "'"+temp+"'";
			else
				return temp;
		}
	}	

	/**
	 * Add a process into the knowledge base
	 *
	 * @param spemPackage Package containing all elements of the process.
	 */
	public void add(SpemPackage spemPackage) throws CharonException {
		Collection<String> processClauses = AgentManager.getInstance().getAgent(MappingAgent.class).map(spemPackage);
		inferenceMachine.addClauses(processClauses);
	}	

	/**
	 * Connects an agent to this knowledge base
	 */
	public synchronized void connect(Agent agent) {
		while (this.agent != null)
			try {
				// Waits the other agent to finish its work
				wait();
			} catch (InterruptedException e) {
				Logger.global.log(Level.WARNING, "Could not synchronize the connections of agents to the knowledge base", e);
				return;
			}

		this.agent = agent;
		
		// Adds the agent's rules into the knowledge base
		inferenceMachine.addClauses(agent.getRules());
	}

	/**
	 * Disconnect the current connected agent from this knowledge base
	 */
	public synchronized void disconnect() {
		if (agent != null) {
			// Removes the agent's rules from the knowledge base
			inferenceMachine.removeClauses(agent.getRules());
			agent = null;
		}

		// Lets other agents work
		notify();
	}

	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#getAllSolutions(java.lang.String)
	 */
	public List<Map<String, Object>> getAllSolutions(String goal) {
		return inferenceMachine.getAllSolutions(goal);
	}

	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#isSolvable(java.lang.String)
	 */
	public boolean isSolvable(String goal) {
		return inferenceMachine.isSolvable(goal);
	}

	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#addClauses(java.util.Collection)
	 */
	public void addClauses(Collection<String> clauses) {
		inferenceMachine.addClauses(clauses);
	}
	
	/**
	 * @see br.ufrj.cos.lens.odyssey.tools.inference.InferenceMachine#removeClauses(java.util.Collection)
	 */
	public void removeClauses(Collection<String> clauses) {
		inferenceMachine.removeClauses(clauses);
	}

	/**
	 * Saves the content of the knowledge base
	 * (useful for debug)
	 */
	public void save() throws CharonException {
		

		ArrayList<String> charonRules = new ArrayList<String>();
		
		
		charonRules.add("(createElement(ElementId, '"+CharonUtil.ACTIVITY+"', activity(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.PROCESS+"', process(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.SYNCHRONISM+"', synchronism(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.DECISION+"', decision(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.INITIAL+"', initial(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.FINAL+"', final(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.ARTIFACT+"', product(ElementId)))");

		
		charonRules.add("(nextVersionId(VersionId) :- class('br.ufrj.cos.lens.odyssey.tools.charon.util.IDGenerator') <- generateID returns VersionId)");
		charonRules.add("(setCurrentVersion(Element, VersionId, PreviousVersionId) :- retract(currentVersion(Element, PreviousVersionId)), assertz(currentVersion(Element, VersionId)))");
		charonRules.add("(associateElementToExperimentLastVersion(ExperimentId, Element) :- currentVersion(experiment(ExperimentId), CurrentVersionId), assertz(experimentVersionDimension(ExperimentId, CurrentVersionId, swfms(SWFMS_Id))))");

		
        // EXPERIMENT
        charonRules.add("(create_experiment(ExperimentId) :- assertz(experiment(ExperimentId)), nextVersionId(VersionId), assertz(version(experiment(ExperimentId), VersionId, '0')), assertz(currentVersion(experiment(ExperimentId), VersionId)))");
        charonRules.add("(create_experimentNewVersion(ExperimentId) :- nextVersionId(VersionId), currentVersion(experiment(ExperimentId), PreviousVersionId), assertz(version(experiment(ExperimentId), VersionId, PreviousVersionId)), setCurrentVersion(process(ProcessClassId), VersionId, PreviousVersionId))");
        charonRules.add("(set_experimentName(ExperimentId, ExperimentName) :- currentVersion(experiment(ExperimentId), CurrentVersionId), assertz(experimentName(CurrentVersionId, ExperimentName)))");
        charonRules.add("(set_experimentRootProcess(ExperimentId, ExperimentRootProcessId) :- currentVersion(experiment(ExperimentId), CurrentVersionId), assertz(experimentRootProcess(CurrentVersionId, ExperimentRootProcessId)))");
		charonRules.add("(create_experimentInstance(ExperimentInstanceId, ExperimentVersionId) :- assertz(experimentInstance(ExperimentInstanceId, ExperimentVersionId)))");
        
        // PROCESS
        charonRules.add("(create_process(ProcessClassId, ProcessClassName, ProcessClassType) :- assertz(process(ProcessClassId)), assertz(processName(ProcessClassId, ProcessClassName)), assertz(processType(ProcessClassId, ProcessClassType)))");
        charonRules.add("(add_processPort(ProcessClassId, PortId) :- assertz(processPort(ProcessClassId, PortId)))");
       
		// ACTIVITY
        charonRules.add("(create_activity(ActivityClassId, ActivityClassName, ActivityClassType) :- assertz(activity(ActivityClassId)), assertz(activityName(ActivityClassId, ActivityClassName)), assertz(activityType(ActivityClassId, ActivityClassType)))");
        charonRules.add("(add_activityPort(ActivityClassId, PortId) :- assertz(activityPort(ActivityClassId, PortId)))");
		
		// SWFMS
		charonRules.add("(create_SWFMS(SWFMS_Id, SWFMS_Name, SWFMS_Host) :- assertz(swfms(SWFMS_Id)), assertz(swfmsName(SWFMS_Id, SWFMS_Name)), assertz(swfmsHost(SWFMS_Id, SWFMS_Host)))");
		
		//Decision and Option
		charonRules.add("(create_decision(DecisionId, DecisionName) :- assertz(decisionName(DecisionId, DecisionName)))");
		charonRules.add("(add_decisionOption(OptionId, OptionName, ToElementType, ToElementId) :- createElement(ToElementId, ToElementType, ToElement), assertz(option(OptionId, OptionName, ToElement)))");
		
		//Defining flow
		charonRules.add("(create_flow(OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), assertz(processFlow(OriginElement, DestinationElement)))");
		charonRules.add("(delete_flow(OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), retract(processFlow(OriginElement, DestinationElement)))");

		//Synchronism
		charonRules.add("(create_synchronism(SynchronismId) :- assertz(synchronism(SynchronismId)))");
		
		//Initial
		charonRules.add("(create_initial(InitialId) :- assertz(initial(InitialId)))");
		
		//Final
		charonRules.add("(create_final(FinalId) :- assertz(final(FinalId)))");
		
		//Artifact
		charonRules.add("(create_artifact(ArtifactId) :- assertz(artifact(ArtifactId)))");
		
		//Port
		charonRules.add("(create_port(PortId, PortType, PortName, PortDataType) :- assertz(port(PortId)), assertz(portType(PortId, PortType)), assertz(portName(PortId, PortName)), assertz(portDataType(PortId, PortDataType)))");
		
		//Process Instance
		charonRules.add("(create_processInstance(ProcessInstanceId, ProcessClassId) :- assertz(processInstance(ProcessInstanceId)), assertz(processInstanceType(ProcessInstanceId, ProcessClassId)))");
		charonRules.add("(set_SWFMSProcess(SWFMSId, ProcessInstanceId) :- assertz(swfmsProcess(ProcessInstanceId, SWFMSId)))");
		charonRules.add("(set_artifactProcessPort(ProcessInstanceId, PortId, ArtifactId) :- assertz(artifactProcessPort(ArtifactId, ProcessInstanceId, PortId)))");
		
		//Activity Instance
		charonRules.add("(create_activityInstance(ActivityInstanceId, ActivityClassId) :- assertz(activityInstance(ActivityInstanceId)), assertz(activityInstanceType(ActivityInstanceId, ActivityClassId)))");
		charonRules.add("(set_artifactActivityPort(ActivityInstanceId, PortId, ArtifactId) :- assertz(artifactActivityPort(ArtifactId, ActivityInstanceId, PortId)))");
		
		// Execution
		charonRules.add("(assertz_executing(ElementType, ElementId, P, T, Performers) :- createElement(ElementId, ElementType, Element), assertz(executing(Element, P, T, Performers)))");
		charonRules.add("(assertz_executed(ElementType, ElementId, P, Ti, Tf, Performers) :- createElement(ElementId, ElementType, Element), assertz(executed(Element, P, Ti, Tf, Performers)))");
		charonRules.add("(assertz_option_selected(OptionId, A, P, T) :- assertz(option_selected(OptionId, A, P, T)))");

		this.removeClauses(charonRules);
		
		try {
			FileWriter fileWriter = new FileWriter(repository);
			fileWriter.write(inferenceMachine.getContent());
			fileWriter.close();
		} catch (IOException e) {
			throw new CharonException("Could not save the knowledge base", e);
		}
	}
	
	/**
	 * Cleans the content of the knowledge base
	 */
	public void clean() throws CharonException {
		inferenceMachine = new InferenceMachine();
	}

	public boolean isStoredInDatabase() {
		// TODO Auto-generated method stub
		return false;
	}
}