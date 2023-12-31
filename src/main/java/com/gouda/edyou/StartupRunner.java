package com.gouda.edyou;

import com.gouda.edyou.entity.Feedback;
import com.gouda.edyou.entity.School;
import com.gouda.edyou.entity.Staff;
import com.gouda.edyou.entity.User;
import com.gouda.edyou.service.FeedbackService;
import com.gouda.edyou.service.SchoolService;
import com.gouda.edyou.service.StaffService;
import com.gouda.edyou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    @Value("${test.email1}")
    private String email1;
    @Value("${test.email2}")
    private String email2;
    @Value("${test.email3}")
    private String email3;

    private final UserService userService;
    private final SchoolService schoolService;
    private final FeedbackService feedbackService;
    private final StaffService staffService;

    @Autowired
    public StartupRunner(UserService userService, SchoolService schoolService, FeedbackService feedbackService, StaffService staffService) {
        this.userService = userService;
        this.schoolService = schoolService;
        this.feedbackService = feedbackService;
        this.staffService = staffService;
    }


    @Override
    public void run(String... args) throws Exception {
        String[] commentPool = new String[]{
          "He's great!",
          "His lectures are a bit boring.",
          "There's too much homework in his class.",
          "He is too harsh on students and affects mental health.",
          "He clearly cares about his students."
        };

        School school = new School();
        school.setName("Rensselaer Polytechnic Institute");
        schoolService.save(school);

        school = new School();
        school.setName("Boston University");
        schoolService.save(school);

        school = new School();
        school.setName("Quinsigamond Community College");
        schoolService.save(school);

        school = new School();
        school.setName("Worcester Polytechnic Institute");
        schoolService.save(school);

        User u1 = new User();
        u1.setName("John Doe");
        u1.setEmail(email1);
        u1.setSchool(school);
        userService.save(u1);

        User u2 = new User();
        u2.setName("Sara Williams");
        u2.setEmail(email2);
        u2.setSchool(school);
        userService.save(u2);

        User u3 = new User();
        u3.setName("Emma Smith");
        u3.setEmail(email3);
        u3.setSchool(school);
        userService.save(u3);

        for (int i = 0; i < 10; i++) {
            Staff staff = new Staff();
            staff.setName("Name " + i);
            staff.setSchool(school);
            staffService.save(staff);

            for (int j = 0; j < 10; j++) {
                Feedback feedback = new Feedback();
                feedback.setComment(commentPool[(int)(Math.random() * commentPool.length)]);
                feedback.setStaff(staff);
                feedbackService.save(feedback, (int)(Math.random()*5) + 1);
            }
        }

        Staff joe = new Staff();
        joe.setName("Demo Joe");
        joe.setSchool(school);
        staffService.save(joe);
    }
}
