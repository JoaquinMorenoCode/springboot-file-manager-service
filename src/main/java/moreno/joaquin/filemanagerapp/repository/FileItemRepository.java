package moreno.joaquin.filemanagerapp.repository;

import moreno.joaquin.filemanagerapp.model.FileItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileItemRepository extends JpaRepository<FileItem,Long> {
}
