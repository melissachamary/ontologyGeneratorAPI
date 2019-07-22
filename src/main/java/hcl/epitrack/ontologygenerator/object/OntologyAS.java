package hcl.epitrack.ontologygenerator.object;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public interface OntologyAS {
	/**
	 * Ontology class 
	 * @param f : File
	 * @exception : OWLOntologyStorageException, FileNotFoundException
	 */
	public void save(File f) throws OWLOntologyStorageException, FileNotFoundException;
	
	/**
	 * Ontology getter 
	 * @return OWLOntology 
	 */
	public OWLOntology getOWLOntology();
	
	/**
	 * getOntologyFactory return ontology data factory from OWLOntology attribute
	 * @return OWLDataFactory
	 * 
	 */
	public OWLDataFactory getOntologyFactory();
	
	/**
	 * PrefixManager getter 
	 * @return DefaultPrefixManager 
	 */
	public DefaultPrefixManager getPrefixManager();
	
	/**
	 * add axioms to current ontology Object
	 * @param axioms Set<OWLAxioms> 
	 */
	public void addAxioms(Set<OWLAxiom> axioms);
	
	/**
	 * Ontology content summary string (IRI, import count , axiom count, entities count)
	 * @return ontology sumary string
	 * 
	 */
	public String toString();
	
}
