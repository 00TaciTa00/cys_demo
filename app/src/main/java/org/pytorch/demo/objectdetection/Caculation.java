package org.pytorch.demo.objectdetection;

import android.graphics.RectF;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Caculation {

    private RectF mRectF;
    private String label;

    public Caculation(RectF rect, String label) {
        mRectF = rect;
        this.label = label;
    }

    public float calculateRectSize() {
        float width = mRectF.right - mRectF.left;
        float height = mRectF.bottom - mRectF.top;
        float area = width * height;

        return area;
    }

    public String getOneFoodAndSugar() {
        String jsonFile = "person.json";  // person.json 파일 경로

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            String jsonContent = stringBuilder.toString();
            JSONArray jsonArray = new JSONArray(jsonContent);

            // 레이블을 검색하여 정보 반환
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String jsonLabel = jsonObject.optString("label");
                if (jsonLabel.equals(label)) {
                    float onefood = (float) jsonObject.optDouble("onefood");
                    float sugar = (float) jsonObject.optDouble("sugar");
                    return serving(calculateRectSize(), onefood, sugar);
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return "none";
    }

    public String serving(float area, float onefood, float sugar){
        float serve = area / onefood;
        float totalsugar = serve * sugar;
        return serve + "인분, 총 당 함유량 : " + totalsugar;
    }
}
