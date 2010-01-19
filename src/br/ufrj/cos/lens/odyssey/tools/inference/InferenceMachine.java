package br.ufrj.cos.lens.odyssey.tools.inference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;

/**
 * Wrapper to the inference machine (tuProlog).
 *
 * @author Leo Murta
 * @version 1.0, 18/12/2001
 */
public class InferenceMachine {

	/**
	 * Inference machine instance
	 */
	private Prolog inferenceMachine = null;
	
	/**
	 * Constructs the inference machine
	 */
	public InferenceMachine() {
		inferenceMachine = new Prolog();
	}
	
	/**
	 * Constructs the inference machine loading an existing knowledge base from "file"
	 */
	public InferenceMachine(File file) throws InvalidTheoryException, FileNotFoundException, IOException {
		this();
		
		Theory theory = new Theory(new FileInputStream(file));
		inferenceMachine.addTheory(theory);

	}

	/**
	 * Adds a collection of clauses into the knowledge base
	 *
	 * @param clauses Collection of clauses to be inserted. Each clause is a
	 * 				  String without a period at the end.
	 */
	public synchronized void addClauses(Collection<String> clauses) {
		Collection<String> asserts = new ArrayList<String>();
		
		for (String clause : clauses) {
			asserts.add("assertz(" + clause + ")");			
		}

		isSolvable(asserts);
	}

	/**
	 * Removes a collection of clauses from the knowledge base
	 *
	 * @param clauses Collection of clauses to be inserted. Each clause is a
	 * 				  String without a period at the end.
	 */
	public synchronized void removeClauses(Collection<String> clauses) {
		Collection<String> retracts = new ArrayList<String>();
		
		for (String clause : clauses) {
			retracts.add("retract(" + clause + ")");
		}
		
		isSolvable(retracts);
	}

	/**
	 * Provides a boolean answer for a given goal. This goal is composed by a collection
	 * of clauses.
	 *
	 * @param clauses The clauses are part of a broad goal, and will be concatenated 
	 * 			      with commas to be submitted to the prolog machine. Each clause
	 * 			      is a String without a period at the end.
	 */
	public synchronized boolean isSolvable(Collection<String> clauses) {
		StringBuffer goal = new StringBuffer();
		
		for (String clause : clauses) {
			goal.append(clause).append(",");
		}

		if (goal.length() != 0) {
			goal.deleteCharAt(goal.length() - 1);
			return isSolvable(goal.append(".").toString());
		}
		
		return false;
	}

	/**
	 * Provides a boolean answer for a given goal.
	 *
	 * @param goal The goal is a String with a period at the end.
	 */
	public synchronized boolean isSolvable(String goal) {
		try {
			SolveInfo solveInfo = inferenceMachine.solve(goal);
			inferenceMachine.solveEnd();
			return solveInfo.isSuccess();
		} catch (MalformedGoalException e) {
			Logger.global.log(Level.WARNING, "Could not query inference machine with " + goal, e);
			return false;
		}
	}

	/**
	 * For a given goal, provides the first match in the form of Map with the variables 
	 * indexing their values. 
	 *
	 * @param goal The goal is a String with a period at the end.
	 * @return Map with the variables indexing their values. Null is returned in the 
	 * 		   case of NO possible solution. A empty Map is returned in the case of YES 
	 * 		   without variables matching.
	 */
	public synchronized Map<String,Object> getSolution(String goal) {
		try {
			Map<String,Object> solution = translate(inferenceMachine.solve(goal));
			inferenceMachine.solveEnd();
			return solution;
		} catch (MalformedGoalException e) {
			Logger.global.log(Level.WARNING, "Could not query inference machine with " + goal, e);
			return null;
		}
	}

	
	/**
	 * For a given goal, provides all possible solutions in the form of a List of Maps
	 * with the variables indexing their values. 
	 *
	 * @param goal The goal is a String with a period at the end.
	 * @return List of Maps with the variables indexing their values. Empty list is returned 
	 * 		   in the case of NO solution.
	 */
	public synchronized List<Map<String,Object>> getAllSolutions(String goal) {
		List<Map<String,Object>> solutions = new ArrayList<Map<String,Object>>();

		try {
			Map<String,Object> solution = translate(inferenceMachine.solve(goal));
			while (solution != null) {
				solutions.add(solution);
				solution = translate(inferenceMachine.solveNext());
			}
		} catch (MalformedGoalException e) {
			Logger.global.log(Level.WARNING, "Could not query inference machine with " + goal, e);
		} catch (NoMoreSolutionException e) {}
		
		return solutions;
	}
	
	/**
	 * Provides the content of the knowledge base in a textual format (read only mode)
	 */
	public String getContent() {
		return inferenceMachine.getTheory().toString();
	}

	/**
	 * Provides a Map with the next answer. This Map has the name of each variable
	 * indexing its value (the name is a String and the value may be null, String or
	 * List).
	 *
	 * @return Map with the variables indexing their values. Null is returned in the 
	 * 		   case of NO possible solution. A empty Map is returned in the case of YES 
	 * 		   without variables matching.
	 */
	private synchronized Map<String,Object> translate(SolveInfo solveInfo) {
		try {
			Map<String,Object> result = new HashMap<String,Object>();
			
			for (Iterator iterator = solveInfo.getBindingVars().iterator(); iterator.hasNext();) {
				Var var = (Var) iterator.next();
				result.put(var.getName(), translate(var.getTerm()));
			}
//			for (Var var : solveInfo.toVarArray()) {					
//				result.put(var.getName(), translate(var.getTerm()));
//			}
			
			return result;
		} catch (NoSolutionException e) {
			return null;
		}
	}
	
	/**
	 * Translates a tuProlog Term into a java Object, recursivelly. The mapping is:
	 * tuProlog NULL -> Java null
	 * tuProlog List -> Java List
	 * any other element -> Java String
	 */
	private Object translate(Term term) {
		if (term.isList()) {
			List<Object> list = new ArrayList<Object>();
			
			Iterator i = ((Struct)term).listIterator();
			while (i.hasNext()) {
				list.add(translate((Term)i.next()));	
			}
			
			return list;
		} else {
			return term.toString();
		}
	}
}