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

public class WeatherAPI extends AsyncTask<Void, Void, String> {

    private String url;
    public WeatherAPI(String url){
        this.url = url;
    }

    @Override
    protected String doInBackground(Void... params) {

        DocumentBuilderFactory dbFacotory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try{
            dBuilder = dbFacotory.newDocumentBuilder();
        }catch (Exception e){
            e.printStackTrace();
        }

        Document doc = null;

        try{
            doc = dBuilder.parse(url);
        }catch (IOException | SAXException e){
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("item");

        for(int temp = 0; temp<nList.getLength(); temp++){
            Node nNode = nList.item(temp);
            if(nNode.getNodeType()==Node.ELEMENT_NODE){

                Element eElement = (Element) nNode;

                // Log.d("OPEN_API","category  : " + getTagValue("category", eElement));
                // Log.d("OPEN_API","fcstValue  : " + getTagValue("fcstValue", eElement));

                // 1. POP 강수확률      %
                // 2. PTY 강수형태      코드값     0없음 1비 2비/눈 3눈 4소나기 5빗방울 6빗방울/눈날림 7눈날림
                // 3. REH 습도          %
                // 4. SKY 하늘상태      코드값     1맑음 3구름많음   4흐림
                // 5. T3H 3시간 기온    ℃
                // 6. UUU 풍속
                // 7. VEC 풍향
                // 8. VVV 풍속
                // 9. WSD 풍속
                // 10. POP 12시 강수확률 %

                // 6
                // LGT 낙뢰                 에너지밀도
                // PTY 강수형태             0없음 1비 2비/눈 3눈 4소나기 5빗방울 6빗방울/눈날림 7눈날림    1
                // RN1 1시간 강수량          mm ex) 1 mm
                // SKY 하늘상태             코드값     1맑음 3구름많음   4흐림                           1
                // T1H 기온                   ℃                                                       1
                // REH 습도                   %                                                        1
                // UUU
                // VVV
                // VEC
                // WSD
                // X    1~5  LGT    11~15 RN1
                // O    6~10 PTY    16~20 SKY     21~25 T1H     26~30 REH

                mainAct.weatheritem.add(getTagValue("fcstValue",eElement));
                Log.i("날씨API 갯수",""+mainAct.weatheritem.size());
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
