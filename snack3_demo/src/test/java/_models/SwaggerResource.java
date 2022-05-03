package _models;

import lombok.Getter;
import lombok.Setter;
import org.noear.solon.Utils;

/**
 * @author noear 2022/5/3 created
 */
@Getter
@Setter
public class SwaggerResource {
    public String packageName;

    public void setPackageName(String packageName) {
        this.packageName = packageName;

        if (Utils.isNotEmpty(packageName)) {
            setUrl("/swagger/api?group=" + packageName);
            setLocation("/swagger/api?group=" + packageName);
            setSwaggerVersion("2.0");
        }
    }

    private String name;
    private String url;
    private String location;
    private String swaggerVersion;
}
