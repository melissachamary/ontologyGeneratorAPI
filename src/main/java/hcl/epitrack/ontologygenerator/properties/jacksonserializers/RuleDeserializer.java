package hcl.epitrack.ontologygenerator.properties.jacksonserializers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import hcl.epitrack.ontology.management.exception.RuleException;
import hcl.epitrack.ontologygenerator.object.*;

public class RuleDeserializer extends JsonDeserializer<RuleAS> {

		@Override
		public RuleAS deserialize(JsonParser jp, DeserializationContext ctx)
				throws IOException, JsonProcessingException {
		    RuleAS rule = null;
		    
		    ObjectCodec oc = jp.getCodec();
		    JsonNode node = oc.readTree(jp);
		    if(node.isObject()){
		    	final String id = node.get("rule_id").asText().trim();
			    final String ontology_id = node.get("ontology_id").asText().trim();
			    final String type = node.get("rule_type").asText().trim();
			    final HashSet<String> dependancies = new HashSet<String>();
			    JsonNode ruleDependancies = node.get("rule_dependancies");
			    if(ruleDependancies != null){
				    System.out.println("rule dependancies == "+ruleDependancies.isNull()+" "+ruleDependancies.isMissingNode());
				    if(ruleDependancies.getNodeType().name() == "ARRAY"){
				    	Iterator<JsonNode> itParam = ruleDependancies.iterator();
				    	JsonNode dp;
				    	while(itParam.hasNext()){
				    		dp =itParam.next();
				    		dependancies.add(dp.asText());
				    	}
				    }
			    	}
			    
			    if(type.equalsIgnoreCase("ruleMapper")){
			    	String pattern = node.get("rule_pattern").asText();
			    	HashSet<String> parameter  = new HashSet<String>();
			    	JsonNode rule_p = node.get("rule_parameter");
			    	if(rule_p.getNodeType().name() == "ARRAY"){
				    	Iterator<JsonNode> itParam = rule_p.iterator();
				    	JsonNode dp;
				    	while(itParam.hasNext()){
				    		dp =itParam.next();
				    		parameter.add(dp.asText());
				    	}
				    }
			    try {
						rule = new XLSXMapperRule( id, ontology_id, pattern,parameter, dependancies);
			    } catch (RuleException e) {
					e.printStackTrace();
				}
			    	// Param Addition
			    	
			    }else{
			    	String name = node.get("element_name").asText(); 
			    	try {
						rule = new SimpleDeclarationRule(type, name , id, ontology_id); 
					} catch (RuleException e) {
						e.printStackTrace();
					}
			    }

			    

		    }
		    
			return rule;
		}
		

		}

