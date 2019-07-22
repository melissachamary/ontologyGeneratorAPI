package hcl.epitrack.ontologygenerator.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.mm.parser.ParseException;
import org.mm.renderer.RendererException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import hcl.epitrack.ontology.management.exception.DependenciesException;
import hcl.epitrack.ontology.management.exception.OntologyMetaDataException;
import hcl.epitrack.ontology.management.exception.RuleException;
import hcl.epitrack.ontologygenerator.object.DependencyObject;
import hcl.epitrack.ontologygenerator.object.Ontology;
import hcl.epitrack.ontologygenerator.object.RuleAS;
import hcl.epitrack.ontologygenerator.properties.OntologyDataSource;
import hcl.epitrack.ontologygenerator.properties.RuleMetaData;
import hcl.epitrack.ontologygenerator.properties.jacksonserializers.DataRuleDeserializer;
import hcl.epitrack.ontologygenerator.properties.jacksonserializers.RuleDeserializer;
/**
 * ProjectOntologyGeneratorMD object intends to create ontology generator project using json metadata files.
 * @extend ProjectOntologyMD by
 * - enabling to schedule the ontology generation by ordering ontologies and rules by ontologies 
 * - generate the ontologies (in schedul order)
 * @author melissa
 */
public class ProjectOntologyGenerator extends ProjectOntology {
	private HashMap<String,HashSet<String>> rulesByOnto; // TODO make to string
	private HashMap<String, RuleAS> rulesIDMap;
	private DependencyObject ruleDependancies;

	/**
	 * @param jsonOntologyMetaData : unmutable parameter containing ontology prefix & dependancies (expressed using ontology-id)
	 * @param jsonOntologyInfo : version dependant parameter containing version information & data sources
	 * @param  jsonRule : unmutable version of rules, link with ontologies & rule dependancies
	 * @throws OntologyMetaDataException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws DependanciesException 
	 */
	public ProjectOntologyGenerator(File jsonOntologyMetaData, File jsonOntologyInfo, File jsonRule, File dataRule) throws RuleException, EncryptedDocumentException, InvalidFormatException, JsonParseException, JsonMappingException, IOException, OntologyMetaDataException, DependenciesException {
		/*--- Instanciation of Ontologies ecosystem --*/
		super(jsonOntologyMetaData, jsonOntologyInfo);
		/*--- Instanciation of Rule part elements to build ontology --*/
			HashMap<String, HashSet<String>> rdep = new HashMap<String,HashSet<String>>();
			this.rulesIDMap = new HashMap<String, RuleAS>();
			this.rulesByOnto = new HashMap<String,HashSet<String>>(); 

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			SimpleModule module = new SimpleModule();
			module.addDeserializer(RuleAS.class, new RuleDeserializer());
			module.addDeserializer(RuleMetaData.class, new DataRuleDeserializer());
			mapper.registerModule(module);
			TypeFactory typeFactory = mapper.getTypeFactory();
		
			/*-- rule description  : jsonRule parser & instantiation of rulesIDMap rulesDependancies rulesByOnto attributes --*/
			CollectionType arrayRule = typeFactory.constructCollectionType(ArrayList.class, RuleAS.class);
			ArrayList<RuleAS> rules = mapper.readValue(jsonRule,arrayRule);
			RuleAS r ;
			HashSet<String> temp ;
			for(int i=0; i<rules.size(); i++){
				r = rules.get(i);
				this.rulesIDMap.put(r.getID(), r);

				if(checkReferencesOntologyID(r.getOntologyID())){
					if(this.rulesByOnto.containsKey(r.getOntologyID())){
						this.rulesByOnto.get(r.getOntologyID()).add(r.getID());
					}else{
						temp = new HashSet<String>();
						temp.add(r.getID());
						this.rulesByOnto.put(r.getOntologyID(), temp);
					}
				}else{
					throw new RuleException("[Rule] rule "+r.getID()+" is linked to non-referred ontology "+r.getOntologyID());
				}
				rdep.put(r.getID(), (HashSet<String>) r.getRuleDependancy());

			}
			
			this.ruleDependancies = new DependencyObject(rdep);

			/*-- rule data bind  : dataRule parser & consistency checking between declared rules and rules data information --*/
			arrayRule = typeFactory.constructCollectionType(ArrayList.class, RuleMetaData.class);
			ArrayList<RuleMetaData> ruleMD = mapper.readValue(dataRule,arrayRule);
			for(int i=0;i<ruleMD.size();i++){
				String ruleMDID = ruleMD.get(i).getRuleID();

				if( this.rulesIDMap.containsKey(ruleMDID)){
					this.rulesIDMap.get(ruleMDID).init(ruleMD.get(i));
				}else{
					System.err.println("[RuleMetaData] rule "+ruleMDID+" doesn't exist in referenced rules");

					throw new RuleException("[RuleMetaData] rule "+ruleMDID+" doesn't exist in referenced rules");
				}
			}
			
	
	}


	/**
	 * Check is called to verify consistency when object is build; extends ProjectOntologyMD check method by
	 * - @TODO verify the cyclicity of rule dependancies (fail if cycle) ==> See cyclic code in previous parameters
	 * @return boolean
	 * @throws OntologyMetaDataException , RuleException
	 * @throws DependenciesException 
	 */
	protected void check() throws OntologyMetaDataException, DependenciesException{
		super.check();
	}
	
	/**
	 * checkReferencesOntologyID is called to verify ontology-id used in rule description corresponds to existing onotlogy id
	 * @return boolean
	 */
	private boolean checkReferencesOntologyID(String id){
		return this.ontoSource.keySet().contains(id);
	}

	/** /!\ to finish  
	 * schedule : check if all generated ontologies data exists
	 * Should be improved by testing only indirect dependancies;
	 * 
	 */
	private LinkedHashSet<String> schedule(HashSet<String> ids){
		try{

			//Ordonner les ontologies
			LinkedHashSet<String> orderOnto = this.dependancies_m.orderDependancies();
			LinkedHashSet<String> scheduler = new LinkedHashSet<String>();

			/*--  --*/
			Iterator<String> itOrder = orderOnto.iterator();
			HashSet<String> idsClone = (HashSet<String>) ids.clone();
			HashSet<String> ruleSet ;
			Iterator<String> itRuleSet;
			boolean schedulable = true;
			String curOrder;
			OntologyDataSource curODS;
			//File curGFile;
			while(itOrder.hasNext() && schedulable && idsClone.size()>0){
				curOrder = itOrder.next();
				if(idsClone.contains(curOrder)){
					idsClone.remove(curOrder);
					scheduler.add(curOrder);

					ruleSet = this.rulesByOnto.get(curOrder);
					if(!ruleSet.isEmpty()){
						itRuleSet = ruleSet.iterator();
						while(itRuleSet.hasNext()){
							RuleAS r = this.rulesIDMap.get(itRuleSet.next());
							if(!  r.applicable()){
								throw new Exception("[ProjectOntologyGenerator schedulable] for "+curOrder+" rule "+r.getID()+" not applicable");
							}
						}

					}
				}else{
					curODS = this.hashODS.get(curOrder);
					if( curODS.getGeneratedFile() != null 
							&& curODS.getGeneratedFile().exists()
							&& curODS.getGeneratedFile().canRead()){
						updateOntologySources(curOrder, curODS.getGeneratedFile());
					}else{
						schedulable = false;
						System.out.println(curODS);
						throw new OntologyMetaDataException("[ProjectOntologyGenerator schedulable] unschedulable ontologies (dependancy data source failure)\n"+curODS.getId());
					}
				}

			}
			return scheduler;

		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	// TODO solve issue in code
	public void generateOntology(String id) throws RendererException, RuleException, ParseException, OWLOntologyStorageException, FileNotFoundException, OntologyMetaDataException, DependenciesException, OWLOntologyCreationException {
		Ontology o = initOntology(id);
		// Add version INFO HERE
		// 3. Ajouter les informations sur la version TODO dans projectOntologyGenerator
					/*
					 * OWLAnnotation anno = df.getOWLAnnotation(
		                df.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_VERSION_INFO.getIRI()),
		                lit);
		        // Now we can add this as an ontology annotation Apply the change in the
		        // usual way
		man.applyChange(new AddOntologyAnnotation(ont, anno));
					 */
		System.out.println("[ProjectOntologyGeneratorMD generateOntology] ontology : "+id);
		Iterator<String> itRuleApply ;
		Set<String> rules = this.rulesByOnto.get(id);
		
		Iterator<String> itRules = rules.iterator();
		
		
		System.out.println(this.ruleDependancies.getDependenciesAdjacencyList().keySet());
		LinkedHashSet<String> orderedRules = this.ruleDependancies.subset(rules).orderDependancies();
		System.out.println("[generate ontology] "+id+" has rule order list "+orderedRules);
		RuleAS r ;
		itRuleApply = orderedRules.iterator();
		while(itRuleApply.hasNext()){
			r = this.rulesIDMap.get(itRuleApply.next());
			System.out.println(r.toString());
			o.addAxioms(r.apply(o));
			System.out.println("[generate ontology]"+r.getID()+" enhance the ontology axiom count to "+o.getOWLOntology().getAxiomCount());
		}
		
		o.save(this.hashODS.get(id).getGeneratedFile());
		updateOntologySources(id, this.hashODS.get(id).getGeneratedFile());
	}


/**
 * TODO	why should we catch exception
 * @param ontoIDS
 * @throws OWLOntologyStorageException
 * @throws FileNotFoundException
 * @throws OntologyMetaDataException
 * @throws DependenciesException
 * @throws OWLOntologyCreationException
 */
	public void generate(HashSet<String> ontoIDS) throws OWLOntologyStorageException, FileNotFoundException, OntologyMetaDataException, DependenciesException, OWLOntologyCreationException{
		System.out.println(ontoIDS);
		LinkedHashSet<String> orderOntologies  = schedule(ontoIDS);

		try {
			System.out.println(orderOntologies);

			Iterator<String> itOntologies = orderOntologies.iterator();
			String idCur;
			//Ontology ontoCur ;
			while(itOntologies.hasNext()){
				idCur = itOntologies.next();
				generateOntology(idCur);
				itOntologies.remove();
			}
		} catch (RuleException e) {
			e.printStackTrace();
			System.err.println("[ProjectOntologyGenerator generate] abnormal end of process : remaining ontologies : "+orderOntologies);
		}catch(ParseException e){
			e.printStackTrace();
			System.err.println("[ProjectOntologyGenerator generate] abnormal end of process : remaining ontologies : "+orderOntologies);

		}

	}


	

}
