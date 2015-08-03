package com.consultation.app.activity;

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

    protected void initCaseDatas() {
        AssetManager asset=getAssets();
        try {
            InputStream input=asset.open("case_type.xml");
            SAXParserFactory spf=SAXParserFactory.newInstance();
            SAXParser parser=spf.newSAXParser();
            CaseXmlParserHandler handler=new CaseXmlParserHandler();
            parser.parse(input, handler);
            input.close();
            caseList=handler.getDataList();
        } catch(Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }
}
