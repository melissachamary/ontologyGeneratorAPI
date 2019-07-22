package hcl.epitrack.ontologygenerator.properties;

import java.io.File;
import java.util.HashMap;

import hcl.epitrack.ontology.management.exception.RuleException;
/**
 * RuleMetaData objects describe particular data to apply XLSXMappingRules object
 * Notice that this class is independant to the XLSX rule description (no rule id control) and is unmutable (private setter method)
 * @author melissa
 *
 */
public class RuleMetaData {
	private File dataPath;
	private String sheetName;
	private String ruleID;
	private HashMap<String,Integer> parameterIndices;
	
	/**
	 * RuleMetaData constructor check the existence of XLSX file, accessibility of sheet, and  presence of parameter description
	 * @param ruleID : String  - rule id meta data belongs to 
	 * @param dataPath : String - absolute path to access XLSX workbook
	 * @param sheetName : String - name of XLSX sheet
	 * @param param : HashSet<String, Integer> - correspondances between rule parameter and column indices (start to 1)
	 * @throws RuleException 
	 */
	public RuleMetaData(String ruleID,String dataPath, String sheetName,  HashMap<String,Integer> param) throws RuleException{
		this.setRuleID(ruleID); 
		File dp = new File(dataPath);
		if(dp.exists()&& dp.canRead()){
			this.setDataPath(dp) ; 
		}else{
			throw new RuleException("[RuleMetaData constructor] dataPath "+dp+" doesn't exist or can't read");
		}
		this.setSheetName(sheetName) ; 
		if(param == null || param.isEmpty()){
			System.err.println("[RuleMetaData constructor] expected unempty or not null param HashMap");
		}else{
			this.setParameterIndices(param); 
		}
	}

	/**
	 * data path getter
	 * @return String
	 */
	public File getDataPath() {
		return dataPath;
	}

	/**
	 * data path setter
	 * @param dataPath String
	 */
	private void setDataPath(File dataPath) {
		this.dataPath = dataPath;
	}
	
/**
 * name of sheet getter 
 * @return
 */
	public String getSheetName() {
		return sheetName;
	}

	/**
	 * sheet name Setter
	 * @param sheetName : String
	 */
	private void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * rule id getter
	 * @return
	 */
	public String getRuleID() {
		return ruleID;
	}

	/**
	 * rule id setter
	 * @param ruleID : String
	 */
	private void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}

	/**
	 * parameter column indices getter
	 * @return
	 */
	public HashMap<String, Integer> getParameterIndices() {
		return parameterIndices;
	}

	/**
	 * parameter column indices setter
	 * @param parameterIndices
	 */
	private void setParameterIndices(HashMap<String, Integer> parameterIndices) {
		this.parameterIndices = parameterIndices;
	}
	
	
}
