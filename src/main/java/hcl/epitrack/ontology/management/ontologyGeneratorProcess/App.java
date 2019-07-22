package hcl.epitrack.ontology.management.ontologyGeneratorProcess;

//import java.io.ByteArrayInputStream;
import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;

import hcl.epitrack.ontologygenerator.project.ProjectOntologyGenerator;

//import static org.mm.ss.SpreadSheetUtil.columnNumber2Name;

public class App 
{
	public static void main( String[] args ) {
		try {
			
			File jsonMD = new File("/home/melissa/Documents/workspace/ontology/epitrack-v1/generation_data/reference_parameters/ontology_dependancy.json");//test_jackson.json");
			File jsonVI = new File("/home/melissa/Documents/workspace/ontology/epitrack-v1/generation_data/reference_parameters/ontology_version_info.json");//test_jackson.json");
			File jsonRule = new File("/home/melissa/Documents/workspace/ontology/epitrack-v1/generation_data/reference_parameters/ontologies_generator_parameter_full-rules-dependancies.json");//test_rule_deserialize.json");
			File dataRule = new File("/home/melissa/Documents/workspace/ontology/epitrack-v1/generation_data/reference_parameters/rule_data_binder.json");
			HashSet<String> scheID = new HashSet<String>();
			//scheID.add("data-epitrack");
			scheID.add("pharmaconsum-epitrack");
			scheID.add("pharma-epitrack");
			scheID.add("semantic-epitrack");
			scheID.add("hospital-epitrack");
			ProjectOntologyGenerator m ; //new ProjectOntologyGeneratorMD(jsonMD, jsonVI, jsonRule);
			m = new ProjectOntologyGenerator(jsonMD, jsonVI, jsonRule,dataRule);
			//m = new ProjectOntologyMD(jsonMD, jsonVI);
			//m.initOntology("pharma-epitrack");
			//m.initOntology("data-epitrack");
			//m.initOntology("pharmaconsum-epitrack");
			m.generate(scheID); 
			/*File jsonMD = new File("/Users/mimi/Documents/HCL_work/doc/Article/JOWO_ontoLOINC/ontoLOINC_rules/ontology_dependancy.json");//test_jackson.json");
			File jsonVI = new File("/Users/mimi/Documents/HCL_work/doc/Article/JOWO_ontoLOINC/ontoLOINC_rules/ontology_version_info.json");//test_jackson.json");
			File jsonRule = new File("/Users/mimi/Documents/HCL_work/doc/Article/JOWO_ontoLOINC/ontoLOINC_rules/ontologies_generator_parameter_full-rules.json");//test_rule_deserialize.json");
			File dataRule = new File("/Users/mimi/Documents/HCL_work/doc/Article/JOWO_ontoLOINC/ontoLOINC_rules/rule_data_binder.json");
			HashSet<String> scheID = new HashSet<String>();
			//scheID.add("data-epitrack");
			//scheID.add("onto-LOINC");
			scheID.add("onto-LOINCTest");*/
			
		/*	ProjectOntologyGenerator m ; //new ProjectOntologyGeneratorMD(jsonMD, jsonVI, jsonRule);
			m = new ProjectOntologyGenerator(jsonMD, jsonVI, jsonRule,dataRule);
			
			m.generate(scheID); */
			/*---- Génération Référentiel Encodage ----
			File owlFile = new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/reference-ontologies_v1/semantic-standards-ontology_v1.owl"
					);
			OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(owlFile);

			
			// Default Set (IRI load & prefix
			SimpleIRIMapper mapper = new SimpleIRIMapper(IRI.create("http://www.chu-lyon.fr/epitrack/epitrack-core"), 
					IRI.create(new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/Full_epitrack-V1/epitrack-core-v1.owl"))
					);
			ontologyManager.getIRIMappers().add(mapper);
			OWLImportsDeclaration importDeclaration=ontologyManager.getOWLDataFactory().
					getOWLImportsDeclaration(IRI.create("http://www.chu-lyon.fr/epitrack/epitrack-core"));
			ontologyManager.applyChange(new AddImport(ontology, importDeclaration));
			ontologyManager.makeLoadImportRequest(importDeclaration);
			OWLDataFactory df = ontologyManager.getOWLDataFactory();
			DefaultPrefixManager pm = new DefaultPrefixManager();
			pm.setDefaultPrefix(ontology.getOntologyID().getOntologyIRI().get() + "#");
			pm.setPrefix("cepi:", "http://www.chu-lyon.fr/epitrack/epitrack-core");

			//TAxonomy
			// Create Taxonomy Concept
			ontologyManager.addAxiom(ontology,df.getOWLDeclarationAxiom(df.getOWLClass("TAXGLIMS_0", pm)));
			;
			ontologyManager.addAxiom(ontology,
					df.getOWLAnnotationAssertionAxiom(df.getOWLClass("TAXGLIMS_0", pm).getIRI(), 
						df.getOWLAnnotation(
								df.getRDFSLabel(), 
								df.getOWLLiteral("Glims Taxonomy")))
					);

			
			File spreadsheetFile = new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/resource/microbiology-data-epitrack-resource/referentiel_encodage/taxonomie/local-taxonomy.xlsx");
			Workbook workbook = WorkbookFactory.create(spreadsheetFile);	       
			SpreadSheetDataSource spreadsheetSource = new SpreadSheetDataSource(workbook);
		
			Set<OWLAxiom> sheetAxiom;
			SpreadsheetAxiomGenerator s ;
			// Add DataProperty aerobic culture
			OWLDeclarationAxiom annot = df.getOWLDeclarationAxiom(df.getOWLAnnotationProperty(
					IRI.create(ontology.getOntologyID().getOntologyIRI().get().toString()+"#"+"isAerobic")));
			ontologyManager.addAxiom(ontology,annot);
			//
			s=  new SpreadsheetAxiomGenerator(ontology, spreadsheetSource, "taxonomy",
					2, 3854, 1,3, 
					null, 
					"Class: @A*(rdfs:label=@B* rdf:ID=(@D*)) SubClassOf: @E* Annotations: cepi:source_id @A* , isAerobic @C*(xsd:boolean)"
					);
			sheetAxiom = s.generate();
			ontologyManager.addAxioms(ontology, sheetAxiom);
			
			//Antibiotic
			spreadsheetFile = new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/resource/microbiology-data-epitrack-resource/referentiel_encodage/antibiotique/local-antibiotic-referentiel.xlsx");
			
			workbook = WorkbookFactory.create(spreadsheetFile);	       
			spreadsheetSource = new SpreadSheetDataSource(workbook);
			
			ontologyManager.addAxiom(ontology,df.getOWLDeclarationAxiom(df.getOWLClass("GLIMS_DRUG", pm)));
			;
			ontologyManager.addAxiom(ontology,
					df.getOWLAnnotationAssertionAxiom(df.getOWLClass("GLIMS_DRUG", pm).getIRI(), 
						df.getOWLAnnotation(
								df.getRDFSLabel(), 
								df.getOWLLiteral("Glims Antibiotic")))
					);

			
			// Concept ATB Hierarchy
			s=  new SpreadsheetAxiomGenerator(ontology, spreadsheetSource, "hierarchy",
					2, 61, 1,3, 
					null, 
					"Class: @A*(rdfs:label=@B* rdf:ID=(@A*)) SubClassOf: @C* Annotations: cepi:source_id @B* "
					);
			sheetAxiom = s.generate();
			ontologyManager.addAxioms(ontology, sheetAxiom);

// antibiotic

s=  new SpreadsheetAxiomGenerator(ontology, spreadsheetSource, "drug",
		2, 311, 1,5, 
		null, 
		"Class: @A*(rdfs:label=@B* rdf:ID=(@A*)) SubClassOf: @D* Annotations: cepi:source_id @A*, skos:exactMatch @C*  EquivalentTo: @C*(mm:prefix=\"cepi\")"
		);
sheetAxiom = s.generate();
ontologyManager.addAxioms(ontology, sheetAxiom);

// Sample Type
 spreadsheetFile = new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/resource/microbiology-data-epitrack-resource/referentiel_encodage/sample_type/local-sample.xlsx");
 workbook = WorkbookFactory.create(spreadsheetFile);	       
 spreadsheetSource = new SpreadSheetDataSource(workbook);
 
 ontologyManager.addAxiom(ontology,df.getOWLDeclarationAxiom(df.getOWLClass("GLIMS_SAMPLETYPE", pm)));
	;
	ontologyManager.addAxiom(ontology,
			df.getOWLAnnotationAssertionAxiom(df.getOWLClass("GLIMS_SAMPLETYPE", pm).getIRI(), 
				df.getOWLAnnotation(
						df.getRDFSLabel(), 
						df.getOWLLiteral("Glims Sample Type")))
			);

	spreadsheetSource = new SpreadSheetDataSource(workbook);
	s=  new SpreadsheetAxiomGenerator(ontology, spreadsheetSource, "sampleType",
      		2, 70, 1,5, 
      		null, "Class: @A*(rdf:ID=(@A*) rdfs:label=@B*) Annotations: cepi:source_id @B* SubClassOf: GLIMS_SAMPLETYPE "
      				
      		);
    
  sheetAxiom=s.generate();
	System.out.println(sheetAxiom.size());
	ontologyManager.addAxioms(ontology, sheetAxiom);
	ontologyManager.saveOntology(ontology, new FileOutputStream(
	        		  "/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/Full_epitrack-V1/referentiels-encodage-v1.owl"))	;
			
			/* ---  Generation of Batch Microbiological Data ----
			File owlFile = new File(
			"/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/reference-ontologies_v1/microbiology-data-epitrack-V1.owl"
			);
				
	File directory = new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/resource/microbiology-data-epitrack-resource/patient_data/batch-data/");
	File processDir = new File(directory.getAbsolutePath() + "/temp_process/");
    FileUtils.copyDirectory(directory, processDir);
	File[] processFiles = processDir.listFiles();
	File spreadsheetFile;
	Set<OWLAxiom> sheetAxiom;
	SpreadsheetAxiomGenerator s ;
	Workbook workbook;
	SpreadSheetDataSource spreadsheetSource;
	System.out.println(directory.isDirectory());
	while(processDir.listFiles().length >0){
		System.out.println("[AST-generation] nb of file to treat : "+processDir.listFiles().length+"\n current file : "+processDir.listFiles()[0].getName());
		String fileName = processDir.listFiles()[0].getName();
		//ontology Init
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(owlFile);
		ontologyManager = ontology.getOWLOntologyManager();
		
		SimpleIRIMapper mapper = new SimpleIRIMapper(IRI.create("http://www.chu-lyon.fr/epitrack/epitrack-core"), 
				IRI.create(new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/Full_epitrack-V1/epitrack-core-v1.owl"))
				);
		ontologyManager.getIRIMappers().add(mapper);
		
		mapper = new SimpleIRIMapper(IRI.create("http://www.chu-lyon.fr/epitrack/"), 
				IRI.create(new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/Full_epitrack-V1/epitrack-core-v1.owl"))
				);
		ontologyManager.getIRIMappers().add(mapper);
		mapper = new SimpleIRIMapper(IRI.create("http://www.chu-lyon.fr/epitrack/ontologies/localSemanticStandard"), 
				IRI.create(new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/Full_epitrack-V1/referentiels-encodage-v1.owl"))
				);
		ontologyManager.getIRIMappers().add(mapper);

		ontologyManager.getIRIMappers().add(mapper);
		mapper = new SimpleIRIMapper(IRI.create("http://www.chu-lyon.fr/epitrack/epitrack-hospital"), 
				IRI.create(new File("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/Full_epitrack-V1/ward-v1.owl"))
				);
		ontologyManager.getIRIMappers().add(mapper);

		
		
		OWLImportsDeclaration importDeclaration=ontologyManager.getOWLDataFactory().
				getOWLImportsDeclaration(IRI.create("http://www.chu-lyon.fr/epitrack/epitrack-core"));
		ontologyManager.applyChange(new AddImport(ontology, importDeclaration));
		ontologyManager.makeLoadImportRequest(importDeclaration);
		
		importDeclaration=ontologyManager.getOWLDataFactory().
				getOWLImportsDeclaration(IRI.create("http://www.chu-lyon.fr/epitrack/ontologies/localSemanticStandard"));
		ontologyManager.applyChange(new AddImport(ontology, importDeclaration));
		ontologyManager.makeLoadImportRequest(importDeclaration);
		

		importDeclaration=ontologyManager.getOWLDataFactory().
				getOWLImportsDeclaration(IRI.create("http://www.chu-lyon.fr/epitrack/epitrack-hospital"));
		ontologyManager.applyChange(new AddImport(ontology, importDeclaration));
		ontologyManager.makeLoadImportRequest(importDeclaration);
				
		DefaultPrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix(ontology.getOntologyID().getOntologyIRI().get() + "#");
		pm.setPrefix("cepi:", "http://www.chu-lyon.fr/epitrack/epitrack-core#");
		pm.setPrefix("encoding:", "http://www.chu-lyon.fr/epitrack/ontologies/localSemanticStandard#");
		pm.setPrefix("hepi:", "http://www.chu-lyon.fr/epitrack/epitrack-hospital#");

		//FLAG SAMPLE
		String iriOnto = ontology.getOntologyID().getOntologyIRI().get().toString();
		OWLAnnotationProperty an = ontologyManager.getOWLDataFactory().getOWLAnnotationProperty(IRI.create(iriOnto,"#"+"UNSOLVED_in_ward"));
		ontologyManager.addAxiom(ontology,
		ontologyManager.getOWLDataFactory().getOWLSubAnnotationPropertyOfAxiom(an,ontologyManager.getOWLDataFactory().getRDFSComment())
				);
		//FLAG TAXO
		an = ontologyManager.getOWLDataFactory().getOWLAnnotationProperty("UNSOLVED_taxon",pm);
		ontologyManager.addAxiom(ontology,
		ontologyManager.getOWLDataFactory().getOWLSubAnnotationPropertyOfAxiom(an,ontologyManager.getOWLDataFactory().getRDFSComment())
				);
		
		spreadsheetFile = processDir.listFiles()[0];
		workbook = WorkbookFactory.create(spreadsheetFile);
	    spreadsheetSource = new SpreadSheetDataSource(workbook);
	    int countNRow;
	    
	    //Patient
	    
	    countNRow= workbook.getSheet("PATIENT").getPhysicalNumberOfRows();
	    s=  new SpreadsheetAxiomGenerator(ontology, spreadsheetSource, "PATIENT",
          		2,countNRow , 1,5, 
          		null, "Individual: @A*(rdf:ID=(@A*)) "
          				+ "Annotations: cepi:source_id @A*(xsd:string) "
          				+"Types: DEPI0000000008 , btl2:bearerOf some @B* "
          				+"Facts: birthDate @C*(xsd:dateTime)"
          		);
      sheetAxiom=s.generate();
	  System.out.println(sheetAxiom.size());
		ontologyManager.addAxioms(ontology, sheetAxiom);
    	
	    //Sample
		countNRow= workbook.getSheet("SAMPLE").getPhysicalNumberOfRows();
		s=  new SpreadsheetAxiomGenerator(ontology, spreadsheetSource, "SAMPLE",
          		2,countNRow , 1,5, 
          		null, "Individual: @A*(rdf:ID=(@A*)) "
          				+ "Annotations: cepi:source_id @A*(xsd:string) , UNSOLVED_in_ward @E* "
          				+"Types: DEPI0000000009 , btl2:bearerOf SOME @B*(mm:EntityIRI mm:prefix=\"encoding\" rdf:ID=@B*) "
          				+"Facts: date @C*(xsd:dateTime), fromPatient @F* , inWard @D*(mm:EntityIRI mm:prefix= \"hepi\" rdf:ID=(\"UF_\",@F*))"
          		);
        
    sheetAxiom=s.generate();
	ontologyManager.addAxioms(ontology, sheetAxiom);
	    //Isolate
	countNRow= workbook.getSheet("OBS-TAXO").getPhysicalNumberOfRows();
	s=  new SpreadsheetAxiomGenerator(ontology, spreadsheetSource, "OBS-TAXO",
      		2, countNRow, 2,6, 
      		null, "Individual: @A*(rdf:ID=(@A*)) "
      				+ "Annotations: cepi:source_id @E*(xsd:string) , UNSOLVED_taxon @C*(xsd:string) "
      				+"Types: DEPI0000000000 , hasResult SOME @B*(mm:prefix=\"encoding\") "
      				+"Facts: observedOn @D*"
      		);
    
sheetAxiom=s.generate();
ontologyManager.addAxioms(ontology, sheetAxiom);
	    //AST
	    countNRow= workbook.getSheet("OBS-AST").getPhysicalNumberOfRows();
		s=  new SpreadsheetAxiomGenerator(ontology, spreadsheetSource, "OBS-AST",
				2,countNRow , 2,6, 
				null, "Individual: @A*(rdf:ID=(@A*)) "
						+ "Annotations: cepi:source_id @A*(xsd:string)  "
						+"Types: DEPI0000000001 , hasInterpretation SOME @D* , hasTest SOME @B*(mm:prefix=\"encoding\") "
						+"Facts: refineObservation @C*"
				);
		sheetAxiom=s.generate();
		ontologyManager.addAxioms(ontology, sheetAxiom);
		spreadsheetFile.delete();
		ontologyManager.saveOntology(ontology, new FileOutputStream("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Version-1/Full_epitrack-V1/microbiology-data_batch/"+fileName+".owl"));
		//processDir.listFiles()[0].delete();

		//ontologyManager.saveOntology(ontology, new FileOutputStream("/Users/mimi/Documents/HCL_work/workspace/epitrack-ontologies/Verison-1/Full_epitrack-V1/microbiologyFullData.owl"));

	}
		
	*/
	
	
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/*
 	private static OWLOntology mergeOntologies(OWLOntology o) throws OWLOntologyCreationException{
		OWLOntology mergeOntology = OWLManager.createOWLOntologyManager().createOntology();
		Set<OWLOntology> importOntologySet = o.getImportsClosure();
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		for(OWLOntology onto: importOntologySet){
			System.out.println(onto.getOntologyID()+" : axiom number "+onto.getAxiomCount());
			axioms.addAll(onto.getAxioms());
		}
		OWLManager.createOWLOntologyManager().addAxioms(mergeOntology, axioms);
		System.out.println(" Total axiom number "+axioms.size());
		return mergeOntology;

	} */
}



