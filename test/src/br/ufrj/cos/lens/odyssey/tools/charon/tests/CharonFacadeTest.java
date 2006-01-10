package br.ufrj.cos.lens.odyssey.tools.charon.tests;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import statemachines.PseudoState;
import br.ufrj.cos.lens.odyssey.tools.charon.Charon;
import br.ufrj.cos.lens.odyssey.tools.charon.CharonException;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonActivity;
import br.ufrj.cos.lens.odyssey.tools.charon.entities.CharonDecision;

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
    private static final String SPEM_METAMODEL = "/02-05-06.xml";
    
	/**
	 * Test instance of SPEM metamodel
	 */
	private static final String SPEM_MODEL = "/input.xmi";
	
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
	 * @throws InterruptedException 
	 */
	@SuppressWarnings("unchecked")
	public synchronized final void testInstanciaProcesso() throws CharonException, InterruptedException {
		// Search for a Work Definition
		WorkDefinition workDefinition = null;
		Iterator iterator = spemPackage.getProcessStructure().getWorkDefinition().refAllOfClass().iterator();
		while (iterator.hasNext()) {
			workDefinition = (WorkDefinition)iterator.next();
			if ("WorkDefinition".equals(workDefinition.getName()))
				break;
		}

		Charon charon = new Charon("c:\\teste");
		
		charon.addProcess(spemPackage);

		String processId = charon.instantiateProcess(workDefinition);
		
		Collection processPerformers = spemPackage.getProcessStructure().getProcessPerformer().refAllOfType();

		// List all pending activities
		Collection<CharonActivity> pendingActivities = charon.getPendingActivities(processPerformers);
		for (CharonActivity charonActivity : pendingActivities) {
			//CallAction callAction = (CallAction)charonActivity.getSpemActivity(repository).getEntry();
			//WorkDefinition activity = (WorkDefinition)callAction.getOperation();
			System.out.println("Activity: " + charonActivity.getId() + " | Context: " + charonActivity.getContext() + " | Performers: " + charonActivity.getPerformers());
		}
		
		// Set the performers of the activities
		wait(1000); // It is here because the prolog machine cannot handle the size of currentTimeMilis. For this reason, we work with seconds.
		List<String> performers = new ArrayList<String>();
		performers.add("murta");
		performers.add("luizgus");
		charon.setPerformers(performers, pendingActivities);
		
		// List all pending activities
		pendingActivities = charon.getPendingActivities(processPerformers);
		for (CharonActivity charonActivity : pendingActivities) {
			//CallAction callAction = (CallAction)charonActivity.getSpemActivity(repository).getEntry();
			//WorkDefinition activity = (WorkDefinition)callAction.getOperation();
			System.out.println("Activity: " + charonActivity.getId() + " | Context: " + charonActivity.getContext() + " | Performers: " + charonActivity.getPerformers());
		}

		// Finishes these activities
		wait(1000); // It is here because the prolog machine cannot handle the size of currentTimeMilis. For this reason, we work with seconds.
		charon.finishActivities("Test User", pendingActivities);
		
		charon.save();

		// List all pending decisions
		Collection<CharonDecision> pendingDecisions = charon.getPendingDecisions(processPerformers);
		for (CharonDecision charonDecision : pendingDecisions) {
			PseudoState decision = charonDecision.getSpemDecision(repository);
			System.out.println("Decision: " + charonDecision.getId());
			
			// Make a decision (Activity)
			for (String option : charonDecision.getOptions(repository)) {
				if ("no".equals(option)) {
					charonDecision.addSelectedOption(option);
					System.out.println("Selection: " + option);
				}
			}
		}
		
		// Make the decisions
		wait(1000);
		charon.makeDecisions("Test User", pendingDecisions);
		
		// List all pending activities
		pendingActivities = charon.getPendingActivities(processPerformers);
		for (CharonActivity charonActivity : pendingActivities) {
			//CallAction callAction = (CallAction)charonActivity.getSpemActivity(repository).getEntry();
			//WorkDefinition activity = (WorkDefinition)callAction.getOperation();
			System.out.println("Activity: " + charonActivity.getId() + " | Context: " + charonActivity.getContext() + " | Performers: " + charonActivity.getPerformers());
		}
	}
	
	/**
	 * Shutdown the repository
	 */
	protected void tearDown() throws Exception {
		repository.shutdown();
		super.tearDown();		
	}
}
