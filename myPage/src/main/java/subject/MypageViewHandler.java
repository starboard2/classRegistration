package subject;

import subject.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenStudentRegistered_then_CREATE_1 (@Payload RegisterCompleted registerCompleted) {
        try {
            System.out.println("##### whenStudentRegistered_then_CREATE_1 [[[[[  IN  ]]]]]");
            if (registerCompleted.isMe()) {
                // view 객체 생성
                System.out.println("##### if (studentRegistered.isMe()) [[[[[  True  ]]]]]");
                Mypage mypage = new Mypage();

                System.out.println("##### listener  : " + registerCompleted.toJson());
                System.out.println("##### myPage - whenStudent'Registered'_then_CREATE_1 #####");
                System.out.println("##### studentRegistered.studentId : " + registerCompleted.getId());
                System.out.println("##### studentRegistered.studentName : " + registerCompleted.getStudentName());
                System.out.println("##### studentRegistered.classId : " + registerCompleted.getClassId());
                System.out.println("##### studentRegistered.status : " + registerCompleted.getStatus());
                System.out.println("##############################################################");

                // view 객체에 이벤트의 Value 를 set 함
                mypage.setStudentId(registerCompleted.getId());
                mypage.setStudentName(registerCompleted.getStudentName());
                mypage.setClassId(registerCompleted.getClassId());
                mypage.setStatus(registerCompleted.getStatus());

                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenRegisterCompleted_then_UPDATE_1(@Payload RegisterCompleted registerCompleted) {
        try {
            System.out.println("##### whenRegisterCompleted_then_UPDATE_1 [[[[[  IN  ]]]]]");
//            System.out.println("##### listener  : " + registerCompleted.toJson());
            if (registerCompleted.isMe()) {
                // view 객체 조회
                System.out.println("##### if (registerCompleted.isMe()) [[[[[  True  ]]]]]");
                List<Mypage> mypageList = mypageRepository.findByStudentId(registerCompleted.getStudentId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
//
                    System.out.println("##### listener  : " + registerCompleted.toJson());
//                    System.out.println("##### myPage - whenRegister'Completed'_then_UPDATE_1 #####");
//                    System.out.println("##### registerCompleted.studentId : " + registerCompleted.getStudentName());
//                    System.out.println("##### registerCompleted.classId : " + registerCompleted.getClassId());
//                    System.out.println("##### registerCompleted.status : " + registerCompleted.getStatus());
//                    System.out.println("##############################################################");

                    mypage.setStatus(registerCompleted.getStatus());

                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRegisterCancelled_then_UPDATE_2(@Payload RegisterCancelled registerCancelled) {
        try {
            if (registerCancelled.isMe()) {
                // view 객체 조회
                List<Mypage> mypageList = mypageRepository.findByStudentId(registerCancelled.getStudentId());
                for(Mypage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함

                    System.out.println("##### listener  : " + registerCancelled.toJson());
                    System.out.println("##### myPage - whenRegister'Cancelled'_then_UPDATE_2 #####");
                    System.out.println("##### registerCancelled.studentId : " + registerCancelled.getStudentName());
                    System.out.println("##### registerCancelled.classId : " + registerCancelled.getClassId());
                    System.out.println("##### registerCancelled.status : " + registerCancelled.getStatus());
                    System.out.println("##############################################################");

                    mypage.setStatus(registerCancelled.getStatus());
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}