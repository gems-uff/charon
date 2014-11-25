package br.ufrj.cos.lens.odyssey.tools.charon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import spem.SpemPackage;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.Agent;
import br.ufrj.cos.lens.odyssey.tools.charon.agents.MappingAgent;
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
	
	private ArrayList<String> charonRules;

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
	
	private void setCharonRules(boolean IsUsingDatabase) {

		charonRules = new ArrayList<String>();
		
		
//		charonRules.add("(mandatory('A'))");
//		charonRules.add("(variationPoint('B'))");
//		charonRules.add("(mandatory('B'))");
//		charonRules.add("(variant('B1', 'B'))");
//		charonRules.add("(variant('B2', 'B'))");
//		charonRules.add("(optional('C'))");
//		charonRules.add("(variationPoint('D'))");
//		charonRules.add("(mandatory('D'))");
//		charonRules.add("(variant('D1', 'D'))");
//		charonRules.add("(variant('D2', 'D'))");
//		charonRules.add("(mandatory('E'))");
//		charonRules.add("(variationPoint('E'))");
//		charonRules.add("(variant('E1', 'E'))");
//		charonRules.add("(variant('E2', 'E'))");
//		
//		charonRules.add("(abstractWorkflow('A'))");
//		charonRules.add("(abstractWorkflow('B'))");
//		charonRules.add("(abstractWorkflow('D'))");
//		charonRules.add("(abstractWorkflow('E'))");
////		charonRules.add("(abstractWorkflow('C'))");
//		charonRules.add("(abstractWorkflow('B1'))");
//		charonRules.add("(abstractWorkflow('D1'))");
//		charonRules.add("(abstractWorkflow('E1'))");
////		charonRules.add("(abstractWorkflow('E2'))");
////		charonRules.add("(abstractWorkflow('B2'))");
		
		
		
//		//Initialize derivation (Select mandatory activities to the abstract workflow) (Deveria tentar achar uma solução para as regras de composição e selecionar as variabilidades e opcionalidades)
//		charonRules.add("(initializeDerivation(_) :- findall(ActivityId, mandatory(ActivityId), ActivityIdList), selectMandatoryActivities(ActivityIdList))");		
//		
//		//Validate Derived Workflow
//		charonRules.add("(isValidDerivedWorkflow(_) :- findall(ActivityIdd, mandatory(ActivityIdd), MandatoryList), findall(ActivityId, abstractWorkflow(ActivityId), AbstActvList), containsAllMandatories(MandatoryList, AbstActvList), findall(VPId, variationPoint(VPId), VPList), checkVariationPoints(VPList, AbstActvList), findall(RuleId, validationRule(RuleId), []))");
//		
//		
//		charonRules.add("(validateRules([]))");
//		charonRules.add("(validateRules([Rule|RuleList]) :- validationRule(Rule), validateRules(RuleList))");
//		
//		
//		//Verify if all mandatory activities are included in the abstract workflow 
//		charonRules.add("(containsAllMandatories([], _))");
//		charonRules.add("(containsAllMandatories([M|MandatoryList], AbstActvList) :- member(M, AbstActvList), containsAllMandatories(MandatoryList, AbstActvList))");
//
//		//Verify if one, and only one, variant is selected for every variation point 
//		charonRules.add("(checkVariationPoints([], _))");
//		charonRules.add("(checkVariationPoints([VP|VPList], AbstActvList) :- optional(VP), not(abstractWorkflow(VP)), checkVariationPoints(VPList, AbstActvList))");
//		charonRules.add("(checkVariationPoints([VP|VPList], AbstActvList) :- findall(VariantId, variant(VariantId, VP), VariantIdList), isVariantSelected(VP, VariantIdList, AbstActvList), not(isThereAnyOtherVariantSelected(VP, VariantIdList, AbstActvList)), checkVariationPoints(VPList, AbstActvList))");
//		
//		
//		charonRules.add("(isMoreVariantSelected(VariationPoint, [Var|VariantList], AbstActvList) :- member(Var, AbstActvList), not(currentDeselection(Var)))");
//		charonRules.add("(isMoreVariantSelected(VariationPoint, [Var|VariantList], AbstActvList) :- member(Var, AbstActvList), currentDeselection(Var), isMoreVariantSelected(VariationPoint, VariantList, AbstActvList))");
//		charonRules.add("(isMoreVariantSelected(VariationPoint, [Var|VariantList], AbstActvList) :- not(member(Var, AbstActvList)), isMoreVariantSelected(VariationPoint, VariantList, AbstActvList))");
//		
//		charonRules.add("(isThereAnyOtherVariantSelected(VariationPoint, [Var|VariantList], AbstActvList) :- member(Var, AbstActvList), not(currentDeselection(Var)), isMoreVariantSelected(VariationPoint, VariantList, AbstActvList))");
//		charonRules.add("(isThereAnyOtherVariantSelected(VariationPoint, [Var|VariantList], AbstActvList) :- member(Var, AbstActvList), currentDeselection(Var), isThereAnyOtherVariantSelected(VariationPoint, VariantList, AbstActvList))");
//		charonRules.add("(isThereAnyOtherVariantSelected(VariationPoint, [Var|VariantList], AbstActvList) :- not(member(Var, AbstActvList)), isThereAnyOtherVariantSelected(VariationPoint, VariantList, AbstActvList))");
//
//		
//		//Function to select all mandatory activities in the derived abstract workflow
//		charonRules.add("(selectMandatoryActivities([]))");
//		charonRules.add("(selectMandatoryActivities([M|MandatoryList]) :- assertz(currentSelection(M)), selectMandatoryActivities(MandatoryList))");
//
//		
//		
//		
//		
//		//Regra de inserção pra mandatorio
//		charonRules.add("(selectElement(E) :- not(abstractWorkflow(E)), mandatory(E), selectMandatoryActivities([E]))");
//		charonRules.add("(selectElement(E) :- (abstractWorkflow(E); currentSelection(E)), mandatory(E))");
//		
//		charonRules.add("(unselectElement(E) :- mandatory(E), assertz(conflict(E)))");
//		
//		//Regra de inserção pra ponto de variação opcional
//		charonRules.add("(selectElement(E) :- not(abstractWorkflow(E)), optional(E), variationPoint(E), assertz(currentSelection(E)), checkForInferredSelection(_))");
//		charonRules.add("(selectElement(E) :- (abstractWorkflow(E); currentSelection(E)), optional(E), variationPoint(E))");
//		
//		charonRules.add("(unselectElement(E) :- abstractWorkflow(E), not(currentDeselection(E)), not(currentSelection(E)),  optional(E), variationPoint(E), assertz(currentDeselection(E)), checkForInferredSelection(_))");
//		charonRules.add("(unselectElement(E) :- not(abstractWorkflow(E)), not(currentSelection(E)), optional(E), variationPoint(E))");
//		charonRules.add("(unselectElement(E) :- currentSelection(E), optional(E), variationPoint(E), assertz(conflict(E)))");
//		
//		//Regra de inserção pra opcional nao ponto de variação
//		charonRules.add("(selectElement(E) :- not(abstractWorkflow(E)), optional(E), assertz(currentSelection(E)), checkForInferredSelection(_))");
//		charonRules.add("(selectElement(E) :- (abstractWorkflow(E); currentSelection(E)), optional(E))");
//		
//		charonRules.add("(unselectElement(E) :- abstractWorkflow(E), not(currentDeselection(E)), not(currentSelection(E)),  optional(E), assertz(currentDeselection(E)), checkForInferredSelection(_))");
//		charonRules.add("(unselectElement(E) :- not(abstractWorkflow(E)), not(currentSelection(E)), optional(E))");
//		charonRules.add("(unselectElement(E) :- currentSelection(E), optional(E), assertz(conflict(E)))");
//
//		
//		//BEGIN: Regras de inserção e remoção de variantes
//		
//		//N�o checa se tem mais de uma variante selecionada (isso nao acontece pq a funcao selectVariant nao existe) e da erro se o conjunto de variantes for vazio.
//		charonRules.add("(isVariantSelected(VariationPoint, [Var|VariantList], AbstActvList) :- member(Var, AbstActvList), not(currentDeselection(Var)))");
//		charonRules.add("(isVariantSelected(VariationPoint, [Var|VariantList], AbstActvList) :- member(Var, AbstActvList), currentDeselection(Var), isVariantSelected(VariationPoint, VariantList, AbstActvList))");
//		charonRules.add("(isVariantSelected(VariationPoint, [Var|VariantList], AbstActvList) :- not(member(Var, AbstActvList)), isVariantSelected(VariationPoint, VariantList, AbstActvList))");
//		
//		//Function to select one variant of a variation point. Before select the functions checks if there is other variant already selected. 
//		charonRules.add("(selectVariant(Var) :- findall(ActivityId, abstractWorkflow(ActivityId), AbstActvList), variant(Var, VP), findall(Var1, variant(Var1, VP), VariantList), findall(ActivityId2, currentSelection(ActivityId2), CurrentSelectList), append(AbstActvList, CurrentSelectList, CompleteList), not(isVariantSelected(VP, VariantList, CompleteList)), assertz(currentSelection(Var)), selectElement(VP))");
//		
//		charonRules.add("(selectElement(E) :- not(abstractWorkflow(E)), not(currentDeselection(E)), not(currentSelection(E)), variant(E, _), selectVariant(E), checkForInferredSelection(_))");
//		
//		charonRules.add("(selectElement(E) :- (abstractWorkflow(E); currentSelection(E)), variant(E, _))");
//		
//		charonRules.add("(selectElement(E) :- not(abstractWorkflow(E)), not(currentDeselection(E)), not(currentSelection(E)), variant(E, _), not(selectVariant(E)), assertz(conflict(E)))");
//		
//		charonRules.add("(selectElement(E) :- currentDeselection(E), variant(E, _), assertz(conflict(E)))");
//		
//		
//		//acho que a desseleção ta errada, pois nao crio um caso quando o elemento jah esta desselecionado
//		charonRules.add("(unselectElement(E) :- abstractWorkflow(E), not(currentDeselection(E)), not(currentSelection(E)), variant(E, _), assertz(currentDeselection(E)), checkForInferredSelection(_))");
//		
//		charonRules.add("(unselectElement(E) :- not(abstractWorkflow(E)), not(currentSelection(E)), variant(E, _))");
//		
//		charonRules.add("(unselectElement(E) :- currentSelection(E), variant(E, _), assertz(conflict(E)))");
//		
//		//END: Regras de inserção e remoção de variantes
//		
//		
//		charonRules.add("(checkForInferredSelection(_) :- rule(_))");
//		
//		
//		//TODO: Fazer a seleção automatica de pontos de variação quando variante for selecionada. Fazer desseleção automatica de pontos de variação quando variantes foram desselecionada.
//		
//		//Selection Commit and rollback current (de)selections
//		
//		charonRules.add("(commitSelectedElements([]))");
//		charonRules.add("(commitSelectedElements([Id|List]) :- retract(currentSelection(Id)), assertz(abstractWorkflow(Id)), commitSelectedElements(List))");
//		
//		charonRules.add("(commitDeselectedElements([]))");
//		charonRules.add("(commitDeselectedElements([Id|List]) :- retract(currentDeselection(Id)), retract(abstractWorkflow(Id)), commitDeselectedElements(List))");
//		
//		charonRules.add("(rollbackSelectedElements([]))");
//		charonRules.add("(rollbackSelectedElements([Id|List]) :- retract(currentSelection(Id)), rollbackSelectedElements(List))");
//		
//		charonRules.add("(rollbackDeselectedElements([]))");
//		charonRules.add("(rollbackDeselectedElements([Id|List]) :- retract(currentDeselection(Id)), rollbackDeselectedElements(List))");
//		
//		charonRules.add("(clearConflicts([]))");
//		charonRules.add("(clearConflicts([Id|List]) :- retract(conflict(Id)), clearConflicts(List))");
//		
//		charonRules.add("(commitSelection(_) :- findall(Id1, currentDeselection(Id1), List1), findall(Id2, currentSelection(Id2), List2), commitSelectedElements(List2), commitDeselectedElements(List1))");
//		
//		charonRules.add("(rollbackSelection(_) :- findall(Id1, currentDeselection(Id1), List1), findall(Id2, currentSelection(Id2), List2), findall(Id3, conflict(Id3), List3), rollbackSelectedElements(List2), rollbackDeselectedElements(List1), clearConflicts(List3))");
//		
//		
////		charonRules.add("(rollbackSelection(_) :- retract(currentSelection(_)), retract(currentDeselection(_)), retract(conflict(_)))");
//		
//		charonRules.add("(initSelection(_) :- rollbackSelection(_))");
//		
//		//TIP: Posso tentar usar listas para guardar todos comaminhos possíveis de inferencia das regras.
//
//		charonRules.add("(rule(_))");
////		charonRules.add("(rule('X') :- ((abstractWorkflow('B1'); currentSelection('B1')) -> selectElement('D1')))");
////		charonRules.add("(rule('Y') :- ((abstractWorkflow('D1'); currentSelection('D1')) -> unselectElement('B1')))");
////		charonRules.add("(rule('Y') :- ((abstractWorkflow('D1'); currentSelection('D1')) -> selectElement('E1')))");
////		charonRules.add("(rule('Y') :- ((abstractWorkflow('E1'); currentSelection('E1')) -> selectElement('D1')))");
////		charonRules.add("(rule('Z') :- ((abstractWorkflow('E1'); currentSelection('E1')) -> selectElement('B1')))");
//		
//		
////		charonRules.add("(validationRule('X') :- not((abstractWorkflow('B1') -> abstractWorkflow('D1'))))");
////		charonRules.add("(validationRule('Y') :- not((abstractWorkflow('D1') -> abstractWorkflow('E1'))))");
//
//		
//		//Essa operacao abaixo nao funciona sendo chamada aqui dentro porque a funcao ainda nao foi cadastrada...
//		//charonRules.add("(initializeDerivation(_))");


		
		this.addClauses(charonRules);

		
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