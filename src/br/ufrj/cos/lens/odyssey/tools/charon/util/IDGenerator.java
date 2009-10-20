package br.ufrj.cos.lens.odyssey.tools.charon.util;

public class IDGenerator {

	static int id = 0;
	
	public static synchronized String generateID(){
		id++;
		return id+"";
	}
}
