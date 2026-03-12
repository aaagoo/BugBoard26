package backend.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaccia per un servizio di storage di file.
 * Definisce le operazioni di base per caricare, scaricare ed eliminare file,
 * astraendo dall'implementazione specifica (es. Supabase, AWS S3, File System locale).
 */
public interface StorageService {

    /**
     * Carica un file nello storage.
     *
     * @param file Il file da caricare.
     * @return L'URL pubblico per accedere al file.
     * @throws Exception Se l'upload fallisce.
     */
    String upload(MultipartFile file) throws Exception;

    /**
     * Scarica un file dallo storage.
     *
     * @param url L'URL del file da scaricare.
     * @return I byte del file.
     * @throws Exception Se il download fallisce.
     */
    byte[] download(String url) throws Exception;

    /**
     * Elimina un file dallo storage.
     *
     * @param url L'URL del file da eliminare.
     */
    void delete(String url);
}
