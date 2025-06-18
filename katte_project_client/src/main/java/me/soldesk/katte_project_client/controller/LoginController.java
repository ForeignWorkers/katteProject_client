package me.soldesk.katte_project_client.controller;

import common.bean.user.UserBean;
import common.util.NicknameGenerator;
import jakarta.servlet.http.HttpSession;
import me.soldesk.katte_project_client.service.CouponService;
import me.soldesk.katte_project_client.service.LoginService;
import me.soldesk.katte_project_client.service.NaverUserDTO;
import me.soldesk.katte_project_client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //로그인 페이지로 이동
    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        return "Loginpage/Login";
    }

    @GetMapping("/signUp")
    public String signUp(Model model,HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }

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
            @RequestParam(value = "over14", required = false) String over14,
            @RequestParam(value = "terms1", required = false) String terms1,
            @RequestParam(value = "terms2", required = false) String terms2,
            @RequestParam(value = "terms3", required = false) String terms3,
            Model model
    ) {
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

        loginService.signUp(user);

        UserBean userBean = userService.getUserByEmail(email);
        if (userBean != null) {
            String user_id = Integer.toString(userBean.getUser_id());

            loginService.addUserTerm(user_id,101, over14.equals("on"));
            loginService.addUserTerm(user_id,102, terms1.equals("on"));
            loginService.addUserTerm(user_id,103, terms2.equals("on"));
            if(terms3 != null) loginService.addUserTerm(user_id,201, terms3.equals("on"));

            for (int i = 1; i < 6; i++) {
                couponService.addCoupon(i,userBean.getUser_id());
            }

            }

        return "redirect:/login?signup=success";
    }

    @PostMapping("/login")
    public String login(@RequestParam("userId") String userId,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        boolean loginSuccess = loginService.isLogin(userId, password);

        if (loginSuccess) {
            UserBean userBean = userService.getUserByEmail(userId);

            //정지 상태 확인
            if (loginService.isBanned(userBean.getUser_id())) {
                model.addAttribute("loginError", "해당 계정은 현재 정지 상태입니다.");
                return "Loginpage/Login";
            }

            session.setAttribute("currentUser", userBean);
            return "redirect:/main";
        } else {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "Loginpage/Login";
        }
    }

    @GetMapping("/login/oauth2/naver")
    public String naverCallback(@RequestParam String code,
                                @RequestParam String state,
                                HttpSession session,
                                Model model) {

        NaverUserDTO naverUser = loginService.getUserProfile(code, state);

        System.out.println("naverCallback In :" + naverUser);

        if (naverUser == null || naverUser.getEmail() == null) {
            model.addAttribute("loginError", "네이버 로그인 실패");
            return "Loginpage/Login";
        }

        // 1. 이미 가입된 유저인지 확인
        UserBean user = userService.getUserByEmail(naverUser.getEmail());

        if (user == null) {
            user = new UserBean();
            user.setEmail_id(naverUser.getEmail());
            user.setIs_admin(false);
            user.setPassword(naverUser.getId());

            try {
                String birthday = naverUser.getBirthday(); // ex) "06-30"
                if (birthday != null && naverUser.getBirthyear() != null) {
                    String formatted = birthday.replace("-", ""); // "0630"
                    String birthStr = naverUser.getBirthyear() + "-" + formatted.substring(0, 2) + "-" + formatted.substring(2); // "1994-06-30"
                    user.setBirth_date(java.sql.Date.valueOf(birthStr));
                }
            } catch (Exception e) {
                System.out.println("⚠️ 생년월일 파싱 실패: " + e.getMessage());
            }

            //닉네임 세팅
            user.setNickname(NicknameGenerator.generateRandomString(naverUser.getNickname()));

            user.setRegistration_date(new Date());

            UserBean savedUser = loginService.signUp(user); // ⚠️ 여기서 /user API로 POST

            if(savedUser != null)
                session.setAttribute("currentUser", savedUser);// 3. 로그인 처리
        }
        else {
                session.setAttribute("currentUser", user);// 3. 로그인 처리
        }
        return "redirect:/main";
    }

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.redirect.url}")
    private String redirectUri;

    @GetMapping("/login/naver")
    public String redirectToNaver() {
        String state = UUID.randomUUID().toString();  // CSRF 방지용 랜덤값
        String url = "https://nid.naver.com/oauth2.0/authorize" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&state=" + state;
        return "redirect:" + url;
    }
}
