package moreno.joaquin.filemanagerapp.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file, int type);

    Stream<Path> loadAll();

    Path load(String filename, ResourceType resourceType);

    Resource loadAsResource(String filename, ResourceType resourceType);

    void deleteAll();
}
