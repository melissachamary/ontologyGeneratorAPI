package hcl.epitrack.ontologygenerator.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.cycle.CycleDetector;


import org.jgrapht.graph.DirectedMultigraph;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import hcl.epitrack.ontology.management.exception.DependenciesException;
import hcl.epitrack.ontology.management.exception.OntologyMetaDataException;
import hcl.epitrack.ontologygenerator.object.Ontology;
import hcl.epitrack.ontologygenerator.object.DependencyObject;
import hcl.epitrack.ontologygenerator.object.OntologyPrefix;
import hcl.epitrack.ontologygenerator.properties.OntologyDataSource;
import hcl.epitrack.ontologygenerator.properties.OntologyMetaData;


 
/**
 * ProjectOntologyMD object intend to manage an ecosystems of ontologies using OntologyDataSource and OntologyMetaData objects.
 * This class enable to take in account ontology dependencies to initiateOntologies and XXXX 	 
 * @author melissa
 *
 */
public class ProjectOntology {
	// data source
	protected ArrayList<OntologyMetaData> rawOMD;
	protected ArrayList<OntologyDataSource> rawODS;
	
	protected HashMap<String, OntologyMetaData> hashOMD;
	protected HashMap<String, OntologyDataSource> hashODS;

	// Mandatory elements to implement the ontologies
	protected HashMap<String,OntologyPrefix> prefixManager;
	protected HashMap<String,IRI> iriManager;
	protected HashMap<String, File> ontoSource;

	protected DependencyObject dependancies_m;
	
	/**
	 * ProjectOntologyMD constructor intends to create ontology java project using json metadata files. It enables to
	 *	- check json metadata consistency 
	 *	- check ontology dependancies 
	 * @param jsonOntologyMetaData : unmutable parameter containing ontology prefix & dependancies (expressed using ontology-id)
	 * @param jsonOntologyInfo : version dependant parameter containing version information & data sources
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws OntologyMetaDataException 
	 * @throws DependanciesException 
	 */
	
	public ProjectOntology(File jsonOntologyMetaData, File jsonOntologyInfo) throws JsonParseException, JsonMappingException, IOException, OntologyMetaDataException, DependenciesException{
	//	 try {
			 ObjectMapper mapper = new ObjectMapper();
			 mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			 TypeFactory typeFactory = mapper.getTypeFactory();
			 CollectionType arrayOMD = typeFactory.constructCollectionType( ArrayList.class, OntologyMetaData.class );	    		
			 CollectionType arrayODS = typeFactory.constructCollectionType(ArrayList.class, OntologyDataSource.class);
			 
			/*-- OntologyMetaData : generation of dependanciesIndirect + prefixManager + IRIManager -- */
			ArrayList<OntologyMetaData> metadataList = mapper.readValue(jsonOntologyMetaData,arrayOMD);
			this.rawOMD = metadataList;
			this.prefixManager = new HashMap<String, OntologyPrefix>();
			this.iriManager = new HashMap<String,IRI>();
			this.hashOMD = new HashMap<String, OntologyMetaData>();
			
			HashMap<String, HashSet<String>> dependancies = new HashMap<String, HashSet<String>>();
			/*--- Ontology attribute set and check ---*/
			Iterator<OntologyMetaData> itOMD = this.rawOMD.iterator();
			while(itOMD.hasNext()){
				OntologyMetaData omd = itOMD.next();
				this.prefixManager.put(omd.getId(), omd.getPrefix());
				this.iriManager.put(omd.getId(),omd.getIri());
				this.hashOMD.put(omd.getId(), omd);
				
			dependancies.put(omd.getId(), (HashSet<String>) omd.getDependancy()); //Direct dependancy storage

			}
			this.dependancies_m = new DependencyObject(dependancies);//new DependenciesManagement<String>(new HashMap<String, HashSet<String>>(dependancies));
			this.dependancies_m.check(null);

			ArrayList<OntologyDataSource> datasourceList = mapper.readValue(jsonOntologyInfo,arrayODS);			
			this.rawODS = datasourceList;
			this.ontoSource = new HashMap<String, File>();
			this.hashODS = new HashMap<String, OntologyDataSource>();

			Iterator<OntologyDataSource> itODS = this.rawODS.iterator();
			while(itODS.hasNext()){
				OntologyDataSource ods = itODS.next();
				this.ontoSource.put(ods.getId(), ods.getSourceFile());
				this.hashODS.put(ods.getId(), ods);
			}
			this.check();

		
	}
	/**
	 * ontology dependancies getter
	 * @return HashMap<String, HashSet<String>>
	 */
	public DependencyObject getDependancies(){
		return this.dependancies_m;
	}
	
	
	/**
	 * updateOntologySources : enable to update the link between ontology (ontology-id) & the file to find it. 
	 * By default link is set using ontology-source field in OntologyDataSource json parameter
	 * @Use in ProjectOntologyGeneratorMD when @schedule method is called and after the generation of ontologies (@TODO)
	 * @param id
	 * @param f
	 */
	protected void updateOntologySources(String id, File f){
		this.ontoSource.replace(id, f);
	}

	/**
	 * initOntology : manage the instanciation of Ontology object in order to be usable in java project
	 * This method loads explicitly  dependancies, manages prefix & adjusts IRI mapper to local file.
	 * TODO review code to link this.dependancies to this.dependancies_m ==> DONE to check
	 * @param id String 
	 * @return Ontology
	 * @throws OntologyMetaDataException ids doesn't refers to any ontology in initial files
	 * @throws OWLOntologyCreationException if loadOntology Fails
	 */
	public Ontology initOntology(String id) throws OntologyMetaDataException, OWLOntologyCreationException{
		Ontology onto = null;
		// 1. Vérifier que l'id existe
		if( ! (this.prefixManager.containsKey(id) && this.ontoSource.containsKey(id) 
				&& this.ontoSource.containsKey(id))){
			throw new OntologyMetaDataException("[initOntology]"+id+"doesn't exists in mandatory ontology meta-data : \n\t "+
					"prefix manager:"+id+" "+this.prefixManager.keySet()+" "+this.prefixManager.containsKey(id)+ "\n\t"+
					"dependancy:"+id+" "+this.dependancies_m.getDependenciesAdjacencyList().keySet()+" "+
					                     this.dependancies_m.getDependenciesAdjacencyList().containsKey(id)+"\n\t"+
					"ontoSource"+id+" "+this.ontoSource.keySet()+" "+this.ontoSource.containsKey(id));
		}
		
		// 2. loader fichier source correspondant dans une ontologie + ajout du prefix de l'ontologie dans l'ontologie
		OWLOntology ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(
					this.ontoSource.get(id));
			System.out.println(this.ontoSource.get(id));
			addIriMapper(id, ontology);
			loadDependancy(id, ontology);
			DefaultPrefixManager prefixM = getOWLPrefixManager(id);
			 onto = new Ontology(ontology, prefixM);		
		return(onto);
	}
	
	
	/**
	 * getOWLPrefixManager : set the prefix manager for specific ontology id
	 * @Use in @init method
	 * @param id : ontology-id
	 * @return DefaultPrefixManager (OWLAPI)
	 */
	private DefaultPrefixManager getOWLPrefixManager(String id){
		DefaultPrefixManager pm = new DefaultPrefixManager();
		OntologyPrefix p = this.prefixManager.get(id);
		pm.setDefaultPrefix(p.getPrefix());
		Iterator<String> itDependancy = this.dependancies_m.getDependencies(id).iterator();
		while(itDependancy.hasNext()){
			String idDep= itDependancy.next();
			p = this.prefixManager.get(idDep);
			pm.setPrefix(p.getNamespace()+":", p.getPrefix());
		}	
		return pm;		
	}
	
	/**
	 * /!\ should be split in two method : getIRIMapper (id) in this object & add IRI mapper (Ontology object) 
	 * @param id
	 * @param onto
	 */
	private void addIriMapper(String id ,OWLOntology onto){
		
		Set<OWLOntologyIRIMapper> mappers = new HashSet<OWLOntologyIRIMapper>();
		SimpleIRIMapper m = new SimpleIRIMapper(this.iriManager.get(id),
				IRI.create(this.ontoSource.get(id)));
		mappers.add(m);
		System.out.println(this.dependancies_m.getDependenciesAdjacencyList());
		if(this.dependancies_m.getDependencies(id) != null){
			Iterator<String> itDependancy = this.dependancies_m.getDependencies(id).iterator();
			while(itDependancy.hasNext()){
				String idDep= itDependancy.next();
				m = new SimpleIRIMapper(this.iriManager.get(idDep),
						IRI.create(this.ontoSource.get(idDep)));
				mappers.add(m);
				System.out.println(IRI.create(this.ontoSource.get(idDep)));
			}
		}
			
		onto.getOWLOntologyManager().setIRIMappers(mappers);
	}

	/**
	 * /!\ should be split in two method : getDependancies (id) in this object & loadDependancies (Ontology object) 
	 * @param id
	 * @param onto
	 * TODO change reference to this.dependencies into this.dependencies_m
	 */
	private void loadDependancy(String id ,OWLOntology onto){
		if(this.dependancies_m.getDependencies(id) != null){
			Iterator<String> itDependancy = this.dependancies_m.getDependencies(id).iterator();//.get(id).iterator();
			
			while(itDependancy.hasNext()){
				String idDep= itDependancy.next();

				
				System.out.println("ONTO_ID : "+id+" source "+this.iriManager.get(idDep));
				OWLImportsDeclaration importDeclaration= onto.getOWLOntologyManager().getOWLDataFactory().
						getOWLImportsDeclaration(IRI.create(this.ontoSource.get(idDep)));//this.iriManager.get(idDep),);
				onto.getOWLOntologyManager().applyChange(new AddImport(onto, importDeclaration));
				onto.getOWLOntologyManager().makeLoadImportRequest(importDeclaration);
		}
		

		}else{
			System.out.println(this.dependancies_m.getDependenciesAdjacencyList().containsKey(id));
		}
		
	}
/**
 * Check element integrity in json files (id uniqueness, ontologydependancy etc.)
 * @return boolean
 * @throws OntologyMetaDataException
 */
	
	/**
	 * Check is called to verify consistency when object is build
	 * - verify the unicity of ontology ids
	 * - verify all ids refers in dependancies exists
	 * - @TODO verify the cyclicity of ontology dependancies (fail if cycle)
	 * @return boolean
	 * @throws OntologyMetaDataException
	 * @throws DependenciesException 
	 */
	protected void check() throws OntologyMetaDataException, DependenciesException{
		boolean check = true;
		StringBuilder exception  = new StringBuilder();
		if(!checkUniqueID()) {
			exception.append("[Ontology MetaData] some ontology ids aren't unique in metadata list \n");
			check = false ;
		}
		 this.dependancies_m.check(this.hashOMD.keySet());
		
		if(!checkOntoDataSourcesID()) {
			exception.append("[Ontology Data Source] some ontology ids aren't unique or doesn't refers to any ontology in metadata list \n");
			check = false ;

		}
		if(!check){
			throw new OntologyMetaDataException(exception.toString());
		}
		

	}
	
	/**
	 * checkUniqueID check the unicity of id String in JSON file on ontology Meta Data
	 * @return boolean
	 */
	private boolean checkUniqueID(){
		Iterator<OntologyMetaData> it = this.rawOMD.iterator();
		boolean allUnique = true ;
		Set<String> idSet= new HashSet<String>();
		while(it.hasNext() && allUnique){
			allUnique = idSet.add(it.next().getId());
		}
		
		return allUnique;
	}
	/**
	 * checkOntoDataSourcesID check adequation of ontology ids in Data Sources element lists
	 * @return boolean
	 */
	private boolean checkOntoDataSourcesID(){
		Iterator<OntologyDataSource> it = this.rawODS.iterator();
		Set<String> ontoMDids = (Set<String>) this.hashOMD.keySet();
		boolean allUnique = true ;
		boolean allAsMetaData = true ;
		Set<String> idSet= new HashSet<String>();
		String ontoDSidCur = null; 
		while(it.hasNext() && allUnique && allAsMetaData){
			ontoDSidCur = it.next().getId();
			allUnique = idSet.add(ontoDSidCur);
			allAsMetaData = ontoMDids.contains(ontoDSidCur);
		}
		
		return allUnique && allAsMetaData;
	}
	
	
	/**
	 * TODO
	 */
	public String toString() {
		
		return "";
		}
	
	
}
