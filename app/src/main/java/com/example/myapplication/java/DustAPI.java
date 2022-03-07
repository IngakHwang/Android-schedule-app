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

public class DustAPI extends AsyncTask<Void, Void, String> {
    private String url;

    public DustAPI(String url) {
        this.url = url;
    }

    @Override
    protected String doInBackground(Void... params) {

        DocumentBuilderFactory dbFacotory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dustBuilder = null;
        try{
            dustBuilder = dbFacotory.newDocumentBuilder();
        }catch (Exception e){
            e.printStackTrace();
        }

        Document dustdoc = null;

        try{
            dustdoc = dustBuilder.parse(url);
        }catch (IOException | SAXException e){
            e.printStackTrace();
        }

        dustdoc.getDocumentElement().normalize();

        NodeList dustList = dustdoc.getElementsByTagName("item");

        for(int temp = 0; temp<dustList.getLength(); temp++){
            Node dustNode = dustList.item(temp);
            if(dustNode.getNodeType()==Node.ELEMENT_NODE){

                Element eElement = (Element) dustNode;

                mainAct.dustitem.add(getTagValue("dataTime",eElement));
                Log.i("Dust 데이터",""+mainAct.dustitem.get(mainAct.dust));
                mainAct.dust++;

                mainAct.dustitem.add(getTagValue("pm10Value",eElement));
                Log.i("Dust 데이터",""+mainAct.dustitem.get(mainAct.dust));
                mainAct.dust++;

                mainAct.dustitem.add(getTagValue("pm25Value",eElement));
                Log.i("Dust 데이터",""+mainAct.dustitem.get(mainAct.dust));
                mainAct.dust++;

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
