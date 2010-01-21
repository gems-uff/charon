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
		
		charonRules.add("(createElement(ElementId, '"+CharonUtil.ACTIVITY+"', activity(ElementId)):- currentVersion(activityInstance(ElementId), VersionId))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.PROCESS+"', process(VersionId)):- currentVersion(processInstance(ElementId), VersionId))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.SYNCHRONISM+"', synchronism(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.DECISION+"', decision(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.INITIAL+"', initial(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.FINAL+"', final(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.ARTIFACT+"', product(ElementId)))");
		
		charonRules.add("(nextVersionId(VersionId) :- class('br.ufrj.cos.lens.odyssey.tools.charon.util.IDGenerator') <- generateID returns VersionId)");
		
		if(IsUsingDatabase){
			charonRules.add("(concat(Str1, Str2, Str3) :- java_object('java.lang.StringBuffer', [Str1], Temp), Temp <- append(Str2), java_object('java.lang.String', [Temp], Str3))");
			charonRules.add("(listToString2([], ''))");
			charonRules.add("(listToString([], ''))");
			charonRules.add("(listToString2([Elements|Element], StringList) :- listToString2(Elements, StringList2), concat(StringList2, Element, StringList3), concat(StringList3, ', ', StringList))");
			charonRules.add("(listToString([Elements|Element], StringList) :- listToString2(Elements, StringList2), concat(StringList2, Element, StringList))");
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
			charonRules.add("(init_dbase(DBase, Username, Password, Connection) :- java_object('br.ufrj.cos.reuse.provmanager.util.db.mysql.MysqlConnector', ['provmanager','root',''], Connection), Connection <- connect('1'))");
			charonRules.add("(exec(Connection, Query) :- Connection <- exec(Query))");
			charonRules.add("(assertz_experiment(ExperimentId, ExperimentName) :- assertz(experiment(ExperimentId)), assertz(experimentName(ExperimentId, ExperimentName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('EXPERIMENT', ['id', 'name'], [ExperimentId, ExperimentName], Query), exec(Conn, Query))");
			charonRules.add("(assertz_experimentRootProcess(ExperimentId, ProcessClassId) :- assertz(experimentRootProcess(ExperimentId, ProcessClassId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createUpdateCommand('EXPERIMENT', ['root_process'], [ProcessClassId], ['id'], [ExperimentId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_process(ProcessClassId, ProcessName, ProcessType) :- assertz(process(ProcessClassId)), assertz(processType(ProcessClassId, ProcessType)), assertz(processName(ProcessClassId, ProcessName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PROCESS', ['id', 'name', 'type_'], [ProcessClassId, ProcessName, ProcessType], Query), exec(Conn, Query))");
			charonRules.add("(assertz_activity(ActivityClassId, ActivityClassName, ActivityClassType) :- assertz(activity(ActivityClassId)), assertz(activityType(ActivityClassId, ActivityClassType)), assertz(activityName(ActivityClassId, ActivityClassName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ACTIVITY', ['id', 'name', 'type_'], [ActivityClassId, ActivityClassName, ActivityClassType], Query), exec(Conn, Query))");
			charonRules.add("(assertz_SWFMS(SWFMS_Id, SWFMS_Name, SWFMS_Host) :- assertz(swfms(SWFMS_Id)), assertz(swfmsName(SWFMS_Id, SWFMS_Name)), assertz(swfmsHost(SWFMS_Id, SWFMS_Host)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('SWFMS', ['id', 'name', 'host'], [SWFMS_Id, SWFMS_Name, SWFMS_Host], Query), exec(Conn, Query))");
			charonRules.add("(assertz_parameter(ParameterId, ParameterName, ParameterType, ParameterValue) :- assertz(parameter(ParameterId)), assertz(parameterName(ParameterId, ParameterName)), assertz(parameterType(ParameterId, ParameterType)), assertz(parameterValue(ParameterId, ParameterValue)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PARAMETER', ['id', 'name', 'type_', 'value_'], [ParameterId, ParameterName, ParameterType, ParameterValue], Query), exec(Conn, Query))");
			charonRules.add("(assertz_synchronism(SynchronismId) :- assertz(synchronism(SynchronismId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('SYNCHRONISM', ['id'], [SynchronismId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_option(OptionId, OptionName, ToElementType, ToElementId) :- createElement(ToElementId, ToElementType, ToElement), assertz(option(OptionId, OptionName)), assertz(optionFlow(OptionId, ToElement)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('OPTION', ['id', 'name'], [OptionId, OptionName], Query), exec(Conn, Query), createInsertCommand('FLOW', ['id_origin_element', 'type_origin_element', 'id_destination_element', 'type_destination_element'], [OptionId, '"+CharonUtil.OPTION+"', ToElementId, ToElementType], Query2), exec(Conn, Query2))");
			charonRules.add("(assertz_decision(DecisionId, DecisionName) :- assertz(decision(DecisionId)), assertz(decisionName(DecisionId, DecisionName)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('DECISION', ['id', 'name'], [DecisionId, DecisionName], Query), exec(Conn, Query))");
			charonRules.add("(assertz_initial(InitialId) :- assertz(initial(InitialId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('INITIAL', ['id'], [InitialId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_final(FinalId) :- assertz(final(FinalId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('FINAL', ['id'], [FinalId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_processInstance(ProcessInstanceId, ProcessClassId) :- assertz(type(ProcessInstanceId, ProcessClassId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PROCESS_INSTANCE', ['instance_id', 'process_id'], [ProcessInstanceId, ProcessClassId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_activityInstance(ActivityInstanceId, ActivityClassId) :- assertz(type(ActivityInstanceId, ActivityClassId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ACTIVITY_INSTANCE', ['instance_id', 'activity_id'], [ActivityInstanceId, ActivityClassId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_activityParameter(ActivityInstanceId, ParameterId) :- assertz(activityParameter(ActivityInstanceId, ParameterId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ACTIVITY_PARAMETER', ['id_instance_activity', 'id_parameter'], [ActivityInstanceId, ParameterId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_processParameter(ProcessInstanceId, ParameterId) :- assertz(processParameter(ProcessInstanceId, ParameterId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PROCESS_PARAMETER', ['id_instance_process', 'id_parameter'], [ProcessInstanceId, ParameterId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_flow(OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), assertz(flow(OriginElement, DestinationElement)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('FLOW', ['id_origin_element', 'type_origin_element', 'id_destination_element', 'type_destination_element'], [OriginElementId, OriginElementType, DestinationElementId, DestinationElementType], Query), exec(Conn, Query))");
			charonRules.add("(retract_flow(OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), retract(flow(OriginElement, DestinationElement)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createDeleteCommand('FLOW', ['id_origin_element', 'type_origin_element', 'id_destination_element', 'type_destination_element'], [OriginElementId, OriginElementType, DestinationElementId, DestinationElementType], Query), exec(Conn, Query))");
			charonRules.add("(assertz_artifact(ArtifactId) :- assertz(artifact(ArtifactId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ARTIFACT', ['id'], [ArtifactId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_port(PortId, PortType, PortName, PortDataType) :- assertz(port(PortId)), assertz(portType(PortId, PortType)), assertz(portName(PortId, PortName)), assertz(portDataType(PortId, PortDataType)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PORT', ['id', 'type', 'name', 'data_type'], [PortId, PortType, PortName, PortDataType], Query), exec(Conn, Query))");
			charonRules.add("(assertz_activityPort(ActivityId, PortId) :- assertz(activityPort(ActivityId, PortId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ACTIVITY_PORT', ['activity', 'port'], [ActivityId, PortId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_processPort(ProcessId, PortId) :- assertz(processPort(ProcessId, PortId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('PROCESS_PORT', ['process', 'port'], [ProcessId, PortId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_artifactActivityPort(ActivityInstanceId, PortId, ArtifactId) :- assertz(artifactActivityPort(ArtifactId, ActivityInstanceId, PortId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ARTIFACT_PORT_ACTIVITY_INSTANCE', ['activity_instance', 'artifact', 'port'], [ActivityInstanceId, ArtifactId, PortId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_artifactProcessPort(ProcessInstanceId, PortId, ArtifactId) :- assertz(artifactProcessPort(ArtifactId, ProcessInstanceId, PortId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('ARTIFACT_PORT_PROCESS_INSTANCE', ['process_instance', 'artifact', 'port'], [ProcessInstanceId, ArtifactId, PortId], Query), exec(Conn, Query))");
			charonRules.add("(assertz_SWFMSProcess(SWFMSId, ProcessInstanceId) :- assertz(swfmsProcess(SWFMSId, ProcessInstanceId)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createUpdateCommand('PROCESS_INSTANCE', ['swfms_id'], [SWFMSId], ['instance_id'], [ProcessInstanceId], Query), exec(Conn, Query))");
			
			charonRules.add("(assertz_executing(ElementType, ElementId, P, T, Performers) :- createElement(ElementId, ElementType, Element), listToString(P, PathList), listToString(Performers, PerformersList), assertz(executing(Element, P, T, Performers)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('EXECUTION_STATUS', ['id_element', 'type_element', 'status', 'start_time', 'path', 'performers'], [ElementId, ElementType, '"+CharonUtil.EXECUTING_STATUS+"', T, PathList, PerformersList], Query), exec(Conn, Query))");
			charonRules.add("(assertz_executed(ElementType, ElementId, P, Ti, Tf, Performers) :- createElement(ElementId, ElementType, Element), assertz(executed(Element, P, Ti, Tf, Performers)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createUpdateCommand('EXECUTION_STATUS', ['status', 'end_time'], ['"+CharonUtil.EXECUTED_STATUS+"', Tf], ['id_element', 'type_element'], [ElementId, ElementType], Query), exec(Conn, Query))");
			charonRules.add("(assertz_option_selected(OptionId, A, P, T) :- assertz(option_selected(OptionId, A, P, T)), init_dbase('"+databaseLocation+"','"+user+"','"+password+"',Conn), createInsertCommand('OPTION_SELECTED', ['id_option', 'name', 'path', 'time'], [OptionId, A, P, T], Query), exec(Conn, Query))");

		}
		else{
			
			// EXPERIMENT
			charonRules.add("(create_experiment(ExperimentId, ExperimentName) :- assertz(experiment(ExperimentId)), nextVersionId(VersionId), assertz(version(experiment(ExperimentId), VersionId, '0')), assertz(currentVersion(experiment(ExperimentId), VersionId)), assertz(experimentName(VersionId, ExperimentName)))");
			charonRules.add("(update_experimentName(ExperimentId, ExperimentName) :- nextVersionId(VersionId), currentVersion(experiment(Id), PreviousVersion), assertz(version(experiment(ExperimentId), VersionId, PreviousVersion)), assertz(experimentName(VersionId, ExperimentName)))");
//			charonRules.add("(delete_experiment(ExperimentId) :- assertz(deleted(experiment(ExperimentId)))");
			charonRules.add("(assertz_experimentRootProcess(ExperimentId, ProcessClassId) :- assertz(experimentRootProcess(ExperimentId, ProcessClassId)))");
			
			// PROCESS
			charonRules.add("(create_process(ProcessClassId, ProcessClassName, ProcessClassType, ProcessClassPorts) :- assertz(process(ProcessClassId)), nextVersionId(VersionId), assertz(version(process(ProcessClassId), VersionId, '0')), assertz(currentVersion(process(ProcessClassId), VersionId)), assertz(processType(VersionId, ProcessClassType)), assertz(processName(VersionId, ProcessClassName)), associate_processPort(VersionId, ProcessClassPorts))");
			charonRules.add("(update_process(ProcessClassId, ProcessClassName, ProcessClassType, ProcessClassPorts) :- nextVersionId(VersionId), currentVersion(process(ProcessClassId), PreviousVersion), assertz(version(process(ProcessClassId), VersionId, PreviousVersion)), assertz(processType(VersionId, ProcessClassType)), assertz(processName(VersionId, ProcessClassName)), associate_processPort(VersionId, ProcessClassPorts))");
			charonRules.add("(update_processName(ProcessClassId, ProcessClassName) :- currentVersion(process(ProcessClassId), currentVersion), processType(currentVersion, ProcessClassType), findall(E, processPort(currentVersion, E), Es), update_process(ProcessClassId, ProcessClassName, ProcessClassType, Es))");
			charonRules.add("(update_processType(ProcessClassId, ProcessClassType) :- currentVersion(process(ProcessClassId), currentVersion), processName(currentVersion, ProcessClassName), findall(E, processPort(currentVersion, E), Es), update_process(ProcessClassId, ProcessClassName, ProcessClassType, Es))");
			charonRules.add("(add_processPort(ProcessClassId, PortId) :- currentVersion(process(ProcessClassId), currentVersion), processName(currentVersion, ProcessClassName), processType(currentVersion, ProcessClassType), findall(E, processPort(currentVersion, E), Es), update_process(ProcessClassId, ProcessClassName, ProcessClassType, [PortId|Es]))");
			charonRules.add("(remove_processPort(ProcessClassId, PortId) :- currentVersion(process(ProcessClassId), currentVersion), processName(currentVersion, ProcessClassName), processType(currentVersion, ProcessClassType), retract(processPort(currentVersion, PortId)), findall(E, processPort(currentVersion, E), Es), assertz(processPort(currentVersion, PortId)), update_process(ProcessClassId, ProcessClassName, ProcessClassType, Es))");
			charonRules.add("(associate_processPort(ProcessClassVersionId, []))");
			charonRules.add("(associate_processPort(ProcessClassVersionId, [ProcessClassPort|ProcessClassPorts]) :- assertz(processPort(ProcessClassVersionId, ProcessClassPort)), associate_processPort(ProcessClassVersionId, ProcessClassPorts))");

			// ACTIVITY
			charonRules.add("(create_activity(ActivityClassId, ActivityClassName, ActivityClassType) :- assertz(activity(ActivityClassId)), nextVersionId(VersionId), assertz(version(activity(ActivityClassId), VersionId, '0')), assertz(currentVersion(activity(ActivityClassId), VersionId)), assertz(activityType(VersionId, ActivityClassType)), assertz(activityName(VersionId, ActivityClassName)))");
			charonRules.add("(update_activity(ActivityClassId, ActivityClassName, ActivityClassType) :- nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)), assertz(activityType(VersionId, ActivityClassType)), assertz(activityName(VersionId, ActivityClassName)))");
			charonRules.add("(update_activityName(ActivityClassId, ActivityClassName) :- nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)), assertz(activityName(VersionId, ActivityClassName)))");
			charonRules.add("(update_activityType(ActivityClassId, ActivityClassType) :- nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)), assertz(activityType(VersionId, ActivityClassType)))");
			charonRules.add("(add_activityPort(ActivityClassId, PortId) :- nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)), assertz(activityPort(VersionId, PortId)))");
			charonRules.add("(remove_activityPort(ActivityClassId, PortId) :- retract(activityPort(VersionId, PortId)), nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)))");
			
			// SWFMS
			charonRules.add("(create_SWFMS(SWFMS_Id, SWFMS_Name, SWFMS_Host) :- assertz(swfms(SWFMS_Id)), nextVersionId(VersionId), assertz(version(swfms(SWFMS_Id), VersionId, '0')), assertz(currentVersion(swfms(SWFMS_Id), VersionId)), assertz(swfmsName(VersionId, SWFMS_Name)), assertz(swfmsHost(VersionId, SWFMS_Host)))");
			charonRules.add("(update_SWFMS(SWFMS_Id, SWFMS_Name, SWFMS_Host) :- nextVersionId(VersionId), currentVersion(swfms(SWFMS_Id), PreviousVersion), assertz(version(swfms(SWFMS_Id), VersionId, PreviousVersion)), assertz(swfmsName(VersionId, SWFMS_Name)), assertz(swfmsHost(VersionId, SWFMS_Host)))");
			charonRules.add("(update_SWFMSName(SWFMS_Id, SWFMS_Name) :- nextVersionId(VersionId), currentVersion(swfms(SWFMS_Id), PreviousVersion), assertz(version(swfms(SWFMS_Id), VersionId, PreviousVersion)), assertz(swfmsName(VersionId, SWFMS_Name)))");
			charonRules.add("(update_SWFMSHost(SWFMS_Id, SWFMS_Host) :- nextVersionId(VersionId), currentVersion(swfms(SWFMS_Id), PreviousVersion), assertz(version(swfms(SWFMS_Id), VersionId, PreviousVersion)), assertz(swfmsHost(VersionId, SWFMS_Host)))");
			
			
//			//Parameter
//			charonRules.add("(create_parameter(ParameterId, ParameterName, ParameterType, ParameterValue) :- assertz(parameter(ParameterId)), nextVersionId(VersionId), assertz(version(parameter(ParameterId), VersionId, '0')), assertz(currentVersion(parameter(ParameterId), VersionId)), assertz(parameterName(VersionId, ParameterName)), assertz(parameterType(VersionId, ParameterType)), assertz(parameterValue(VersionId, ParameterValue)))");
//			charonRules.add("(update_parameter(ParameterId, ParameterName, ParameterType, ParameterValue) :- nextVersionId(VersionId), currentVersion(parameter(ParameterId), PreviousVersion), assertz(version(parameter(ParameterId), VersionId, PreviousVersion)), assertz(parameterName(VersionId, ParameterName)), assertz(parameterType(VersionId, ParameterType)), assertz(parameterValue(VersionId, ParameterValue)))");
//			charonRules.add("(update_parameterName(ParameterId, ParameterName) :- nextVersionId(VersionId), currentVersion(parameter(ParameterId), PreviousVersion), assertz(version(parameter(ParameterId), VersionId, PreviousVersion)), assertz(parameterName(VersionId, ParameterName)))");
//			charonRules.add("(update_parameterType(ParameterId, ParameterType) :- nextVersionId(VersionId), currentVersion(parameter(ParameterId), PreviousVersion), assertz(version(parameter(ParameterId), VersionId, PreviousVersion)), assertz(parameterType(VersionId, ParameterType)))");
//			charonRules.add("(update_parameterValue(ParameterId, ParameterValue) :- nextVersionId(VersionId), currentVersion(parameter(ParameterId), PreviousVersion), assertz(version(parameter(ParameterId), VersionId, PreviousVersion)), assertz(parameterValue(VersionId, ParameterValue)))");
			
			
			//Synchronism
			charonRules.add("(create_synchronism(SynchronismId) :- assertz(synchronism(SynchronismId)), nextVersionId(VersionId), assertz(version(synchronism(SynchronismId), VersionId, '0')), assertz(currentVersion(synchronism(SynchronismId), VersionId)))");
			
			//Decision and Option
			charonRules.add("(create_decision(DecisionId, DecisionName) :- assertz(decision(DecisionId)), currentVersion(decision(DecisionId), PreviousVersion), nextVersionId(VersionId), assertz(version(decision(DecisionId), VersionId, '0')), assertz(currentVersion(decision(DecisionId), VersionId)), assertz(decisionName(VersionId, DecisionName)))");
			charonRules.add("(add_decisionOption(DecisionId, OptionName, ToElementType, ToElementId) :- nextVersionId(VersionId), currentVersion(decision(DecisionId), PreviousVersion), assertz(version(decision(DecisionId), VersionId, PreviousVersion)), createElement(ToElementId, ToElementType, ToElement), assertz(option(VersionId, OptionName, ToElement)))");
			
			//Initial
			charonRules.add("(create_intial(InitialId) :- assertz(initial(InitialId)), nextVersionId(VersionId), assertz(version(initial(InitialId), VersionId, '0')), assertz(currentVersion(initial(InitialId), VersionId)))");
			
			//Final
			charonRules.add("(create_final(FinalId) :- assertz(final(FinalId)), nextVersionId(VersionId), assertz(version(final(FinalId), VersionId, '0')), assertz(currentVersion(final(FinalId), VersionId)))");
			
			//Process and Activity Instance
			charonRules.add("(assertz_processInstance(ProcessInstanceId, ProcessClassVersionId) :- assertz(processInstance(ProcessInstanceId)), nextVersionId(VersionId), assertz(version(processInstance(ProcessInstanceId), VersionId, '0')), assertz(currentVersion(processInstance(ProcessInstanceId), VersionId)), assertz(processInstanceType(ProcessInstanceId, ProcessClassVersionId)))");
			charonRules.add("(assertz_activityInstance(ActivityInstanceId, ActivityClassVersionId) :- assertz(activityInstance(ActivityInstanceId)), nextVersionId(VersionId), assertz(version(activityInstance(ActivityInstanceId), VersionId, '0')), assertz(currentVersion(activityInstance(ActivityInstanceId), VersionId)), assertz(activityInstanceType(ActivityInstanceId, ActivityClassVersionId)))");
			
			//Defining flow
			charonRules.add("(create_flow(ProcessClassId, OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- nextVersionId(VersionId), currentVersion(process(ProcessClassId), PreviousVersion), assertz(version(process(ProcessClassId), VersionId, PreviousVersion)), createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), assertz(processFlow(VersionId, OriginElement, DestinationElement)))");
			charonRules.add("(delete_flow(ProcessClassId, OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- nextVersionId(VersionId), currentVersion(process(ProcessClassId), PreviousVersion), assertz(version(process(ProcessClassId), VersionId, PreviousVersion)), createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), retract(processFlow(PreviousVersion, OriginElement, DestinationElement)))");			
						
			//Artifact
			charonRules.add("(create_artifact(ArtifactId) :- assertz(artifact(ArtifactId)), nextVersionId(VersionId), assertz(version(artifact(ArtifactId), VersionId, '0')), assertz(currentVersion(artifact(ArtifactId), VersionId)))");
			
			//Port
			charonRules.add("(create_port(PortId, PortType, PortName, PortDataType) :- assertz(port(PortId)), nextVersionId(VersionId), assertz(version(port(PortId), VersionId, '0')), assertz(currentVersion(port(PortId), VersionId)), assertz(portType(VersionId, PortType)), assertz(portName(VersionId, PortName)), assertz(portDataType(VersionId, PortDataType)))");
			charonRules.add("(update_port(PortId, PortType, PortName, PortDataType) :- nextVersionId(VersionId), currentVersion(port(PortId), PreviousVersion), assertz(version(port(PortId), VersionId, PreviousVersion)), assertz(portType(VersionId, PortType)), assertz(portName(VersionId, PortName)), assertz(portDataType(VersionId, PortDataType)))");
			charonRules.add("(update_portType(PortId, PortType) :- nextVersionId(VersionId), currentVersion(port(PortId), PreviousVersion), assertz(version(port(PortId), VersionId, PreviousVersion)), assertz(portType(VersionId, PortType)))");
			charonRules.add("(update_portName(PortId, PortName) :- nextVersionId(VersionId), currentVersion(port(PortId), PreviousVersion), assertz(version(port(PortId), VersionId, PreviousVersion)), assertz(portName(VersionId, PortName)))");
			charonRules.add("(update_portDataType(PortId, PortDataType) :- nextVersionId(VersionId), currentVersion(port(PortId), PreviousVersion), assertz(version(port(PortId), VersionId, PreviousVersion)), assertz(portDataType(VersionId, PortDataType)))");

			//Defining artifact to port
			charonRules.add("(assertz_artifactActivityPort(ActivityInstanceId, PortId, ArtifactId) :- nextVersionId(VersionId), currentVersion(activityInstance(ActivityInstanceId), PreviousVersion), assertz(version(activityInstance(ActivityInstanceId), VersionId, PreviousVersion)), assertz(artifactActivityPort(ArtifactId, VersionId, PortId)))");
			charonRules.add("(assertz_artifactProcessPort(ProcessInstanceId, PortId, ArtifactId) :- nextVersionId(VersionId), currentVersion(processInstance(ProcessInstanceId), PreviousVersion), assertz(version(processInstance(ProcessInstanceId), VersionId, PreviousVersion)), assertz(artifactProcessPort(ArtifactId, VersionId, PortId)))");
			
			
			//Defining what SWfMS that the process instance is deployed
			charonRules.add("(assertz_SWFMSProcess(SWFMSId, ProcessInstanceId) :- nextVersionId(VersionId), currentVersion(processInstance(ProcessInstanceId), PreviousVersion), assertz(version(processInstance(ProcessInstanceId), VersionId, PreviousVersion)), assertz(swfmsProcess(VersionId, SWFMSId)))");

			
			// Execução
			charonRules.add("(assertz_executing(ElementType, ElementId, P, T, Performers) :- createElement(ElementId, ElementType, Element), assertz(executing(Element, P, T, Performers)))");
			charonRules.add("(assertz_executed(ElementType, ElementId, P, Ti, Tf, Performers) :- createElement(ElementId, ElementType, Element), assertz(executed(Element, P, Ti, Tf, Performers)))");
			charonRules.add("(assertz_option_selected(OptionId, A, P, T) :- assertz(option_selected(OptionId, A, P, T)))");

			
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
			
			inferenceMachine.isSolvable("assertz(synchronism('"+synchronismId+"')).");
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
			
			inferenceMachine.isSolvable("assertz(option('"+optionId+"')), assertz(optionName('"+optionId+"', '"+optionName+"')).");
		}
		
		//Load selected options
		
		result = session.query("SELECT * FROM OPTION_SELECTED");
		
		while(result.next()){
			String id_option = result.getString(1);
			String name = result.getString(2);
			String path = result.getString(3);
			String time = result.getInt(4)+"";
			
			inferenceMachine.isSolvable("assertz(option_selected('"+id_option+"', '"+name+"', '"+path+"', '"+time+"')).");
		}
		
//		//Load parameters
//		
//		result = session.query("SELECT * FROM PARAMETER");
//		
//		while(result.next()){
//			String parameterId = result.getString(1);
//			String parameterType = result.getString(2);
//			String parameterName = result.getString(3);
//			String parameterValue = result.getString(4);
//			
//			inferenceMachine.isSolvable("assertz(parameter('"+parameterId+"')), assertz(parameterType('"+parameterId+"', '"+parameterType+"')), assertz(parameterName('"+parameterId+"', '"+parameterName+"')), assertz(parameterValue('"+parameterId+"', '"+parameterValue+"')).");
//		}
		
		//Load experiments
		
		result = session.query("SELECT * FROM EXPERIMENT");
		
		while(result.next()){
			String experimentId = result.getString(1);
			String experimentRootProcess = result.getString(2);
			String experimentName = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(experiment('"+experimentId+"')), assertz(experimentRootProcess('"+experimentId+"', '"+experimentRootProcess+"')), assertz(experimentName('"+experimentId+"', '"+experimentName+"')).");
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
			String swfmsId = result.getString(3);
			
			inferenceMachine.isSolvable("assertz(type('"+instanceId+"', '"+processId+"')), assertz(swfmsProcess('"+swfmsId+"', '"+instanceId+"')).");
		}
		
		//Load activity instance
		
		result = session.query("SELECT * FROM ACTIVITY_INSTANCE");
		
		while(result.next()){
			String instanceId = result.getString(1);
			String activityId = result.getString(2);
			
			inferenceMachine.isSolvable("assertz(type('"+instanceId+"', '"+activityId+"')).");
		}
		
//		//Load activity parameters
//		
//		result = session.query("SELECT * FROM ACTIVITY_PARAMETER");
//		
//		while(result.next()){
//			String activityInstanceId = result.getString(1);
//			String parameterId = result.getString(2);
//			
//			inferenceMachine.isSolvable("assertz(activityParameter('"+activityInstanceId+"', '"+parameterId+"')).");
//		}
//		
//		//Load process parameters
//		
//		result = session.query("SELECT * FROM PROCESS_PARAMETER");
//		
//		while(result.next()){
//			String processInstanceId = result.getString(1);
//			String parameterId = result.getString(2);
//			
//			inferenceMachine.isSolvable("assertz(processParameter('"+processInstanceId+"', '"+parameterId+"')).");
//		}
		
		//Load flows
		
		result = session.query("SELECT * FROM FLOW");
		
		while(result.next()){
			String originElementId = result.getString(1);
			String originElementType = result.getString(2);
			String destinationElementId = result.getString(3);
			String destinationElementType = result.getString(4);
			
			if(originElementType == CharonUtil.OPTION)
				inferenceMachine.isSolvable("assertz(optionFlow('"+originElementId+"', "+CharonUtil.createElement(originElementType, originElementId)+")).");
			else
				inferenceMachine.isSolvable("assertz(flow("+CharonUtil.createElement(originElementType, originElementId)+", "+CharonUtil.createElement(destinationElementType, destinationElementId)+")).");
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
		
		//Load process parameters
		
		result = session.query("SELECT * FROM EXECUTION_STATUS");
		
		while(result.next()){
			String elementId = result.getString(1);
			String elementType = result.getString(2);
			String status = result.getString(3);
			String startTime = result.getString(4);
			String endTime = result.getString(5);
			String path = result.getString(6);
			String performers = result.getString(7);
			
			
			if(status == CharonUtil.EXECUTING_STATUS)
				inferenceMachine.isSolvable("assertz(executing("+CharonUtil.createElement(elementType, elementId)+", ["+path+"], '"+startTime+"', [])).");
			else
			if(status == CharonUtil.EXECUTED_STATUS)
				inferenceMachine.isSolvable("assertz(executed("+CharonUtil.createElement(elementType, elementId)+", ["+path+"], '"+startTime+"', '"+endTime+"', [])).");
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
		
		
		charonRules.add("(createElement(ElementId, '"+CharonUtil.ACTIVITY+"', activity(ElementId)):- currentVersion(activityInstance(ElementId), VersionId))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.PROCESS+"', process(VersionId)):- currentVersion(processInstance(ElementId), VersionId))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.SYNCHRONISM+"', synchronism(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.DECISION+"', decision(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.INITIAL+"', initial(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.FINAL+"', final(ElementId)))");
		charonRules.add("(createElement(ElementId, '"+CharonUtil.ARTIFACT+"', product(ElementId)))");
		
		charonRules.add("(nextVersionId(VersionId) :- class('br.ufrj.cos.lens.odyssey.tools.charon.util.IDGenerator') <- generateID returns VersionId)");
		
		charonRules.add("(create_experiment(ExperimentId, ExperimentName) :- assertz(experiment(ExperimentId)), nextVersionId(VersionId), assertz(version(experiment(ExperimentId), VersionId, '0')), assertz(currentVersion(experiment(ExperimentId), VersionId)), assertz(experimentName(VersionId, ExperimentName)))");
		charonRules.add("(update_experimentName(ExperimentId, ExperimentName) :- nextVersionId(VersionId), currentVersion(experiment(Id), PreviousVersion), assertz(version(experiment(ExperimentId), VersionId, PreviousVersion)), assertz(experimentName(VersionId, ExperimentName)))");
//		charonRules.add("(delete_experiment(ExperimentId) :- assertz(deleted(experiment(ExperimentId)))");
		charonRules.add("(assertz_experimentRootProcess(ExperimentId, ProcessClassId) :- assertz(experimentRootProcess(ExperimentId, ProcessClassId)))");
		
		
		// PROCESS
		charonRules.add("(create_process(ProcessClassId, ProcessClassName, ProcessClassType, ProcessClassPorts) :- assertz(process(ProcessClassId)), nextVersionId(VersionId), assertz(version(process(ProcessClassId), VersionId, '0')), assertz(currentVersion(process(ProcessClassId), VersionId)), assertz(processType(VersionId, ProcessClassType)), assertz(processName(VersionId, ProcessClassName)), associate_processPort(VersionId, ProcessClassPorts))");
		charonRules.add("(update_process(ProcessClassId, ProcessClassName, ProcessClassType, ProcessClassPorts) :- nextVersionId(VersionId), currentVersion(process(ProcessClassId), PreviousVersion), assertz(version(process(ProcessClassId), VersionId, PreviousVersion)), assertz(processType(VersionId, ProcessClassType)), assertz(processName(VersionId, ProcessClassName)), associate_processPort(VersionId, ProcessClassPorts))");
		charonRules.add("(update_processName(ProcessClassId, ProcessClassName) :- currentVersion(process(ProcessClassId), currentVersion), processType(currentVersion, ProcessClassType), findall(E, processPort(currentVersion, E), Es), update_process(ProcessClassId, ProcessClassName, ProcessClassType, Es))");
		charonRules.add("(update_processType(ProcessClassId, ProcessClassType) :- currentVersion(process(ProcessClassId), currentVersion), processName(currentVersion, ProcessClassName), findall(E, processPort(currentVersion, E), Es), update_process(ProcessClassId, ProcessClassName, ProcessClassType, Es))");
		charonRules.add("(add_processPort(ProcessClassId, PortId) :- currentVersion(process(ProcessClassId), currentVersion), processName(currentVersion, ProcessClassName), processType(currentVersion, ProcessClassType), findall(E, processPort(currentVersion, E), Es), update_process(ProcessClassId, ProcessClassName, ProcessClassType, [PortId|Es]))");
		charonRules.add("(remove_processPort(ProcessClassId, PortId) :- currentVersion(process(ProcessClassId), currentVersion), processName(currentVersion, ProcessClassName), processType(currentVersion, ProcessClassType), retract(processPort(currentVersion, PortId)), findall(E, processPort(currentVersion, E), Es), assertz(processPort(currentVersion, PortId)), update_process(ProcessClassId, ProcessClassName, ProcessClassType, Es))");
		charonRules.add("(associate_processPort(ProcessClassVersionId, []))");
		charonRules.add("(associate_processPort(ProcessClassVersionId, [ProcessClassPort|ProcessClassPorts]) :- assertz(processPort(ProcessClassVersionId, ProcessClassPort)), associate_processPort(ProcessClassVersionId, ProcessClassPorts))");


		// ACTIVITY
		charonRules.add("(create_activity(ActivityClassId, ActivityClassName, ActivityClassType) :- assertz(activity(ActivityClassId)), nextVersionId(VersionId), assertz(version(activity(ActivityClassId), VersionId, '0')), assertz(currentVersion(activity(ActivityClassId), VersionId)), assertz(activityType(VersionId, ActivityClassType)), assertz(activityName(VersionId, ActivityClassName)))");
		charonRules.add("(update_activity(ActivityClassId, ActivityClassName, ActivityClassType) :- nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)), assertz(activityType(VersionId, ActivityClassType)), assertz(activityName(VersionId, ActivityClassName)))");
		charonRules.add("(update_activityName(ActivityClassId, ActivityClassName) :- nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)), assertz(activityName(VersionId, ActivityClassName)))");
		charonRules.add("(update_activityType(ActivityClassId, ActivityClassType) :- nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)), assertz(activityType(VersionId, ActivityClassType)))");
		charonRules.add("(add_activityPort(ActivityClassId, PortId) :- nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)), assertz(activityPort(VersionId, PortId)))");
		charonRules.add("(remove_activityPort(ActivityClassId, PortId) :- retract(activityPort(VersionId, PortId)), nextVersionId(VersionId), currentVersion(activity(ActivityClassId), PreviousVersion), assertz(version(activity(ActivityClassId), VersionId, PreviousVersion)))");
		
		
		// SWFMS
		charonRules.add("(create_SWFMS(SWFMS_Id, SWFMS_Name, SWFMS_Host) :- assertz(swfms(SWFMS_Id)), nextVersionId(VersionId), assertz(version(swfms(SWFMS_Id), VersionId, '0')), assertz(currentVersion(swfms(SWFMS_Id), VersionId)), assertz(swfmsName(VersionId, SWFMS_Name)), assertz(swfmsHost(VersionId, SWFMS_Host)))");
		charonRules.add("(update_SWFMS(SWFMS_Id, SWFMS_Name, SWFMS_Host) :- nextVersionId(VersionId), currentVersion(swfms(SWFMS_Id), PreviousVersion), assertz(version(swfms(SWFMS_Id), VersionId, PreviousVersion)), assertz(swfmsName(VersionId, SWFMS_Name)), assertz(swfmsHost(VersionId, SWFMS_Host)))");
		charonRules.add("(update_SWFMSName(SWFMS_Id, SWFMS_Name) :- nextVersionId(VersionId), currentVersion(swfms(SWFMS_Id), PreviousVersion), assertz(version(swfms(SWFMS_Id), VersionId, PreviousVersion)), assertz(swfmsName(VersionId, SWFMS_Name)))");
		charonRules.add("(update_SWFMSHost(SWFMS_Id, SWFMS_Host) :- nextVersionId(VersionId), currentVersion(swfms(SWFMS_Id), PreviousVersion), assertz(version(swfms(SWFMS_Id), VersionId, PreviousVersion)), assertz(swfmsHost(VersionId, SWFMS_Host)))");
		
		
//		//Parameter
//		charonRules.add("(create_parameter(ParameterId, ParameterName, ParameterType, ParameterValue) :- assertz(parameter(ParameterId)), nextVersionId(VersionId), assertz(version(parameter(ParameterId), VersionId, '0')), assertz(currentVersion(parameter(ParameterId), VersionId)), assertz(parameterName(VersionId, ParameterName)), assertz(parameterType(VersionId, ParameterType)), assertz(parameterValue(VersionId, ParameterValue)))");
//		charonRules.add("(update_parameter(ParameterId, ParameterName, ParameterType, ParameterValue) :- nextVersionId(VersionId), currentVersion(parameter(ParameterId), PreviousVersion), assertz(version(parameter(ParameterId), VersionId, PreviousVersion)), assertz(parameterName(VersionId, ParameterName)), assertz(parameterType(VersionId, ParameterType)), assertz(parameterValue(VersionId, ParameterValue)))");
//		charonRules.add("(update_parameterName(ParameterId, ParameterName) :- nextVersionId(VersionId), currentVersion(parameter(ParameterId), PreviousVersion), assertz(version(parameter(ParameterId), VersionId, PreviousVersion)), assertz(parameterName(VersionId, ParameterName)))");
//		charonRules.add("(update_parameterType(ParameterId, ParameterType) :- nextVersionId(VersionId), currentVersion(parameter(ParameterId), PreviousVersion), assertz(version(parameter(ParameterId), VersionId, PreviousVersion)), assertz(parameterType(VersionId, ParameterType)))");
//		charonRules.add("(update_parameterValue(ParameterId, ParameterValue) :- nextVersionId(VersionId), currentVersion(parameter(ParameterId), PreviousVersion), assertz(version(parameter(ParameterId), VersionId, PreviousVersion)), assertz(parameterValue(VersionId, ParameterValue)))");
		
		
		//Synchronism
		charonRules.add("(create_synchronism(SynchronismId) :- assertz(synchronism(SynchronismId)), nextVersionId(VersionId), assertz(version(synchronism(SynchronismId), VersionId, '0')), assertz(currentVersion(synchronism(SynchronismId), VersionId)))");
		
		//Decision and Option
		charonRules.add("(create_decision(DecisionId, DecisionName) :- assertz(decision(DecisionId)), currentVersion(decision(DecisionId), PreviousVersion), nextVersionId(VersionId), assertz(version(decision(DecisionId), VersionId, '0')), assertz(currentVersion(decision(DecisionId), VersionId)), assertz(decisionName(VersionId, DecisionName)))");
		charonRules.add("(add_decisionOption(DecisionId, OptionName, ToElementType, ToElementId) :- nextVersionId(VersionId), currentVersion(decision(DecisionId), PreviousVersion), assertz(version(decision(DecisionId), VersionId, PreviousVersion)), createElement(ToElementId, ToElementType, ToElement), assertz(option(VersionId, OptionName, ToElement)))");
		
		//Initial
		charonRules.add("(create_intial(InitialId) :- assertz(initial(InitialId)), nextVersionId(VersionId), assertz(version(initial(InitialId), VersionId, '0')), assertz(currentVersion(initial(InitialId), VersionId)))");
		
		//Final
		charonRules.add("(create_final(FinalId) :- assertz(final(FinalId)), nextVersionId(VersionId), assertz(version(final(FinalId), VersionId, '0')), assertz(currentVersion(final(FinalId), VersionId)))");
		
		//Process and Activity Instance
		charonRules.add("(assertz_processInstance(ProcessInstanceId, ProcessClassVersionId) :- assertz(processInstance(ProcessInstanceId)), nextVersionId(VersionId), assertz(version(processInstance(ProcessInstanceId), VersionId, '0')), assertz(currentVersion(processInstance(ProcessInstanceId), VersionId)), assertz(processInstanceType(ProcessInstanceId, ProcessClassVersionId)))");
		charonRules.add("(assertz_activityInstance(ActivityInstanceId, ActivityClassVersionId) :- assertz(activityInstance(ActivityInstanceId)), nextVersionId(VersionId), assertz(version(activityInstance(ActivityInstanceId), VersionId, '0')), assertz(currentVersion(activityInstance(ActivityInstanceId), VersionId)), assertz(activityInstanceType(ActivityInstanceId, ActivityClassVersionId)))");
		
		//Defining flow
		charonRules.add("(create_flow(ProcessClassId, OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- nextVersionId(VersionId), currentVersion(process(ProcessClassId), PreviousVersion), assertz(version(process(ProcessClassId), VersionId, PreviousVersion)), createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), assertz(processFlow(VersionId, OriginElement, DestinationElement)))");
		charonRules.add("(delete_flow(ProcessClassId, OriginElementType, OriginElementId, DestinationElementType, DestinationElementId) :- nextVersionId(VersionId), currentVersion(process(ProcessClassId), PreviousVersion), assertz(version(process(ProcessClassId), VersionId, PreviousVersion)), createElement(OriginElementId, OriginElementType, OriginElement), createElement(DestinationElementId, DestinationElementType, DestinationElement), retract(processFlow(PreviousVersion, OriginElement, DestinationElement)))");			
					
		//Artifact
		charonRules.add("(create_artifact(ArtifactId) :- assertz(artifact(ArtifactId)), nextVersionId(VersionId), assertz(version(artifact(ArtifactId), VersionId, '0')), assertz(currentVersion(artifact(ArtifactId), VersionId)))");
		
		//Port
		charonRules.add("(create_port(PortId, PortType, PortName, PortDataType) :- assertz(port(PortId)), nextVersionId(VersionId), assertz(version(port(PortId), VersionId, '0')), assertz(currentVersion(port(PortId), VersionId)), assertz(portType(VersionId, PortType)), assertz(portName(VersionId, PortName)), assertz(portDataType(VersionId, PortDataType)))");
		charonRules.add("(update_port(PortId, PortType, PortName, PortDataType) :- nextVersionId(VersionId), currentVersion(port(PortId), PreviousVersion), assertz(version(port(PortId), VersionId, PreviousVersion)), assertz(portType(VersionId, PortType)), assertz(portName(VersionId, PortName)), assertz(portDataType(VersionId, PortDataType)))");
		charonRules.add("(update_portType(PortId, PortType) :- nextVersionId(VersionId), currentVersion(port(PortId), PreviousVersion), assertz(version(port(PortId), VersionId, PreviousVersion)), assertz(portType(VersionId, PortType)))");
		charonRules.add("(update_portName(PortId, PortName) :- nextVersionId(VersionId), currentVersion(port(PortId), PreviousVersion), assertz(version(port(PortId), VersionId, PreviousVersion)), assertz(portName(VersionId, PortName)))");
		charonRules.add("(update_portDataType(PortId, PortDataType) :- nextVersionId(VersionId), currentVersion(port(PortId), PreviousVersion), assertz(version(port(PortId), VersionId, PreviousVersion)), assertz(portDataType(VersionId, PortDataType)))");

		//Defining artifact to port
		charonRules.add("(assertz_artifactActivityPort(ActivityInstanceId, PortId, ArtifactId) :- nextVersionId(VersionId), currentVersion(activityInstance(ActivityInstanceId), PreviousVersion), assertz(version(activityInstance(ActivityInstanceId), VersionId, PreviousVersion)), assertz(artifactActivityPort(ArtifactId, VersionId, PortId)))");
		charonRules.add("(assertz_artifactProcessPort(ProcessInstanceId, PortId, ArtifactId) :- nextVersionId(VersionId), currentVersion(processInstance(ProcessInstanceId), PreviousVersion), assertz(version(processInstance(ProcessInstanceId), VersionId, PreviousVersion)), assertz(artifactProcessPort(ArtifactId, VersionId, PortId)))");
		
		
		//Defining what SWfMS that the process instance is deployed
		charonRules.add("(assertz_SWFMSProcess(SWFMSId, ProcessInstanceId) :- nextVersionId(VersionId), currentVersion(processInstance(ProcessInstanceId), PreviousVersion), assertz(version(processInstance(ProcessInstanceId), VersionId, PreviousVersion)), assertz(swfmsProcess(VersionId, SWFMSId)))");

		
		// Execução
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