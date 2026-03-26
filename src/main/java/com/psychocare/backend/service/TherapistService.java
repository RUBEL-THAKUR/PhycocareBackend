package com.psychocare.backend.service;

import com.psychocare.backend.dto.*;
import com.psychocare.backend.exception.BadRequestException;
import com.psychocare.backend.exception.ResourceNotFoundException;
import com.psychocare.backend.exception.UnauthorizedException;
import com.psychocare.backend.model.*;
import com.psychocare.backend.repository.*;
import com.psychocare.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TherapistService {

    private final TherapistRepository therapistRepository;
    private final TherapistProfileRepository profileRepository;
    private final TherapistQualificationRepository qualificationRepository;
    private final TherapistExperienceRepository experienceRepository;
    private final TherapistSpecializationRepository specializationRepository;
    private final TherapistAreaOfExpertiseRepository areaOfExpertiseRepository;
    private final TherapistAwardRepository awardRepository;
    private final TherapistProfessionalMembershipRepository membershipRepository;
    private final TherapistBankDetailsRepository bankDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.upload.cv.dir:uploads/cv/}")
    private String cvUploadDir;

    @Value("${app.upload.profile.dir:uploads/profiles/}")
    private String profileUploadDir;

    @Transactional
    public Therapist signup(TherapistSignupRequest request, MultipartFile cvFile) throws IOException {
        if (therapistRepository.existsByEmailId(request.getEmailId())) {
            throw new BadRequestException("Email is already registered");
        }
        if (therapistRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new BadRequestException("Mobile number is already registered");
        }
        if (!request.isAbove18()) {
            throw new BadRequestException("You must be 18 years or older to register as a therapist");
        }
        if (!request.isAcceptedTerms()) {
            throw new BadRequestException("You must accept the terms and conditions");
        }
        if (cvFile == null || cvFile.isEmpty()) {
            throw new BadRequestException("CV document is required");
        }

        String cvFileName = saveFile(cvFile, cvUploadDir);

        Therapist therapist = Therapist.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .emailId(request.getEmailId())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobileNumber(request.getMobileNumber())
                .cvFileName(cvFileName)
                .isAbove18(request.isAbove18())
                .acceptedTerms(request.isAcceptedTerms())
                .status(TherapistStatus.PENDING)
                .build();

        return therapistRepository.save(therapist);
    }

    public TherapistLoginResponseWrapper login(TherapistLoginRequest request) {
        Therapist therapist = therapistRepository.findByEmailId(request.getEmailId())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), therapist.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        if (therapist.getStatus() == TherapistStatus.PENDING) {
            throw new UnauthorizedException("Your account is pending admin approval");
        }
        if (therapist.getStatus() == TherapistStatus.REJECTED) {
            throw new UnauthorizedException("Your account has been rejected. Reason: " + therapist.getRejectionReason());
        }
        if (therapist.getStatus() == TherapistStatus.SUSPENDED) {
            throw new UnauthorizedException("Your account has been suspended. Contact support.");
        }

        String token = jwtUtil.generateTherapistToken(therapist.getId(), therapist.getEmailId());
        return new TherapistLoginResponseWrapper(token, therapist);
    }

    public Therapist getById(UUID id) {
        return therapistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Therapist not found with id: " + id));
    }

    public Therapist getByEmail(String email) {
        return therapistRepository.findByEmailId(email)
                .orElseThrow(() -> new ResourceNotFoundException("Therapist not found"));
    }

    public List<Therapist> getByStatus(TherapistStatus status) {
        return therapistRepository.findByStatus(status);
    }

    public List<Therapist> getAll() {
        return therapistRepository.findAll();
    }

    @Transactional
    public Therapist approve(UUID id, String approvedBy) {
        Therapist therapist = getById(id);
        therapist.setStatus(TherapistStatus.APPROVED);
        therapist.setApprovedAt(LocalDateTime.now());
        therapist.setApprovedBy(approvedBy);
        therapist.setRejectionReason(null);
        return therapistRepository.save(therapist);
    }

    @Transactional
    public Therapist reject(UUID id, String reason) {
        Therapist therapist = getById(id);
        therapist.setStatus(TherapistStatus.REJECTED);
        therapist.setRejectionReason(reason);
        therapist.setApprovedAt(null);
        return therapistRepository.save(therapist);
    }

    // Profile CRUD
    @Transactional
    public TherapistProfile saveProfile(UUID therapistId, TherapistProfileRequest req) {
        Therapist therapist = getById(therapistId);

        TherapistProfile profile = profileRepository.findByTherapistId(therapistId)
                .orElse(new TherapistProfile());
        profile.setTherapist(therapist);
        profile.setPrefix(req.getPrefix());
        profile.setFirstName(req.getFirstName());
        profile.setMiddleName(req.getMiddleName());
        profile.setLastName(req.getLastName());
        profile.setCategory(req.getCategory());
        profile.setEmailId(req.getEmailId());
        profile.setUserName(req.getUserName());
        profile.setMobile(req.getMobile());
        profile.setDateOfBirth(req.getDateOfBirth());
        profile.setGender(req.getGender());
        profile.setLanguage(req.getLanguage());
        profile.setBriefDescription(req.getBriefDescription());
        profile.setPresentAddress(req.getPresentAddress());
        profile.setPresentCountry(req.getPresentCountry());
        profile.setPresentState(req.getPresentState());
        profile.setPresentCity(req.getPresentCity());
        profile.setPresentDistrict(req.getPresentDistrict());
        profile.setPresentPinCode(req.getPresentPinCode());
        profile.setTimeZone(req.getTimeZone());
        profile.setExperience(req.getExperience());
        profile.setConsultationFeeChat(req.getConsultationFeeChat());
        profile.setConsultationFeeAudio(req.getConsultationFeeAudio());
        profile.setConsultationFeeVideo(req.getConsultationFeeVideo());

        if (req.isSameAsPresentAddress()) {
            profile.setClinicAddress(req.getPresentAddress());
            profile.setClinicCountry(req.getPresentCountry());
            profile.setClinicState(req.getPresentState());
            profile.setClinicCity(req.getPresentCity());
            profile.setClinicDistrict(req.getPresentDistrict());
            profile.setClinicPinCode(req.getPresentPinCode());
        } else {
            profile.setClinicAddress(req.getClinicAddress());
            profile.setClinicCountry(req.getClinicCountry());
            profile.setClinicState(req.getClinicState());
            profile.setClinicCity(req.getClinicCity());
            profile.setClinicDistrict(req.getClinicDistrict());
            profile.setClinicPinCode(req.getClinicPinCode());
        }

        TherapistProfile saved = profileRepository.save(profile);

        // Sync consultation fee to the therapist entity for public listing
        if (req.getConsultationFeeChat() != null) {
            therapist.setConsultationFee(req.getConsultationFeeChat());
            therapistRepository.save(therapist);
        }

        return saved;
    }

    public TherapistProfile getProfile(UUID therapistId) {
        return profileRepository.findByTherapistId(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found. Please complete your profile."));
    }

    // Qualifications
    @Transactional
    public TherapistQualification addQualification(UUID therapistId, QualificationRequest req) {
        Therapist therapist = getById(therapistId);
        TherapistQualification q = new TherapistQualification();
        q.setTherapist(therapist);
        q.setDegree(req.getDegree());
        q.setInstitution(req.getInstitution());
        q.setPassingYear(req.getPassingYear());
        q.setSpecialization(req.getSpecialization());
        return qualificationRepository.save(q);
    }

    public List<TherapistQualification> getQualifications(UUID therapistId) {
        return qualificationRepository.findByTherapistId(therapistId);
    }

    @Transactional
    public void deleteQualification(Long id) {
        qualificationRepository.deleteById(id);
    }

    // Experience
    @Transactional
    public TherapistExperience addExperience(UUID therapistId, ExperienceRequest req) {
        Therapist therapist = getById(therapistId);
        TherapistExperience e = new TherapistExperience();
        e.setTherapist(therapist);
        e.setOrganization(req.getOrganization());
        e.setDesignation(req.getDesignation());
        e.setFromYear(req.getFromYear());
        e.setToYear(req.getToYear());
        e.setCurrent(req.isCurrent());
        return experienceRepository.save(e);
    }

    public List<TherapistExperience> getExperiences(UUID therapistId) {
        return experienceRepository.findByTherapistId(therapistId);
    }

    @Transactional
    public void deleteExperience(Long id) {
        experienceRepository.deleteById(id);
    }

    // Specializations
    @Transactional
    public TherapistSpecialization addSpecialization(UUID therapistId, SpecializationRequest req) {
        Therapist therapist = getById(therapistId);
        TherapistSpecialization s = new TherapistSpecialization();
        s.setTherapist(therapist);
        s.setSpecializationName(req.getSpecializationName());
        return specializationRepository.save(s);
    }

    public List<TherapistSpecialization> getSpecializations(UUID therapistId) {
        return specializationRepository.findByTherapistId(therapistId);
    }

    @Transactional
    public void deleteSpecialization(Long id) {
        specializationRepository.deleteById(id);
    }

    // Areas of Expertise
    @Transactional
    public TherapistAreaOfExpertise addAreaOfExpertise(UUID therapistId, AreaOfExpertiseRequest req) {
        Therapist therapist = getById(therapistId);
        TherapistAreaOfExpertise a = new TherapistAreaOfExpertise();
        a.setTherapist(therapist);
        a.setAreaName(req.getAreaName());
        return areaOfExpertiseRepository.save(a);
    }

    public List<TherapistAreaOfExpertise> getAreasOfExpertise(UUID therapistId) {
        return areaOfExpertiseRepository.findByTherapistId(therapistId);
    }

    @Transactional
    public void deleteAreaOfExpertise(Long id) {
        areaOfExpertiseRepository.deleteById(id);
    }

    // Awards
    @Transactional
    public TherapistAward addAward(UUID therapistId, AwardRequest req) {
        Therapist therapist = getById(therapistId);
        TherapistAward a = new TherapistAward();
        a.setTherapist(therapist);
        a.setAwardName(req.getAwardName());
        a.setAwardingBody(req.getAwardingBody());
        a.setYear(req.getYear());
        return awardRepository.save(a);
    }

    public List<TherapistAward> getAwards(UUID therapistId) {
        return awardRepository.findByTherapistId(therapistId);
    }

    @Transactional
    public void deleteAward(Long id) {
        awardRepository.deleteById(id);
    }

    // Professional Memberships
    @Transactional
    public TherapistProfessionalMembership addMembership(UUID therapistId, MembershipRequest req) {
        Therapist therapist = getById(therapistId);
        TherapistProfessionalMembership m = new TherapistProfessionalMembership();
        m.setTherapist(therapist);
        m.setOrganizationName(req.getOrganizationName());
        m.setMembershipId(req.getMembershipId());
        m.setValidTill(req.getValidTill());
        return membershipRepository.save(m);
    }

    public List<TherapistProfessionalMembership> getMemberships(UUID therapistId) {
        return membershipRepository.findByTherapistId(therapistId);
    }

    @Transactional
    public void deleteMembership(Long id) {
        membershipRepository.deleteById(id);
    }

    // Bank Details
    @Transactional
    public TherapistBankDetails saveBankDetails(UUID therapistId, BankDetailsRequest req) {
        Therapist therapist = getById(therapistId);
        TherapistBankDetails bd = bankDetailsRepository.findByTherapistId(therapistId)
                .orElse(new TherapistBankDetails());
        bd.setTherapist(therapist);
        bd.setAccountHolderName(req.getAccountHolderName());
        bd.setAccountNumber(req.getAccountNumber());
        bd.setIfscCode(req.getIfscCode());
        bd.setBankName(req.getBankName());
        bd.setBranchName(req.getBranchName());
        bd.setUpiId(req.getUpiId());
        return bankDetailsRepository.save(bd);
    }

    public TherapistBankDetails getBankDetails(UUID therapistId) {
        return bankDetailsRepository.findByTherapistId(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank details not found"));
    }

    // Build public listing DTO
    public TherapistPublicResponse toPublicResponse(Therapist t) {
        TherapistProfile profile = profileRepository.findByTherapistId(t.getId()).orElse(null);
        List<String> specializations = specializationRepository.findByTherapistId(t.getId())
                .stream().map(TherapistSpecialization::getSpecializationName).collect(Collectors.toList());
        List<String> expertise = areaOfExpertiseRepository.findByTherapistId(t.getId())
                .stream().map(TherapistAreaOfExpertise::getAreaName).collect(Collectors.toList());

        return TherapistPublicResponse.builder()
                .id(t.getId())
                .firstName(t.getFirstName())
                .lastName(t.getLastName())
                .category(profile != null ? profile.getCategory() : null)
                .briefDescription(profile != null ? profile.getBriefDescription() : null)
                .experience(profile != null ? profile.getExperience() : null)
                .rating(t.getRating())
                .totalSessions(t.getTotalSessions())
                .profileImageUrl(t.getProfileImage())
                .consultationFeeChat(profile != null ? profile.getConsultationFeeChat() : t.getConsultationFee())
                .consultationFeeAudio(profile != null ? profile.getConsultationFeeAudio() : null)
                .consultationFeeVideo(profile != null ? profile.getConsultationFeeVideo() : null)
                .specializations(specializations)
                .areasOfExpertise(expertise)
                .status(t.getStatus())
                .build();
    }

    private String saveFile(MultipartFile file, String dir) throws IOException {
        Path dirPath = Paths.get(dir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".") ? original.substring(original.lastIndexOf(".")) : ".pdf";
        String fileName = UUID.randomUUID().toString() + ext;
        Files.write(dirPath.resolve(fileName), file.getBytes());
        return fileName;
    }

    // Inner wrapper class used by login
    public static class TherapistLoginResponseWrapper {
        public final String token;
        public final Therapist therapist;
        public TherapistLoginResponseWrapper(String token, Therapist therapist) {
            this.token = token;
            this.therapist = therapist;
        }
    }
}
