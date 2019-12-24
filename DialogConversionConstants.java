package com.abbott.adc.myfreestyle.constants;

import java.util.Map;
import java.util.TreeMap;

public class DialogConversionConstants {
	public static final String NAME_CHECK = "done";
	public static final String FIXED_COLUMNS="granite/ui/components/coral/foundation/fixedcolumns";
	public static final String DEFAULT= "default";
	public static final String CQ_COMPONENT= "cq:Component";
	public static final String CQ_PANEL= "cq:Panel";
	public static final String CQ_DIALOG= "cq:dialog";
	public static final String CORAL= "coral";
	public static final String CLASSIC= "classic";
	public static final String DIALOG= "dialog";
	public static final String PRIMARY_TYPE= "jcr:primaryType";
	public static final String UNSTRUCTURED= "nt:unstructured";
	public static final String XTYPE = "xtype";
	public static final String SLING_RESOURCETYPE = "sling:resourceType";
	public static final String NAME = "name";	
	public static final String FIELD_LABEL="fieldLabel";
	public static final String DEFAULT_VALUE = "defaultValue";
	public static final String TYPE ="type";
	public static final String MIME_TYPES = "mimeTypes";	
	public static final String ALLOW_UPLOAD = "allowUpload";
	public static final String VALUE= "value";
	public static final String TRUE= "true";
	public static final String FALSE= "false";
	public static final String TEXT= "text";
	public static final String FIELD_CONFIG_NODE="fieldConfigs";
	public static final String FIELD_NODE="field";
	public static final String MULTIFIELD = "mtmulticompositefield";
	public static final String CHECKBOX="checkbox";
	public static final String HTML5_IMAGE="html5smartimage";
	public static final String SELECTION="selection";
	public static final String IMAGE="image";
	public static final String FILENAME_PARAMETER="fileNameParameter";
	public static final String FILEREF_PARAMETER="fileReferenceParameter";
	
	
	public static final Map<String, String> resourceType = new TreeMap<String, String>(); 
	static {
	resourceType.put("pathfield", "granite/ui/components/coral/foundation/form/pathfield");
    resourceType.put("richtext", "cq/gui/components/authoring/dialog/richtext");
    resourceType.put("textfield", "granite/ui/components/coral/foundation/form/textfield");	    
    resourceType.put("select", "granite/ui/components/coral/foundation/form/select");
    resourceType.put("checkbox", "granite/ui/components/coral/foundation/form/checkbox");
    resourceType.put("mtmulticompositefield", "granite/ui/components/coral/foundation/form/multifield");
    resourceType.put("html5smartimage", "cq/gui/components/authoring/dialog/fileupload");
	}
	
	public static final Map<String, String> nodePropertiesCoral = new TreeMap<String, String>(); 
	static {
		nodePropertiesCoral.put("fieldLabel", "fieldLabel");
		nodePropertiesCoral.put("fieldDescription", "fieldDescription");
		nodePropertiesCoral.put("fieldSubLabel", "fieldDescription");	    
		nodePropertiesCoral.put("allowBlank", "required");
		nodePropertiesCoral.put("defaultValue", "value");
		nodePropertiesCoral.put("jcr:primaryType", "jcr:primaryType");
		nodePropertiesCoral.put("xtype", "sling:resourceType");
		nodePropertiesCoral.put("title", "granite:title");
		nodePropertiesCoral.put("allowUpload", "allowUpload");
		nodePropertiesCoral.put("name", "name");
		nodePropertiesCoral.put("fileReferenceParameter", "fileReferenceParameter");
		nodePropertiesCoral.put("fileNameParameter", "fileNameParameter");
		nodePropertiesCoral.put("mimeTypes", "mimeTypes");
		nodePropertiesCoral.put("value", "value");
		
	}
}
