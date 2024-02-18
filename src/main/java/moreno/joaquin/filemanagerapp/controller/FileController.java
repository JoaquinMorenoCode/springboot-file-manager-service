package moreno.joaquin.filemanagerapp.controller;


import moreno.joaquin.filemanagerapp.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.plaf.PanelUI;
import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class FileController {

    @Autowired
    StorageService storageService;


    //Generates URI for each path in the stream, then it parses it to the serveFile class inside the controller
    @GetMapping("/")
    public String listAllFiles(Model model) throws IOException{
        model.addAttribute("files",storageService.loadAll().map(
                path -> MvcUriComponentsBuilder
                        .fromMethodName(FileController.class,
                                "serveFile",
                                path.getFileName().toString()).build().toUri().toString()).collect(Collectors.toList()));
        return  "uploadForm";
    }


    @GetMapping("files/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename){

        Resource file = storageService.loadAsResource(filename);

        if(file==null){
            return ResponseEntity.notFound().build();
        }

        //the \ allow us to have a " in the string
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }

    @PostMapping
    public String handleFileUpload(@RequestParam("file")MultipartFile file, RedirectAttributes ra){

        storageService.store(file);
        ra.addFlashAttribute("message", "File " + file.getOriginalFilename() + " successfully uploaded");

        return "redirect:/";
    }






}
