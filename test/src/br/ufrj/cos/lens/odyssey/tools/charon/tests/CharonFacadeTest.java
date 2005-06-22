package br.ufrj.cos.lens.odyssey.tools.charon.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.jmi.xmi.MalformedXMIException;

import junit.framework.TestCase;

import org.netbeans.api.mdr.CreationFailedException;

import processstructure.WorkDefinition;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonFacade;

public class CharonFacadeTest extends TestCase {

	private static final String XMI_INPUT_FILE = "resource/input.xmi";
	
	public CharonFacadeTest(String arg0) {
		super(arg0);
	}

	public void testInstanciaProcesso() {
		//CharonFacade.getInstancia().instanciaProcesso(getWorkDefinition());
	}
	
	private WorkDefinition getWorkDefinition(){
		InputStream input = ClassLoader.getSystemResourceAsStream(XMI_INPUT_FILE);
		Collection jmiCollection = null;
		try {
			jmiCollection = XMIManager.getInstance().read(input);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedXMIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CreationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * Considerando que o arquivo xmi possui um WorkDefinition:
		 */
		for(Iterator i = jmiCollection.iterator(); i.hasNext();){
			Object jmiObject = i.next();
			if (jmiObject instanceof WorkDefinition){
				Logger.global.info("WorkDefinition: "+((WorkDefinition)jmiObject).getName());
				return (WorkDefinition)jmiObject;
			}
		}
		return null;
	}

}
