package hcl.epitrack.ontologygenerator.object;

import hcl.epitrack.ontology.management.exception.OntologyMetaDataException;
/**
 * OntologyPrefix is a two attributes object to describe ontology prefix ans namespace metadata
 * @author melissa
 *
 */
public class OntologyPrefix {
	private String prefix;
	private String namespace;
	
	/**
	 * 
	 * @param prefix : String
	 * @param namespace : String 
	 * @throws OntologyMetaDataException (if prefix or namespace is empty)
	 */
	public OntologyPrefix(String prefix, String namespace) throws OntologyMetaDataException{
		if(prefix.isEmpty() || namespace.isEmpty()){
			throw new OntologyMetaDataException();
		}
		this.prefix= prefix;
		this.namespace= namespace;
	}
	
/**
 * 	prefix getter
 * @return String
 */
	public String getPrefix() {
		return prefix;
	}
/**
 * namespace getter
 * @return String
 */
	public String getNamespace() {
		return namespace;
	}
	
/**
 * toString method return String description of the object
 * pattern : Prefix namespace:<prefix> getter
 * @return String
 */
	public String toString() {
	      StringBuffer sb =  new StringBuffer() ;
	       return sb.append("Prefix ").append(this.namespace).append(":<").append(this.prefix).append(">").toString() ;
	   }
	
}
