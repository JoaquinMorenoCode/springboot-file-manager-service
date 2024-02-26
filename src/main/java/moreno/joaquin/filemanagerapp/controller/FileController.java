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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.plaf.PanelUI;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping({"/api", "/",""  })
public class FileController {

    @Autowired
    StorageService storageService;

    @Autowired
    FileItemService fileItemService;


    //Generates URI for each path in the stream, then it parses it to the serveFile class inside the controller
    @GetMapping({"/", " "})
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


        return  "uploadFileForm";
    }

    @GetMapping("/update/{id}")
    public String handleUpdateForm(@PathVariable Long id, Model model, RedirectAttributes ra){

        Optional<FileItem> fileToUpdate = fileItemService.getFileItem(id);

        if(fileToUpdate.isEmpty()){

            ra.addFlashAttribute("message", "File with Id " + id + " does not exist");
            return "redirect:/api/";

        }

        model.addAttribute("fileItem", fileToUpdate.get());
        //model.addAttribute("message", fileToUpdate.get().getId());

        return "editFileForm";


    }


    @GetMapping("files/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile( @PathVariable String filename, @RequestParam ResourceType resourceType){

        Resource file = storageService.loadAsResource(filename, resourceType);

        if(file==null){
            return ResponseEntity.notFound().build();
        }

        //the \ allow us to have a " in the string
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }

    @PostMapping("/post")
    public String handleFileUpload(@Validated @ModelAttribute FileItem fileItem,BindingResult bindingResult , Model model, RedirectAttributes ra ){
        if(fileItem.getFile().isEmpty()){
            bindingResult.rejectValue("file","MultipartNotEmpty");
        }
        if(fileItemService.getFileItem(fileItem.getFile().getOriginalFilename()).isPresent() ){
            bindingResult.rejectValue("file","MultipartDuplicateFile");
            return "uploadFileForm";


        }

        if(!fileItem.getImage().isEmpty() && !fileItem.getImage().getContentType().startsWith("image/")){
            bindingResult.rejectValue("image","MultipartInvalidFile");
            return "uploadFileForm";

        }



        if(bindingResult.getFieldError("version")!=null) {

                bindingResult.rejectValue("version","TypeMissmatch");
            return "uploadFileForm";


        }


        if(bindingResult.hasErrors()){
            model.addAttribute("fileItem", fileItem);
            return "uploadFileForm";

        }

        fileItemService.saveFileItem(fileItem);
        ra.addFlashAttribute("message", "File " + fileItem.getFile().getOriginalFilename() + " successfully uploaded");

        return "redirect:/api/";
    }


    @PostMapping("/update/{id}")
    public String handleFileUpdate(@PathVariable Long id, @Validated @ModelAttribute FileItem fileItem,BindingResult bindingResult , Model model, RedirectAttributes ra ) throws IOException {

        Optional<FileItem> fileToUpdate = fileItemService.getFileItem(id);
        if(fileToUpdate.isEmpty()){

            ra.addFlashAttribute("message", "File with Id " + id + " does not exist");
            return "redirect:/api/";
        }
        if(!fileItem.getFile().isEmpty()){
            //Check Duplicate File Entry
            if (fileItemService.getFileItem(fileItem.getFile().getOriginalFilename()).isPresent() &&
                    !fileItemService.getFileItem(fileToUpdate.get().getId()).get().getFilename().equals(fileItem.getFile().getOriginalFilename())) {
                bindingResult.rejectValue("file", "MultipartDuplicateFile");
                return "editFileForm";
            }
            //need to use storage service
            fileToUpdate.get().setFile((fileItem.getFile()));

        }
        if(!fileItem.getImage().isEmpty()){
            //Check Image Type
            if(!fileItem.getImage().getContentType().startsWith("image/")){
                bindingResult.rejectValue("image","MultipartInvalidFile");
                return "editFileForm";

            }
            fileToUpdate.get().setImage((fileItem.getImage()));
        }


        if(bindingResult.hasErrors()){
            model.addAttribute("fileItem", fileItem);
            return "editFileForm";
        }

        if(fileItem.getVersion() != null){
            fileToUpdate.get().setVersion(fileItem.getVersion());

        }

        fileToUpdate.get().setAuthor(fileItem.getAuthor());

        System.out.println(fileToUpdate.get());
        fileItemService.saveFileItem(fileToUpdate.get());
        ra.addFlashAttribute("message", "File  successfully updated");

        return "redirect:/api/";
    }





}
