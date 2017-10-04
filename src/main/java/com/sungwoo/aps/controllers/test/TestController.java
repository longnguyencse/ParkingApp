package com.sungwoo.aps.controllers.test;

import com.sungwoo.aps.domain.prime.Area;
import com.sungwoo.aps.services.AreaService;
import com.sungwoo.aps.services.GateService;
import com.sungwoo.aps.support.InsertDB;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "Monitor")
@Controller
@RequestMapping("test")
@Component
@EnableGlobalMethodSecurity(securedEnabled = true)
public class TestController {
    @Value("${name:World}")
    private String name;

    private final AreaService areaService;
    private final GateService gateService;

    private final InsertDB insertDB;

    @Autowired
    public TestController(AreaService areaService, GateService gateService, InsertDB insertDB) {
        this.areaService = areaService;
        this.gateService = gateService;
        this.insertDB = insertDB;
    }


    @Secured("IS_AUTHENTICATED_ANONYMOUSLY")
    @ResponseBody
    @GetMapping("greeting")
    public ResponseEntity getHelloMessage() {
        return new ResponseEntity<>(String.format("Hello %s", this.name), HttpStatus.OK);
    }

    @Secured("ADMIN")
    @ResponseBody
    @GetMapping("ad/greeting")
    public ResponseEntity getHelloMessageRoleAdmin() {
        return new ResponseEntity<>(String.format("Hello %s", this.name), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("aop/greeting")
    public ResponseEntity getHelloMessageOnSecure(/*@RequestHeader(value="User-Agent", defaultValue="foo") String userAgent,*/
                                                  @RequestParam boolean isAdmin) {
        return new ResponseEntity<>(String.format("Hello %s", this.name), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("batch")
    public ResponseEntity batchArea() {
        List<Area> areas = insertDB.initDefaultLocal();
        for (Area area : areas) {
            Area a = areaService.findFirstByName(area.getName());
            if (a != null) {
                area.setUid(a.getUid());
            }
        }
        areaService.saveAreas(areas);
        gateService.saveGate(insertDB.initDefaultGate());

        return new ResponseEntity<>(String.format("Hello %s", this.name), HttpStatus.OK);
    }

    @Secured("IS_AUTHENTICATED_ANONYMOUSLY")
    @ResponseBody
    @GetMapping("/admin/greeting")
    public ResponseEntity getHelloMessageHasSecure() {
        return new ResponseEntity<>(String.format("Hello %s", this.name), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/auth/greeting")
    public ResponseEntity getHelloMessageWithReqAuth() {
        return new ResponseEntity<>(String.format("Hello %s", this.name), HttpStatus.OK);
    }

    @GetMapping("")
    String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/403")
    String accessDenied() {
        return "403";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/logout")
    public String getLogin(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/test";
    }

}