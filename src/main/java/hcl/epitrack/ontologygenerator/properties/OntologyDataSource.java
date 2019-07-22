package hcl.epitrack.ontologygenerator.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import hcl.epitrack.ontology.management.exception.OntologyMetaDataException;
import hcl.epitrack.ontologygenerator.object.OntologyPrefix;

/**
 * TODO
 * @author melissa
 *
 */
public class OntologyDataSource {
	private String id;
	private File sourceFile;
	private String generatedFile;

	private OntologyPrefix prefix ;
	private Set<OntologyDataSource> dependancy ; 
	
	public OntologyDataSource(@JsonProperty("ontology_id") String id,
			@JsonProperty("owl_ref_file") String sourceFile, 
			@JsonProperty("owl_file") String generatedFile) throws OntologyMetaDataException, FileNotFoundException{
		if(id.isEmpty() || sourceFile.isEmpty()){
			throw new OntologyMetaDataException();
		}else if(!new File(sourceFile).exists() || ! new File(sourceFile).canRead()){
			System.out.println(sourceFile+" exist:"+new File(sourceFile).exists()+" readable"+new File(sourceFile).canRead());
			throw new FileNotFoundException();
		}else{
			this.id = id;
			this.sourceFile = new File(sourceFile);
			if(generatedFile.isEmpty()){
				this.generatedFile = null;
			}else{
				this.generatedFile = generatedFile;
			}
		}
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public File getGeneratedFile() {
		if(this.generatedFile.isEmpty()|| this.generatedFile == null){
			return null;
		}else{
			return new File(generatedFile);

		}
	}

	public void setGeneratedFile(String generatedFile) {
		this.generatedFile = generatedFile;
	}

	public OntologyPrefix getPrefix() {
		return prefix;
	}

	public void setPrefix(OntologyPrefix prefix) {
		this.prefix = prefix;
	}

	public Set<OntologyDataSource> getDependancy() {
		return dependancy;
	}

	public void setDependancy(Set<OntologyDataSource> dependancy) {
		this.dependancy = dependancy;
	}

	public String toString(){
		StringBuffer sb =  new StringBuffer() ;
	       return sb.append("Ontology ").append(this.id).append("\n\t ref_owl :").append(this.getSourceFile().getAbsolutePath()).
	    		   append("\n\t output_owl :").append(this.getGeneratedFile()).toString() ;

	}
}
