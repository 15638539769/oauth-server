package cn.sunyu.manage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author ArnBunChen
 */
@Controller
@SessionAttributes("authorizationRequest")
public class SecurityController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String needLogin(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Object errorMessage = session.getAttribute("errorMessage");
        if (null != errorMessage) {
            session.removeAttribute("errorMessage");
        }

        String referer = request.getHeader("referer");

        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("loginProcessUrl", "/login/post");
        return "login";
    }


    @RequestMapping("/oauth/confirm_access")
    public String getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) {
        return "oauth_approval";
    }
}
