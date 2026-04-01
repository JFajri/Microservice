import com.produser.produser.controller;

import org.springframework.beans.factory.annotation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProduserController {
    @Autowired
    private ProdukService produserService;

    @GetMapping("/send")
    public String sendMessage(@RequestParam String messsage){
        produserService.sendMessage(messsage);
        return "Message sent. " + messsage;
    }
}
