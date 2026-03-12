package backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class SupabaseStorageService implements StorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png");

    @Override
    public String upload(MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Formato file non supportato. Sono ammessi solo JPG e PNG.");
        }

        String originalFileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String sanitizedFileName = originalFileName.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9._-]", "");
        String fileName = UUID.randomUUID().toString() + "_" + sanitizedFileName;

        String urlString = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
        conn.setRequestProperty("Content-Type", contentType);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(file.getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
        } else {
            try (java.io.InputStream errorStream = conn.getErrorStream()) {
                String errorBody = new String(errorStream.readAllBytes());
                System.err.println("Errore Supabase: " + responseCode + ", Body: " + errorBody);
                throw new RuntimeException("Errore upload Supabase: " + responseCode + " - " + errorBody);
            }
        }
    }

    @Override
    public byte[] download(String url) throws Exception {
        if (!url.startsWith(supabaseUrl)) {
            throw new IllegalArgumentException("URL non valido o esterno non consentito");
        }

        URL downloadUrl = new URL(url);
        try (InputStream in = downloadUrl.openStream()) {
            return in.readAllBytes();
        }
    }

    @Override
    public void delete(String url) {
        try {
            if (url == null || url.isEmpty() || !url.startsWith(supabaseUrl)) {
                return;
            }
            
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            String deleteUrlString = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

            URL deleteUrl = new URL(deleteUrlString);
            HttpURLConnection conn = (HttpURLConnection) deleteUrl.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 204) {
                System.out.println("Immagine eliminata da Supabase: " + fileName);
            } else {
                System.err.println("Errore eliminazione immagine Supabase. Codice: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Eccezione durante eliminazione immagine: " + e.getMessage());
        }
    }
}
