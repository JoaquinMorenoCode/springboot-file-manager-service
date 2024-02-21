package moreno.joaquin.filemanagerapp.service;


import moreno.joaquin.filemanagerapp.config.StorageProperties;
import moreno.joaquin.filemanagerapp.model.FileItem;
import moreno.joaquin.filemanagerapp.repository.FileItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
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
        if(fileItem.getImage().isEmpty()){
           //ileItem.setImageFilename("default-thumbnail.jpg");
            fileItem.setImageFilename(null);
        }else{
            //Store Thumbnail
            fileSystemStorageService.store(fileItem.getImage(),1);
            //Set Image Filename
            String imageFilename = fileItem.getImage().getOriginalFilename();//+ fileItem.getImage().getContentType();
            fileItem.setImageFilename(imageFilename);

        }
        //Store File
        fileSystemStorageService.store(fileItem.getFile(),0);
        //Set Filename
        fileItem.setFilename(fileItem.getFile().getOriginalFilename());




        return fileItemRepository.save(fileItem);
    }

    public FileItem getFileItem(Long id){
        return fileItemRepository.getById(id);

    }

    public List<FileItem> getFiles(){
        return fileItemRepository.findAll();
    }


}
