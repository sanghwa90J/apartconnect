package com.aptconnect.controller;

import com.aptconnect.entity.*;
import com.aptconnect.repository.ApartmentRepository;
import com.aptconnect.repository.ErrorLogRepository;
import com.aptconnect.repository.UserActivityRepository;
import com.aptconnect.repository.UserRepository;
import com.aptconnect.service.ApartmentService;
import com.aptconnect.service.MasterService;
import com.aptconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/master")
@PreAuthorize("hasRole('ROLE_MASTER')") // 🔥 MASTER만 접근 가능
public class MasterController {
    private final UserRepository userRepository;
    private final UserService userService;

    private final ApartmentRepository apartmentRepository;
    private final ApartmentService apartmentService;

    private final ErrorLogRepository errorLogRepository;
    private final MasterService masterService;

    private final UserActivityRepository userActivityRepository;

    @GetMapping("/dashboard")
    public String masterDashboard(Model model, Authentication authentication) {
        model.addAttribute("monthlyApartmentRegistration", apartmentService.getMonthlyApartmentRegistration()); // 월별 아파트 등록 데이터
        model.addAttribute("apartmentStatus", apartmentService.getApartmentStatus()); // 아파트 등록 현황 데이터
        model.addAttribute("userStatus", userService.getUserStatus()); // 사용자 역할별 현황 데이터
        model.addAttribute("monthlyUserRegistration", userService.getMonthlyUserRegistration()); // 월별 사용자 등록 현황
        model.addAttribute("dailyLogCounts", masterService.getDailyErrorLogCounts());
        model.addAttribute("currentPage", "dashboard"); // 🎯 현재 페이지 정보 추가

        return "/master/dashboard";
    }

    /// 아파트 기준정보 관리

    // 1️⃣ 아파트 등록 및 관리 페이지
    @GetMapping("/apartments")
    public String viewApartments(@RequestParam(required = false) String search, Model model) {
        List<Apartment> apartments = (search != null && !search.isEmpty())? apartmentRepository.findByNameContaining(search): apartmentRepository.findAll();

        model.addAttribute("apartments", apartments != null ? apartments : new ArrayList<>()); // 🔥 Null 방지
        model.addAttribute("searchKeyword", search);
        model.addAttribute("currentPage", "apartments"); // 🎯 현재 페이지 정보 추가

        return "/master/apartments"; // 📌 경로 정리
    }

    @PostMapping("/apartments/add")
    public String addApartment(@RequestParam String name, @RequestParam String address) {
        Apartment apartment = new Apartment(name, address);
        apartmentRepository.save(apartment);
        return "redirect:/master/apartments";
    }

    @PostMapping("/apartments/edit")
    public String editApartment(@RequestParam Long id, @RequestParam String name, @RequestParam String address) {
        Apartment apartment = apartmentRepository.findById(id).orElseThrow(() -> new RuntimeException("아파트를 찾을 수 없습니다."));
        apartment.setName(name);
        apartment.setAddress(address);
        apartmentRepository.save(apartment);
        return "redirect:/master/apartments";
    }

    @PostMapping("/apartments/delete")
    public String deleteApartment(@RequestParam Long id) {
        apartmentRepository.deleteById(id);
        return "redirect:/master/apartments";
    }
    /// 아파트 기준정보 관리


    /// 회원정보 관리

    // 2️⃣ 회원 관리 페이지
    @GetMapping("/users")
    public String viewUsers(@RequestParam(required = false) String search, @RequestParam(required = false) String roleFilter, Model model) {
        List<User> users = userRepository.findAll();
        List<User> usersList = new ArrayList<>();

        for(User user : users) {
            if(user.getRole() != Role.MASTER){
                if ((search == null || search.isEmpty()) || user.getName().contains(search) || user.getEmail().contains(search)
                        && (roleFilter == null || roleFilter.isEmpty()) || user.getRole().toString().equals(roleFilter)) {

                    usersList.add(user);
                }
            }
        }

        model.addAttribute("users", usersList);
        model.addAttribute("searchKeyword", search);
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("currentPage", "users"); // 🎯 현재 페이지 정보 추가

        return "/master/users";
    }

    // 권한 변경
    @PostMapping("/update-role/{userId}")
    public String updateUserRole(@PathVariable Long userId, @RequestParam String newRole) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (user.getRole() == Role.MASTER) {
            throw new RuntimeException("MASTER 권한은 변경할 수 없습니다.");
        }

        user.setRole(Role.valueOf(newRole)); // 역할 변경
        userRepository.save(user);
        return "redirect:/master/users";
    }

    @PostMapping("/users/update-access")
    public String updateApartmentAccess(@RequestParam Long userId, @RequestParam String newAccess) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (!newAccess.equals("PENDING") && !newAccess.equals("APPROVED")) {
            throw new IllegalArgumentException("잘못된 접근 상태 값");
        }

        user.setApartmentAccess(newAccess); // ✅ 상태 변경
        userRepository.save(user);
        return "redirect:/master/users";
    }

    /// 회원정보 관리

    // log

    // 전체 로그를 화면에 출력하는 페이지
    @GetMapping("/logs")
    public String viewLogs(
            @RequestParam(required = false) String search
            , @RequestParam(required = false) String logFilter
            ,@RequestParam(defaultValue = "0") int page // 기본 페이지 0
            ,@RequestParam(defaultValue = "10") int size // 한 페이지에 10개씩 표시
            , Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<ErrorLog> logPage;

        // 🔹 검색어가 있을 경우
        if (search != null && !search.isEmpty()) {
            logPage = errorLogRepository.searchLogs(search, pageable);

        } else if (logFilter != null && !logFilter.isEmpty()) { // 🔹 특정 로그 레벨만 필터링
            logPage = errorLogRepository.findByLevelIgnoreCase(logFilter, pageable);

        } else { // 🔹 기본적으로 모든 로그 조회
            logPage = errorLogRepository.findAll(pageable);
        }

        model.addAttribute("logPage", logPage);
        model.addAttribute("errorLogs", logPage);
        model.addAttribute("searchKeyword", search);
        model.addAttribute("logsFilter", logFilter);
        model.addAttribute("currentPage", "logs"); // 🎯 현재 페이지 정보 추가

        return "/master/logs";
    }

    @PostMapping("/logs/delete")
    public String deleteLog(@RequestParam Long logId, RedirectAttributes redirectAttributes) {
        errorLogRepository.deleteById(logId);
        redirectAttributes.addFlashAttribute("successMessage", "로그가 삭제되었습니다.");
        return "redirect:/master/logs";
    }

    @GetMapping("/test-error")
    public void testError() {
        throw new RuntimeException("테스트용 에러 발생!");
    }
    // log

    // 활동 추적
    @GetMapping("/user-activity")
    public String viewUserActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "loginTime"));
        Page<UserActivity> activityPage = userActivityRepository.findAll(pageable);

        model.addAttribute("userActivities", activityPage);
        model.addAttribute("currentPage", "user-Activity"); // 🎯 현재 페이지 정보 추가

        return "/master/user-activity";
    }
    // 활동 추적
}