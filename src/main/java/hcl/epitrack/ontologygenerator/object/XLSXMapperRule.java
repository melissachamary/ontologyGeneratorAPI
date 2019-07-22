package hcl.epitrack.ontologygenerator.object;

import static org.mm.ss.SpreadSheetUtil.columnNumber2Name;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.mm.core.OWLAPIOntology;
import org.mm.core.TransformationRule;
import org.mm.core.settings.ReferenceSettings;
import org.mm.parser.ASTExpression;
import org.mm.parser.MappingMasterParser;
import org.mm.parser.ParseException;
import org.mm.parser.node.ExpressionNode;
import org.mm.parser.node.MMExpressionNode;
import org.mm.renderer.owlapi.OWLRenderer;
import org.mm.rendering.owlapi.OWLRendering;
import org.mm.ss.SpreadSheetDataSource;
import org.mm.ss.SpreadSheetUtil;
import org.mm.ss.SpreadsheetLocation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;


import hcl.epitrack.ontology.management.exception.RuleException;
import hcl.epitrack.ontologygenerator.properties.RuleMetaData;

/**
 * XLSXMapperRule objects correspond to complex concept or individual axiom set generation rules and use metadata parameter that can be set after the object construction. 
 * They use MappingMaster API syntax to define rules in Manchester Syntax (https://github.com/protegeproject/mapping-master/wiki)
 * @see RuleAS interface
 * @author melissa
 */
public class XLSXMapperRule implements RuleAS {
	private String id;
	private String ontologyID;

	private String refRulePattern ;
	private HashSet<String> ruleParameter;
	private String rule;
	private boolean applicable ;
	private SpreadSheetDataSource spreadsheetSource ;
	private String sheetName;
	private HashMap<String, Integer> sheetInfo;
	private Set<String> dependancies;

	/**
	 * XLSXMapperRule constructor (unapplicable rules untill init function used)
	 * @param id id of rules( used for dependancy management)
	 * @param ontologyID ontology id on which rule should apply 
	 * @param rulePattern : rule expression in OWL Manchester Syntax 
	 * @implNote The two constructor doesn't have same exception management behavior ==> Check in V2
	 */
	private XLSXMapperRule( String id, String ontologyID ,String rulePattern) {
		try{	
			if(rulePattern != null && ! rulePattern.isEmpty()){
				this.id =id;
				this.ontologyID=ontologyID;
				this.refRulePattern = rulePattern;
				this.applicable = false;
				this.spreadsheetSource = new SpreadSheetDataSource();
				this.sheetInfo = new HashMap<String, Integer>();
				this.sheetName = null;
			}else{
				throw new RuleException("[Rule constructor] rule pattern provided is empty or null");
			}

		}catch(RuleException e){
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * XLSXMapperRule constructor (inapplicable rules until init function used)
	 * @param id id of rules( used for dependancy management)
	 * @param ontologyID ontology id on which rule should apply 
	 * @param rulePattern : rule expression in OWL Manchester Syntax 
	 * @param ruleParameter : parameter used in rule to make replacement, usually parameter is writen following the pattern <VAR>
	 * @param dependancies Set of rule id dependencies
	 * 
	 * @throws RuleException
	 * @implNote The two constructor doesn't have same exception management behavior ==> Check in V2
	 * @implNote the "applicable" attribute should depend on the existance of ruleParameter variable ==> TODO in V2
	 */
	public XLSXMapperRule( String id, String ontologyID , String rulePattern, HashSet<String> ruleParameter, HashSet<String> dependancies) throws RuleException{
		this(  id,  ontologyID, rulePattern);
		this.ruleParameter = new HashSet<String>();
		Iterator<String> itParam ;
		String curParam;
		if(ruleParameter != null && !  ruleParameter.isEmpty()){
			itParam = ruleParameter.iterator();

			while(itParam.hasNext()){
				curParam = itParam.next();
				if(this.refRulePattern.contains(curParam)){
					this.ruleParameter.add(curParam);
				}else{
					System.out.println("[XLSXMapperRule constructor] parameter "+curParam+" not found in Rule "+ this.id);;
				}	
			}
			this.dependancies = dependancies;
		}else{
			throw new RuleException("[XLSXMapperRule constructor] don't have parameter list");
		}
	}
	
	

	/**
	 * init method transform rule pattern according metadata, Excel sheet file and assert if rule is applicable/not. 
	 * @param md RuleMetaData
	 * @return boolean
	 * @exception EncryptedDocumentException, InvalidFormatException, IOException, RuleException
	 */
  public void init(RuleMetaData md) throws EncryptedDocumentException, InvalidFormatException, IOException, RuleException{
		if(this.id.equals(md.getRuleID())){
			Workbook w ;
			Set<String> replaceParameter = (Set<String>) this.ruleParameter.clone();
			Set<Entry<String, Integer>> mdParam;
			String ruleReplace = this.refRulePattern;
			
			// Data for rule processor;
			int stNRow;
			int endNRow ;
			int minCol=Integer.MAX_VALUE , maxCol=0;
			int stNCol, endNCol ;
			
			if(md.getDataPath().isFile()){
				w = WorkbookFactory.create(md.getDataPath());
				if(w.getSheet(md.getSheetName()) != null){
					this.spreadsheetSource = new SpreadSheetDataSource(w);
					this.sheetName = md.getSheetName();
					mdParam = md.getParameterIndices().entrySet();
					for (final Iterator<Entry<String, Integer>> iter = mdParam.iterator(); iter.hasNext();) {
						final Entry<String, Integer> entry = (Entry<String, Integer>) iter.next();
						final int value = (int) entry.getValue();
						if(replaceParameter.contains(entry.getKey())
								&& value >0){
							if(value<minCol){minCol= value;}
							if(value>maxCol){maxCol= value;}

						}
							ruleReplace = ruleReplace.replaceAll("("+(String) entry.getKey()+")", getColumnReference(value));
							replaceParameter.remove(entry.getKey());
						}
					}
					if(replaceParameter.size()>0){
						throw new RuleException("[RuleAS init] in rule "+this.id+" incomplete rule medata \n\t parameters missing "+replaceParameter);
					}else{
						
						// Add sheet parameter
						endNRow = w.getSheet(this.sheetName).getLastRowNum()+1;
						stNRow= w.getSheet(this.sheetName).getFirstRowNum()+1;
						stNCol = minCol;
						endNCol = maxCol;
						stNRow=stNRow+1;
						
						if(stNCol>=endNCol || stNCol <=0 || endNCol <=0){
							throw new RuleException("[RuleAS init] "+sheetName+" column data issues"+"("+stNCol+";"+endNCol+")");

						}
						if(stNRow >= endNRow){
							throw new RuleException("[RuleAS init] "+sheetName+" is empty"+stNRow+"-"+endNRow);
						}else{
							this.sheetInfo.put("stRow", stNRow);
							this.sheetInfo.put("endRow", endNRow);
							this.sheetInfo.put("stCol", stNCol);
							this.sheetInfo.put("endCol", endNCol);

						this.applicable = true;
						this.rule = ruleReplace;
					}
				}
			}else {
				throw new RuleException("[RuleAS init] md data path not a file"+ md.getDataPath());//TODO
			}

		}else{
			throw new RuleException("[XLSXMapperRule init] rule meta data doesn't match (by ruleID)");
		}

	}
	
	public Set<OWLAxiom> apply(OntologyAS o) throws RuleException, ParseException {
		OWLOntology ontology = o.getOWLOntology();
		
		if(this.applicable){
			Set<OWLAxiom> renderedOWLAxioms = new HashSet<OWLAxiom>();
			TransformationRule mmExpression;
			MappingMasterParser parser;
			mmExpression = new TransformationRule(this.sheetName,
					columnNumber2Name(this.sheetInfo.get("stCol")),
					columnNumber2Name(this.sheetInfo.get("endCol")),
					this.sheetInfo.get("stRow").toString(), this.sheetInfo.get("endRow").toString(),"",rule);
			parser = new MappingMasterParser(
					new ByteArrayInputStream(mmExpression.getRuleString().getBytes()), new ReferenceSettings(), -1);
			MMExpressionNode mmExpressionNode = new ExpressionNode((ASTExpression)parser.expression()).getMMExpressionNode();

			// Create an OWL renderer and supply it with an ontology and a spreadsheet. 
			// An OWL renderer renders a set of OWLAPI-based OWL axioms from a Mapping Master expression.
			OWLRenderer owlRenderer = new OWLRenderer(new OWLAPIOntology(ontology), 
					this.spreadsheetSource);
			for (int columnNumber = this.sheetInfo.get("stCol"); columnNumber <= this.sheetInfo.get("endCol"); columnNumber++) {
				for (int rowNumber = this.sheetInfo.get("stRow"); rowNumber <= this.sheetInfo.get("endRow"); rowNumber++) {
					spreadsheetSource.setCurrentLocation(new SpreadsheetLocation(this.sheetName, columnNumber, rowNumber));
					Optional<OWLRendering> owlRendering = owlRenderer.render(mmExpressionNode);
					if (owlRendering.isPresent()) {
						renderedOWLAxioms.addAll(owlRendering.get().getOWLAxioms());   
					}
				}

			}
			System.out.println("[Generate Axiom] "+ this.sheetName+
					" sheet enable to generate "+renderedOWLAxioms.size()+" Axioms");

			
			return renderedOWLAxioms;


		}else{
			throw new RuleException("[Rule apply] un applicable rule \n \t use init method to init necessary information");
		}
	}



	public String getID() {
		return this.id;
	}
	public boolean applicable() {
		return this.applicable;
	}


	public String getOntologyID() {
		return this.ontologyID;
	}
	
	public Set<String> getRuleDependancy() {
		return this.dependancies;
	}
	
	public String toString(){
		return("rule : "+this.id+"\n\t applicable = "+applicable()+"\n\t rule = \n\t\t"+this.rule);
	}
	
	
	/**
	 * getColumnReference change id to column reference
	 * @param i
	 * @return String corresponding to column letter name prefixed with @
	 */
	private String getColumnReference(int i){
		return "@"+SpreadSheetUtil.columnNumber2Name(i);
	}

}
