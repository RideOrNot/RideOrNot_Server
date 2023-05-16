package com.example.hanium2023;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Controller
public class HomeController {

    @Autowired
    private SubstationInfoRepository infoRepository;

    @GetMapping("/api")
    public String index(){
        return "index";
    }

    @PostMapping("/api")
    public String load_save(@RequestParam("date") String date, Model model){
        String result = "";

        try {
            String requestDate=date;
            URL url = new URL("http://openapi.seoul.go.kr:8088/" + "받은인증키/" +
                    "json/CardSubwayStatsNew/1/700/"+requestDate);
            BufferedReader bf;
            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            result = bf.readLine();

            JSONParser jsonParser = new JSONParser(result);
            JSONObject jsonObject = (JSONObject)jsonParser.parse();
            JSONObject CardSubwayStatsNew = (JSONObject)jsonObject.get("CardSubwayStatsNew");
            Long totalCount=(Long)CardSubwayStatsNew.get("list_total_count");

            JSONObject subResult = (JSONObject)CardSubwayStatsNew.get("RESULT");
            JSONArray infoArr = (JSONArray) CardSubwayStatsNew.get("row");

            for(int i=0;i<infoArr.length();i++){
                JSONObject tmp = (JSONObject)infoArr.get(i);
                SubstationInfo infoObj=new SubstationInfo(i+(long)1, (String)tmp.get("USE_DT"),(String)tmp.get("LINE_NUM"),(String)tmp.get("SUB_STA_NM"),
                        (double)tmp.get("RIDE_PASGR_NUM"), (double)tmp.get("ALIGHT_PASGR_NUM"),(String)tmp.get("WORK_DT"));
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return "redirect:/findname";
    }
}