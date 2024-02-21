package moreno.joaquin.filemanagerapp.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage") //return properties prefixed with "storage" in the application.properties
public class StorageProperties {

    //Folder Location for storing files
    private String fileLocation = "upload-dir/files";
    private String imageLocation = "upload-dir/images";
//    private String imageLocation = "\\resources\\static\\images";



    public String getFileLocation(){
        return fileLocation;
    }

    public String getImageLocation(){
        return imageLocation;
    }

    public void setFileLocation(String fileLocation){
        this.fileLocation = fileLocation;
    }
    public void setImageLocation(String imageLocation){
        this.imageLocation = imageLocation;
    }



}
