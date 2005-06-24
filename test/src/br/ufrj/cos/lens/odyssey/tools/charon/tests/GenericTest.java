package br.ufrj.cos.lens.odyssey.tools.charon.tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.reflect.RefPackage;
import javax.jmi.xmi.MalformedXMIException;
import javax.jmi.xmi.XmiReader;
import javax.jmi.xmi.XmiWriter;

import junit.framework.TestCase;

import modelmanagement.ModelManagementPackage;
import modelmanagement.Package;

import org.netbeans.api.mdr.CreationFailedException;
import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.api.xmi.XMIReaderFactory;
import org.netbeans.api.xmi.XMIWriterFactory;

import activitygraphs.ActivityGraph;

import processstructure.WorkDefinition;
import processstructure.WorkDefinitionClass;
import spem.SpemPackage;

/**
 * Provides access to XMI read (and write) facilities
 * Specific for SPEM 
 * @author murta, luizgus
 * @version $Id$
 */
public class GenericTest extends TestCase {
  
	/**
	 * Metamodel extent names
	 */
    private static final String EXTENT_NAME = "SPEM";
	
	/**
	 * Metamodel XMI file names
	 */
    private static final String XMI_FILE = "/resource/02-05-06.xml";
    
	/**
	 * Output XMI file name
	 */
    private static final String OUTPUT_FILE = "output.xml";

	/**
	 * In-memory repository of XMI data
	 */
	private MDRepository repository = null;
	
	/**
	 * XmiReader instance
	 */
	private XmiReader xmiReader;

	/**
	 * XmiWriter instance
	 */
	private XmiWriter xmiWriter;
	
	/**
	 * Model element used to instantiate the metamodel
	 */
	private MofPackage modelPackage;
	
	/**
	 * Counts the number of instantiated models
	 */
	private int instancesCounter = 0;

	/**
	 * Prepares the environment, opens and init the repository
	 * @param metaModelType Type of the metamodel which this Facade will deal with
	 */
	public void testGeneric() throws CreationFailedException, IOException, MalformedXMIException {
		
		// Create instance of xmi reader and xmi writer
		xmiReader = XMIReaderFactory.getDefault().createXMIReader();
		xmiWriter = XMIWriterFactory.getDefault().createXMIWriter();
		
		// Create the repository
		repository = createMDRepository();

		// Init Repository with a metamodel
		// @TODO Verify if this "if" is needed
		if (modelPackage == null) {
			ModelPackage metaModel = createMetamodel();

			// Get the model package
			modelPackage = getModelElement(metaModel, EXTENT_NAME);
			
			process();
		}
	}

	/**
	 * Creates a in-memory MDR repository
	 * @return In-memory MDR repository
	 */
	private MDRepository createMDRepository() {
		System.setProperty("org.netbeans.mdr.storagemodel.StorageFactoryClassName", "org.netbeans.mdr.persistence.memoryimpl.StorageFactoryImpl");            
		MDRManager manager = MDRManager.getDefault();
		return manager.getDefaultRepository();
	}
		
	/**
	 * Create and populate a metamodel 
	 * @param type Type of metamodel to be created 
	 * @return metamodel
	 */
	private ModelPackage createMetamodel() throws CreationFailedException, IOException, MalformedXMIException {
		ModelPackage metaModel = (ModelPackage)repository.getExtent(EXTENT_NAME);
		if (metaModel == null){
			metaModel = (ModelPackage)repository.createExtent(EXTENT_NAME);
			URL url = this.getClass().getResource(XMI_FILE);
			xmiReader.read(url.toString(), metaModel);			
		}
		return metaModel;
	}
	
	/**
	 * Finds the requested package inside a metamodel
	 * @param metaModel Metamodel to query the searched package
	 * @param packageName Name of the searched package
	 * @return requested package or null if the package is not found
	 */
	private MofPackage getModelElement(ModelPackage metaModel, String packageName) {
		Iterator iterator = metaModel.getMofPackage().refAllOfClass().iterator();
		while (iterator.hasNext()) {
			MofPackage mofPackage = (MofPackage)iterator.next();
			if (packageName.equals(mofPackage.getName()))
				return mofPackage;
		}
		return null;
	}
	
	/**
	 * Reads Odyssey metamodel objects from a XMI stream.
	 * @param stream XMI input stream for reading
	 * @return Collection of JMI objects 
	 */
	private void process() throws IOException, MalformedXMIException, CreationFailedException {
		SpemPackage spemPackage = (SpemPackage)createModel();
		FileOutputStream fileOut = new FileOutputStream(OUTPUT_FILE);

		WorkDefinition wd = spemPackage.getProcessStructure().getWorkDefinition().createWorkDefinition("Work Definition 1");
		Package pck = spemPackage.getModelManagement().getPackage().createPackage("Package 1");
		
		ActivityGraph ag = spemPackage.getActivityGraphs().getActivityGraph().createActivityGraph();
		ag.setContext(wd);
		
		//pck.setNamespace(spemPackage);
		
		//spemPackage.
		
		xmiWriter.write(fileOut, spemPackage, "Test");
		deleteModel(spemPackage);
	}
	
	private synchronized RefPackage createModel() throws CreationFailedException {
		String name = EXTENT_NAME + " INSTANCE " + ++instancesCounter;
		return (RefPackage)repository.createExtent(name, modelPackage);
	}
	
	private void deleteModel(RefPackage refPackage) {
		refPackage.refDelete();
	}
}
