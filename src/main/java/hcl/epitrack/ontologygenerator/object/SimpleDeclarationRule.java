package hcl.epitrack.ontologygenerator.object;

import java.util.HashSet;
import java.util.Set;

import org.mm.parser.ParseException;
import org.mm.renderer.RendererException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import hcl.epitrack.ontology.management.exception.RuleException;
import hcl.epitrack.ontologygenerator.properties.RuleMetaData;

/**
 * SimpleDeclarationRule objects correspond to self sufficiant rule (contary to XLSXMapperRules). This class is used to describe concept, annotation axiom declaration (further object or data property rules)
 * @see RuleAS interface
 * @author melissa
 *
 */
public class SimpleDeclarationRule implements RuleAS {
	private String type; //Annotation, Class
	private String id;
	private String ontologyID;
	private String elementName;
	
	/**
	 * SimpleDeclarationRule constructor for single concept or annotation creation 
	 * @param String type in [class, annotation]
	 * @param String name entity name 
	 * @param String ruleID rule identifier
	 * @param String ontologyID ontologyID in which the rule applies
	 * @implements RuleAS
	**/
	public SimpleDeclarationRule(String type, String name, String ruleID, String ontologyID ) throws RuleException{
		if(type == null ||type.isEmpty() ){
			throw new RuleException("[RULE constructor]"+"type empty or null");
		}else if( (type.compareTo("class")>0) && type.compareTo("annotation")>0){//type.equalsIgnoreCase() && 
			throw new RuleException("[RULE constructor]"+"unrecognised type "+type);
		}else if(name.isEmpty() && (id.isEmpty()|| id == null)){
			throw new RuleException("[RULE constructor]"+"no id (rule id or elementName) to generate element ");

		}else{
			this.type = type.toLowerCase();
			this.id=ruleID;
			this.ontologyID = ontologyID;
			if(name.isEmpty()){
				this.elementName = this.id;
			}else{
				this.elementName = name;
			}
		}
	}
	/**
	 
	 */
	public Set<OWLAxiom> apply(OntologyAS o) {
		Set<OWLAxiom> axSet = new HashSet<OWLAxiom>();
		switch(this.type) {
		   case "annotation" :
			   axSet = generateAnnotationDeclaration(o);
			   break; 
		   
		   case "class" :
			   axSet = generateConceptAxiom(o);
		      break; 
		}
		return axSet;
	}
	
	public boolean applicable() {
		return true;
	}
	

	public String getID(){
		return this.id;
	}

	public String getOntologyID() {
		return this.ontologyID;
	}

	/**
	 * getRuleDependancy is useless function because simpleRulesDeclaration objects should'nt depend on anything 
	 * @return empty HashSet  
	 */
	public Set<String> getRuleDependancy() {
		return new HashSet<String>();
	}

	
	/**
	 * init function is useless in this class 
	 */
	public void init(RuleMetaData md) {
		System.out.println("[SimpleDeclarationRule init] shouldn't have any RuleMetaData parameter");
	}
	public String toString(){
		return("rule : "+this.id+"\n\t applicable = "+this.applicable()+"\n\t ruleType = "+this.type);
	}
	
	/**
	 * generateConceptAxiom privatz method to generate concept declaration axiom
	 * @param df
	 * @param pm
	 * @return
	 */
	private Set<OWLAxiom> generateConceptAxiom(OntologyAS o){
		OWLDataFactory df = o.getOntologyFactory();
		DefaultPrefixManager pm = o.getPrefixManager();
		
		Set<OWLAxiom> axSet = new HashSet<OWLAxiom>();
		OWLClass cl = df.getOWLClass(this.elementName, pm);
		OWLDeclarationAxiom ax = df.getOWLDeclarationAxiom(cl);
		axSet.add(ax);
		return axSet;
	}

	/**
	 * 
	 * @param df OWLDataFactory
	 * @param pm DefaultPrefixManager
	 * @return
	 */
	private Set<OWLAxiom> generateAnnotationDeclaration(OntologyAS o){
		OWLDataFactory df = o.getOntologyFactory();
		DefaultPrefixManager pm = o.getPrefixManager();
		
		Set<OWLAxiom> axSet = new HashSet<OWLAxiom>();
		OWLAnnotationProperty an = df.getOWLAnnotationProperty(this.elementName, pm);
		OWLDeclarationAxiom ax = df.getOWLDeclarationAxiom(an);
		axSet.add(ax);
		return axSet;
	}
	
	
}
