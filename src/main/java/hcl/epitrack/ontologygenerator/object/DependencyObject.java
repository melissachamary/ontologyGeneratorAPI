package hcl.epitrack.ontologygenerator.object;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DirectedMultigraph;

import hcl.epitrack.ontology.management.exception.DependenciesException;
import hcl.epitrack.ontology.management.exception.OntologyMetaDataException;

public class DependencyObject<T> {
public Map<T, HashSet<T>> data;

	
	/**
	 * Constructor check integrity of dependency element list and uncyclicity of dependency graph
	 * @param dependencies : HashMap<T,HashSet<String>> - adjacency list
	 * @throws DependenciesException
	 * TODO think about log file in java
	 */
	public DependencyObject(HashMap<T,HashSet<T>> dependencies) throws DependenciesException{
		this.data = new HashMap<T, HashSet<T> >();

		if(dependencies.isEmpty() || dependencies == null) {
			
			System.err.println("[DependanciesManagement] is empty object");
			
		}else {
			this.data = dependencies.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));			
			check(null);
			
		}
	}
	
	/**
	 * check dependency management object according specification (unclyclic and dependant element in adjacency list belongs to key in HashMap)s
	 * if set is unempty , dependancies element are also check into set list
	 * @param set Set<String> object set to match
	 * @throws DependenciesException
	 */
	public void check(Set<String> set) throws DependenciesException {
		this.checkCyclicity();
		this.checkDependencies();
		if( set != null) {
			this.checkByExternalRef(set);
		}
	}
	/**
	 * check internal control into dependency management object 
	 * @throws DependenciesException
	 */
	private void checkDependencies() throws DependenciesException {
		Iterator<HashSet<T>> depElement = this.data.values().iterator(); // LA => entre pas dans le while pb depElement
		Set<T> ids =  this.data.keySet();
		HashSet<T> dependenciesIdsCur = null;
		boolean check = true;
		while(depElement.hasNext() && check) {
			dependenciesIdsCur = (HashSet<T>) depElement.next();
			check = ids.containsAll(dependenciesIdsCur) || dependenciesIdsCur == null ; 
		}
		
		if(!check) {
			throw new DependenciesException("[checkDependancies] some dependancies references doesn't exists as key");
		}
		
	}
	
	/**
	 * check all ids are present into an external reference id set
	 * @param set HashSet<String>
	 * @throws DependenciesException
	 */
	public void checkByExternalRef(Set<String> set) throws DependenciesException { 
		if(! (this.data.keySet().containsAll(set) && set.containsAll(this.data.keySet()))){
			throw new DependenciesException("[checkByExternalRef] ids are not striclty equivalent set");
		}
	}
	/**
	 * checkCyclicity check cycle in DependancyManagement object
	 * @throws OntologyMetaDataException
	 * @throws DependanciesException 
	 */
	private void checkCyclicity() throws DependenciesException {
		DirectedMultigraph g = new DirectedMultigraph(String.class); // @TODO change the Type
		Iterator<T> itDepOnto ;
		String idDep;
		System.out.print(this.data.entrySet().hashCode());
		for(Entry<T, HashSet<T>> entry: this.data.entrySet()){
		   itDepOnto = entry.getValue().iterator();
		   System.out.println(entry.getKey());
		    if(! g.containsVertex(entry.getKey())){
			   g.addVertex(entry.getKey());
		   }
		   while(itDepOnto.hasNext()){
			   idDep = itDepOnto.next().toString();
			   if(!g.containsVertex(idDep)){
				   g.addVertex(idDep);
			   }
			  g.addEdge(entry.getKey(), idDep, entry.getKey().toString()+"=>"+idDep.toString());
		   }
		}
		
		CycleDetector cG = new CycleDetector(g);
		if(cG.detectCycles()) {
			throw new DependenciesException("[Dependancy Management] cycle is detected into dependancies elements");
		};
	
	}
	
	public HashSet<T> getDependencies(T object){
		if(this.data.get(object) == null) {
			return( new HashSet<T>());
		}else {
			return (HashSet<T>) (this.data.get(object)) ;
		}
	}
	
	/** 
	 * orderDependancies enable to order ontology ids according their dependancies. Use java graph lib
	 * @return LinkOrderSet
	 */
	public LinkedHashSet<T> orderDependancies(){
		Map<T, HashSet<T>> dependancyMap = this.data; //= (HashMap<String, Set<String>>) this.data.clone();
		LinkedHashSet<T> orderSet = new LinkedHashSet<T>();
		
		Set<T> idToTreat = dependancyMap.keySet();
		Iterator<T> itTreat = idToTreat.iterator();

		T curID = null;
		HashSet<T> depForCur = null;
		Iterator<T>  itCurDep = null;
		T depID = null;
		boolean allDepManage = true;

		
		while(itTreat.hasNext()){
			curID = itTreat.next();
			depForCur = getDependencies(curID);
			itCurDep = depForCur.iterator();
			allDepManage = true; 
			
			while(itCurDep.hasNext() && allDepManage){
				depID = itCurDep.next();
				allDepManage = orderSet.contains(depID);
			}
			
			if(allDepManage){
				orderSet.add(curID);
				idToTreat.remove(curID);
				itTreat = idToTreat.iterator();
			}
		}
		return orderSet;
	}
	public HashMap<T, HashSet<T>> getDependenciesAdjacencyList(){
		return(HashMap<T, HashSet<T>>) this.data;
	}

	/**
	 * create subset of DependencyObject according ids Set
	 * @parameter setID Set<T>
	 * @return DependencyObject
	 * @throws DependenciesException if empty/null parameter, or some ids corresponds to anything, or error into dependency object creation
	 * 
	 */
	public DependencyObject subset(Set<T> setID) throws DependenciesException {
		DependencyObject subsetD = null ; 
		Map<T, HashSet<T>> sub =  this.getDependenciesAdjacencyList().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue)) ;
	
		if(setID == null || setID.isEmpty()) {
			throw new DependenciesException("[subset] null or empty argument");
		}else if(! this.data.keySet().containsAll(setID)){
			throw new DependenciesException("[subset] all ids aren't in dependency object");
		}else {
			sub.keySet().retainAll(setID);
			subsetD = new DependencyObject((HashMap<T, HashSet<T>>) sub);

		}
		return subsetD;
		
	}
}
