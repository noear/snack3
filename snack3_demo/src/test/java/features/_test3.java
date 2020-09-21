package features;

import _models.PanOcrModel;
import _models.ShanYunResModel;
import org.junit.Test;
import org.noear.snack.ONode;

public class _test3 {
    @Test
    public void test() {
        String json = "{\"reportId\":\"N4293fAVK86Jq1Mf465B\",\"statusCode\":0,\"statusMessage\":\"E_SUCCESS\",\"IP\":\"122.224.92.122\",\"data\":{\"errorCode\":2000,\"errMessage\":\"E_OCR_SUCCESS\",\"cardType\":\"PAN_FRONT\",\"panCode\":\"MEUPS2579N\",\"panName\":\"YEKKALADEVI SUBRAMANYAM\",\"dateOfBirth\":\"05/03/1988\",\"fatherName\":\"YEKKALADEVI RAGHUNANDANA RAO\"}}";


        ONode oNode = ONode.loadStr(json);
        oNode.get("data").set("@type", PanOcrModel.class.getName());

        ShanYunResModel<PanOcrModel> tmp = oNode.toObject(ShanYunResModel.class);

        assert tmp.data instanceof PanOcrModel;
    }
}
