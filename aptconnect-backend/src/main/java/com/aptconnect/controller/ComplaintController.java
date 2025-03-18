package com.aptconnect.controller;

import com.aptconnect.component.AuthenticationFacade;
import com.aptconnect.entity.announcement.Complaint;
import com.aptconnect.entity.Role;
import com.aptconnect.entity.User;
import com.aptconnect.service.ComplaintService;
import com.aptconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;
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

    @GetMapping("/{id}")
    public String viewComplaintDetail(@PathVariable Long id, Model model, @ModelAttribute("currentUser") User currentUser) {
        Complaint complaint = complaintService.getComplaintById(id);

        // 본인이 작성했거나, ADMIN 또는 MASTER인 경우만 상세 페이지 접근 가능
        if (!currentUser.getRole().equals(Role.MASTER) &&
                !currentUser.getRole().equals(Role.ADMIN) &&
                !complaint.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("해당 민원을 조회할 권한이 없습니다.");
        }

        model.addAttribute("complaint", complaint);
        model.addAttribute("currentRole", "ROLE_" + currentUser.getRole());
        model.addAttribute("currentPage", "complaints"); // 🎯 현재 페이지 정보 추가

        return "complaints/complaint-detail"; // 상세 페이지 템플릿
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
    public String createComplaint(@ModelAttribute Complaint complaint, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

        complaint.setCreatedBy(user);
        complaint.setApartmentName(user.getApartmentName());
        System.out.println("TEST-POINT : " + complaint.getIsAnonymous());
        complaintService.saveComplaint(complaint);
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
    public String updateComplaint(@PathVariable Long id,
                                  @ModelAttribute Complaint updatedComplaint) {
        System.out.println("🚀 [DEBUG] update 요청 받음 - id: " + id + ", createdBy: " + updatedComplaint.getCreatedBy().getId());

        // createdBy 정보가 null인지 확인
        if (updatedComplaint.getCreatedBy() == null || updatedComplaint.getCreatedBy().getId() == null) {
            throw new RuntimeException("createdBy 정보가 없습니다.");
        }

        User user = userRepository.findById(updatedComplaint.getCreatedBy().getId())
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));
        System.out.println("✅ [DEBUG] User 조회 완료 - userId: " + user.getId());

        complaintService.updateComplaint(id, updatedComplaint, user);
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