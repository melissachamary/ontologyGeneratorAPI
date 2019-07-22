package hcl.epitrack.ontologygenerator.properties;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import com.fasterxml.jackson.annotation.JsonProperty;

import hcl.epitrack.ontology.management.exception.OntologyMetaDataException;
import hcl.epitrack.ontologygenerator.object.OntologyPrefix;

/**
 * OntologyMetaData objects describe general ontology organisation and dependancies
 * @author melissa
 *
 */
public class OntologyMetaData implements Serializable{
	private static final long serialVersionUID = 10128901800L;

	private String id;
	private IRI iri;
	private OntologyPrefix prefix;
	private Set<String> dependancy;
	
	
/**
 * OntologyMetaData constructor linked to the JSON parser (jackson api)
 * Notice that the ontology IRI is calculated by removing last character of prefix String
 * @param id : String - ontology id
 * @param prefix : String - ontology prefix (common part of URI for all elements into the ontology)
 * @param namespace : String - namespace used for the ontology whithin the current ontology as well as for dependancies management
 * @param dependancy : HashSet<String> - list of ontology dependancies (by id) 
 * @throws OntologyMetaDataException (if id is empty or null or OntologyPrefix object creation fails)
 */
	public OntologyMetaData(@JsonProperty("ontology_id") String id, 
			@JsonProperty("ontology_prefix") String prefix , 
			@JsonProperty("prefix_name") String namespace,
			@JsonProperty("ontology_dependancy") HashSet<String> dependancy) throws OntologyMetaDataException{
		this.prefix = new OntologyPrefix(prefix, namespace);
		if(id.isEmpty()){
			throw new OntologyMetaDataException();
		}
		else{
			this.id=id;
			this.iri = IRI.create(prefix.substring(0,prefix.length()-1));
			this.dependancy= dependancy;
		}
	}
	
	/**
	 * 
	 * @param id
	 * @param prefix
	 * @throws OntologyMetaDataException
	 */
	public OntologyMetaData(String id,OntologyPrefix prefix ) throws OntologyMetaDataException{
		this.prefix = prefix;
		if(id.isEmpty()){
			throw new OntologyMetaDataException();
		}
		else{
			this.id=id;
			this.dependancy= new HashSet<String>();
		}
	}
	
	
	/**
	 * ontology iri getter
	 * @return IRI 
	 */
	public IRI getIri() {
		return iri;
	}
	
	/**
	 * ontology iri setter
	 * @param iri : IRI
	 */
	public void setIri(IRI iri) {
		this.iri = iri;
	}
	
	/**
	 * ontology dependancy setter
	 * @param dep
	 */
	public void setDependancy(Set<String> dep){
		dependancy = dep;
	}
	
	/**
	 * ontology id getter
	 * @return String
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * ontology id setter
	 * @param id (String)
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * ontology prefix getter
	 * @return OntologyPrefix
	 */
	public OntologyPrefix getPrefix() {
		return prefix;
	}
	/**
	 * ontology prefix setter
	 * @param OntologyPrefix
	 */
	public void setPrefix(OntologyPrefix pref) {
		this.prefix = pref;
	}
	
	/**
	 * ontology id dependancies getter
	 * @return Set<String>
	 */
	public Set<String> getDependancy() {
		if(dependancy == null){
			return new HashSet<String>();
		}else{
			return dependancy;
		}
	}
	
	/**
	 * toString return string that sumarize the ontology metadata object information
	 * @retirn String (ID;prefix)>
	 */
	public String toString() {
	      StringBuffer sb =  new StringBuffer() ;
	       return sb.append("Ontology ").append(this.id).append("\n\t").append(this.prefix.toString()).toString() ;
	   }
}

