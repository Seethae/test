package com.abbott.adc.myfreestyle.servlets;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.jcr.query.Query;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
		Resource componentFolder = resource.getResourceResolver()
				.getResource("/apps/adc/myfreestyle/components/content");
		// Resource component =
		 resource.getResourceResolver().getResource("/apps/adc/myfreestyle/components/content/zigzagtextimage");
		 Comparator<Resource> compareByName = new Comparator<Resource>() {
			@Override
			public int compare(Resource o1, Resource o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
		final Map<String, Map<String, ArrayList<Resource>>> componentMapClassic = new TreeMap<String, Map<String, ArrayList<Resource>>>();
		final Map<String, Map<String, ArrayList<Resource>>> componentMapTouch = new TreeMap<String, Map<String, ArrayList<Resource>>>();
		ArrayList<Resource> allComponents = getChildResources(componentFolder, componentFolder.getPath(),
				DialogConversionConstants.CQ_COMPONENT);
		for (Resource component : allComponents) {

			Iterator<Resource> itrCompNodes = component.listChildren();
			while (itrCompNodes.hasNext()) {
				Resource compNodes = itrCompNodes.next();
				if (compNodes.getName().equalsIgnoreCase(DialogConversionConstants.DIALOG)) {
					Map<String, ArrayList<Resource>> panelMapClassic = new TreeMap<String, ArrayList<Resource>>();
					ArrayList<Resource> tabs = getTabResources(compNodes, compNodes.getPath(), DialogConversionConstants.CQ_PANEL, DialogConversionConstants.CLASSIC);
					ArrayList<Resource> nodesInTab = null;
					if (tabs != null) {
						for (Resource tabPanel : tabs) {

							ArrayList<Resource> childResources = getChildResources(tabPanel, tabPanel.getPath(),
									DialogConversionConstants.UNSTRUCTURED);
							nodesInTab = new ArrayList<Resource>();
							for (Resource rs : childResources) {
								String xtype = rs.getValueMap().get(DialogConversionConstants.XTYPE, DialogConversionConstants.DEFAULT);
								if (xtype.equals(DialogConversionConstants.SELECTION)) {
									xtype = rs.getValueMap().get(DialogConversionConstants.TYPE, String.class);
								}
								if (DialogConversionConstants.resourceType.containsKey(xtype)) {
									nodesInTab.add(rs);
								}

							}
							if (!nodesInTab.isEmpty()) {
								Collections.sort(nodesInTab, compareByName);
								panelMapClassic.put(tabPanel.getName(), nodesInTab);
							}
						}
						if (!panelMapClassic.isEmpty())
							componentMapClassic.put(component.getName(), panelMapClassic);
					}
				}

				if (compNodes.getName().equalsIgnoreCase(DialogConversionConstants.CQ_DIALOG)) {
					Map<String, ArrayList<Resource>> panelMap = new TreeMap<String, ArrayList<Resource>>();					
					ArrayList<Resource> nodesInTab = null;
					ArrayList<Resource> tabs = getTabResources(compNodes, compNodes.getPath(), DialogConversionConstants.UNSTRUCTURED,
							DialogConversionConstants.CORAL);
					if (tabs != null) {
						for (Resource tabPanel : tabs) {
							ArrayList<Resource> childResources = getChildResources(tabPanel, tabPanel.getPath(),
									DialogConversionConstants.UNSTRUCTURED);
							nodesInTab = new ArrayList<Resource>();
							for (Resource rs : childResources) {
								String resourceTypeCoral = rs.getResourceType();
								if (DialogConversionConstants.resourceType.containsValue(resourceTypeCoral)) {
									nodesInTab.add(rs);
								}

							}
							if (!nodesInTab.isEmpty()) {
								Collections.sort(nodesInTab, compareByName);
								panelMap.put(tabPanel.getName(), nodesInTab);
							}
						}
						if (!panelMap.isEmpty())
							componentMapTouch.put(component.getName(), panelMap);
					}

				}
			}
	}
		try {
			JSONArray componentList=compareMaps(componentMapClassic, componentMapTouch);
			LOG.info("JSON ARRAY"+componentList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Map.Entry<String, Map<String, ArrayList<Resource>>> letterEntry : componentMapClassic.entrySet()) {
			String letter = letterEntry.getKey();
			if (letterEntry.getValue() != null) {
				for (Map.Entry<String, ArrayList<Resource>> nameEntry : letterEntry.getValue().entrySet()) {
					String name = nameEntry.getKey();
					ArrayList<Resource> nodes = nameEntry.getValue();
					for (Resource rs : nodes) {
						// LOG.debug("MAP"+rs.getPath());
					}
				}
			}
		}

		for (Map.Entry<String, Map<String, ArrayList<Resource>>> letterEntry : componentMapTouch.entrySet()) {
			String letter = letterEntry.getKey();
			for (Map.Entry<String, ArrayList<Resource>> nameEntry : letterEntry.getValue().entrySet()) {
				String name = nameEntry.getKey();
				ArrayList<Resource> nodes = nameEntry.getValue();
				for (Resource rs : nodes) {
					// LOG.debug("MAPTOUCH" + rs.getPath());
				}

			}
		}

	}

	private ArrayList<Resource> getChildResources(Resource rs, String rootPath, String nodeType) {
		String selectQuery = "SELECT * FROM [" + nodeType + "] AS s WHERE ISDESCENDANTNODE(s,'" + rootPath + "')";
		Iterator<Resource> result = rs.getResourceResolver().findResources(selectQuery, Query.JCR_SQL2);
		ArrayList<Resource> childResources = new ArrayList<Resource>();
		while (result.hasNext()) {
			Resource node = result.next();
			if(!node.getPath().contains("rtePlugins")&&!node.getPath().contains("htmlRules"))
			childResources.add(node);
		}
		return childResources;

	}

	private ArrayList<Resource> getTabResources(Resource rs, String rootPath, String nodeType, String dialogType) {
		String selectQuery = "SELECT * FROM [" + nodeType + "] AS s WHERE ISDESCENDANTNODE(s,'" + rootPath + "')";
		Iterator<Resource> result = rs.getResourceResolver().findResources(selectQuery, Query.JCR_SQL2);
		ArrayList<Resource> childResources = null;
		if (dialogType.equalsIgnoreCase(DialogConversionConstants.CORAL)) {
			childResources = new ArrayList<Resource>();
			while (result.hasNext()) {
				Resource node = result.next();
				String resourceTypeCoral = node.getResourceType();
				if (resourceTypeCoral.equalsIgnoreCase(DialogConversionConstants.FIXED_COLUMNS)) {

					childResources.add(node);

				}
			}

		}

		if (dialogType.equalsIgnoreCase(DialogConversionConstants.CLASSIC)) {
			childResources = new ArrayList<Resource>();
			while (result.hasNext()) {
				Resource node = result.next();
				childResources.add(node);
			}
		}
		return childResources;
	}

	JSONArray compareMaps(Map<String, Map<String, ArrayList<Resource>>> componentMapClassic,
			Map<String, Map<String, ArrayList<Resource>>> componentMapCoral) throws JSONException {
		JSONArray componentList = new JSONArray();
		JSONObject componentObj=null;
		JSONObject dialog =null;
		for (Map.Entry<String, Map<String, ArrayList<Resource>>> entry : componentMapClassic.entrySet()) {
			String component = entry.getKey();
			Map<String, ArrayList<Resource>> dialogMapCoral = componentMapCoral.get(component);
			componentObj=new JSONObject();
			dialog =new JSONObject();
			JSONArray tabList =null;
			
			if (dialogMapCoral == null) {
				LOG.info("No Coral dialogue for this component" + component);
				dialog.put("No coral Dialog", component);
			} else {
				tabList =new JSONArray();
				JSONObject tab =null;
				for (Map.Entry<String, ArrayList<Resource>> tabMapClassic : entry.getValue().entrySet()) {
					tab = new JSONObject();
					String name = tabMapClassic.getKey();
					ArrayList<Resource> nodeListCoral = dialogMapCoral.get(tabMapClassic.getKey());
					if (nodeListCoral == null) {
						LOG.info("No corresponding tab in touch Ui" + name);
						tab.put(name, "no tab in coral"+name);
					} else {
						ArrayList<Resource> nodesListClassic = tabMapClassic.getValue();
						JSONArray nodeList=compareNodes(nodesListClassic,nodeListCoral);
						if(nodeList!=null &&nodeList.length()>0)
						tab.put(name, nodeList);
					}
					if(tab!=null &&tab.length()>0)
					tabList.put(tab);
				}
				if(tabList!=null &&tabList.length()>0)
				dialog.put(DialogConversionConstants.DIALOG, tabList);

			}
			if(dialog!=null &&dialog.length()>0)			
			componentObj.put(component, dialog);
			if(componentObj!=null &&componentObj.length()>0)
			componentList.put(componentObj);

		}
		return componentList;
	}

	JSONArray compareNodes(ArrayList<Resource> nodeListClassic, ArrayList<Resource> nodeListCoral)
			throws JSONException {
		JSONArray nodeList = new JSONArray();
		JSONObject node = null;
		for (Resource rsClassic : nodeListClassic) {
			node = new JSONObject();

			int count = 0;

			for (Resource rsCoral : nodeListCoral) {

				if (rsCoral.getName().equalsIgnoreCase(rsClassic.getName())) {
					node = new JSONObject();
					count = 1;

					String xtype = getPropertyValue(rsClassic, DialogConversionConstants.XTYPE);
					LOG.info("xtype" + xtype + "$$" + rsClassic.getPath());
					if (xtype.equals(DialogConversionConstants.MULTIFIELD)) {

						String multiNodeName = rsCoral.getChild(DialogConversionConstants.FIELD_NODE).getValueMap()
								.get(DialogConversionConstants.NAME, DialogConversionConstants.DEFAULT);
						LOG.info("multiNodeNamecoral"+multiNodeName+"classicname"+getPropertyValue(rsClassic, DialogConversionConstants.NAME));
						if (!getPropertyValue(rsClassic, DialogConversionConstants.NAME).equals(multiNodeName)) {
							LOG.info("Name doent match with classic" + rsCoral.getPath());
							node.put("name mismatch", rsCoral.getPath());
						}
					}
					else if (!xtype.equals(DialogConversionConstants.MULTIFIELD)) {
						if ((!getPropertyValue(rsClassic, DialogConversionConstants.NAME).startsWith("./")) && rsClassic
								.getParent().getName().equalsIgnoreCase(DialogConversionConstants.FIELD_CONFIG_NODE)) {
							if (!getPropertyValue(rsClassic, DialogConversionConstants.NAME).equals(
									getPropertyValue(rsCoral, DialogConversionConstants.NAME).replace("./", "")))								
							node.put("name mismatch", rsCoral.getPath());
						}

						else if (!getPropertyValue(rsClassic, DialogConversionConstants.NAME)
								.equals(getPropertyValue(rsCoral, DialogConversionConstants.NAME))) {

							LOG.info("Name doent match with classic" + rsCoral.getPath());
							node.put("name mismatch", rsCoral.getPath());
						}

					} 
					if (xtype.equals(DialogConversionConstants.CHECKBOX)) {
						if ((!getPropertyValue(rsClassic, DialogConversionConstants.FIELD_LABEL)
								.equals(getPropertyValue(rsCoral, DialogConversionConstants.TEXT)))) {
							LOG.info("text for checkbox doentmatch with classic" + rsClassic.getPath());
							node.put("text mismatch", rsCoral.getPath());

						}
						if (!getPropertyValue(rsCoral, DialogConversionConstants.VALUE)
								.equals(DialogConversionConstants.TRUE)) {
							node.put("value mismatch", rsCoral.getPath());
						}

					} 
					if (xtype.equals(DialogConversionConstants.HTML5_IMAGE)) {
						LOG.info("imageseetha");
						if (!getPropertyValue(rsClassic, DialogConversionConstants.FILENAME_PARAMETER)
								.equals(getPropertyValue(rsCoral, DialogConversionConstants.FILENAME_PARAMETER))) {
							LOG.info("file name parameter doesnt match with classic" + rsClassic.getPath());
							node.put("fileNameParameter mismatch", rsCoral.getPath());
						}
						if (!getPropertyValue(rsClassic, DialogConversionConstants.FILEREF_PARAMETER)
								.equals(getPropertyValue(rsCoral, DialogConversionConstants.FILEREF_PARAMETER))) {
							LOG.info("file ref parameter doesnt match with classic" + rsClassic.getPath());
							node.put("fileReferenceParameter mismatch", rsCoral.getPath());
						}
						if (!getPropertyValue(rsCoral, DialogConversionConstants.MIME_TYPES)
								.equals(DialogConversionConstants.IMAGE)) {
							node.put("mimeTypes is not image", rsCoral.getPath());
						}
						if (!getPropertyValue(rsCoral, DialogConversionConstants.ALLOW_UPLOAD)
								.equals(DialogConversionConstants.FALSE)) {
							node.put("allowUpload is not false", rsCoral.getPath());
						}	
					}
					
					if (!getPropertyValue(rsCoral, DialogConversionConstants.PRIMARY_TYPE)
							.equals(DialogConversionConstants.UNSTRUCTURED)) {
						LOG.info("Wrong Primary Type" + rsClassic.getPath());
						node.put("Wrong Primary Type", rsCoral.getPath());
					}
					if (!DialogConversionConstants.resourceType
							.get(getPropertyValue(rsClassic, DialogConversionConstants.XTYPE))
							.equals(getPropertyValue(rsCoral, DialogConversionConstants.SLING_RESOURCETYPE))) {
						LOG.info("ResourceType doent match with classic" + rsClassic.getPath());
						node.put("resourceType mismatch", rsCoral.getPath());
					}

					if ((!getPropertyValue(rsClassic, DialogConversionConstants.FIELD_LABEL)
							.equals(getPropertyValue(rsCoral, DialogConversionConstants.FIELD_LABEL)))
							&& !xtype.equals(DialogConversionConstants.CHECKBOX)) {
						LOG.info("Field Label doentmatch with classic" + rsClassic.getPath());
						node.put("fieldLabel mismatch", rsCoral.getPath());

					}
					if (!getPropertyValue(rsClassic, DialogConversionConstants.DEFAULT_VALUE)
							.equals(getPropertyValue(rsCoral, DialogConversionConstants.VALUE))) {
						LOG.info("Default Value doent match with classic" + rsClassic.getPath());
						node.put("Default Value mismatch", rsCoral.getPath());
					}

					// }
					if (node.length() != 0) {
						nodeList.put(node);
					}

				}
			}

			if (count == 0) {
				node = new JSONObject();
				LOG.info("Clasic node is not present in touch" + rsClassic.getPath());
				node.put("Node mismatch", rsClassic.getPath());
				nodeList.put(node);
			}

		}
		return nodeList;

	}



	private String getPropertyValue(Resource rs, String prop) {
		String property = "";
		ValueMap vp = rs.getValueMap();
		property = vp.get(prop, DialogConversionConstants.DEFAULT);
		if (property.equals(DialogConversionConstants.SELECTION)) {
			property = vp.get(DialogConversionConstants.TYPE, DialogConversionConstants.DEFAULT);
		}
		LOG.info("$$"+rs.getName()+prop+"$"+property);
		return property;
	}
	

}
