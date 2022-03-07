package com.example.myapplication.java;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.java.mainAct;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CovidAPI extends AsyncTask<Void, Void, String> {

    private String url;

    public CovidAPI(String url) {
        this.url = url;
    }

    @Override
    protected String doInBackground(Void... params) {

        DocumentBuilderFactory dbFacotory = DocumentBuilderFactory.newInstance();
        DocumentBuilder covidBuilder = null;
        try{
            covidBuilder = dbFacotory.newDocumentBuilder();
        }catch (Exception e){
            e.printStackTrace();
        }

        Document coviddoc = null;

        try{
            coviddoc = covidBuilder.parse(url);
        }catch (IOException | SAXException e){
            e.printStackTrace();
        }

        coviddoc.getDocumentElement().normalize();

        NodeList covidList = coviddoc.getElementsByTagName("item");

        for(int temp = 0; temp<covidList.getLength(); temp++){
            Node covidNode = covidList.item(temp);
            if(covidNode.getNodeType()==Node.ELEMENT_NODE){

                Element eElement = (Element) covidNode;

                mainAct.coviditem.add(getTagValue("decideCnt",eElement));           // 확진자
                Log.i("Covid 데이터",""+mainAct.coviditem.get(mainAct.covid));
                mainAct.covid++;

                mainAct.coviditem.add(getTagValue("examCnt",eElement));             // 검사진행
                Log.i("Covid 데이터",""+mainAct.coviditem.get(mainAct.covid));
                mainAct.covid++;

                mainAct.coviditem.add(getTagValue("clearCnt",eElement));            // 격리해제
                Log.i("Covid 데이터",""+mainAct.coviditem.get(mainAct.covid));
                mainAct.covid++;

                mainAct.coviditem.add(getTagValue("deathCnt",eElement));            //사망자
                Log.i("Covid 데이터",""+mainAct.coviditem.get(mainAct.covid));
                mainAct.covid++;

            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    private String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if(nValue == null)
            return null;
        return nValue.getNodeValue();
    }
}
