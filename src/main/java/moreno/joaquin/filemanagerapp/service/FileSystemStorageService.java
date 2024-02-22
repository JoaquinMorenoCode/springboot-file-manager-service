package moreno.joaquin.filemanagerapp.service;

import moreno.joaquin.filemanagerapp.config.StorageProperties;
import moreno.joaquin.filemanagerapp.exception.StorageException;
import moreno.joaquin.filemanagerapp.exception.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {


    private final Path rootLocation;



    @Autowired
    public FileSystemStorageService(StorageProperties properties){
        if(properties.getLocation().trim().length() ==0){
            throw new StorageException("File upload location can not be empty");
        }

        this.rootLocation = Paths.get(properties.getLocation());



    }

    @Override
    public void init() {

        try {
            Files.createDirectories(rootLocation.resolve("files"));
            Files.createDirectories(rootLocation.resolve("images"));


        }catch (IOException e){
            throw new StorageException("Unable to initialize storage",e);
        }

    }

    @Override
    public void store(MultipartFile file, int type) {

        try {
            //check if file is empty
            if(file.isEmpty()){
                throw new StorageException("Can not save an empty file.");

            }

            Path destinationPath =  type==0 ?  this.rootLocation.resolve("files") : this.rootLocation.resolve("images");


            //Set destination path
            Path destinationFile = destinationPath.resolve(
                    Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if(!destinationFile.getParent().equals(destinationPath.toAbsolutePath())){
                throw new StorageException("File can not be saved outside of currenty directory");
            }

            //try to copy the file
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream,destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }


        }catch (IOException e){
            throw new StorageException("Failed to store file",e);
        }

    }

    @Override
    public Stream<Path> loadAll() {

        try {
            return Files.walk(this.rootLocation,1)
                    .filter(path -> !path.equals(this.rootLocation)) //Excludes all files in the same directory of rootlocation, getting only its inmediately childs
                    .map(this.rootLocation::relativize);
        }catch (IOException e){
            throw new StorageException("Failed to read stored files");
        }

    }

    @Override
    public Path load(String filename, ResourceType resourceType) {

        Path getResource;

        if(resourceType == ResourceType.FILE){
             getResource = rootLocation.resolve("files").resolve(filename);
        }else{
             getResource = rootLocation.resolve("images").resolve(filename);
        }

        return getResource;



    }

    @Override
    public Resource loadAsResource(String filename, ResourceType resourceType) {
        try{
            Path file = load(filename, resourceType);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new StorageFileNotFoundException("Unable to load " + filename);
            }
        }catch (MalformedURLException e){
            throw new StorageFileNotFoundException("Unable to load " + filename,e);
        }
    }

    @Override
    public void deleteAll() {
      FileSystemUtils.deleteRecursively(rootLocation.toFile());


    }
}
