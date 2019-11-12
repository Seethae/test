package com.abbott.adc.myfreestyle.servlets;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.servlet.Servlet;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abbott.adc.myfreestyle.constants.DialogConversionConstants;

@Component(immediate = true, metatype = true, label = "Dialog Conversion Servlet")
@Service(Servlet.class)
@Properties({ @Property(name = "service.description", value = "Dialog Conversion Servlet"),
		@Property(name = "service.vendor", value = "abbott"),
		@Property(name = "sling.servlet.paths", value = "/bin/servlet/abbott/myfreestyle/dialogconversion"),
		// Generic handler for all get requests
		@Property(name = "sling.servlet.methods", value = "GET", propertyPrivate = true) })
public class DialogConversionServlet extends SlingAllMethodsServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(DialogConversionServlet.class);

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServerException, IOException {

		Resource resource = request.getResource();
		LOG.info("$Path" + resource.getPath());
		Resource Components = resource.getResourceResolver()
				.getResource("/apps/adc/myfreestyle/components/content/email");
		// LOG.info("$comp" + compfolder.getPath());
		// Iterator<Resource> itrAllComponents = compfolder.listChildren();

		final Map<String, Map<String, ArrayList<Resource>>> componentMapClassic = new TreeMap<String, Map<String, ArrayList<Resource>>>();
		final Map<String, Map<String, ArrayList<Resource>>> componentMapTouch = new TreeMap<String, Map<String, ArrayList<Resource>>>();
		try {
			Iterator<Resource> itrCompNodes = Components.listChildren();
			while (itrCompNodes.hasNext()) {
				Resource compNodes = itrCompNodes.next();
				if (compNodes.getName().equalsIgnoreCase("dialog")) {
					Map<String, ArrayList<Resource>> panelMap = new TreeMap<String, ArrayList<Resource>>();
					Resource classic = resource.getResourceResolver().getResource(compNodes.getPath() + "/items/items");
					Iterator<Resource> tabs = classic.listChildren();
					ArrayList<Resource> nodesInTab = new ArrayList<Resource>();
					while (tabs.hasNext()) {

						Resource tabPanel = tabs.next();
						Node cNode = tabPanel.adaptTo(Node.class);
						String type;
						type = cNode.getProperty("jcr:primaryType").getValue().getString();
						String panelname = cNode.getName();
						if (type.equalsIgnoreCase("cq:Panel")) {
							Resource tabchild = tabPanel.getChild("items");
							nodesInTab = populateTabMap(panelname, tabchild);
							panelMap.put(panelname, nodesInTab);
						}
					}
					componentMapClassic.put(Components.getName(), panelMap);
				}

				if (compNodes.getName().equalsIgnoreCase("cq:dialog")) {
					Map<String, ArrayList<Resource>> panelMap = new TreeMap<String, ArrayList<Resource>>();
					ArrayList<Resource> nodesInTab = new ArrayList<Resource>();
					Resource touch = resource.getResourceResolver()
							.getResource(compNodes.getPath() + "/content/items/tabs/items");
					Iterator<Resource> tabs = touch.listChildren();
					String panelName = "";
					while (tabs.hasNext()) {
						Resource tabPanel = tabs.next();

						if (tabPanel.getResourceType()
								.equalsIgnoreCase("granite/ui/components/coral/foundation/fixedcolumns")) {
							panelName = tabPanel.getName();
							//LOG.info("nodesintabbefore"+getTabChildeNodes(tabPanel.getPath(), tabPanel).toString());
							//Resource tabchild = tabPanel.getChild("items/columns/items");
							nodesInTab = getTabChildNodes(tabPanel.getPath(), tabPanel);
							LOG.info("nodesintabaftercall"+panelName+nodesInTab.toString());
							panelMap.put(panelName, nodesInTab);
						}
					}
					componentMapTouch.put(Components.getName(), panelMap);
				}

				// }
			}
		} catch (ValueFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		compareMaps(componentMapClassic, componentMapTouch);
		for (Map.Entry<String, Map<String, ArrayList<Resource>>> letterEntry : componentMapClassic.entrySet()) {
			String letter = letterEntry.getKey();
			for (Map.Entry<String, ArrayList<Resource>> nameEntry : letterEntry.getValue().entrySet()) {
				String name = nameEntry.getKey();
				ArrayList<Resource> nodes = nameEntry.getValue();
				for (Resource rs : nodes) {
					// LOG.info("MAP"+rs.getPath());
				}
				// ...
			}
		}

		for (Map.Entry<String, Map<String, ArrayList<Resource>>> letterEntry : componentMapTouch.entrySet()) {
			String letter = letterEntry.getKey();
			for (Map.Entry<String, ArrayList<Resource>> nameEntry : letterEntry.getValue().entrySet()) {
				String name = nameEntry.getKey();
				ArrayList<Resource> nodes = nameEntry.getValue();
				for (Resource rs : nodes) {
					 LOG.info("MAPTOUCH"+rs.getPath());
				}
				// ...
			}
		}

	}

	private String getJcrPrimary(Resource rs) {
		Node cNode = rs.adaptTo(Node.class);
		String type = "";
		try {
			type = cNode.getProperty("jcr:primaryType").getValue().getString();
		} catch (ValueFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return type;
	}

	private ArrayList<Resource> populateTabMap(String panelName, Resource tabchild) {

		// Map<String, ArrayList<Resource>> panelMap = new TreeMap<String,
		// ArrayList<Resource>>();

		ArrayList<Resource> nodesInTab = new ArrayList<Resource>();

		// LOG.info("$tabchild" + tabchild.getPath());

		Iterator<Resource> itrTabChild = tabchild.listChildren();

		while (itrTabChild.hasNext()) {
			Resource node = itrTabChild.next();
			nodesInTab.add(node);
			/*
			 * ValueMap vp1=node.getValueMap(); String xtype=vp1.get("xtype", String.class);
			 * String name=vp1.get("name", String.class); LOG.info("$xtypename"+xtype+name);
			 * myMap.put(name, xtype);
			 */
		}

		return nodesInTab;
	}
	 public ArrayList<Resource> getTabChildNodes(String path, Resource tabchild){
		 ArrayList<Resource> nodesInTabTouch = new ArrayList<Resource>();
		 LOG.info("path"+path);
		 if(!path.equals("")){
           Resource startResource = tabchild.getResourceResolver().getResource(path);
           
           
           if(startResource.hasChildren())
           {
           Iterator<Resource> itrChild = startResource.listChildren();
            while(itrChild.hasNext()){
                 Resource childNode = itrChild.next();
                 
                 String resourceTypeCoral = childNode.getResourceType();
                	LOG.info("resourceTypeCoral"+DialogConversionConstants.resourceType.containsValue(resourceTypeCoral));	 
                 if(DialogConversionConstants.resourceType.containsValue(resourceTypeCoral))							
					{
                	 LOG.info("in if"+childNode.getPath());
                	 Iterator<Resource> itrTabChild = childNode.getParent().listChildren();
                	 LOG.info("in if"+childNode.getPath());
                	 while (itrTabChild.hasNext()) {
                		
             			Resource node = itrTabChild.next();
             			 LOG.info("in while"+node.getPath());
             			nodesInTabTouch.add(node);
             			/*
             			 * ValueMap vp1=node.getValueMap(); String xtype=vp1.get("xtype", String.class);
             			 * String name=vp1.get("name", String.class); LOG.info("$xtypename"+xtype+name);
             			 * myMap.put(name, xtype);
             			 */
             			LOG.info("nodesInTab"+nodesInTabTouch.toString());
             		}
                	 LOG.info("nodesInTabafterwhile"+nodesInTabTouch.toString());
                	 LOG.info("returning in if");
                	 return nodesInTabTouch;
                   }
                   else{
                	   LOG.info("IN else");
                	   getTabChildNodes(childNode.getPath(),tabchild);
                       }
            } 
            }           
           }
		 LOG.info("returningmethodend");
		 return nodesInTabTouch;
		}
			   
			   
			   

	void compareMaps(Map<String, Map<String, ArrayList<Resource>>> componentMapClassic,
			Map<String, Map<String, ArrayList<Resource>>> componentMapTouch) {
		for (Map.Entry<String, Map<String, ArrayList<Resource>>> letterEntry : componentMapClassic.entrySet()) {
			String letter = letterEntry.getKey();
			Map<String, ArrayList<Resource>> tabMapTouch = componentMapTouch.get(letter);
			if (tabMapTouch == null) {
				LOG.info("No touch UI dialogue for this component");
			} else {

				for (Map.Entry<String, ArrayList<Resource>> nameEntry : letterEntry.getValue().entrySet()) {
					String name = nameEntry.getKey();
					ArrayList<Resource> NodeList = tabMapTouch.get(nameEntry.getKey());
					if (NodeList == null) {
						LOG.info("No corresponding tab in touch Ui");
					} else {
						ArrayList<Resource> nodes = nameEntry.getValue();
						for (Resource rs : nodes) {
							int count = 0;
							for (Resource rstouch : NodeList) {
								if (rstouch.getName().equalsIgnoreCase(rs.getName())) {
									count = 1;
									ValueMap vp1 = rs.getValueMap();
									ValueMap vptouch = rstouch.getValueMap();
									String xtype = vp1.get("xtype", String.class);
									if (xtype.equals("selection")) {
										xtype = vp1.get("type", String.class);
									}
									String nameClassic = vp1.get("name", String.class);
									String resourceTypeCoral = vptouch.get("sling:resourceType", String.class);
									String coralName = vptouch.get("name", String.class);
									if (!nameClassic.equals(coralName)) {
										LOG.info("Name doent match with classic");
									}
									if (!xtype.equals("mtmulticompositefield")) {
										if (DialogConversionConstants.resourceType.get(xtype)
												.equals(resourceTypeCoral)) {
											LOG.info(resourceTypeCoral, "ResourceType doent match with classic");
										}

									}

								}
							}
							if (count == 0) {
								LOG.info("Clasic node is not present in touch");
							}
						}
					}
				}

			}

		}
	}

}
