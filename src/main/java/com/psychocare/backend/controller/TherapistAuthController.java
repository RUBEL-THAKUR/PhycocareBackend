package com.psychocare.backend.controller;

import com.psychocare.backend.dto.ApiResponse;
import com.psychocare.backend.dto.TherapistLoginRequest;
import com.psychocare.backend.dto.TherapistSignupRequest;
import com.psychocare.backend.model.Therapist;
import com.psychocare.backend.service.TherapistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/therapist")
@RequiredArgsConstructor
public class TherapistAuthController {

    private final TherapistService therapistService;

    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Therapist>> signup(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("emailId") String emailId,
            @RequestParam("password") String password,
            @RequestParam("mobileNumber") String mobileNumber,
            @RequestParam("isAbove18") boolean isAbove18,
            @RequestParam("acceptedTerms") boolean acceptedTerms,
            @RequestParam("cv") MultipartFile cvFile
    ) throws IOException {
        TherapistSignupRequest request = new TherapistSignupRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmailId(emailId);
        request.setPassword(password);
        request.setMobileNumber(mobileNumber);
        request.setAbove18(isAbove18);
        request.setAcceptedTerms(acceptedTerms);

        Therapist therapist = therapistService.signup(request, cvFile);
        return ResponseEntity.ok(ApiResponse.ok(
                "Registration submitted. Your application is pending admin approval.", therapist));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody TherapistLoginRequest request) {
        TherapistService.TherapistLoginResponseWrapper result = therapistService.login(request);
        Map<String, Object> data = new HashMap<>();
        data.put("token", result.token);
        data.put("therapist", result.therapist);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", data));
    }

    // ✅ CV ab Cloudinary pe hai — DB mein Cloudinary URL save hai
    // Frontend ko sirf cvFileName (jo ab full URL hai) redirect karo
    // Purana local file serve karna hata diya — ab kaam nahi karega production pe
    @GetMapping("/cv/{fileName}")
    public ResponseEntity<Map<String, String>> getCvUrl(@PathVariable String fileName) {
        // cvFileName ab Cloudinary secure_url hai jaise:
        // https://res.cloudinary.com/your-cloud/raw/upload/psychocare/cv/uuid.pdf
        // Frontend directly is URL pe redirect kar sakta hai
        Map<String, String> response = new HashMap<>();
        response.put("url", fileName); // fileName field mein ab full URL stored hai
        return ResponseEntity.ok(response);
    }
}


//package com.psychocare.backend.controller;
//
//import com.psychocare.backend.dto.ApiResponse;
//import com.psychocare.backend.dto.TherapistLoginRequest;
//import com.psychocare.backend.dto.TherapistSignupRequest;
//import com.psychocare.backend.model.Therapist;
//import com.psychocare.backend.service.TherapistService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@RestController
//@RequestMapping("/api/therapist")
//@RequiredArgsConstructor
//public class TherapistAuthController {
//
//    private final TherapistService therapistService;
//
//    @PostMapping(value = "/signup", consumes = "multipart/form-data")
//    public ResponseEntity<ApiResponse<Therapist>> signup(
//            @RequestParam("firstName") String firstName,
//            @RequestParam("lastName") String lastName,
//            @RequestParam("emailId") String emailId,
//            @RequestParam("password") String password,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("isAbove18") boolean isAbove18,
//            @RequestParam("acceptedTerms") boolean acceptedTerms,
//            @RequestParam("cv") MultipartFile cvFile
//    ) throws IOException {
//        TherapistSignupRequest request = new TherapistSignupRequest();
//        request.setFirstName(firstName);
//        request.setLastName(lastName);
//        request.setEmailId(emailId);
//        request.setPassword(password);
//        request.setMobileNumber(mobileNumber);
//        request.setAbove18(isAbove18);
//        request.setAcceptedTerms(acceptedTerms);
//
//        Therapist therapist = therapistService.signup(request, cvFile);
//        return ResponseEntity.ok(ApiResponse.ok(
//                "Registration submitted. Your application is pending admin approval.", therapist));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody TherapistLoginRequest request) {
//        TherapistService.TherapistLoginResponseWrapper result = therapistService.login(request);
//        Map<String, Object> data = new HashMap<>();
//        data.put("token", result.token);
//        data.put("therapist", result.therapist);
//        return ResponseEntity.ok(ApiResponse.ok("Login successful", data));
//    }
//
//    @GetMapping("/cv/{fileName}")
//    public ResponseEntity<Resource> downloadCv(
//            @PathVariable String fileName,
//            HttpServletRequest request
//    ) throws IOException {
//        Path filePath = Paths.get("uploads/cv/").resolve(fileName).normalize();
//        Resource resource = new UrlResource(filePath.toUri());
//
//        if (!resource.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        String contentType = "application/pdf";
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "inline; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//    }
//}
