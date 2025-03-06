

import com.example.service.AIChatService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AIChatController {

    @Resource
    private AIChatService aiChatService;

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> params) {
        String message = params.get("message");
        return aiChatService.chat(message);
    }
}