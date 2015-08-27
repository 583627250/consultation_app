package com.consultation.app.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Activity;
import android.content.res.AssetManager;

import com.consultation.app.listener.CaseXmlParserHandler;
import com.consultation.app.model.CaseModel;

public class CaseBaseActivity extends Activity {
    
    protected List<CaseModel> caseList;
    
    protected void initCaseDatas(String name) {
        AssetManager asset=getAssets();
        try {
            InputStream input=asset.open(name);
            SAXParserFactory spf=SAXParserFactory.newInstance();
            SAXParser parser=spf.newSAXParser();
            CaseXmlParserHandler handler=new CaseXmlParserHandler();
            parser.parse(input, handler);
            input.close();
            caseList=handler.getDataList();
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }
    
    protected void XMLCaseDatas(String xmlString) {
        try {
            InputStream input = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
            SAXParserFactory spf=SAXParserFactory.newInstance();
            SAXParser parser=spf.newSAXParser();
            CaseXmlParserHandler handler=new CaseXmlParserHandler();
            parser.parse(input, handler);
            input.close();
            caseList=handler.getDataList();
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }
}
