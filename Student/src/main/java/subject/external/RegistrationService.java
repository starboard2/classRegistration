
package subject.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

//@FeignClient(name="Registration", url="http://Registration:8080")
@FeignClient(name="Registration", url="${api.Registration.url}")
public interface RegistrationService {

    @RequestMapping(method= RequestMethod.POST, path="/registrations")
    public void registerRequest(@RequestBody Registration registration);

}