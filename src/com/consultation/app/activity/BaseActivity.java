package com.consultation.app.activity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Activity;
import android.content.res.AssetManager;

import com.consultation.app.listener.XmlParserHandler;
import com.consultation.app.model.CityModel;
import com.consultation.app.model.DistrictModel;
import com.consultation.app.model.ProvinceModel;

public class BaseActivity extends Activity {

    protected String[] mProvinceDatas;

    protected Map<String, String[]> mCitisDatasMap=new HashMap<String, String[]>();

    protected Map<String, String[]> mDistrictDatasMap=new HashMap<String, String[]>();

    protected Map<String, String> mZipcodeDatasMap=new HashMap<String, String>();

    protected String mCurrentProviceName;

    protected String mCurrentCityName;

    protected String mCurrentDistrictName="";

    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList=null;
        AssetManager asset=getAssets();
        try {
            InputStream input=asset.open("province_data.xml");
            SAXParserFactory spf=SAXParserFactory.newInstance();
            SAXParser parser=spf.newSAXParser();
            XmlParserHandler handler=new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            provinceList=handler.getDataList();
            if(provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName=provinceList.get(0).getName();
                List<CityModel> cityList=provinceList.get(0).getCityList();
                if(cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName=cityList.get(0).getName();
                    List<DistrictModel> districtList=cityList.get(0).getDistrictList();
                    mCurrentDistrictName=districtList.get(0).getName();
                }
            }
            mProvinceDatas=new String[provinceList.size()];
            for(int i=0; i < provinceList.size(); i++) {
                mProvinceDatas[i]=provinceList.get(i).getName();
                List<CityModel> cityList=provinceList.get(i).getCityList();
                String[] cityNames=new String[cityList.size()];
                for(int j=0; j < cityList.size(); j++) {
                    cityNames[j]=cityList.get(j).getName();
                    List<DistrictModel> districtList=cityList.get(j).getDistrictList();
                    String[] distrinctNameArray=new String[districtList.size()];
                    DistrictModel[] distrinctArray=new DistrictModel[districtList.size()];
                    for(int k=0; k < districtList.size(); k++) {
                        DistrictModel districtModel=
                            new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k]=districtModel;
                        distrinctNameArray[k]=districtModel.getName();
                    }
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch(Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

}
