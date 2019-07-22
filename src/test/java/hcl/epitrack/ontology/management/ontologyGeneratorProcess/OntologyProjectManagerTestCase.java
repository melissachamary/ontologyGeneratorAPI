package hcl.epitrack.ontology.management.ontologyGeneratorProcess;

import junit.framework.TestCase;
import java.io.File;
import java.io.IOException;

import hcl.epitrack.ontology.management.exception.DependenciesException;
import hcl.epitrack.ontology.management.exception.OntologyMetaDataException;
import hcl.epitrack.ontologygenerator.project.ProjectOntology;

public class OntologyProjectManagerTestCase extends TestCase {
	public void testOntologyIdsMetaDataError()  {
		System.out.println("---- testOntologyIdsMetaDataError ---- ");
		String path = "src/test/resources/projectOntologyGenerator/testOntologyIdsMetaDataError/";
		File jsonOntologyMetaData = new File(path+"/oMD.json") ; 
		File jsonOntologyInfo = new File(path+"/oDS.json") ; 
		try {
			new ProjectOntology(jsonOntologyMetaData, jsonOntologyInfo);
			fail("Une exception de type OntologyMetaDataException aurait du etre levee");

		} catch (IOException | OntologyMetaDataException | DependenciesException e) {
			e.printStackTrace();
		}
		System.out.println("----------------------------------------");

	}
	public void testOntologyIdsDataSourceError() {
		System.out.println("---- testOntologyIdsDataSourceError ---- ");

		String path = "src/test/resources/projectOntologyGenerator/testOntologyIdsDataSourceError/";
		File jsonOntologyMetaData = new File(path+"/oMD.json") ; 
		File jsonOntologyInfo = new File(path+"/oDS.json") ; 
		try {
			new ProjectOntology(jsonOntologyMetaData, jsonOntologyInfo);
			fail("Une exception de type OntologyMetaDataException aurait du etre levee");

		} catch (IOException | OntologyMetaDataException | DependenciesException e) {
			e.printStackTrace();
		}
		System.out.println("----------------------------------------");
	}
	public void testDependancyCycleError(){
		System.out.println("---- testDependancyCycleError ---- ");
		String path = "src/test/resources/projectOntologyGenerator/testDependancyCycleError/";
		File jsonOntologyMetaData = new File(path+"/oMD.json") ; 
		File jsonOntologyInfo = new File(path+"/oDS.json") ; 
		try {
			new ProjectOntology(jsonOntologyMetaData, jsonOntologyInfo);
			fail("Une exception de type OntologyMetaDataException aurait du etre levee");
		} catch (IOException | OntologyMetaDataException | DependenciesException e) {
			e.printStackTrace();
		}
		System.out.println("----------------------------------------");
	}
	
	//TODO 
	public void testOntologyProjectMD() {

	}
}
