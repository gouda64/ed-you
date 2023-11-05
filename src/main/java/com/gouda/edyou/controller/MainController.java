package com.gouda.edyou.controller;

import com.gouda.edyou.entity.Feedback;
import com.gouda.edyou.entity.School;
import com.gouda.edyou.entity.Staff;
import com.gouda.edyou.entity.User;
import com.gouda.edyou.service.FeedbackService;
import com.gouda.edyou.service.SchoolService;
import com.gouda.edyou.service.StaffService;
import com.gouda.edyou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

@Controller
public class MainController {
    private final UserService userService;
    private final StaffService staffService;
    private final SchoolService schoolService;
    private final FeedbackService feedbackService;

    @Autowired
    public MainController(UserService userService, StaffService staffService, SchoolService schoolService, FeedbackService feedbackService) {
        this.userService = userService;
        this.staffService = staffService;
        this.schoolService = schoolService;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/student-portal")
    public String studentPortal() {
        return "student-portal";
    }

    @GetMapping("/student-feedback")
    public String studentPortal(@RequestParam(name = "code", required = false) String code, Model model) {
        School school = schoolService.findByCode(code);
        if (school == null) return "redirect:/student-portal";
        List<Staff> staff = new ArrayList<>(school.getStaff().stream().toList());
        Collections.sort(staff, new Comparator<Staff>() {
            @Override
            public int compare(Staff o1, Staff o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        model.addAttribute("school", school);
        model.addAttribute("staffList", staff);
        return "feedback";
    }
    @PostMapping("/student-feedback")
    public String studentPortalPost(@RequestParam(name = "code") String code,
                                    @RequestParam(name = "staff") long staffId,
                                    @RequestParam(name = "comment") String comment,
                                    Model model) {
        School school = schoolService.findByCode(code);
        Staff staff = staffService.findById(staffId);
        if (school == null || staff == null) return "redirect:/student-portal";
        Feedback feedback = new Feedback();
        feedback.setStaff(staff);
        feedback.setComment(comment);
        feedbackService.save(feedback);
        return "redirect:/student-feedback?code=" + code;
    }

    @GetMapping("/{staffId}")
    public String staff(@PathVariable long staffId, Principal principal, Model model) {
        if (principal == null) return "redirect:/";
        User user = userService.findByEmail(principal.getName());
        if (!user.getSchool().getStaff().stream().anyMatch(staff -> staff.getId() == staffId)) {
            return "redirect:/";
        }
        Staff staff = staffService.findById(staffId);
        List<Feedback> feedback = new ArrayList<>(staff.getFeedback().stream().toList());
        Collections.sort(feedback, new Comparator<Feedback>() {
            @Override
            public int compare(Feedback o1, Feedback o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        model.addAttribute("staff", staff);
        model.addAttribute("feedbackList", feedback);
        return "staff";
    }

    @GetMapping(value = {"/signup"})
    public String redirect() {
        return "redirect:/";
    }

    @GetMapping(value = {"/", "/index", "/home"})
    public String home(Principal principal, Model model) {
        if (principal == null) return "home";

        User user = userService.findByEmail(principal.getName());
        School school = user.getSchool();
        List<Staff> staff = new ArrayList<>(school.getStaff().stream().toList());
        Collections.sort(staff, new Comparator<Staff>() {
            @Override
            public int compare(Staff o1, Staff o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        model.addAttribute("staffList", staff);
        model.addAttribute("admin", user);
        model.addAttribute("schoolCode", school.getCode().toString());
        return "admin";
    }

    @GetMapping("/login")
    public String login(Principal principal, Model model, @RequestParam(name = "logout", required = false) Optional<String> logout) {
        if (principal != null || logout.isPresent()) return "redirect:/";
        return "login";
    }
}
