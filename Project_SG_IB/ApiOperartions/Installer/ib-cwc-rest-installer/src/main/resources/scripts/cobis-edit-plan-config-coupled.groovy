import groovy.util.Node;
/**
 * No se debe modificar la clase
 * @author fabad
 *
 */
class ModifyPlans {

	   //Metodo para agreagar al ultimo
	   void addConfigAtTheEndDeleteIfExist(Node rootVar, Node nodeVar){
		  def configs =  rootVar.own[0].children()
		  def config = configs.find { it.@id == nodeVar.@id }
		  if(config != null) {
              configs.remove(config)
		  }
		  rootVar.own[0].append(nodeVar)
	   }
	
		//Metodos de agregacion
	
		void addPlanBefore(Node rootVar, Object id, Node nodeVar) {
			def configs =  rootVar.own[0].children()
			def config = configs.find{ it.@id == nodeVar.@id }
			def index = configs.indexOf(config)
			configs.add(index, nodeVar)
		}
 
		void addPlanAfter(Node rootVar, Object id, Node nodeVar) {
			def configs =  rootVar.own[0].children()
			def config = configs.find{ it.@id == id }
			def index = configs.indexOf(config)
			configs.add(index + 1, nodeVar)
		}
		
		void addPlanBeforeDeleteIfExists(Node rootVar, Object id, Node nodeVar) {
			def configs =  rootVar.own[0].children()
			def config = configs.find { it.@id == nodeVar.@id }
			if(config != null) {
				 configs.remove(config)
			}
			addPlanBefore(rootVar, id, nodeVar)
		}
		
		void addPlanAfterDeleteIfExists(Node rootVar, Object id, Node nodeVar) {
			def configs =  rootVar.own[0].children()
			def config = configs.find { it.@id == nodeVar.@id }
			if(config != null) {
				configs.remove(config)
			}
			addPlanAfter(rootVar, id, nodeVar)
		}
	 
		void movePlanBefore(Node rootVar, Object idMove, Node id){
			def configs =  rootVar.own[0].children()
			def config = configs.find { it.@id == id }
			configs.remove(config)
			addPlanBefore(rootVar, id, config)
		}
	 
		void movePlanAfter(Node rootVar, Object idMove, Object id){
			def configs =  rootVar.own[0].children()
			def config = configs.find { it.@id == idMove }
			configs.remove(config)
			addPlanAfter(rootVar, id, config)
		}

}
//plugins

def planIBRestService = new Node(null, "plan", [ id: "services-plan-api-rest-wm", reloadable: "true"])
planIBRestService.append(new Node(null, "plugin", [name: "data-otp-wm-plan-dto", path: "../plugins/ApiServicesWM/COBISCorp.eCOBIS.DataContractOperations.DTO-1.0.0.0.jar"]))
planIBRestService.append(new Node(null, "plugin", [name: "service-otp-wm-service", path: "../plugins/ApiServicesWM/COBISCorp.eCOBIS.ServiceContractOperations.Service-1.0.0.0.jar"]))
planIBRestService.append(new Node(null, "plugin", [name: "service-otp-wm-service-rest", path: "../plugins/ApiServicesWM/COBISCorp.eCOBIS.ServiceContractOperations.Service-rest-1.0.0.0.jar"]))

def modPlan = new ModifyPlans()
modPlan.addPlanAfterDeleteIfExists(root, "cobis-container-service-impl", planIBRestService)

