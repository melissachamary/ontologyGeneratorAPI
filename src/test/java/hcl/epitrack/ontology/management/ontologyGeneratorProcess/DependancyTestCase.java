package hcl.epitrack.ontology.management.ontologyGeneratorProcess;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.HashSet;

import hcl.epitrack.ontology.management.exception.DependenciesException;
import hcl.epitrack.ontologygenerator.object.DependencyObject;

public class DependancyTestCase extends TestCase {
	
	
	public void testCyclicityDirectError() {
		System.out.println("----- testCyclicityDirectError ----");
		HashMap<String, HashSet<String>> cyclicDirectDep = new HashMap<String, HashSet<String>>(){{
			put("a", new HashSet<String>(){{ add("b") ;}} );
			put("b", new HashSet<String>(){{ add("a") ;}} );
			put("c", new HashSet<String>(){{ add("a") ;}} );

		}};
		
		try {
			new DependencyObject(cyclicDirectDep) ;
			fail("Should raise error");
		}catch (DependenciesException e) {
			System.err.println(e);
		}
		
	}
	
	public void testCyclciclicityIndirectError() {
		System.out.println("----- testCyclciclicityIndirectError ----");

		HashMap<String, HashSet<String>> cyclicIndDep = new HashMap<String, HashSet<String>>(){{
			put("a", new HashSet<String>(){{ add("c") ;}} );
			put("b", new HashSet<String>(){{ add("a") ;}} );
			put("c", new HashSet<String>(){{ add("b") ;}} );

		}};
		try {
			new DependencyObject(cyclicIndDep) ;
			fail("Should raise error");
		}catch (DependenciesException e) {
			System.err.println(e);

		}
		
	}
	
	public void testSubsetError() {
		System.out.println("----- testSubsetError ----");
		HashMap<String, HashSet<String>> depHM = new HashMap<String, HashSet<String>>(){{
			put("a", new HashSet<String>() );
			put("b", new HashSet<String>(){{ add("a") ;}} );
			put("c", new HashSet<String>(){{ add("b") ;}} );
			put("d", new HashSet<String>());
		}};
		try {
		HashSet<String> subE = new HashSet<String>()
				{{ add("b") ;
				   add("c") ;
				}} ; 
		DependencyObject dep = new DependencyObject(depHM) ;
		assertNotNull(dep);
		dep.subset(subE) ;
		fail("Should raise error");
		}catch (DependenciesException e) {
			System.err.println(e);

		}
	}
	public void testSubsetPass() {
		System.out.println("----- testSubsetError ----");
		HashMap<String, HashSet<String>> depHM = new HashMap<String, HashSet<String>>(){{
			put("a", new HashSet<String>() );
			put("b", new HashSet<String>(){{ add("a") ;}} );
			put("c", new HashSet<String>(){{ add("b") ;}} );
			put("d", new HashSet<String>());
		}};
		try {
		HashSet<String> subData = new HashSet<String>()
				{{ add("a") ;
				   add("d") ;
				}} ; 
		DependencyObject dep = new DependencyObject(depHM) ;
		assertNotNull(dep);
		DependencyObject sub = dep.subset(subData) ;
		System.out.println(sub.getDependenciesAdjacencyList());
		assertEquals(sub.getDependenciesAdjacencyList().size(),2);
		
		}catch (DependenciesException e) {
			System.err.println(e);
			fail("shouldn't raise error");

		}
	}
}
