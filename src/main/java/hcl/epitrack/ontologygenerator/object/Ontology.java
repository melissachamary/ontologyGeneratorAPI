package hcl.epitrack.ontologygenerator.object;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class Ontology implements OntologyAS {
	private OWLOntology ontology;
	private DefaultPrefixManager prefixManager;
	
	
	public Ontology(OWLOntology onto, DefaultPrefixManager pm){
		try{
		if(onto != null && pm != null){
			this.ontology = onto;
			this.prefixManager = pm;
		}else{
			throw new OWLException("[Ontology Creation Error]");
		}
		}catch(OWLException e){
			e.printStackTrace();
		}
	}
	
	
	public OWLOntology getOWLOntology() {
		return this.ontology;
	}
	
	
	public DefaultPrefixManager getPrefixManager(){
		return this.prefixManager;
	}
	
	
	public void save(File f) throws OWLOntologyStorageException, FileNotFoundException{
		this.ontology.saveOntology(new FileOutputStream(f));
		
	};
	
	/*public void save() throws OWLOntologyStorageException, FileNotFoundException{
		
	//};*/
	
	
	public String toString(){
		StringBuilder st = new StringBuilder();
		st.append("#######");
		st.append(" Ontology : ").append(this.ontology.getOntologyID().getOntologyIRI().get().getShortForm());
		st.append("\n");
		st.append("IRI ").append(this.ontology.getOntologyID().getOntologyIRI().get().toString());
		st.append("\n");
		st.append("Axiom count ").append(this.ontology.getAxiomCount());
		st.append("\n");
		st.append("Logical Axiom count ").append(this.ontology.getLogicalAxiomCount());
		st.append("\n");
		st.append("Class count ").append(this.ontology.getClassesInSignature().size());
		st.append("\n");
		st.append("Individual count ").append(this.ontology.getIndividualsInSignature().size());
		st.append("\n");
		st.append("Import count ").append(this.ontology.getImportsDeclarations().size());
		st.append("\n#######");

		return st.toString();
	}
	
	
	public void addAxioms(Set<OWLAxiom> axioms) {
		this.ontology.getOWLOntologyManager().addAxioms(this.ontology, axioms);
	}


	public OWLDataFactory getOntologyFactory() {
		return this.getOWLOntology().getOWLOntologyManager().getOWLDataFactory();
	}
	
	/*public boolean init() {
	// TODO Auto-generated method stub
	return false;
}*/


}
