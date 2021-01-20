package subject;

import subject.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired//추가
    RegistrationRepository registrationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCancelled_registerCancel(@Payload Cancelled cancelled){

        if(cancelled.isMe()){
            System.out.println("##### listener  : " + cancelled.toJson());

            System.out.println("##### Registration - whenever'Cancelled'_registerCancel #####");
            System.out.println("##### Registration.classID : " + cancelled.getClassId());
            System.out.println("##### Registration.studentID : " + cancelled.getId());
            System.out.println("##############################################################");

            Optional<Registration> paymentOptional = registrationRepository.findById(Long.valueOf(cancelled.getId()));
            Registration registration = paymentOptional.get();//위에서 find한 오더 객체를 찾아서 매핑
            registration.setStatus("Registration Cancelled");
            registrationRepository.save(registration);
        }
    }

}
