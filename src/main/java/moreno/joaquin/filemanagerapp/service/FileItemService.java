package moreno.joaquin.filemanagerapp.service;


import moreno.joaquin.filemanagerapp.config.StorageProperties;
import moreno.joaquin.filemanagerapp.exception.StorageException;
import moreno.joaquin.filemanagerapp.model.FileItem;
import moreno.joaquin.filemanagerapp.repository.FileItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.util.List;
import java.util.Optional;

@Service
public class FileItemService {

    @Autowired
    FileItemRepository fileItemRepository;

    @Autowired
    FileSystemStorageService fileSystemStorageService;

    @Autowired
    StorageProperties storageProperties;


    public FileItem saveFileItem(FileItem fileItem){
        if(fileItem.getImage() == null && fileItem.getImageFilename() == null){
            fileItem.setImageFilename(null);
        }else{
            if(!fileItem.getImage().isEmpty()) {
                //Store Thumbnail
                    if(fileItem.getImage().getContentType().startsWith("image/")) {
                        fileSystemStorageService.store(fileItem.getImage(), 1);
                        //Set Image Filename
                        String imageFilename = fileItem.getImage().getOriginalFilename();
                        fileItem.setImageFilename(imageFilename);
                    }else{
                        throw new StorageException("Invalid File");
                    }


            }

        }
        //Store File
        if(fileItem.getFile() != null) {
            fileSystemStorageService.store(fileItem.getFile(), 0);
            //Set Filename
            fileItem.setFilename(fileItem.getFile().getOriginalFilename());
        }


        return fileItemRepository.save(fileItem);
    }

    public Optional<FileItem> getFileItem(Long id){
        return  fileItemRepository.findById(id);

    }



    public List<FileItem> getFiles(){
        return fileItemRepository.findAll();
    }


    public Optional<FileItem> getFileItem(String filename) {
        return fileItemRepository.getByFilename(filename);

    }
}
