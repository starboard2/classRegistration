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
    StudentRepository studentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegisterCompleted_UpdateStatus(@Payload RegisterCompleted registerCompleted){

        if(registerCompleted.isMe()){
            System.out.println("##### listener  : " + registerCompleted.toJson());
            System.out.println("##### Student - wheneverRegister'Completed'_UpdateStatus #####");
            System.out.println("##### student.ID : " + registerCompleted.getStudentId());
            System.out.println("##############################################################");

            Optional<Student> orderOptional = studentRepository.findById(registerCompleted.getStudentId());
            Student student = orderOptional.get();//위에서 find한 오더 객체를 찾아서 매핑
            student.setStatus("register completed");
            studentRepository.save(student);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegisterCancelled_UpdateStatus(@Payload RegisterCancelled registerCancelled){

        if(registerCancelled.isMe()){
            System.out.println("##### listener  : " + registerCancelled.toJson());

            System.out.println("##### Student - wheneverRegister'Cancelled'_UpdateStatus #####");
            System.out.println("##### student.ID : " + registerCancelled.getStudentId());
            System.out.println("############################################################");

            Optional<Student> orderOptional = studentRepository.findById(registerCancelled.getStudentId());
            Student student = orderOptional.get();//위에서 find한 오더 객체를 찾아서 매핑
            student.setStatus("register Cancelled");
            studentRepository.save(student);
        }
    }

}
