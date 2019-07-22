package hcl.epitrack.ontologygenerator.properties.jacksonserializers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import hcl.epitrack.ontology.management.exception.RuleException;
import hcl.epitrack.ontologygenerator.properties.RuleMetaData;

public class DataRuleDeserializer extends JsonDeserializer<RuleMetaData> {
	public RuleMetaData deserialize(JsonParser jp, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		RuleMetaData rule = null;
		try {
			ObjectCodec oc = jp.getCodec();
			JsonNode node = oc.readTree(jp);
			if(node.isObject()){
				final String id = node.get("rule_id").asText().trim();
				final String data_source_file = node.get("data_source_file").asText().trim();
				final String data_sheet = node.get("data_sheet").asText().trim();

				JsonNode dparamNode = node.get("data_parameter");
				HashMap<String, Integer> param = new HashMap<String, Integer>();
				if(dparamNode.getNodeType().name() == "ARRAY"){
					Iterator<JsonNode> itParam = dparamNode.iterator();
					JsonNode dp;
					while(itParam.hasNext()){
						dp =itParam.next();
						if(dp.get("ind").isInt()){
							param.put(dp.get("param").asText(),dp.get("ind").asInt());
						}else{
							throw new RuleException("[RuleMetaData deserializer]  indice of parameter "+dp.get("param").asText()+" not an integer");
						}
					}
				}
				rule =  new RuleMetaData(id, data_source_file, data_sheet, param);
			} 
		} catch (RuleException e) {
			e.printStackTrace();
		}

		return(rule);
	}

}
