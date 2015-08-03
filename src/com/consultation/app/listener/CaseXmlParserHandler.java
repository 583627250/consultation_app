package com.consultation.app.listener;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.consultation.app.model.CaseModel;
import com.consultation.app.model.ItemModel;
import com.consultation.app.model.OptionsModel;
import com.consultation.app.model.TitleModel;

public class CaseXmlParserHandler extends DefaultHandler {
    
    private List<CaseModel> caseList = new ArrayList<CaseModel>();

	public CaseXmlParserHandler() {
		
	}

	public List<CaseModel> getDataList() {
		return caseList;
	}

	@Override
	public void startDocument() throws SAXException {
	}

	private CaseModel caseModel;
	private TitleModel titleModel;
	private ItemModel itemModel;
	private ItemModel subItemModel;
	private OptionsModel optionsModel;
	private String tagName;
    private boolean isTitle = false;
    private boolean isOptions = false;
    private boolean isSubOptions = false;
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
	    tagName = qName;
		if (qName.equals("Root")) {
		    caseModel = new CaseModel();
		    caseModel.setId(attributes.getValue("ID"));
		    caseModel.setName(attributes.getValue("Name"));
		    caseModel.setLevel(attributes.getValue("Level"));
		    caseModel.setTitleModels(new ArrayList<TitleModel>());
		} else if (qName.equals("Group")) {
		    isTitle = true;
		    titleModel = new TitleModel();
		    titleModel.setId(attributes.getValue("ID"));
		    titleModel.setName(attributes.getValue("Name"));
		    titleModel.setLevel(attributes.getValue("Level"));
		    titleModel.setChildCount(attributes.getValue("ChildCount"));
		    titleModel.setItemModels(new ArrayList<ItemModel>());
		} else if (qName.equals("SubItem")) {
		    subItemModel = new ItemModel();
		    subItemModel.setId(attributes.getValue("ID"));
		    subItemModel.setName(attributes.getValue("Name"));
		    subItemModel.setFirstStr(attributes.getValue("FirstStr"));
		    subItemModel.setLastStr(attributes.getValue("LastStr"));
		    subItemModel.setLevel(attributes.getValue("Level"));
		    subItemModel.setType(attributes.getValue("Type"));
		    subItemModel.setOptionsModels(new ArrayList<OptionsModel>());
		    isSubOptions = true;
        } else if (qName.equals("Item")) {
            isSubOptions = false;
            itemModel = new ItemModel();
            itemModel.setId(attributes.getValue("ID"));
            itemModel.setName(attributes.getValue("Name"));
            itemModel.setFirstStr(attributes.getValue("FirstStr"));
            itemModel.setLastStr(attributes.getValue("LastStr"));
            itemModel.setLevel(attributes.getValue("Level"));
            itemModel.setType(attributes.getValue("Type"));
            itemModel.setOptionsModels(new ArrayList<OptionsModel>());
            itemModel.setItemModels(new ArrayList<ItemModel>());
        } else if (qName.equals("Options")) {
            isOptions = true;
            optionsModel = new OptionsModel();
            optionsModel.setId(attributes.getValue("ID"));
            optionsModel.setChecked(attributes.getValue("Checked"));
        }
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	    if (qName.equals("Root")) {
	        caseList.add(caseModel);
        } else if (qName.equals("Group")) {
            caseModel.getTitleModels().add(titleModel);
            isTitle  = false;
        } else if (qName.equals("Item")) {
            titleModel.getItemModels().add(itemModel);
        } else if (qName.equals("SubItem")) {
            itemModel.getItemModels().add(subItemModel);
        } else if (qName.equals("Options")) {
            if(isSubOptions){
                subItemModel.getOptionsModels().add(optionsModel);
            }else{
                itemModel.getOptionsModels().add(optionsModel);
            }
            isOptions = false;
        }
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	    if (this.tagName != null) {  
            String data = new String(ch, start, length);  
            if (this.tagName.equals("Title")) { 
                if(null != data && !"".equals(data)){
                    if(titleModel != null && isTitle && (titleModel.getTitle() == null || "".equals(titleModel.getTitle()))){
                        titleModel.setTitle(data);
                    }
                }
            } else if (this.tagName.equals("Options")) {  
                if(null != data && !"".equals(data)){
                    if(optionsModel != null && isOptions  && (optionsModel.getName() == null || "".equals(optionsModel.getName()))){
                        optionsModel.setName(data);  
                    }
                }
            }  
        }  
	}

}
