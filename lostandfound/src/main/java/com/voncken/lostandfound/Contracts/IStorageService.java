package com.voncken.lostandfound.Contracts;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

// based on the uploading-files guide from spring.io
public interface IStorageService {
    
    void init();

    void store(MultipartFile file);
    
    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
}
