package moreno.joaquin.filemanagerapp.service;


import moreno.joaquin.filemanagerapp.config.StorageProperties;
import moreno.joaquin.filemanagerapp.model.FileItem;
import moreno.joaquin.filemanagerapp.repository.FileItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(fileItem.getImagePath().trim() ==null){
            fileItem.setImagePath("default-imagePath");
        }else{
            //Store Thumbnail
            fileSystemStorageService.store(fileItem.getImage());
            //Set Image Path (Do I need it?)
            fileItem.setImagePath(storageProperties.getLocation() + "\\" + fileItem.getFilename());

        }
        //Store File
        fileSystemStorageService.store(fileItem.getFile());




        return fileItemRepository.save(fileItem);
    }

    public FileItem getFileItem(Long id){
        return fileItemRepository.getById(id);
    }


}
