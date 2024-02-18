package moreno.joaquin.filemanagerapp.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage") //return properties prefixed with "storage" in the application.properties
public class StorageProperties {

    //Folder Location for storing files
    private String location = "upload-dir";

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }



}
