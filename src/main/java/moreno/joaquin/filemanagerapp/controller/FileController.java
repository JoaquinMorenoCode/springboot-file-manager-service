package moreno.joaquin.filemanagerapp.controller;


import moreno.joaquin.filemanagerapp.model.FileItem;
import moreno.joaquin.filemanagerapp.service.FileItemService;
import moreno.joaquin.filemanagerapp.service.ResourceType;
import moreno.joaquin.filemanagerapp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.plaf.PanelUI;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class FileController {

    @Autowired
    StorageService storageService;

    @Autowired
    FileItemService fileItemService;


    //Generates URI for each path in the stream, then it parses it to the serveFile class inside the controller
    @GetMapping("/")
    public String listAllFiles(Model model) throws IOException{
        //Empty Model for Form
        model.addAttribute("fileItem", FileItem.builder().build());

        List<FileItem> fileItems = fileItemService.getFiles();

        //Get List of Thumbnails
        List<String> imageUrls = fileItems.stream().map(fileItem -> {
            String filename = fileItem.getImageFilename();
            return MvcUriComponentsBuilder.fromMethodName(
                    FileController.class, "serveFile", filename, ResourceType.IMAGE)
                    .build()
                    .toUri()
                    .toString();
        }).collect(Collectors.toList());

        //Get List of Files
        List<String> fileUrls = fileItems.stream().map(fileItem -> {
            String filename = fileItem.getFilename();
            return MvcUriComponentsBuilder.fromMethodName(
                            FileController.class, "serveFile", filename, ResourceType.FILE)
                    .build()
                    .toUri()
                    .toString();
        }).collect(Collectors.toList());



        model.addAttribute("files", fileItems);
        model.addAttribute("imageResource", imageUrls);
        model.addAttribute("fileResource", fileUrls);



//        //List all files
//        model.addAttribute("files",storageService.loadAll().map(
//                path -> MvcUriComponentsBuilder
//                        .fromMethodName(FileController.class,
//                                "serveFile",
//                                path.getFileName().toString()).build().toUri().toString()).collect(Collectors.toList()));






        return  "uploadFileForm";
    }


    @GetMapping("files/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, @RequestParam ResourceType resourceType){

        Resource file = storageService.loadAsResource(filename, resourceType);

        if(file==null){
            return ResponseEntity.notFound().build();
        }

        //the \ allow us to have a " in the string
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }





    @PostMapping("/post")
    public String handleFileUpload(@ModelAttribute FileItem fileItem,Model model, RedirectAttributes ra ){



        fileItemService.saveFileItem(fileItem);
        ra.addFlashAttribute("message", "File " + fileItem.getFile().getOriginalFilename() + " successfully uploaded");

        return "redirect:/api/";
    }






}
