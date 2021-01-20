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
    SubjectRepository subjectRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegisterCancelled_Modify(@Payload RegisterCancelled registerCancelled){

        if(registerCancelled.isMe()){
            System.out.println("##### listener  : " + registerCancelled.toJson());

            //추가
            Optional<Subject> productOptional = subjectRepository.findById(Long.valueOf(registerCancelled.getClassId()).longValue());
            Subject subject = productOptional.get();//위에서 find한 오더 객체를 찾아서 매핑

            System.out.println("##### Subject - wheneverRegister'Cancelled'_Modify #####");
            System.out.println("##### subject.qty() : " + subject.getMaximumStudent());
            System.out.println("######################################################");

            long modifyQty = subject.getMaximumStudent() + 1;
            subject.setMaximumStudent(modifyQty);
            subjectRepository.save(subject);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegisterCompleted_Modify(@Payload RegisterCompleted registerCompleted){

        if(registerCompleted.isMe()){
            System.out.println("##### listener  : " + registerCompleted.toJson());

            //추가
            Optional<Subject> productOptional = subjectRepository.findById(Long.valueOf(registerCompleted.getClassId()).longValue());
            Subject subject = productOptional.get();//위에서 find한 오더 객체를 찾아서 매핑

            System.out.println("##### Subject - wheneverRegister'Completed'_Modify #####");
            System.out.println("##### subject.qty() : " + subject.getMaximumStudent());
            System.out.println("######################################################");

            long modifyQty = subject.getMaximumStudent() - 1;
            subject.setMaximumStudent(modifyQty);
            subjectRepository.save(subject);
        }
    }

}
