package moreno.joaquin.filemanagerapp.repository;

import moreno.joaquin.filemanagerapp.model.FileItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileItemRepository extends JpaRepository<FileItem,Long> {

    Optional<FileItem> getByFilename(String filename);



}
