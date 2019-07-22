package hcl.epitrack.ontologygenerator.object;

import java.io.IOException;
//import java.util.HashSet;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.mm.parser.ParseException;
import org.mm.renderer.RendererException;
import org.semanticweb.owlapi.model.OWLAxiom;
//import org.semanticweb.owlapi.model.OWLDataFactory;
//import org.semanticweb.owlapi.model.OWLOntology;
//import org.semanticweb.owlapi.util.DefaultPrefixManager;

import hcl.epitrack.ontologygenerator.properties.RuleMetaData;
import hcl.epitrack.ontologygenerator.properties.jacksonserializers.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import hcl.epitrack.ontology.management.exception.RuleException;


@JsonDeserialize(using = RuleDeserializer.class)
/**
 * RuleAS interface describe methods to check and build concepts, relations and annotation from JSON paramater Rule description files.
 * @author melissa
 *
 */
public interface RuleAS {
	/**
	 * apply return AxiomSet corresponding to the rule
	 * @param Ontology
	 * @return boolean
	 * @exception RuleException, RendererException, ParseException;
	 */
	public Set<OWLAxiom> apply(OntologyAS o) throws RuleException, RendererException, ParseException;
	
	/**
	 * applicable return true iff rule can be processed i.e. if all data are correctly set
	 * @return boolean
	 */
	public boolean applicable();
	
	/**
	 * getOntologyID return the ontologyID in which rule should be applied
	 * @return boolean
	 */
	public String getOntologyID();
	
	/**
	 * getOntologyID return the ruleID
	 * @return boolean
	 */
	public String getID();
	
	/**
	 * getRuleDependancy return HashSet<String> of rule id. 
	 * NB : SimpleDeclarationRules shouldn't depend on anything
	 * @return HashSet<String> that corresponds to ruleID
	 */
	public Set<String> getRuleDependancy();
	
	/**
	 * getRuleDependancy return HashSet<String> of rule id. 
	 * Nota Bene : SimpleDeclarationRules shouldn't depend on anything
	 * @return HashSet<String> that corresponds to ruleID
	 * @exception EncryptedDocumentException, InvalidFormatException, IOException, RuleException
	 */
	public void init(RuleMetaData md) throws EncryptedDocumentException, InvalidFormatException, IOException, RuleException;
	
	/**
	 * Rule description string
	 * @return shorten rule description (rule id, rule pattern/type and applicable) 
	 */
	public String toString();

}
