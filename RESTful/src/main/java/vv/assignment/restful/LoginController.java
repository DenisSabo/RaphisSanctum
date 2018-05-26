package vv.assignment.restful;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(@RequestParam(name="logout", required=false) String logout,
                        @RequestParam(name="error", required=false) String error, Model model) {
        model.addAttribute("logout", logout);
        model.addAttribute("error", error);
        return "login";
    }

}

/**
 @GetMapping("/login")
 public String login(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
 // The value of the name parameter is added to a Model object, ultimately making it accessible to the view template.
 model.addAttribute("name", name);
 return "greeting";
 }
 */
