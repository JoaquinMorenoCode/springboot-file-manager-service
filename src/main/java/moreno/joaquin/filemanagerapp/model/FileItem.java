package moreno.joaquin.filemanagerapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FileItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String filename;



    private Float version;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    @NotBlank(message = "Author must not be blank")
    private String author;
    private String imageFilename;

    @Transient
    private MultipartFile image;
    @Transient
    private MultipartFile file;



    @PrePersist
    void prePersist(){
        if(version == null){
            version = 1.00F;
        }

        creationDate = LocalDateTime.now();
        updateDate = creationDate;
    }

    @PreUpdate
    void preUpdate(){
        //version += 0.10F;
        updateDate = LocalDateTime.now();
    }


}
