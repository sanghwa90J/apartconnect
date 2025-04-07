package com.aptconnect.menu.complaint.controller;

import com.aptconnect.component.AuthenticationFacade;
import com.aptconnect.menu.complaint.entity.Complaint;
import com.aptconnect.menu.auth.entity.Role;
import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.complaint.entity.ComplaintDetailDto;
import com.aptconnect.menu.complaint.entity.ComplaintFile;
import com.aptconnect.menu.complaint.service.ComplaintFileService;
import com.aptconnect.menu.complaint.service.ComplaintService;
import com.aptconnect.menu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;
    private final ComplaintFileService complaintFileService;

    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade; // 🔥 사용자 정보 가져오는 컴포넌트

    @GetMapping
    public String viewComplaints(Model model, @ModelAttribute("currentUser") User currentUser) {
        String currentRole = authenticationFacade.getCurrentUserRole();
        List<Complaint> complaints;

        if ("ROLE_MASTER".equals(currentRole)) {
            complaints = complaintService.getAllComplaints(); // 모든 민원 조회
        } else if ("ROLE_ADMIN".equals(currentRole)) {
            complaints = complaintService.getComplaintsByApartment(currentUser.getApartmentName()); // 같은 아파트 민원 조회
        } else {
            complaints = complaintService.getComplaintsByUser(currentUser); // 개인 민원 조회
        }

        model.addAttribute("currentPage", "complaints"); // 🎯 현재 페이지 정보 추가
        model.addAttribute("currentRole", currentRole);
        model.addAttribute("complaints", complaints);
        return "/complaints/complaint-list";
    }

    @GetMapping("/{complaintId}")
    public String viewComplaintDetail(@PathVariable("complaintId") Long complaintId, Model model, @ModelAttribute("currentUser") User currentUser) {
        Complaint complaint = complaintService.getComplaintById(complaintId);

        // 본인이 작성했거나, ADMIN 또는 MASTER인 경우만 상세 페이지 접근 가능
        if (!currentUser.getRole().equals(Role.MASTER) &&
                !currentUser.getRole().equals(Role.ADMIN) &&
                !complaint.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("해당 민원을 조회할 권한이 없습니다.");
        }

        // ✅ 파일 목록 따로 조회해서 모델에 추가
        List<ComplaintFile> files = complaintFileService.getFilesByComplaint(complaintId);

        model.addAttribute("complaint", complaint);
        model.addAttribute("complaintDto", new ComplaintDetailDto(complaint, complaintFileService.getFilesByComplaint(complaintId)));

        model.addAttribute("files", files); // 👈 따로 넘김

        model.addAttribute("currentRole", "ROLE_" + currentUser.getRole());
        model.addAttribute("currentPage", "complaints"); // 🎯 현재 페이지 정보 추가
        model.addAttribute("currentUser", currentUser); // ✅ 기존 currentUser를 그대로 사용

        return "complaints/complaint-detail"; // 상세 페이지 템플릿
    }

    // 🔥 민원 상태 변경 API
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> updateComplaintStatus(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        String currentUser = authenticationFacade.getCurrentUserRole();
        String newStatus = requestBody.get("newStatus"); // ✅ JSON 데이터에서 newStatus 추출

        // ✅ 권한 체크: ADMIN 또는 MASTER만 가능
        if (!(currentUser.equals("ROLE_ADMIN") || currentUser.equals("ROLE_MASTER"))) {
            throw new SecurityException("상태 변경 권한이 없습니다.");
        }

        if (newStatus == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "newStatus 값이 필요합니다."));
        }

        Complaint updatedComplaint = complaintService.updateComplaintStatus(id, newStatus);
        Map<String, String> response = new HashMap<>();
        response.put("newStatus", updatedComplaint.getStatus().name()); // 변경된 상태 반환

        return ResponseEntity.ok(response);
    }

    @GetMapping("/create")
    public String showComplaintForm(Model model) {
        // 🔥 currentRole 추가
        String currentRole = authenticationFacade.getCurrentUserRole();
        model.addAttribute("currentRole", currentRole);
        model.addAttribute("currentPage", "complaints"); // 🎯 현재 페이지 정보 추가
        model.addAttribute("complaint", new Complaint());
        return "/complaints/complaint-form";
    }

    @PostMapping("/create")
    public String createComplaint(@ModelAttribute Complaint complaint
            , @RequestParam(value = "files", required = false) MultipartFile[] files // ✅ 배열로 변경
            , Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

        complaint.setCreatedBy(user);
        complaint.setApartmentName(user.getApartmentName());

        Complaint savedComplaint = complaintService.saveComplaint(complaint);

        // ✅ 파일이 존재할 경우에만 저장
        if (files != null) {
            complaintFileService.saveFiles(savedComplaint, files); // ✅ 파일 저장
        }

        return "redirect:/complaints";
    }

    @GetMapping("/edit/{id}")
    public String editComplaint(@PathVariable Long id, Model model, Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        Complaint complaint = complaintService.getComplaintById(id);

        // 작성자 본인만 수정 가능
        if (!complaint.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("해당 민원을 수정할 권한이 없습니다.");
        }

        model.addAttribute("currentRole", "ROLE_" + currentUser.getRole());
        model.addAttribute("currentPage", "complaints");
        model.addAttribute("complaint", complaint);
        model.addAttribute("createdById", complaint.getCreatedBy().getId()); // ✅ createdById 추가

        return "/complaints/complaint-edit";
    }

    @PostMapping("/update/{id}")
    public String updateComplaint(@PathVariable Long id
            , @RequestParam(value = "files", required = false) MultipartFile[] files // ✅ 배열로 변경
            , @ModelAttribute Complaint updatedComplaint) {
        System.out.println("🚀 [DEBUG] update 요청 받음 - id: " + id + ", createdBy: " + updatedComplaint.getCreatedBy().getId());

        // createdBy 정보가 null인지 확인
        if (updatedComplaint.getCreatedBy() == null || updatedComplaint.getCreatedBy().getId() == null) {
            throw new RuntimeException("createdBy 정보가 없습니다.");
        }

        User user = userRepository.findById(updatedComplaint.getCreatedBy().getId())
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));
        System.out.println("✅ [DEBUG] User 조회 완료 - userId: " + user.getId());

        complaintService.updateComplaint(id, updatedComplaint, user);

        // ✅ 파일이 존재할 경우에만 저장
        if (files != null) {
            complaintFileService.saveFiles(updatedComplaint, files); // ✅ 파일 저장
        }

        return "redirect:/complaints/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteComplaint(@PathVariable Long id, Authentication authentication) {
        Complaint complaint = complaintService.getComplaintById(id);

        // 🔥 현재 로그인한 사용자 정보를 DB에서 조회
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        // 작성자 본인만 삭제 가능
        if (!complaint.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("해당 민원을 삭제할 권한이 없습니다.");
        }
        complaintService.deleteComplaint(id);
        return "redirect:/complaints";
    }



}