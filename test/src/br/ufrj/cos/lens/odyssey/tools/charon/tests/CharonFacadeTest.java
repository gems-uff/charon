package br.ufrj.cos.lens.odyssey.tools.charon.tests;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.xmi.XmiReader;

import junit.framework.TestCase;

import org.netbeans.api.mdr.MDRepository;
import org.netbeans.api.xmi.XMIReaderFactory;
import org.netbeans.mdr.NBMDRepositoryImpl;

import processstructure.WorkDefinition;
import spem.SpemPackage;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonException;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonFacade;

public class CharonFacadeTest extends TestCase {

	/**
	 * Metamodel extent
	 */
    private static final String METAMODEL_EXTENT = "METAMODEL";
	
	/**
	 * Model extent
	 */
    private static final String MODEL_EXTENT = "MODEL";
    
	/**
	 * SPEM metamodel
	 */
    private static final String SPEM_METAMODEL = "/resource/02-05-06.xml";
    
	/**
	 * Test instance of SPEM metamodel
	 */
	private static final String SPEM_MODEL = "/resource/input.xmi";
	
	/**
	 * MDR repository
	 */
	private MDRepository repository = null;
	
	/**
	 * Spem model (instance)
	 */
	private SpemPackage spemPackage = null;

	/**
	 * Initializes the repository and loads the test model
	 */
	protected void setUp() throws Exception {
		super.setUp();

		// Create instance of xmi reader
		XmiReader xmiReader = XMIReaderFactory.getDefault().createXMIReader();
		
		// Create a in-memory repository
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("storage", "org.netbeans.mdr.persistence.memoryimpl.StorageFactoryImpl");            
		repository = new NBMDRepositoryImpl(properties);

		// Init Repository with SPEM metamodel
		ModelPackage modelPackage = (ModelPackage)repository.createExtent(METAMODEL_EXTENT);
		URL xmiSpemMetamodel = this.getClass().getResource(SPEM_METAMODEL);
		xmiReader.read(xmiSpemMetamodel.toString(), modelPackage);			

		// Init the SPEM model extent
		Iterator iterator = modelPackage.getMofPackage().refAllOfClass().iterator();
		while (iterator.hasNext()) {
			MofPackage mofPackage = (MofPackage)iterator.next();
			if ("SPEM".equals(mofPackage.getName())) {
				spemPackage = (SpemPackage)repository.createExtent(MODEL_EXTENT, mofPackage);
				URL xmiSpemModel = this.getClass().getResource(SPEM_MODEL);
				xmiReader.read(xmiSpemModel.toString(), spemPackage);
				break;
			}
		}
	}
	
	/**
	 * Test method for 'br.ufrj.cos.lens.odyssey.tools.charon.CharonFacade.instanciaProcesso(Object, Package)'
	 */
	public final void testInstanciaProcesso() throws CharonException {
		// Search for a Work Definition
		WorkDefinition workDefinition = null;
		Iterator iterator = spemPackage.getProcessStructure().getWorkDefinition().refAllOfClass().iterator();
		while (iterator.hasNext()) {
			workDefinition = (WorkDefinition)iterator.next();
			if ("WorkDefinition".equals(workDefinition.getName()))
				break;
		}

		CharonFacade.getInstance().instantiateProcess("Test Context", spemPackage, workDefinition);
	}
	
	/**
	 * Shutdown the repository
	 */
	protected void tearDown() throws Exception {
		repository.shutdown();
		super.tearDown();		
	}
}
