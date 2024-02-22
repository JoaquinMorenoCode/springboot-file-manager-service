package moreno.joaquin.filemanagerapp.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage") //return properties prefixed with "storage" in the application.properties
public class StorageProperties {

    //Folder Location for storing files

    private String rootLocation = "upload-dir";

//    private String imageLocation = "\\resources\\static\\images";




    public String getLocation() {
        return rootLocation;
    }
    public void setLocation(String rootLocation) {
        this.rootLocation = rootLocation;
    }
}
