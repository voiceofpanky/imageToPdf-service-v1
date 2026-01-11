package org.example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for converting images to PDF.
 */
@RestController
@RequestMapping("/api/pdf")
public class ImageToPdfController {

    private final ImageToPdfService service;

    public ImageToPdfController(ImageToPdfService service) {
        this.service = service;
    }

    /**
     * Endpoint to convert an image file to PDF.
     *
     * @param file the image file to convert
     * @return the converted PDF file as a byte array
     * @throws Exception if an error occurs during conversion
     */
    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> convertImageToPdf(@RequestParam("file") MultipartFile file) throws Exception {

        byte[] pdfBytes = service.convert(file.getBytes());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}