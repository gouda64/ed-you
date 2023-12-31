package com.gouda.edyou.controller;

import com.gouda.edyou.entity.Feedback;
import com.gouda.edyou.entity.School;
import com.gouda.edyou.entity.Staff;
import com.gouda.edyou.entity.User;
import com.gouda.edyou.service.FeedbackService;
import com.gouda.edyou.service.SchoolService;
import com.gouda.edyou.service.StaffService;
import com.gouda.edyou.service.UserService;
import com.gouda.edyou.validator.FeedbackValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.*;

@Controller
public class MainController {
//    private static final String COHERE_URL = "https://api.cohere.ai/v1/classify";
//
//    @Value("${cohere.apiKey}")
//    private String apiKey;
//    @Value("${cohere.rankingModel}")
//    private String rankingModelID;
//    private final RestTemplate restTemplate = new RestTemplate();

    private final UserService userService;
    private final StaffService staffService;
    private final SchoolService schoolService;
    private final FeedbackService feedbackService;
    private final FeedbackValidator feedbackValidator;


    @Autowired
    public MainController(UserService userService, StaffService staffService, SchoolService schoolService, FeedbackService feedbackService, FeedbackValidator feedbackValidator) {
        this.userService = userService;
        this.staffService = staffService;
        this.schoolService = schoolService;
        this.feedbackService = feedbackService;
        this.feedbackValidator = feedbackValidator;
    }

    @GetMapping("/student-portal")
    public String studentPortal(@RequestParam(name = "error") Optional<String> error, Model model) {
        if (error.isPresent()) {
            model.addAttribute("hasError", true);
        }
        return "student-portal";
    }

//    @GetMapping("/test")
//    public String test(@RequestParam(name = "comment") String comment, Model model) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + apiKey);
//
//        String requestJson = "{" +
//                "\"inputs\": [ \"" + comment + "\" ], " +
//                "\"model\": \"" + toxicityModelID + "\"" +
//                "}";
//        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
//        ResponseEntity<String> response = restTemplate.postForEntity(COHERE_URL, request, String.class);
//        model.addAttribute("test", response.getBody());
//        return "home";
//    }

    @GetMapping("/student-feedback")
    public String studentPortal(@RequestParam(name = "code", required = false) String code,
                                @RequestParam(name = "success", required = false) Optional<String> successful,
                                Model model) {
        School school = schoolService.findByCode(code);
        if (school == null) return "redirect:/student-portal?error";
        List<Staff> staff = new ArrayList<>(school.getStaff().stream().toList());
        Collections.sort(staff, new Comparator<Staff>() {
            @Override
            public int compare(Staff o1, Staff o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        model.addAttribute("school", school);
        model.addAttribute("staffList", staff);
        model.addAttribute("feedbackForm", new Feedback());
        model.addAttribute("successful", successful.isPresent());
        return "feedback";
    }
    @PostMapping("/student-feedback")
    public String studentPortalPost(@RequestParam(name = "code") String code,
                                    @RequestParam(name = "staff") long staffId,
                                    @ModelAttribute("feedbackForm") Feedback feedbackForm,
                                    BindingResult bindingResult, Model model) {
        School school = schoolService.findByCode(code);
        Staff staff = staffService.findById(staffId);
        if (school == null || staff == null) return "redirect:/student-portal";

        feedbackValidator.validate(feedbackForm, bindingResult);
        if (bindingResult.hasErrors()) {
            List<Staff> staffList = new ArrayList<>(school.getStaff().stream().toList());
            Collections.sort(staffList, new Comparator<Staff>() {
                @Override
                public int compare(Staff o1, Staff o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            model.addAttribute("staffList", staffList);
            model.addAttribute("school", school);
            return "/feedback";
        }
        feedbackForm.setStaff(staff);

        feedbackService.save(feedbackForm);
        return "redirect:/student-feedback?code=" + code + "&success";
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
                return -1*o1.getDate().compareTo(o2.getDate());
            }
        });
        model.addAttribute("staff", staff);
        model.addAttribute("feedbackList", feedback);

        double rating = 0;
        int numRatings = 0;
        for (Feedback fb : feedback) {
            if (fb.getRating() != -1) {
                rating += fb.getRating();
                numRatings++;
            }
        }
        rating /= numRatings;
        model.addAttribute("rating",  new Double(Math.floor(rating * 100) / 100));
        model.addAttribute("summary", staffService.getSummary(staff));

        return "staff";
    }

    @GetMapping(value = {"/signup", "/login"})
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

        double[] ratings = new double[staff.size()];
        for (int i = 0; i < staff.size(); i++) {
            Staff s = staff.get(i);
            double rating = 0;
            int numRatings = 0;
            for (Feedback fb : s.getFeedback()) {
                if (fb.getRating() != -1) {
                    rating += fb.getRating();
                    numRatings++;
                }
            }
            rating /= numRatings;
            ratings[i] = Math.floor(rating * 100) / 100;
        }
        model.addAttribute("ratings", ratings);

        return "admin";
    }
}
