package me.soldesk.katte_project_client.controller;

import common.bean.user.UserBean;
import jakarta.servlet.http.HttpSession;
import lombok.ToString;
import me.soldesk.katte_project_client.service.LoginService;
import me.soldesk.katte_project_client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @GetMapping("/signUp")
    public String signUp(Model model) {
        model.addAttribute("apiBaseUrl", "https://api-katte.jp.ngrok.io");
        return "Signuppage/Signup";
    }

    @PostMapping("/signUp_pro")
    public String signUp_pro(
            @RequestParam("email_id") String email,
            @RequestParam("password") String password,
            @RequestParam("birthYear") int birthYear,
            @RequestParam("birthMonth") int birthMonth,
            @RequestParam("birthDay") int birthDay,
            @RequestParam("nickname") String nickname,
            @RequestParam(value = "terms", required = false) List<String> terms,
            Model model
    ) {
        System.out.println("signUp_pro In");
        // 🎯 생년월일 조합
        LocalDate birth = LocalDate.of(birthYear, birthMonth, birthDay);
        Date birthDate = java.sql.Date.valueOf(birth);

        // 🎯 UserBean 객체 생성
        UserBean user = new UserBean();
        user.setEmail_id(email);
        user.setPassword(password);
        user.setBirth_date(birthDate);
        user.setNickname(nickname);
        user.setIs_admin(false); // 기본값 설정
        user.setRegistration_date(new Date());

        System.out.println("✅ 사용자 이메일: " + email);
        System.out.println("✅ 사용자 pass: " + password);
        System.out.println("✅ 사용자 생년월일: " + birthDate);
        System.out.println("✅ 사용자 닉네임: " + nickname);
        System.out.println("✅ 약관 목록: " + terms);

        loginService.signUp(user);

        UserBean userBean = userService.getUserByEmail(email);
        if (userBean != null) {
            String user_id = Integer.toString(userBean.getUser_id());
            for (int i = 0; i < terms.size(); i++) {
                switch (i) {
                    case 0:
                        loginService.addUserTerm(user_id,101, terms.get(i).equals("on"));
                        break;
                    case 1:
                        loginService.addUserTerm(user_id,102, terms.get(i).equals("on"));
                        break;
                    case 2:
                        loginService.addUserTerm(user_id,103, terms.get(i).equals("on"));
                        break;
                    case 3:
                        loginService.addUserTerm(user_id,201, terms.get(i).equals("on"));
                        break;

                }
            }
        }

        return "Loginpage/Login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("userId") String userId,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        boolean loginSuccess = loginService.isLogin(userId, password);

        System.out.println("AAAAA" + loginSuccess);

        if (loginSuccess) {
            session.setAttribute("userId", userId);
            return "Mainpage/Mainpage";
        } else {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "Loginpage/Login";
        }
    }
}
